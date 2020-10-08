package com.northghost.hydraclient.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.northghost.hydraclient.BuildConfig;
import com.northghost.hydraclient.R;
import com.northghost.hydraclient.utils.Converter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class UIActivity extends AppCompatActivity {

    protected static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.main_toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.login_btn)
    TextView loginBtnTextView;

    @BindView(R.id.login_state)
    TextView loginStateTextView;

    @BindView(R.id.login_progress)
    ProgressBar loginProgressBar;

    @BindView(R.id.connect_btn)
    TextView connectBtnTextView;

    @BindView(R.id.connection_state)
    TextView connectionStateTextView;

    @BindView(R.id.connection_progress)
    ProgressBar connectionProgressBar;

    @BindView(R.id.traffic_stats)
    TextView trafficStats;

    @BindView(R.id.traffic_limit)
    TextView trafficLimitTextView;

    @BindView(R.id.optimal_server_btn)
    TextView currentServerBtn;

    @BindView(R.id.selected_server)
    TextView selectedServerTextView;

    @BindView(R.id.url) EditText url;
    @BindView(R.id.carrier) EditText carrier;
    UnifiedSDK unifiedSDK;
    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    final Runnable mUIUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            checkRemainingTraffic();
            mUIHandler.postDelayed(mUIUpdateRunnable, 10000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initSDK();
    }

    private void initSDK() {
        final SharedPreferences prefs = getPrefs();
        final String url = prefs.getString(BuildConfig.STORED_HOST_URL_KEY, BuildConfig.BASE_HOST);
        final String carrier = prefs.getString(BuildConfig.STORED_CARRIER_ID_KEY, "");
        this.url.setText(url);
        this.carrier.setText(carrier);
        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(carrier)) {
            ClientInfo clientInfo = ClientInfo.newBuilder()
                    .baseUrl(url)
                    .carrierId(carrier)
                    .build();

            UnifiedSDK.clearInstances();
            unifiedSDK = UnifiedSDK.getInstance(clientInfo);
            loginBtnTextView.setEnabled(true);
        } else {
            loginBtnTextView.setEnabled(false);
        }
    }

    public SharedPreferences getPrefs() {
        return getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    startUIUpdateTask();
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUIUpdateTask();
    }

    @OnClick(R.id.login_btn)
    public void onLoginBtnClick(View v) {
        if (unifiedSDK == null) {
            Toast.makeText(this, "SDK is not configured", Toast.LENGTH_LONG).show();
            return;
        }
        if (UnifiedSDK.getInstance().getBackend().isLoggedIn()) {
            logOutFromVnp();
        }else{
            loginToVpn();
        }
    }

    protected abstract void isLoggedIn(Callback<Boolean> callback);

    protected abstract void loginToVpn();

    protected abstract void logOutFromVnp();

    @OnClick(R.id.init_btn)
    public void onInitClick(View v) {
        final SharedPreferences prefs = getPrefs();
        prefs.edit()
                .putString(BuildConfig.STORED_HOST_URL_KEY, url.getText().toString())
                .putString(BuildConfig.STORED_CARRIER_ID_KEY, carrier.getText().toString())
                .apply();
        initSDK();
    }

    @OnClick(R.id.connect_btn)
    public void onConnectBtnClick(View v) {
        if (unifiedSDK == null) {
            Toast.makeText(this, "SDK is not configured", Toast.LENGTH_LONG).show();
            return;
        }
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    disconnectFromVnp();
                } else {
                    connectToVpn();
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
    }

    protected abstract void isConnected(Callback<Boolean> callback);

    protected abstract void connectToVpn();

    protected abstract void disconnectFromVnp();

    @OnClick(R.id.optimal_server_btn)
    public void onServerChooserClick(View v) {
        if (unifiedSDK == null) {
            Toast.makeText(this, "SDK is not configured", Toast.LENGTH_LONG).show();
            return;
        }
        chooseServer();
    }

    protected abstract void chooseServer();

    protected abstract void getCurrentServer(Callback<String> callback);

    protected void startUIUpdateTask() {
        stopUIUpdateTask();
        mUIHandler.post(mUIUpdateRunnable);
    }

    protected void stopUIUpdateTask() {
        mUIHandler.removeCallbacks(mUIUpdateRunnable);
        updateUI();
    }

    protected abstract void checkRemainingTraffic();

    protected void updateUI() {
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {

                trafficStats.setVisibility(vpnState == VPNState.CONNECTED ? View.VISIBLE : View.INVISIBLE);
                trafficLimitTextView.setVisibility(vpnState == VPNState.CONNECTED ? View.VISIBLE : View.INVISIBLE);

                switch (vpnState) {
                    case IDLE: {
                        connectBtnTextView.setEnabled(true);
                        connectBtnTextView.setText(R.string.connect);
                        connectionStateTextView.setText(R.string.disconnected);
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTED: {
                        connectBtnTextView.setEnabled(true);
                        connectBtnTextView.setText(R.string.disconnect);
                        connectionStateTextView.setText(R.string.connected);
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTING_VPN:
                    case CONNECTING_CREDENTIALS:
                    case CONNECTING_PERMISSIONS: {
                        connectBtnTextView.setText(R.string.connecting);
                        connectionStateTextView.setText(R.string.connecting);
                        connectBtnTextView.setEnabled(false);
                        showConnectProgress();
                        break;
                    }
                    case PAUSED: {
                        connectBtnTextView.setEnabled(false);
                        connectBtnTextView.setText(R.string.paused);
                        connectionStateTextView.setText(R.string.paused);
                        break;
                    }
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        UnifiedSDK.getInstance().getBackend().isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean isLoggedIn) {
                loginBtnTextView.setText(isLoggedIn ? R.string.log_out : R.string.log_in);
                loginStateTextView.setText(isLoggedIn ? R.string.logged_in : R.string.logged_out);
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });

        getCurrentServer(new Callback<String>() {
            @Override
            public void success(@NonNull final String currentServer) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentServerBtn.setText(currentServer != null ? R.string.current_server : R.string.optimal_server);
                        selectedServerTextView.setText(currentServer != null ? currentServer : "UNKNOWN");
                    }
                });
            }

            @Override
            public void failure(@NonNull VpnException e) {
                currentServerBtn.setText(R.string.optimal_server);
                selectedServerTextView.setText("UNKNOWN");
            }
        });
    }

    protected void updateTrafficStats(long outBytes, long inBytes) {
        String outString = Converter.humanReadableByteCountOld(outBytes, false);
        String inString = Converter.humanReadableByteCountOld(inBytes, false);

        trafficStats.setText(getResources().getString(R.string.traffic_stats, outString, inString));
    }

    protected void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        if (remainingTrafficResponse.isUnlimited()) {
            trafficLimitTextView.setText("UNLIMITED available");
        } else {
            String trafficUsed = Converter.megabyteCount(remainingTrafficResponse.getTrafficUsed()) + "Mb";
            String trafficLimit = Converter.megabyteCount(remainingTrafficResponse.getTrafficLimit()) + "Mb";

            trafficLimitTextView.setText(getResources().getString(R.string.traffic_limit, trafficUsed, trafficLimit));
        }
    }

    protected void showLoginProgress() {
        loginProgressBar.setVisibility(View.VISIBLE);
        loginStateTextView.setVisibility(View.GONE);
    }

    protected void hideLoginProgress() {
        loginProgressBar.setVisibility(View.GONE);
        loginStateTextView.setVisibility(View.VISIBLE);
    }

    protected void showConnectProgress() {
        connectionProgressBar.setVisibility(View.VISIBLE);
        connectionStateTextView.setVisibility(View.GONE);
    }

    protected void hideConnectProgress() {
        connectionProgressBar.setVisibility(View.GONE);
        connectionStateTextView.setVisibility(View.VISIBLE);
    }

    protected void showMessage(String msg) {
        Toast.makeText(UIActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
