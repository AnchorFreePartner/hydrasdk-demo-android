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


## 2.4.1 - 3.0.0

Use UnifiedSDK.getInstance(ClientInfo) to get sdk instance
HydraSdk.countries - UnifiedSDK.getInstance().getBackend().countries
HydraSdk.addTrafficListener - UnifiedSDK.addTrafficListener
HydraSdk.addVpnListener - UnifiedSDK.addVpnStateListener
HydraSdk.removeTrafficListener - UnifiedSDK.removeTrafficListener
HydraSdk.removeVpnListener - UnifiedSDK.removeVpnStateListener 
HydraSdk.deletePurchase - UnifiedSDK.getInstance().getBackend().deletePurchase
HydraSdk.getStartVpnTimestamp - UnifiedSDK.getInstance().getVPN().getStartTimestamp
HydraSdk.getVpnState - UnifiedSDK.getVpnState
HydraSdk.getConnectionStatus - UnifiedSDK.getConnectionStatus
HydraSdk.remainingTraffic - UnifiedSDK.getInstance().getBackend().remainingTraffic
HydraSdk.restartVpn - UnifiedSDK.getInstance().getVPN().restart
DnsRule - TrafficRule
HydraSdk.setLoggingLevel - UnifiedSDK.setLoggingLevel
HydraSdk.isPausedForReconnection - removed. Use UnifiedSDK.getVpnState
HydraSdk.collectDebugInfo - removed
HydraSdk.purchase - UnifiedSDK.getInstance().getBackend().purchase
HydraSdk.getDeviceId - UnifiedSDK.getInstance().getInfo
HydraSdk.currentUser - UnifiedSDK.getInstance().getBackend().currentUser
HydraSdk.getSessionInfo - UnifiedSDK.getStatus
HydraSdk.getAccessToken - UnifiedSDK.getInstance().getBackend().getAccessToken
HydraSdk.requestVpnPermission - VpnPermissions.request
HydraSdk.getConnectionAttemptId - removed, use UnifiedSDK.getStatus
HydraSdk.getLoggingLevel - removed
HydraSdk.isABISupported - removed
HydraSdk.addVpnCallListener - UnifiedSDK.addVpnCallListener
HydraSdk.removeVpnCallListener - UnifiedSDK.removeVpnCallListener
HydraSdk.updateConfig - UnifiedSDK.getInstance().getVPN().updateConfig