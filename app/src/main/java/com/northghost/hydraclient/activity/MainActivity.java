package com.northghost.hydraclient.activity;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.reporting.TrackingConstants;
import com.anchorfree.sdk.SdkInfo;
import com.anchorfree.sdk.SessionConfig;
import com.anchorfree.sdk.SessionInfo;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.VpnPermissions;
import com.anchorfree.sdk.exceptions.CnlBlockedException;
import com.anchorfree.sdk.exceptions.InvalidTransportException;
import com.anchorfree.sdk.exceptions.PartnerApiException;
import com.anchorfree.sdk.fireshield.FireshieldCategory;
import com.anchorfree.sdk.fireshield.FireshieldConfig;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.anchorfree.vpnsdk.callbacks.TrafficListener;
import com.anchorfree.vpnsdk.callbacks.VpnCallback;
import com.anchorfree.vpnsdk.callbacks.VpnStateListener;
import com.anchorfree.vpnsdk.compat.CredentialsCompat;
import com.anchorfree.vpnsdk.exceptions.BrokenRemoteProcessException;
import com.anchorfree.vpnsdk.exceptions.ConnectionCancelledException;
import com.anchorfree.vpnsdk.exceptions.ConnectionTimeoutException;
import com.anchorfree.vpnsdk.exceptions.CorruptedConfigException;
import com.anchorfree.vpnsdk.exceptions.CredentialsLoadException;
import com.anchorfree.vpnsdk.exceptions.GenericPermissionException;
import com.anchorfree.vpnsdk.exceptions.InternalException;
import com.anchorfree.vpnsdk.exceptions.NetworkChangeVpnException;
import com.anchorfree.vpnsdk.exceptions.NetworkRelatedException;
import com.anchorfree.vpnsdk.exceptions.NoCredsSourceException;
import com.anchorfree.vpnsdk.exceptions.NoNetworkException;
import com.anchorfree.vpnsdk.exceptions.NoVpnTransportsException;
import com.anchorfree.vpnsdk.exceptions.ServiceBindFailedException;
import com.anchorfree.vpnsdk.exceptions.StopCancelledException;
import com.anchorfree.vpnsdk.exceptions.TrackableException;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionDeniedException;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionNotGrantedExeption;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionRevokedException;
import com.anchorfree.vpnsdk.exceptions.VpnTransportException;
import com.anchorfree.vpnsdk.exceptions.WrongStateException;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.anchorfree.vpnsdk.transporthydra.HydraVpnTransportException;
import com.anchorfree.vpnsdk.vpnservice.ConnectionStatus;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.anchorfree.vpnsdk.vpnservice.credentials.AppPolicy;
import com.anchorfree.vpnsdk.vpnservice.credentials.CaptivePortalException;
import com.northghost.caketube.CaketubeTransport;
import com.northghost.caketube.exceptions.CaketubeTransportException;
import com.northghost.hydraclient.MainApplication;
import com.northghost.hydraclient.dialog.LoginDialog;
import com.northghost.hydraclient.dialog.RegionChooserDialog;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends UIActivity implements TrafficListener, VpnStateListener,
        LoginDialog.LoginConfirmationInterface, RegionChooserDialog.RegionChooserInterface {

    private String selectedCountry = "";

    @Override
    protected void onStart() {
        super.onStart();
        UnifiedSDK.addTrafficListener(this);
        UnifiedSDK.addVpnStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UnifiedSDK.removeVpnStateListener(this);
        UnifiedSDK.removeTrafficListener(this);
    }

    @Override
    public void onTrafficUpdate(long bytesTx, long bytesRx) {
        updateUI();
        updateTrafficStats(bytesTx, bytesRx);
    }

    @Override
    public void vpnStateChanged(VPNState vpnState) {
        updateUI();
    }

    @Override
    public void vpnError(VpnException e) {
        updateUI();
        handleError(e);
    }

    @Override
    protected void isLoggedIn(Callback<Boolean> callback) {
        UnifiedSDK.getInstance().getBackend().isLoggedIn(callback);
    }

    @Override
    protected void loginToVpn() {
        showLoginProgress();
        AuthMethod authMethod = AuthMethod.anonymous();
        UnifiedSDK.getInstance().getBackend().login(authMethod, new Callback<User>() {
            @Override
            public void success(User user) {
                hideLoginProgress();
                updateUI();
            }

            @Override
            public void failure(VpnException e) {
                hideLoginProgress();
                updateUI();

                handleError(e);
            }
        });
    }

    @Override
    protected void logOutFromVnp() {
        showLoginProgress();

        UnifiedSDK.getInstance().getBackend().logout(new CompletableCallback() {
            @Override
            public void complete() {
                hideLoginProgress();
                updateUI();
            }

            @Override
            public void error(VpnException e) {
                hideLoginProgress();
                updateUI();
            }
        });
        selectedCountry = "";
    }

    @Override
    protected void isConnected(Callback<Boolean> callback) {
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                callback.success(vpnState == VPNState.CONNECTED);
            }

            @Override
            public void failure(@NonNull VpnException e) {
                callback.success(false);
            }
        });
    }

    @Override
    protected void connectToVpn() {
        isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    List<String> fallbackOrder = new ArrayList<>();
                    fallbackOrder.add(HydraTransport.TRANSPORT_ID);
                    fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
                    fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
                    showConnectProgress();
                    List<String> bypassDomains = new LinkedList<>();
                    bypassDomains.add("*domain1.com");
                    bypassDomains.add("*domain2.com");
                    UnifiedSDK.getInstance().getVPN().start(new SessionConfig.Builder()
                            .withReason(TrackingConstants.GprReasons.M_UI)
                            .withTransportFallback(fallbackOrder)
                            .withTransport(HydraTransport.TRANSPORT_ID)
                            .withVirtualLocation(selectedCountry)
                            .addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains))
                            .build(), new CompletableCallback() {
                        @Override
                        public void complete() {
                            hideConnectProgress();
                            startUIUpdateTask();
                        }

                        @Override
                        public void error(@NonNull VpnException e) {
                            hideConnectProgress();
                            updateUI();

                            handleError(e);
                        }
                    });
                } else {
                    showMessage("Login please");
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
    }

    @Override
    protected void disconnectFromVnp() {
        showConnectProgress();
        UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
                hideConnectProgress();
                stopUIUpdateTask(true);
            }

            @Override
            public void error(VpnException e) {
                hideConnectProgress();
                updateUI();

                handleError(e);
            }
        });
    }

    @Override
    protected void chooseServer() {
        isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    RegionChooserDialog.newInstance().show(getSupportFragmentManager(), RegionChooserDialog.TAG);
                } else {
                    showMessage("Login please");
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
    }

    @Override
    protected void getCurrentServer(final Callback<String> callback) {
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState state) {
                if (state == VPNState.CONNECTED) {
                    UnifiedSDK.getStatus(new Callback<SessionInfo>() {
                        @Override
                        public void success(@NonNull SessionInfo sessionInfo) {
                            callback.success(CredentialsCompat.getServerCountry(sessionInfo.getCredentials()));
                        }

                        @Override
                        public void failure(@NonNull VpnException e) {
                            callback.success(selectedCountry);
                        }
                    });
                } else {
                    callback.success(selectedCountry);
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                callback.failure(e);
            }
        });
    }

    @Override
    protected void checkRemainingTraffic() {
        UnifiedSDK.getInstance().getBackend().remainingTraffic(new Callback<RemainingTraffic>() {
            @Override
            public void success(RemainingTraffic remainingTraffic) {
                updateRemainingTraffic(remainingTraffic);
            }

            @Override
            public void failure(VpnException e) {
                updateUI();

                handleError(e);
            }
        });
    }

    @Override
    public void setLoginParams(String hostUrl, String carrierId) {
        ((MainApplication) getApplication()).setNewHostAndCarrier(hostUrl, carrierId);
    }

    @Override
    public void loginUser() {
        loginToVpn();
    }

    @Override
    public void onRegionSelected(Country item) {

        selectedCountry = item.getCountry();
        updateUI();

        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState state) {
                if (state == VPNState.CONNECTED) {
                    showMessage("Reconnecting to VPN with " + selectedCountry);
                    UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                        @Override
                        public void complete() {
                            connectToVpn();
                        }

                        @Override
                        public void error(VpnException e) {
                            // In this case we try to reconnect
                            selectedCountry = "";
                            connectToVpn();
                        }
                    });
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
    }

    // Example of error handling
    public void handleError(Throwable e) {
        Log.w(TAG, e);
        if (e instanceof NetworkRelatedException) {
            showMessage("Check internet connection");
        } else if (e instanceof VpnException) {
            if (e instanceof VpnPermissionRevokedException) {
                showMessage("User revoked vpn permissions");
            } else if (e instanceof VpnPermissionDeniedException) {
                showMessage("User canceled to grant vpn permissions");
            } else if (e instanceof HydraVpnTransportException) {
                HydraVpnTransportException hydraVpnTransportException = (HydraVpnTransportException) e;
                if (hydraVpnTransportException.getCode() == HydraVpnTransportException.HYDRA_ERROR_BROKEN) {
                    showMessage("Connection with vpn server was lost");
                } else if (hydraVpnTransportException.getCode() == HydraVpnTransportException.HYDRA_DCN_BLOCKED_BW) {
                    showMessage("Client traffic exceeded");
                } else {
                    showMessage("Error in VPN transport");
                }
            } else if (e instanceof PartnerApiException) {
                switch (((PartnerApiException) e).getContent()) {
                    case PartnerApiException.CODE_NOT_AUTHORIZED:
                        showMessage("User unauthorized");
                        break;
                    case PartnerApiException.CODE_TRAFFIC_EXCEED:
                        showMessage("Server unavailable");
                        break;
                    default:
                        showMessage("Other error. Check PartnerApiException constants");
                        break;
                }
            }
        } else {
            showMessage("Error in VPN Service");
        }
    }

    //fake method to support migration documentation and list all available methods
    public void sdkMethodsList() {
        final UnifiedSDK instance = UnifiedSDK.getInstance();
        ClientInfo.newBuilder().addUrl("").carrierId("test").addUrls(new ArrayList<>()).build();
        instance.getBackend().deletePurchase(0, CompletableCallback.EMPTY);
        instance.getVPN().getStartTimestamp(new Callback<Long>() {
            @Override
            public void success(@NonNull Long aLong) {

            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {

            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        UnifiedSDK.getConnectionStatus(new Callback<ConnectionStatus>() {
            @Override
            public void success(@NonNull ConnectionStatus connectionStatus) {

            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        instance.getBackend().remainingTraffic(new Callback<RemainingTraffic>() {
            @Override
            public void success(@NonNull RemainingTraffic remainingTraffic) {

            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        instance.getVPN().restart(new SessionConfig.Builder()
                .withReason(TrackingConstants.GprReasons.M_UI)
                .addDnsRule(TrafficRule.Builder.blockDns().fromAssets(""))
                .addProxyRule(TrafficRule.Builder.blockPkt().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.bypass().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.proxy().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.vpn().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.vpn().fromDomains(new ArrayList<>()))
                .addDnsRule(TrafficRule.Builder.vpn().fromFile(""))
                .addDnsRule(TrafficRule.Builder.vpn().fromResource(0))
                .exceptApps(new ArrayList<>())
                .forApps(new ArrayList<>())
                .withVirtualLocation("")
                .withPolicy(AppPolicy.newBuilder().build())
                .withFireshieldConfig(new FireshieldConfig.Builder()
                        .addCategory(FireshieldCategory.Builder.block(""))
                        .addCategory(FireshieldCategory.Builder.blockAlertPage(""))
                        .addCategory(FireshieldCategory.Builder.bypass(""))
                        .addCategory(FireshieldCategory.Builder.custom("", ""))
                        .addCategory(FireshieldCategory.Builder.proxy(""))
                        .addCategory(FireshieldCategory.Builder.vpn(""))
                        .build())
                .build(), new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(@NonNull VpnException e) {

            }
        });
        UnifiedSDK.setLoggingLevel(Log.VERBOSE);

        instance.getBackend().purchase("", new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(VpnException e) {

            }
        });
        instance.getBackend().purchase("", "", new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(VpnException e) {

            }
        });
        UnifiedSDK.getInstance().getInfo(new Callback<SdkInfo>() {
            @Override
            public void success(@NonNull SdkInfo sdkInfo) {
                String deviceId = sdkInfo.getDeviceId();
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });

        instance.getBackend().currentUser(new Callback<User>() {
            @Override
            public void success(@NonNull User user) {

            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        UnifiedSDK.getStatus(new Callback<SessionInfo>() {
            @Override
            public void success(@NonNull SessionInfo sessionInfo) {

            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        UnifiedSDK.getInstance().getBackend().getAccessToken();

        VpnPermissions.request(new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(VpnException e) {

            }
        });

        UnifiedSDK.addVpnCallListener(new VpnCallback() {
            @Override
            public void onVpnCall(Parcelable parcelable) {

            }
        });
        UnifiedSDK.removeVpnCallListener(null);

        instance.getVPN().updateConfig(new SessionConfig.Builder()
                .withReason(TrackingConstants.GprReasons.M_UI)
                .addDnsRule(TrafficRule.Builder.blockDns().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.blockPkt().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.bypass().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.proxy().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.vpn().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.vpn().fromDomains(new ArrayList<>()))
                .addDnsRule(TrafficRule.Builder.vpn().fromFile(""))
                .addDnsRule(TrafficRule.Builder.vpn().fromResource(0))
                .addDnsRule(TrafficRule.Builder.vpn().fromIp("", 0))
                .addDnsRule(TrafficRule.Builder.vpn().fromIp("", 0, 0))
                .addDnsRule(TrafficRule.Builder.vpn().fromIp("", 0, 0, 0))
                .addDnsRule(TrafficRule.Builder.vpn().tcp())
                .addDnsRule(TrafficRule.Builder.vpn().tcp(0))
                .addDnsRule(TrafficRule.Builder.vpn().tcp(0, 0))
                .addDnsRule(TrafficRule.Builder.vpn().udp())
                .addDnsRule(TrafficRule.Builder.vpn().udp(0))
                .addDnsRule(TrafficRule.Builder.vpn().udp(0, 0))
                .exceptApps(new ArrayList<>())
                .withTransport("")
                .withSessionId("")
                .forApps(new ArrayList<>())
                .withVirtualLocation("")
                .withPolicy(AppPolicy.newBuilder().build())
                .withFireshieldConfig(new FireshieldConfig.Builder()
                        .addCategory(FireshieldCategory.Builder.block(""))
                        .addCategory(FireshieldCategory.Builder.blockAlertPage(""))
                        .addCategory(FireshieldCategory.Builder.bypass(""))
                        .addCategory(FireshieldCategory.Builder.custom("", ""))
                        .addCategory(FireshieldCategory.Builder.proxy(""))
                        .addCategory(FireshieldCategory.Builder.vpn(""))
                        .build())
                .build(), new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(VpnException e) {

            }
        });
        //exceptions
        Class[] ex = new Class[] {
                VpnException.class,
                BrokenRemoteProcessException.class,
                CaketubeTransportException.class,
                CaptivePortalException.class,
                CnlBlockedException.class,
                ConnectionCancelledException.class,
                ConnectionTimeoutException.class,
                CorruptedConfigException.class,
                CredentialsLoadException.class,
                GenericPermissionException.class,
                HydraVpnTransportException.class,
                InternalException.class,
                InvalidTransportException.class,
                NetworkChangeVpnException.class,
                NetworkRelatedException.class,
                NoCredsSourceException.class,
                NoNetworkException.class,
                NoVpnTransportsException.class,
                PartnerApiException.class,
                ServiceBindFailedException.class,
                StopCancelledException.class,
                TrackableException.class,
                VpnPermissionDeniedException.class,
                VpnPermissionRevokedException.class,
                VpnPermissionNotGrantedExeption.class,
                VpnTransportException.class,
                WrongStateException.class
        };
        String[] serrors = new String[] {
                PartnerApiException.CODE_PARSE_EXCEPTION,
                PartnerApiException.CODE_SESSIONS_EXCEED,
                PartnerApiException.CODE_DEVICES_EXCEED,
                PartnerApiException.CODE_INVALID,
                PartnerApiException.CODE_OAUTH_ERROR,
                PartnerApiException.CODE_TRAFFIC_EXCEED,
                PartnerApiException.CODE_NOT_AUTHORIZED,
                PartnerApiException.CODE_SERVER_UNAVAILABLE,
                PartnerApiException.CODE_INTERNAL_SERVER_ERROR,
                PartnerApiException.CODE_USER_SUSPENDED,
        };
        Integer[] errors = new Integer[] {
                CaketubeTransportException.CONNECTION_BROKEN_ERROR,
                CaketubeTransportException.CONNECTION_FAILED_ERROR,
                CaketubeTransportException.CONNECTION_AUTH_FAILURE,
                HydraVpnTransportException.HYDRA_CONNECTION_LOST,
                HydraVpnTransportException.TRAFFIC_EXCEED,
                HydraVpnTransportException.HYDRA_CANNOT_CONNECT,
                HydraVpnTransportException.HYDRA_ERROR_CONFIG,
                HydraVpnTransportException.HYDRA_ERROR_UNKNOWN,
                HydraVpnTransportException.HYDRA_ERROR_CONFIGURATION,
                HydraVpnTransportException.HYDRA_ERROR_BROKEN,
                HydraVpnTransportException.HYDRA_ERROR_INTERNAL,
                HydraVpnTransportException.HYDRA_ERROR_SERVER_AUTH,
                HydraVpnTransportException.HYDRA_ERROR_CANT_SEND,
                HydraVpnTransportException.HYDRA_ERROR_TIME_SKEW,
                HydraVpnTransportException.HYDRA_DCN_MIN,
                HydraVpnTransportException.HYDRA_DCN_SRV_SWITCH,
                HydraVpnTransportException.HYDRA_DCN_BLOCKED_BW,
                HydraVpnTransportException.HYDRA_DCN_BLOCKED_ABUSE,
                HydraVpnTransportException.HYDRA_DCN_BLOCKED_MALWARE,
                HydraVpnTransportException.HYDRA_DCN_BLOCKED_MISC,
                HydraVpnTransportException.HYDRA_DCN_REQ_BY_CLIAPP,
                HydraVpnTransportException.HYDRA_DCN_BLOCKED_AUTH,
                HydraVpnTransportException.HYDRA_DCN_MAX,
                HydraVpnTransportException.HYDRA_NOTIFY_AUTH_OK,
                HydraVpnTransportException.HYDRA_CONFIG_MALFORMED,
                VpnTransportException.TRANSPORT_ERROR_START_TIMEOUT,
        };
    }
}
