## 2.4.1

- no changes required

## 2.3.1 - 2.4.0

- no changes required

## 2.3.0 - 2.3.1

- no changes required

## 2.2.4 - 2.3.0

- no changes required

## 1.0.1 - 2.2.4

- removed HydraSdk.current(Callback<User>): use HydraSdk.currentUser instead
- removed HydraSdk.getTrafficStats(): use HydraSdk.addTrafficListener instead
- removed HydraSdk.getVpnState(): use HydraSdk.getVpnState(Callback<VPNState>) instead
- removed HydraSdk.isLoggingEnabled: use HydraSdk.getLoggingLevel
- removed HydraSdk.getStartVpnTimestamp(): use HydraSdk.getStartVpnTimestamp(Callback<Long>)
- removed HydraSdk.isVpnStarted: use HydraSdk.getVpnState(Callback<VPNState>) and check if its VPNState.CONNECTED
- removed HydraSdk.startVPN(String, Callback<ServerCredentials>) use HydraSdk.startVPN(SessionConfig, Callback<ServerCredentials>())
- removed HydraSdk.startVPN(String,String, Callback<ServerCredentials>) use HydraSdk.startVPN(SessionConfig, Callback<ServerCredentials>())
- removed HydraSdk.startVPNExceptApps(String,List<String>, Callback<ServerCredentials>) use HydraSdk.startVPN(SessionConfig, Callback<ServerCredentials>())
- removed HydraSdk.startVPNExceptApps(String,List<String>,String, Callback<ServerCredentials>) use HydraSdk.startVPN(SessionConfig, Callback<ServerCredentials>())
- removed HydraSdk.startVPNForApps(String,List<String>, Callback<ServerCredentials>) use HydraSdk.startVPN(SessionConfig, Callback<ServerCredentials>())
- removed HydraSdk.startVPNForApps(String,List<String>,String, Callback<ServerCredentials>) use HydraSdk.startVPN(SessionConfig, Callback<ServerCredentials>())
- removed HydraSdk.collectDebugInfo(Context, new Callback<String>()) use HydraSdk.collectDebugInfo(new Callback<String>()) instead
- **VpnStateListener** method **vpnError** parameter changed from **VPNException** to **HydraException**
- NotificationConfig.Builder#enableConnectionLost was removed. No action required.
- HydraSDKConfigBuilder#addBlacklistDomain removed. Use SessionConfig.Builder#addDnsRule instead
- HydraSDKConfigBuilder#addBlacklistDomains removed. Use SessionConfig.Builder#addDnsRule instead
- HydraSDKConfigBuilder#addBypassDomain removed. Use SessionConfig.Builder#addDnsRule instead
- HydraSDKConfigBuilder#addBypassDomains removed. Use SessionConfig.Builder#addDnsRule instead
- HydraSdk.restartVpn(String, String, AppPolicy, Bundle, Callback<Bundle>) removed use HydraSdk.restartVpn(SessionConfig, Callback<Bundle>) instead
- HydraSdk.isPausedForReconnection() removed use HydraSdk.isPausedForReconnection(Callback<Boolean>) instead
- HydraSdk.logout() removed, use HydraSdk.logout(CompletableCallback) instead


