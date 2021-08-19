package com.northghost.hydraclient.activity;

import android.util.Log;

import androidx.annotation.NonNull;

import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.reporting.TrackingConstants;
import com.anchorfree.sdk.SessionConfig;
import com.anchorfree.sdk.SessionInfo;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.exceptions.PartnerApiException;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.anchorfree.vpnsdk.callbacks.TrafficListener;
import com.anchorfree.vpnsdk.callbacks.VpnStateListener;
import com.anchorfree.vpnsdk.compat.CredentialsCompat;
import com.anchorfree.vpnsdk.exceptions.NetworkRelatedException;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionDeniedException;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionRevokedException;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.anchorfree.vpnsdk.transporthydra.HydraVpnTransportException;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.northghost.caketube.CaketubeTransport;
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
}
