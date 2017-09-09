package com.northghost.hydraclient.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.api.response.RemainingTraffic;
import com.northghost.hydraclient.R;
import com.northghost.hydraclient.dialog.LoginDialog;
import com.northghost.hydraclient.utils.Converter;

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

    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    final Runnable mUIUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            checkRemainingTraffic();
            mUIHandler.postDelayed(mUIUpdateRunnable, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isConnected()) {
            startUIUpdateTask();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUIUpdateTask();
    }

    @OnClick(R.id.login_btn)
    public void onLoginBtnClick(View v) {
        if (isLoggedIn()) {
            logOutFromVnp();
        } else {
            LoginDialog.newInstance().show(getSupportFragmentManager(), LoginDialog.TAG);
        }
    }

    protected abstract boolean isLoggedIn();

    protected abstract void loginToVpn();

    protected abstract void logOutFromVnp();

    @OnClick(R.id.connect_btn)
    public void onConnectBtnClick(View v) {
        if (isConnected()) {
            disconnectFromVnp();
        } else {
            connectToVpn();
        }
    }

    protected abstract boolean isConnected();

    protected abstract void connectToVpn();

    protected abstract void disconnectFromVnp();

    @OnClick(R.id.optimal_server_btn)
    public void onServerChooserClick(View v) {
        chooseServer();
    }

    protected abstract void chooseServer();

    protected abstract String getCurrentServer();

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
        loginBtnTextView.setText(HydraSdk.isLoggedIn() ? R.string.log_out : R.string.log_in);
        loginStateTextView.setText(HydraSdk.isLoggedIn() ? R.string.logged_in : R.string.logged_out);

        connectBtnTextView.setText(HydraSdk.isVpnStarted() ? R.string.disconnect : R.string.connect);
        connectionStateTextView.setText(HydraSdk.isVpnStarted() ? R.string.connected : R.string.disconnected);

        trafficStats.setVisibility(HydraSdk.isVpnStarted() ? View.VISIBLE : View.INVISIBLE);
        trafficLimitTextView.setVisibility(HydraSdk.isVpnStarted() ? View.VISIBLE : View.INVISIBLE);

        String currentServer = getCurrentServer();
        currentServerBtn.setText(currentServer != null ? R.string.current_server : R.string.optimal_server);
        selectedServerTextView.setText(currentServer != null ? currentServer : "UNKNOWN");
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
