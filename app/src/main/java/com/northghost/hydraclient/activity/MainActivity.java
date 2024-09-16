package com.northghost.hydraclient.activity;

import android.util.Log;

import androidx.annotation.NonNull;

import unified.vpn.sdk.*;
import com.northghost.hydraclient.MainApplication;
import com.northghost.hydraclient.adapter.RegionListAdapter;
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
        UnifiedSdk.addTrafficListener(this);
        UnifiedSdk.addVpnStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UnifiedSdk.removeVpnStateListener(this);
        UnifiedSdk.removeTrafficListener(this);
    }

    @Override
    public void onTrafficUpdate(long bytesTx, long bytesRx) {
        updateUI();
        updateTrafficStats(bytesTx, bytesRx);
    }

    @Override
    public void vpnStateChanged(VpnState vpnState) {
        updateUI();
    }

    @Override
    public void vpnError(VpnException e) {
        updateUI();
        handleError(e);
    }

    @Override
    protected void isLoggedIn(Callback<Boolean> callback) {
        UnifiedSdk.getInstance().getBackend().isLoggedIn(callback);
    }

    @Override
    protected void loginToVpn() {
        showLoginProgress();
        AuthMethod authMethod = AuthMethod.anonymous();
        UnifiedSdk.getInstance().getBackend().login(authMethod, new Callback<User>() {
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

        UnifiedSdk.getInstance().getBackend().logout(new CompletableCallback() {
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
        UnifiedSdk.getVpnState(new Callback<VpnState>() {
            @Override
            public void success(@NonNull VpnState vpnState) {
                callback.success(vpnState == VpnState.CONNECTED);
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
                    fallbackOrder.add(OpenVpnTransport.TRANSPORT_ID_TCP);
                    fallbackOrder.add(OpenVpnTransport.TRANSPORT_ID_UDP);
                    showConnectProgress();
                    List<String> bypassDomains = new LinkedList<>();
                    bypassDomains.add("*domain1.com");
                    bypassDomains.add("*domain2.com");
                    final ArrayList<String> domains = new ArrayList<>();
                    domains.add("ip.me");
                    UnifiedSdk.getInstance().getVpn().start(new SessionConfig.Builder()
                            .withReason(TrackingConstants.GprReasons.M_UI)
                            .withTransportFallback(fallbackOrder)
                            .withTransport(HydraTransport.TRANSPORT_ID)
                            .withFireshieldConfig(new FireshieldConfig.Builder()
                                    .addService(FireshieldConfig.Services.IP)
                                    .addService(FireshieldConfig.Services.BITDEFENDER)
                                    .addCategory(FireshieldCategory.Builder.proxy(FireshieldConfig.Categories.SAFE))
                                    .addCategory(FireshieldCategory.Builder.proxy(FireshieldConfig.Categories.UNSAFE))
                                    .addCategory(FireshieldCategory.Builder.bypass("safeCategory"))
                                    .addCategoryRule(FireshieldCategoryRule.Builder.fromDomains("safeCategory", domains))
                                    .build())

                            .withLocation(selectedCountry)
//                            .addDnsRule(TrafficRule.dns().bypass().fromDomains(bypassDomains))
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
        UnifiedSdk.getInstance().getVpn().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
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
        UnifiedSdk.getVpnState(new Callback<VpnState>() {
            @Override
            public void success(@NonNull VpnState state) {
                if (state == VpnState.CONNECTED) {
                    UnifiedSdk.getStatus(new Callback<SessionInfo>() {
                        @Override
                        public void success(@NonNull SessionInfo sessionInfo) {
                            callback.success(sessionInfo.getCredentials().getFirstServerIp());
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
        UnifiedSdk.getInstance().getBackend().remainingTraffic(new Callback<RemainingTraffic>() {
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
    public void onRegionSelected(RegionListAdapter.Region item) {

        selectedCountry = item.getName();
        updateUI();

        UnifiedSdk.getVpnState(new Callback<VpnState>() {
            @Override
            public void success(@NonNull VpnState state) {
                if (state == VpnState.CONNECTED) {
                    showMessage("Reconnecting to VPN with " + selectedCountry);
                    UnifiedSdk.getInstance().getVpn().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
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
