package com.northghost.hydraclient.activity;

import android.util.Log;

import androidx.annotation.NonNull;

import unified.vpn.sdk.*;
import com.northghost.hydraclient.MainApplication;
import com.northghost.hydraclient.adapter.RegionListAdapter;
import com.northghost.hydraclient.dialog.LoginDialog;
import com.northghost.hydraclient.dialog.RegionChooserDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends UIActivity implements TrafficListener, VpnStateListener,
        LoginDialog.LoginConfirmationInterface, RegionChooserDialog.RegionChooserInterface {
    public static final String TAG = "MainActivity";
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
    protected void logOutFromVpn() {
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
    protected void connectToVpn(CompletableCallback callback) {
        isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    showConnectProgress();
                    UnifiedSdk.getInstance().getVpn().start(getSessionConfig(), new CompletableCallback() {
                        @Override
                        public void complete() {
                            callback.complete();
                            hideConnectProgress();
                            startUIUpdateTask();
                        }

                        @Override
                        public void error(@NonNull VpnException e) {
                            callback.error(e);
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

    private SessionConfig getSessionConfig() {
        final ArrayList<String> domains = new ArrayList<>();
        domains.add("ip.me");
        List<String> fallbackOrder = new ArrayList<>();
        fallbackOrder.add(HydraTransport.TRANSPORT_ID);
        fallbackOrder.add(OpenVpnTransport.TRANSPORT_ID_TCP);
        fallbackOrder.add(OpenVpnTransport.TRANSPORT_ID_UDP);
        SessionConfig.Builder config = new SessionConfig.Builder()
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

                .withLocation(selectedCountry);
//                            .addDnsRule(TrafficRule.dns().bypass().fromDomains(bypassDomains))
        if (!patchAddress.isEmpty()) {
            config = SdkConfigPatcherFactory.Companion.addPatcherToSessionConfig(config, patchAddress);
        }
        return config.build();
    }

    @Override
    protected void disconnectFromVpn() {
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
                            callback.success(Objects.requireNonNull(sessionInfo.getCredentials()).getFirstServerIp());
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

    void logRemove(VpnStateListener stateListener) {
        Log.d(TAG, stateListener + " remove");
        UnifiedSdk.removeVpnStateListener(stateListener);
    }

    void logAdd(VpnStateListener stateListener) {
        Log.d(TAG, stateListener + " add");
        UnifiedSdk.addVpnStateListener(stateListener);
    }

    List<VpnStateListener> history = new ArrayList<>();

    class WaitConnectAndReconnect implements VpnStateListener {
        final int idx;

        WaitConnectAndReconnect(int idx) {
            this.idx = idx;
        }

        @Override
        public String toString() {
            return "WaitConnectAndReconnect-" + idx;
        }

        @Override
        public void vpnStateChanged(@NonNull final VpnState vpnState) {
            Log.d(TAG, this + " got state " + vpnState);
            VpnStateListener self = this;
            if (vpnState == VpnState.CONNECTED) {
                // get to connected. can restart
                Log.d(TAG, this + " will call restart");
                logRemove(self);
                UnifiedSdk.getInstance().getVpn().restart(getSessionConfig(), new CompletableCallback() {
                    @Override
                    public void complete() {
                        // got connected
                        Log.d(TAG, self + " got connected");

                    }

                    @Override
                    public void error(@NonNull VpnException e) {
                        // got error when restarting. Can try again or show user an error
                        Log.e(TAG, self + " restart got error", e);
                        showMessage("Cannot connect to server");
                    }
                });
            }
        }

        @Override
        public void vpnError(@NonNull final VpnException e) {
            // got vpn error from sdk. Can do another connection attempt or report error to user
            Log.e(TAG, this + " vpnError", e);
            VpnStateListener self = this;
            logRemove(self);
            connectToVpn(new CompletableCallback() {
                @Override
                public void complete() {
                    Log.d(TAG, self + " connect complete");
                }

                @Override
                public void error(@NonNull VpnException e) {
                    Log.e(TAG, self + " connect vpnError", e);
                }
            });
        }
    }

    ;

    class WaitIdleAndConnect implements VpnStateListener {
        final int idx;

        WaitIdleAndConnect(int idx) {
            this.idx = idx;
        }

        @Override
        public String toString() {
            return "WaitIdleAndConnect-" + idx;
        }

        @Override
        public void vpnStateChanged(@NonNull final VpnState vpnState) {
            Log.d(TAG, this + " got state" + vpnState);
            VpnStateListener self = this;
            if (vpnState == VpnState.IDLE) {
                logRemove(this);
                connectToVpn(new CompletableCallback() {
                    @Override
                    public void complete() {
                        Log.d(TAG, self + " connect complete");
                    }

                    @Override
                    public void error(@NonNull VpnException e) {
                        Log.e(TAG, self + " connect vpnError", e);
                    }
                });
            }
        }

        @Override
        public void vpnError(@NonNull final VpnException e) {
            // got vpn error from sdk. Can do another connection attempt or report error to user
            VpnStateListener self = this;
            Log.e(TAG, self + " vpnError", e);
            logRemove(this);
            connectToVpn(new CompletableCallback() {
                @Override
                public void complete() {
                    Log.d(TAG, self + " connect complete");
                }

                @Override
                public void error(@NonNull VpnException e) {
                    Log.e(TAG, self + " connect vpnError", e);
                }
            });
        }
    }

    ;
    int idx = 0;

    @Override
    public void onRegionSelected(RegionListAdapter.Region item) {

        selectedCountry = item.getName();
        updateUI();
        for (VpnStateListener listener : history) {
            logRemove(listener);
        }


        UnifiedSdk.getVpnState(new Callback<VpnState>() {
            @Override
            public void success(@NonNull VpnState state) {
                Log.d(TAG, String.format("onRegionSelected got %s", state));
                switch (state) {
                    case IDLE -> {
                        // when in idle can connect right away
                        final WaitIdleAndConnect listener = new WaitIdleAndConnect(++idx);
                        logAdd(listener);
                        history.add(listener);
                    }
                    case CONNECTING_CREDENTIALS, CONNECTING_PERMISSIONS, CONNECTING_VPN -> {
                        // when in connecting need to wait completion
                        final WaitConnectAndReconnect listener = new WaitConnectAndReconnect(++idx);
                        logAdd(listener);
                        history.add(listener);
                    }
                    case PAUSED -> {
                        // if in paused - need to wait for connected state. Or do something else, depends on UX
                        final WaitConnectAndReconnect listener = new WaitConnectAndReconnect(++idx);
                        logAdd(listener);
                        history.add(listener);
                    }
                    case DISCONNECTING -> {
                        // when in progress of disconnect, wait idle and then connect
                        final WaitIdleAndConnect listener = new WaitIdleAndConnect(++idx);
                        logAdd(listener);
                        history.add(listener);
                    }
                    case CONNECTED -> {
                        // when connected can restart
                        final WaitConnectAndReconnect listener = new WaitConnectAndReconnect(++idx);
                        logAdd(listener);
                        history.add(listener);
                    }

                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                Log.e(TAG, "onRegionSelected got error", e);
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
