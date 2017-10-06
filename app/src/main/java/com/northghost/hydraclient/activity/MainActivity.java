package com.northghost.hydraclient.activity;

import android.util.Log;
import com.anchorfree.hydrasdk.callbacks.Callback;
import com.anchorfree.hydrasdk.callbacks.CompletableCallback;
import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.api.ApiCallback;
import com.anchorfree.hydrasdk.api.ApiRequest;
import com.anchorfree.hydrasdk.api.AuthMethod;
import com.anchorfree.hydrasdk.api.data.Country;
import com.anchorfree.hydrasdk.api.data.ServerCredentials;
import com.anchorfree.hydrasdk.api.response.RemainingTraffic;
import com.anchorfree.hydrasdk.api.response.User;
import com.anchorfree.hydrasdk.callbacks.TrafficListener;
import com.anchorfree.hydrasdk.callbacks.VpnStateListener;
import com.anchorfree.hydrasdk.exceptions.ApiException;
import com.anchorfree.hydrasdk.exceptions.ApiHydraException;
import com.anchorfree.hydrasdk.exceptions.CaptivePortalErrorException;
import com.anchorfree.hydrasdk.exceptions.HttpException;
import com.anchorfree.hydrasdk.exceptions.HydraException;
import com.anchorfree.hydrasdk.exceptions.InternalException;
import com.anchorfree.hydrasdk.exceptions.NetworkException;
import com.anchorfree.hydrasdk.exceptions.RequestException;
import com.anchorfree.hydrasdk.exceptions.SystemPermissionsErrorException;
import com.anchorfree.hydrasdk.exceptions.VPNException;
import com.anchorfree.hydrasdk.vpnservice.VPNState;

import com.northghost.hydraclient.MainApplication;
import com.northghost.hydraclient.dialog.LoginDialog;
import com.northghost.hydraclient.dialog.RegionChooserDialog;
import java.net.HttpURLConnection;

public class MainActivity extends UIActivity implements TrafficListener, VpnStateListener,
        LoginDialog.LoginConfirmationInterface, RegionChooserDialog.RegionChooserInterface {

    private String selectedCountry = null;

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
        HydraSdk.login(authMethod, new ApiCallback<User>() {
            @Override
            public void success(ApiRequest apiRequest, User user) {
                hideLoginProgress();
                updateUI();
            }

            @Override
            public void failure(ApiException e) {
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
        selectedCountry = null;

        hideLoginProgress();
        updateUI();
    }

    @Override
    protected boolean isConnected() {
        return HydraSdk.isVpnStarted();
    }

    @Override
    protected void connectToVpn() {
        if (HydraSdk.isLoggedIn()) {
            showConnectProgress();
            HydraSdk.startVPN(selectedCountry, new Callback<ServerCredentials>() {
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
        HydraSdk.stopVPN(new CompletableCallback() {
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
    protected String getCurrentServer() {
        if (HydraSdk.isVpnStarted()) {
            return HydraSdk.getServerCredentials().getCountry();
        } else {
            return selectedCountry;
        }
    }

    @Override
    protected void checkRemainingTraffic() {
        HydraSdk.remainingTraffic(new ApiCallback<RemainingTraffic>() {
            @Override
            public void success(ApiRequest apiRequest, RemainingTraffic remainingTraffic) {
                updateRemainingTraffic(remainingTraffic);
            }

            @Override
            public void failure(ApiException e) {
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

        if (HydraSdk.isVpnStarted()) {
            showMessage("Reconnecting to VPN with " + selectedCountry);
            HydraSdk.stopVPN(new CompletableCallback() {
                @Override
                public void complete() {
                    connectToVpn();
                }

                @Override
                public void error(HydraException e) {
                    // In this case we try to reconnect
                    selectedCountry = null;
                    connectToVpn();
                }
            });
        }
    }

    // Example of error handling
    public void handleError(Throwable e) {
        Log.w(TAG, e);
        if (e instanceof NetworkException) {
            showMessage("Check internet connection");
        } else if (e instanceof VPNException) {
            switch (((VPNException) e).getCode()) {
                case VPNException.CRASH_FORCE:
                    showMessage("Hydra called forceStop");
                    break;
                case VPNException.CRASH_TIMEOUT:
                    showMessage("Hydra connect timeout");
                    break;
                case VPNException.TRAFFIC_EXCEED:
                    showMessage("Client traffic exceeded");
                    break;
                default:
                    showMessage("Error in VPN Service");
                    break;
            }
        } else if (e instanceof HttpException) {
            showMessage("Network error: " + e.toString());
        } else if (e instanceof InternalException) {
            if (e.getCause() instanceof SystemPermissionsErrorException) {
                // Attention! In case of receiving this Exception all Android 5.0 and 5.0.1 must be
                // rebooted due to open bug in VpnService: https://issuetracker.google.com/issues/37011385
                showMessage("VPN Permission error. Reboot device");
            } else if (e.getCause() instanceof CaptivePortalErrorException) {
                showMessage("Captive portal detected");
            } else if (e.getCause() instanceof NetworkException) {
                showMessage("Network exception");
            } else if (e.getCause() instanceof RequestException) {
                RequestException requestException = (RequestException) e.getCause();
                if (RequestException.CODE_TRAFFIC_EXCEED.equals(requestException.getResult())) {
                    showMessage("Traffic exceed");
                } else {
                    showMessage("Request error " + requestException.getResult());
                }
            } else {
                showMessage("Unexpected error");
            }
        } else if (e instanceof ApiHydraException) {
            switch (((ApiHydraException) e).getCode()) {
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    showMessage("User unauthorized");
                    break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    showMessage("Server unavailable");
                    break;
            }
        }
    }
}
