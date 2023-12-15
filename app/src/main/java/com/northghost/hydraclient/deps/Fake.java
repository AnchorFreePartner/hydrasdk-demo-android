package com.northghost.hydraclient.deps;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import unified.vpn.sdk.*;

public class Fake {
    //fake method to support migration documentation and list all available methods
    public void sdkMethodsList() {
        final UnifiedSdk instance = UnifiedSdk.getInstance();
        ClientInfo.newBuilder().addUrl("").carrierId("test").addUrls(new ArrayList<>()).build();
        instance.getCarrierId();

        backend(instance);
        cnl(instance);
        vpn(instance);
        config(instance);
//        final String id1 = OpenVpnTransport.TRANSPORT_ID_TCP;
//        final String id2 = OpenVpnTransport.TRANSPORT_ID_UDP;
        UnifiedSdk.getVpnState(Callback.EMPTY);
        UnifiedSdk.getConnectionStatus(Callback.EMPTY);
        UnifiedSdk.setLoggingLevel(Log.VERBOSE);
        UnifiedSdk.getStatus(Callback.EMPTY);
        VpnPermissions.request(CompletableCallback.EMPTY);

        UnifiedSdk.addVpnCallListener(parcelable -> {

        });
        UnifiedSdk.removeVpnCallListener(null);
        UnifiedSdk.getInstance().getInfo(new Callback<SdkInfo>() {
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
//                OpenVpnTransportException.class,
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
                PartnerApiException.class,
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
//                OpenVpnTransportException.CONNECTION_BROKEN_ERROR,
//                OpenVpnTransportException.CONNECTION_FAILED_ERROR,
//                OpenVpnTransportException.CONNECTION_AUTH_FAILURE,
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

    private void config(UnifiedSdk instance) {
        RemoteConfig config = instance.getRemoteConfig();
        ObservableSubscription s = config.listen(new ObservableListener() {
            @Override
            public void onChange(@Nullable String s) {

            }
        });
        config.get("",new Object());
    }

    private void vpn(UnifiedSdk instance) {
        Vpn vpn = instance.getVpn();
        vpn.getStartTimestamp(Callback.EMPTY);
        vpn.stop(TrackingConstants.GprReasons.M_UI, CompletableCallback.EMPTY);
        vpn.restart(getSessionConfig(), CompletableCallback.EMPTY);
        vpn.updateConfig(getSessionConfig(), CompletableCallback.EMPTY);
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
                .withLocation("")
                .withCountry("")
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

    private void backend(UnifiedSdk instance) {
        Backend backend = instance.getBackend();
        backend.credentials(Callback.EMPTY);
        backend.credentials(new CredentialsRequest.Builder()
                .withConnectionType(ConnectionType.HYDRA_TCP)
                .withCountry("")
                .withLocation("")
                .withExtras(new HashMap<>())
                .withPrivateGroup("")
                .build(), Callback.EMPTY);
        backend.currentUser(Callback.EMPTY);
        backend.countries(ConnectionType.HYDRA_TCP, Callback.EMPTY);
        backend.locations(ConnectionType.HYDRA_TCP, new Callback<AvailableLocations>() {
            @Override
            public void success(@NonNull AvailableLocations availableLocations) {

            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
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

    private void cnl(UnifiedSdk instance) {
        instance.getCnl().clear(CompletableCallback.EMPTY);
        instance.getCnl().loadList(Callback.EMPTY);
        instance.getCnl().updateList(new ArrayList<>(), CompletableCallback.EMPTY);
    }
}
