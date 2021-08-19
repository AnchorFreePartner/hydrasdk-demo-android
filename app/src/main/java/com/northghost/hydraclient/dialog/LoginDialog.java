package com.northghost.hydraclient.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.northghost.hydraclient.BuildConfig;
import com.northghost.hydraclient.MainApplication;
import com.northghost.hydraclient.R;

public class LoginDialog extends DialogFragment {

    public static final String TAG = LoginDialog.class.getSimpleName();

    @BindView(R.id.host_url_ed)
    EditText hostUrlEditText;

    @BindView(R.id.carrier_id_ed)
    EditText carrierIdEditText;

    LoginConfirmationInterface loginConfirmationInterface;

    public LoginDialog() {
    }

    public static LoginDialog newInstance() {
        LoginDialog frag = new LoginDialog();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_login, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        SharedPreferences prefs = ((MainApplication) getActivity().getApplication()).getPrefs();

        hostUrlEditText.setText(prefs.getString(BuildConfig.STORED_HOST_URL_KEY, BuildConfig.BASE_HOST));
        carrierIdEditText.setText(prefs.getString(BuildConfig.STORED_CARRIER_ID_KEY, ""));

        // Show soft keyboard automatically and request focus to field
        hostUrlEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        if (ctx instanceof LoginConfirmationInterface) {
            loginConfirmationInterface = (LoginConfirmationInterface) ctx;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loginConfirmationInterface = null;
    }

    @OnClick(R.id.login_btn)
    public void onLoginBtnClick(View v) {
        String hostUrl = hostUrlEditText.getText().toString();
        if (hostUrl.equals("")) hostUrl = BuildConfig.BASE_HOST;
        String carrierId = carrierIdEditText.getText().toString();

        loginConfirmationInterface.setLoginParams(hostUrl, carrierId);
        loginConfirmationInterface.loginUser();
        dismiss();
    }

    public interface LoginConfirmationInterface {
        void setLoginParams(String hostUrl, String carrierId);

        void loginUser();
    }
}
