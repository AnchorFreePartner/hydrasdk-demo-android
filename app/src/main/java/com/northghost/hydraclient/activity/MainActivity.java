package com.northghost.hydraclient.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import com.anchorfree.hydrasdk.HydraSDKConfig;
import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.SessionConfig;
import com.anchorfree.hydrasdk.SessionInfo;
import com.anchorfree.hydrasdk.api.AuthMethod;
import com.anchorfree.hydrasdk.api.ClientInfo;
import com.anchorfree.hydrasdk.api.data.Country;
import com.anchorfree.hydrasdk.api.data.ServerCredentials;
import com.anchorfree.hydrasdk.api.response.RemainingTraffic;
import com.anchorfree.hydrasdk.api.response.User;
import com.anchorfree.hydrasdk.callbacks.Callback;
import com.anchorfree.hydrasdk.callbacks.CompletableCallback;
import com.anchorfree.hydrasdk.callbacks.TrafficListener;
import com.anchorfree.hydrasdk.callbacks.VpnCallback;
import com.anchorfree.hydrasdk.callbacks.VpnStateListener;
import com.anchorfree.hydrasdk.compat.CredentialsCompat;
import com.anchorfree.hydrasdk.dns.DnsRule;
import com.anchorfree.hydrasdk.exceptions.ApiHydraException;
import com.anchorfree.hydrasdk.exceptions.HydraException;
import com.anchorfree.hydrasdk.exceptions.NetworkRelatedException;
import com.anchorfree.hydrasdk.exceptions.RequestException;
import com.anchorfree.hydrasdk.exceptions.VPNException;
import com.anchorfree.hydrasdk.fireshield.FireshieldCategory;
import com.anchorfree.hydrasdk.fireshield.FireshieldConfig;
import com.anchorfree.hydrasdk.vpnservice.ConnectionStatus;
import com.anchorfree.hydrasdk.vpnservice.VPNState;
import com.anchorfree.hydrasdk.vpnservice.config.VpnConfig;
import com.anchorfree.hydrasdk.vpnservice.connectivity.NotificationConfig;
import com.anchorfree.hydrasdk.vpnservice.credentials.AppPolicy;
import com.anchorfree.reporting.TrackingConstants;
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
        HydraSdk.addTrafficListener(this);
        HydraSdk.addVpnListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HydraSdk.removeVpnListener(this);
        HydraSdk.removeTrafficListener(this);
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
    public void vpnError(VPNException e) {
        updateUI();
        handleError(e);
    }

    @Override
    protected boolean isLoggedIn() {
        return HydraSdk.isLoggedIn();
    }

    @Override
    protected void loginToVpn() {
        showLoginProgress();
        AuthMethod authMethod = AuthMethod.anonymous();
        HydraSdk.login(authMethod, new Callback<User>() {
            @Override
            public void success(User user) {
                hideLoginProgress();
                updateUI();
            }

            @Override
            public void failure(HydraException e) {
                hideLoginProgress();
                updateUI();

                handleError(e);
            }
        });
    }

    @Override
    protected void logOutFromVnp() {
        showLoginProgress();

        HydraSdk.logout();
        selectedCountry = "";

        hideLoginProgress();
        updateUI();
    }

    @Override
    protected void isConnected(Callback<Boolean> callback) {
        HydraSdk.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                callback.success(vpnState == VPNState.CONNECTED);
            }

            @Override
            public void failure(@NonNull HydraException e) {
                callback.success(false);
            }
        });
    }

    @Override
    protected void connectToVpn() {
        if (HydraSdk.isLoggedIn()) {
            showConnectProgress();
            List<String> bypassDomains = new LinkedList<>();
            bypassDomains.add("*facebook.com");
            bypassDomains.add("*wtfismyip.com");
            HydraSdk.startVPN(new SessionConfig.Builder()
                    .withReason(TrackingConstants.GprReasons.M_UI)
                    .withVirtualLocation(selectedCountry)
                    .addDnsRule(DnsRule.Builder.bypass().fromDomains(bypassDomains))
                    .build(), new Callback<ServerCredentials>() {
                @Override
                public void success(ServerCredentials serverCredentials) {
                    hideConnectProgress();
                    startUIUpdateTask();
                }

                @Override
                public void failure(HydraException e) {
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
    protected void disconnectFromVnp() {
        showConnectProgress();
        HydraSdk.stopVPN(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
                hideConnectProgress();
                stopUIUpdateTask();
            }

            @Override
            public void error(HydraException e) {
                hideConnectProgress();
                updateUI();

                handleError(e);
            }
        });
    }

    @Override
    protected void chooseServer() {
        if (HydraSdk.isLoggedIn()) {
            RegionChooserDialog.newInstance().show(getSupportFragmentManager(), RegionChooserDialog.TAG);
        } else {
            showMessage("Login please");
        }
    }

    @Override
    protected void getCurrentServer(final Callback<String> callback) {
        HydraSdk.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState state) {
                if (state == VPNState.CONNECTED) {
                    HydraSdk.getSessionInfo(new Callback<SessionInfo>() {
                        @Override
                        public void success(@NonNull SessionInfo sessionInfo) {
                            callback.success(CredentialsCompat.getServerCountry(sessionInfo.getCredentials()));
                        }

                        @Override
                        public void failure(@NonNull HydraException e) {
                            callback.success(selectedCountry);
                        }
                    });
                } else {
                    callback.success(selectedCountry);
                }
            }

            @Override
            public void failure(@NonNull HydraException e) {
                callback.failure(e);
            }
        });
    }

    @Override
    protected void checkRemainingTraffic() {
        HydraSdk.remainingTraffic(new Callback<RemainingTraffic>() {
            @Override
            public void success(RemainingTraffic remainingTraffic) {
                updateRemainingTraffic(remainingTraffic);
            }

            @Override
            public void failure(HydraException e) {
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

        HydraSdk.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState state) {
                if (state == VPNState.CONNECTED) {
                    showMessage("Reconnecting to VPN with " + selectedCountry);
                    HydraSdk.stopVPN(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                        @Override
                        public void complete() {
                            connectToVpn();
                        }

                        @Override
                        public void error(HydraException e) {
                            // In this case we try to reconnect
                            selectedCountry = "";
                            connectToVpn();
                        }
                    });
                }
            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
    }

    // Example of error handling
    public void handleError(Throwable e) {
        Log.w(TAG, e);
        if (e instanceof NetworkRelatedException) {
            showMessage("Check internet connection");
        } else if (e instanceof VPNException) {
            switch (((VPNException) e).getCode()) {
                case VPNException.REVOKED:
                    showMessage("User revoked vpn permissions");
                    break;
                case VPNException.VPN_PERMISSION_DENIED_BY_USER:
                    showMessage("User canceled to grant vpn permissions");
                    break;
                case VPNException.HYDRA_ERROR_BROKEN:
                    showMessage("Connection with vpn service was lost");
                    break;
                case VPNException.HYDRA_DCN_BLOCKED_BW:
                    showMessage("Client traffic exceeded");
                    break;
                default:
                    showMessage("Error in VPN Service");
                    break;
            }
        } else if (e instanceof ApiHydraException) {
            switch (((ApiHydraException) e).getContent()) {
                case RequestException.CODE_NOT_AUTHORIZED:
                    showMessage("User unauthorized");
                    break;
                case RequestException.CODE_TRAFFIC_EXCEED:
                    showMessage("Server unavailable");
                    break;
                default:
                    showMessage("Other error. Check RequestException constants");
                    break;
            }
        }
    }

    //fake method to support migration documentation and list all available methods
    public void sdkMethodsList() {
        HydraSdk.deletePurchase(0, CompletableCallback.EMPTY);
        HydraSdk.getStartVpnTimestamp(new Callback<Long>() {
            @Override
            public void success(@NonNull Long aLong) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.getConnectionStatus(new Callback<ConnectionStatus>() {
            @Override
            public void success(@NonNull ConnectionStatus connectionStatus) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.remainingTraffic(new Callback<RemainingTraffic>() {
            @Override
            public void success(@NonNull RemainingTraffic remainingTraffic) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.restartVpn("", TrackingConstants.GprReasons.A_APP_RUN, AppPolicy.newBuilder().build(), new Bundle(), new Callback<Bundle>() {
            @Override
            public void success(@NonNull Bundle bundle) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.setLoggingLevel(Log.VERBOSE);
        HydraSdk.isPausedForReconnection();
        HydraSdk.collectDebugInfo(new Callback<HydraSdk.DebugInfo>() {
            @Override
            public void success(@NonNull HydraSdk.DebugInfo debugInfo) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.collectDebugInfo(new Callback<HydraSdk.DebugInfo>() {
            @Override
            public void success(@NonNull HydraSdk.DebugInfo debugInfo) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.purchase("", new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(HydraException e) {

            }
        });
        HydraSdk.purchase("", "", new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(HydraException e) {

            }
        });
        HydraSdk.getConnectionStatus(new Callback<ConnectionStatus>() {
            @Override
            public void success(@NonNull ConnectionStatus connectionStatus) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.current(new Callback<User>() {
            @Override
            public void success(@NonNull User user) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.getDeviceId();
        HydraSdk.currentUser(new Callback<User>() {
            @Override
            public void success(@NonNull User user) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.getSessionInfo(new Callback<SessionInfo>() {
            @Override
            public void success(@NonNull SessionInfo sessionInfo) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.getAccessToken();
        HydraSdk.getTrafficStats();
        HydraSdk.requestVpnPermission(new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(HydraException e) {

            }
        });
        HydraSdk.getConnectionAttemptId();
        HydraSdk.getStartVpnTimestamp();
        HydraSdk.getVpnState();
        HydraSdk.getTrafficStats();
        HydraSdk.getLoggingLevel();
        HydraSdk.isABISupported();
        HydraSdk.isLoggingEnabled();
        HydraSdk.isVpnStarted();
        HydraSdk.isLoggingEnabled();
        HydraSdk.collectDebugInfo(new Callback<HydraSdk.DebugInfo>() {
            @Override
            public void success(@NonNull HydraSdk.DebugInfo debugInfo) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.collectDebugInfo(this, new Callback<String>() {
            @Override
            public void success(@NonNull String s) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.current(new Callback<User>() {
            @Override
            public void success(@NonNull User user) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.startVPN("", new Callback<ServerCredentials>() {
            @Override
            public void success(@NonNull ServerCredentials serverCredentials) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.startVPN("", "", new Callback<ServerCredentials>() {
            @Override
            public void success(@NonNull ServerCredentials serverCredentials) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.startVPNExceptApps(new ArrayList<>(), "", new Callback<ServerCredentials>() {
            @Override
            public void success(@NonNull ServerCredentials serverCredentials) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.startVPNExceptApps("", new ArrayList<>(), "", new Callback<ServerCredentials>() {
            @Override
            public void success(@NonNull ServerCredentials serverCredentials) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.startVPNForApps(new ArrayList<>(), "", new Callback<ServerCredentials>() {
            @Override
            public void success(@NonNull ServerCredentials serverCredentials) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.startVPNForApps("", new ArrayList<>(), "", new Callback<ServerCredentials>() {
            @Override
            public void success(@NonNull ServerCredentials serverCredentials) {

            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
        HydraSdk.addTrafficListener(new TrafficListener() {
            @Override
            public void onTrafficUpdate(long l, long l1) {

            }
        });
        HydraSdk.addVpnCallListener(new VpnCallback() {
            @Override
            public void onVpnCall(Parcelable parcelable) {

            }
        });
        HydraSdk.addVpnListener(new VpnStateListener() {
            @Override
            public void vpnStateChanged(VPNState vpnState) {

            }

            @Override
            public void vpnError(VPNException e) {

            }
        });
        HydraSdk.removeTrafficListener(null);
        HydraSdk.removeVpnCallListener(null);
        HydraSdk.removeVpnListener(null);

        HydraSdk.init(this, ClientInfo.newBuilder()
                        .baseUrl("")
                        .carrierId("")
                        .email("")
                        .build(),
                NotificationConfig.newBuilder()
                        .inConnected("", "")
                        .channelId("")
                        .clickAction("")
                        .disabled()
                        .enableConnectionLost()
                        .icon(null)
                        .inConnecting("", "")
                        .inIdle("", "")
                        .title("")
                        .smallIconId(0)
                        .build(),
                HydraSDKConfig.newBuilder()
                        .captivePortal(false)
                        .moveToIdleOnPause(false)
                        .observeNetworkChanges(true)
                        .unsafeClient(false)
                        .addBlacklistDomain("")
                        .addBlacklistDomains(0)
                        .addBypassDomain("")
                        .addBypassDomains(0)
                        .addBypassDomain(VpnConfig.MODE.BYPASS, "")
                        .addBypassDomain(VpnConfig.MODE.PROXY_PEER, "")
                        .addBypassDomain(VpnConfig.MODE.VPN, "")
                        .build());
        HydraSdk.update(NotificationConfig.newBuilder()
                .inConnected("", "")
                .channelId("")
                .clickAction("")
                .disabled()
                .enableConnectionLost()
                .icon(null)
                .inConnecting("", "")
                .inIdle("", "")
                .title("")
                .smallIconId(0)
                .build());
        HydraSdk.updateConfig(new SessionConfig.Builder()
                .addDnsRule(DnsRule.Builder.block().fromAssets(""))
                .addDnsRule(DnsRule.Builder.bypass().fromAssets(""))
                .addDnsRule(DnsRule.Builder.proxy().fromAssets(""))
                .addDnsRule(DnsRule.Builder.vpn().fromAssets(""))
                .addDnsRule(DnsRule.Builder.vpn().fromDomains(new ArrayList<>()))
                .addDnsRule(DnsRule.Builder.vpn().fromFile(""))
                .addDnsRule(DnsRule.Builder.vpn().fromResource(0))
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
            public void error(HydraException e) {

            }
        });
    }
}
