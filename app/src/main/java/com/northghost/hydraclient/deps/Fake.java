package com.northghost.hydraclient.deps;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.data.ConnectionType;
import com.anchorfree.partner.api.data.CredentialsRequest;
import com.anchorfree.reporting.TrackingConstants;
import com.anchorfree.sdk.Backend;
import com.anchorfree.sdk.SdkInfo;
import com.anchorfree.sdk.SessionConfig;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.VPN;
import com.anchorfree.sdk.VpnPermissions;
import com.anchorfree.sdk.exceptions.CnlBlockedException;
import com.anchorfree.sdk.exceptions.InvalidTransportException;
import com.anchorfree.sdk.exceptions.PartnerApiException;
import com.anchorfree.sdk.fireshield.FireshieldCategory;
import com.anchorfree.sdk.fireshield.FireshieldConfig;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
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
import com.anchorfree.vpnsdk.transporthydra.HydraVpnTransportException;
import com.anchorfree.vpnsdk.vpnservice.VpnParams;
import com.anchorfree.vpnsdk.vpnservice.credentials.AppPolicy;
import com.anchorfree.vpnsdk.vpnservice.credentials.CaptivePortalException;
import com.northghost.caketube.exceptions.CaketubeTransportException;

import java.util.ArrayList;
import java.util.HashMap;

public class Fake {
    //fake method to support migration documentation and list all available methods
    public void sdkMethodsList() {
        final UnifiedSDK instance = UnifiedSDK.getInstance();
        ClientInfo.newBuilder().addUrl("").carrierId("test").addUrls(new ArrayList<>()).build();
        instance.getCarrierId();

        backend(instance);
        cnl(instance);
        vpn(instance);

        UnifiedSDK.getVpnState(Callback.EMPTY);
        UnifiedSDK.getConnectionStatus(Callback.EMPTY);
        UnifiedSDK.setLoggingLevel(Log.VERBOSE);
        UnifiedSDK.getStatus(Callback.EMPTY);
        VpnPermissions.request(CompletableCallback.EMPTY);

        UnifiedSDK.addVpnCallListener(parcelable -> {

        });
        UnifiedSDK.removeVpnCallListener(null);
        UnifiedSDK.getInstance().getInfo(new Callback<SdkInfo>() {
            @Override
            public void success(@NonNull SdkInfo sdkInfo) {
                String deviceId = sdkInfo.getDeviceId();
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        //exceptions
        Class[] ex = new Class[]{
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
        String[] serrors = new String[]{
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
        Integer[] errors = new Integer[]{
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

    private void vpn(UnifiedSDK instance) {
        VPN vpn = instance.getVPN();
        vpn.getStartTimestamp(Callback.EMPTY);
        instance.getVPN().stop(TrackingConstants.GprReasons.M_UI, CompletableCallback.EMPTY);
        instance.getVPN().restart(getSessionConfig(), CompletableCallback.EMPTY);
        instance.getVPN().updateConfig(getSessionConfig(), CompletableCallback.EMPTY);
    }

    private SessionConfig getSessionConfig() {
        return new SessionConfig.Builder()
                .withReason(TrackingConstants.GprReasons.M_UI)
                .addProxyRule(TrafficRule.Builder.blockDns().fromAssets(""))
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
                .captivePortalBlockBypass(false)
                .withVpnParams(VpnParams.newBuilder().build())
                .clearDnsRules()
                .clearProxyRules()
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
                .build();
    }

    private void backend(UnifiedSDK instance) {
        Backend backend = instance.getBackend();
        backend.credentials(Callback.EMPTY);
        backend.credentials(new CredentialsRequest.Builder()
                .withConnectionType(ConnectionType.HYDRA_TCP)
                .withCountry("")
                .withExtras(new HashMap<>())
                .withPrivateGroup("")
                .build(), Callback.EMPTY);
        backend.credentials("", ConnectionType.HYDRA_TCP, "", Callback.EMPTY);
        backend.currentUser(Callback.EMPTY);
        backend.countries(ConnectionType.HYDRA_TCP, Callback.EMPTY);
        backend.getAccessToken();
        backend.getAccessToken(Callback.EMPTY);
        backend.isLoggedIn();
        backend.isLoggedIn(Callback.EMPTY);
        backend.login(AuthMethod.anonymous(), Callback.EMPTY);
        backend.login(AuthMethod.anonymous(), Bundle.EMPTY, Callback.EMPTY);
        backend.logout(CompletableCallback.EMPTY);
        backend.remainingTraffic(Callback.EMPTY);
        backend.remoteConfig(Callback.EMPTY);
        backend.deletePurchase(0, CompletableCallback.EMPTY);
        backend.purchase("", CompletableCallback.EMPTY);
        backend.purchase("", "", CompletableCallback.EMPTY);
        backend.currentUser(Callback.EMPTY);
    }

    private void cnl(UnifiedSDK instance) {
        instance.getCnl().clear(CompletableCallback.EMPTY);
        instance.getCnl().loadList(Callback.EMPTY);
        instance.getCnl().updateList(new ArrayList<>(), CompletableCallback.EMPTY);
    }
}
