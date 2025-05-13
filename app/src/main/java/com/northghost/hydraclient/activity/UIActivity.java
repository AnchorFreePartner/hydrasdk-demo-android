package com.northghost.hydraclient.activity;

import static com.northghost.hydraclient.extensions.ActivityExtensionsKt.showDialogIfRooted;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.northghost.hydraclient.BuildConfig;
import com.northghost.hydraclient.R;
import com.northghost.hydraclient.databinding.ActivityMainBinding;
import com.northghost.hydraclient.utils.Converter;

import unified.vpn.sdk.*;

public abstract class UIActivity extends AppCompatActivity {

    protected static final String TAG = MainActivity.class.getSimpleName();
    protected String patchAddress = "";
    private ActivityMainBinding binding;
    UnifiedSdk unifiedSDK;
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
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.mainToolbar);
        showDialogIfRooted(this, this);
        initSDK();
        binding.loginBtn.setOnClickListener(this::onLoginBtnClick);
        binding.initBtn.setOnClickListener(this::onInitClick);
        binding.connectBtn.setOnClickListener(this::onConnectBtnClick);
        binding.optimalServerBtn.setOnClickListener(this::onServerChooserClick);
        binding.etServerOverride.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                patchAddress = s.toString().trim();
            }
        });
    }

    private void initSDK() {
        final SharedPreferences prefs = getPrefs();
        final String url = prefs.getString(BuildConfig.STORED_HOST_URL_KEY, BuildConfig.BASE_HOST);
        final String carrier = prefs.getString(BuildConfig.STORED_CARRIER_ID_KEY, "");
        this.binding.url.setText(url);
        this.binding.carrier.setText(carrier);
        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(carrier)) {
            ClientInfo clientInfo = ClientInfo.newBuilder()
                    .addUrl(url)
                    .carrierId(carrier)
                    .build();

            UnifiedSdk.clearInstances();
            unifiedSDK = UnifiedSdk.getInstance(clientInfo);
            binding.loginBtn.setEnabled(true);
        } else {
            binding.loginBtn.setEnabled(false);
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
        stopUIUpdateTask(false);
    }


    public void onLoginBtnClick(View v) {
        if (unifiedSDK == null) {
            Toast.makeText(this, "SDK is not configured", Toast.LENGTH_LONG).show();
            return;
        }
        if (UnifiedSdk.getInstance().getBackend().isLoggedIn()) {
            logOutFromVpn();
        }else{
            loginToVpn();
        }
    }

    protected abstract void isLoggedIn(Callback<Boolean> callback);

    protected abstract void loginToVpn();

    protected abstract void logOutFromVpn();

    public void onInitClick(View v) {
        final SharedPreferences prefs = getPrefs();
        prefs.edit()
                .putString(BuildConfig.STORED_HOST_URL_KEY, binding.url.getText().toString())
                .putString(BuildConfig.STORED_CARRIER_ID_KEY, binding.carrier.getText().toString())
                .apply();
        initSDK();
    }

    public void onConnectBtnClick(View v) {

        if (unifiedSDK == null) {
            Toast.makeText(this, "SDK is not configured", Toast.LENGTH_LONG).show();
            return;
        }
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    disconnectFromVpn();
                } else {
                    connectToVpn(CompletableCallback.EMPTY);
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                connectToVpn(CompletableCallback.EMPTY);
            }
        });
    }

    protected abstract void isConnected(Callback<Boolean> callback);

    protected abstract void connectToVpn(CompletableCallback callback);

    protected abstract void disconnectFromVpn();

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
        stopUIUpdateTask(true);
        mUIHandler.post(mUIUpdateRunnable);
    }

    protected void stopUIUpdateTask(boolean b) {
        mUIHandler.removeCallbacks(mUIUpdateRunnable);
        if (b) {
            updateUI();
        }
    }

    protected abstract void checkRemainingTraffic();

    protected void updateUI() {
        UnifiedSdk.getVpnState(new Callback<VpnState>() {
            @Override
            public void success(@NonNull VpnState vpnState) {

                binding.trafficStats.setVisibility(vpnState == VpnState.CONNECTED ? View.VISIBLE : View.INVISIBLE);
                binding.trafficLimit.setVisibility(vpnState == VpnState.CONNECTED ? View.VISIBLE : View.INVISIBLE);

                switch (vpnState) {
                    case IDLE: {
                        binding.connectBtn.setEnabled(true);
                        binding.connectBtn.setText(R.string.connect);
                        binding.connectionState.setText(R.string.disconnected);
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTED: {
                        binding.connectBtn.setEnabled(true);
                        binding.connectBtn.setText(R.string.disconnect);
                        binding.connectionState.setText(R.string.connected);
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTING_VPN:
                    case CONNECTING_CREDENTIALS:
                    case CONNECTING_PERMISSIONS: {
                        binding.connectBtn.setText(R.string.connecting);
                        binding.connectionState.setText(R.string.connecting);
                        binding.connectBtn.setEnabled(false);
                        showConnectProgress();
                        break;
                    }
                    case PAUSED: {
                        binding.connectBtn.setEnabled(false);
                        binding.connectBtn.setText(R.string.paused);
                        binding.connectionState.setText(R.string.paused);
                        break;
                    }
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        UnifiedSdk.getInstance().getBackend().isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean isLoggedIn) {
                binding.loginBtn.setText(isLoggedIn ? R.string.log_out : R.string.log_in);
                binding.loginState.setText(isLoggedIn ? R.string.logged_in : R.string.logged_out);
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
                        binding.optimalServerBtn.setText(currentServer != null ? R.string.current_server : R.string.optimal_server);
                        binding.selectedServer.setText(currentServer != null ? currentServer : "UNKNOWN");
                    }
                });
            }

            @Override
            public void failure(@NonNull VpnException e) {
                binding.optimalServerBtn.setText(R.string.optimal_server);
                binding.selectedServer.setText("UNKNOWN");
            }
        });
    }

    protected void updateTrafficStats(long outBytes, long inBytes) {
        String outString = Converter.humanReadableByteCountOld(outBytes, false);
        String inString = Converter.humanReadableByteCountOld(inBytes, false);

        binding.trafficStats.setText(getResources().getString(R.string.traffic_stats, outString, inString));
    }

    protected void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        if (remainingTrafficResponse.isUnlimited()) {
            binding.trafficLimit.setText("UNLIMITED available");
        } else {
            String trafficUsed = Converter.megabyteCount(remainingTrafficResponse.getTrafficUsed()) + "Mb";
            String trafficLimit = Converter.megabyteCount(remainingTrafficResponse.getTrafficLimit()) + "Mb";

            binding.trafficLimit.setText(getResources().getString(R.string.traffic_limit, trafficUsed, trafficLimit));
        }
    }

    protected void showLoginProgress() {
        binding.loginProgress.setVisibility(View.VISIBLE);
        binding.loginState.setVisibility(View.GONE);
    }

    protected void hideLoginProgress() {
        binding.loginProgress.setVisibility(View.GONE);
        binding.loginState.setVisibility(View.VISIBLE);
    }

    protected void showConnectProgress() {
        binding.connectionProgress.setVisibility(View.VISIBLE);
        binding.connectionState.setVisibility(View.GONE);
    }

    protected void hideConnectProgress() {
        binding.connectionProgress.setVisibility(View.GONE);
        binding.connectionState.setVisibility(View.VISIBLE);
    }

    protected void showMessage(String msg) {
        Toast.makeText(UIActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
