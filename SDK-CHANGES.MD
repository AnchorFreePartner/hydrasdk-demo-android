## 0.28.3

- Deprecated
    - TrafficStats getTrafficStats()
    - VPNState getVpnState()
    - void current(@NonNull final Callback<User> callback)
    - void startVPNForApps(@NonNull final List<String> allowedApps, @TrackingConstants.GprReason @NonNull final String reason, @NonNull final Callback<ServerCredentials> callback)
    - void startVPNExceptApps(@NonNull final List<String> disallowedApps, @TrackingConstants.GprReason @NonNull final String reason, @NonNull final Callback<ServerCredentials> callback)
    - boolean isLoggingEnabled()
    - void setLoggingEnabled()
    - boolean isVpnStarted()
    - VPNState getVpnState()

## 0.28.4

- Changed
    - HydraSDKConfig.Builder to HydraSDKConfigBuilder
    - ApiCallback to Callback in all public HydraSdk calls
    - ApiCompletableCallback to CompletableCallback in all public HydraSdk calls
    - VPNException with VPNException.WRONG_STATE code to WrongStateException
    - NotAuthorizedException to ApiHydraException with HttpsURLConnection.HTTP_UNAUTHORIZED code
    - NetworkException to NetworkRelatedException
    - SystemPermissionsErrorException to VPNException with VPNException.VPN_FD_NULL_NO_PERMISSIONS

- Removed
    - HttpException
    - SystemPermissionsErrorException

## 0.28.5

- Added

    - method **channelId** to **NotificationConfig** for notifications support on Android O+

## 0.28.7-alpha2

- Changed

    - ServerCredentials now have multiple servers we are trying to connect

## 1.0.0

- Added
    - class SessionConfig to configure starting vpn session
    - To update vpn config without restarting vpn(limited options update) void updateConfig(@NonNull final SessionConfig sessionConfig, @NonNull final CompletableCallback callback)
    - void startVPN(@NonNull final SessionConfig sessionConfig, @NonNull final Callback<ServerCredentials> callback)

- Deprecated
    - void startVPN(@TrackingConstants.GprReason @NonNull final String reason, @NonNull final Callback<ServerCredentials> callback)
    - void startVPN(@NonNull final String countryCode, @TrackingConstants.GprReason @NonNull final String reason, @NonNull final Callback<ServerCredentials> callback)
    - void startVPNForApps(@NonNull final String countryCode, @NonNull final List<String> allowedApps, @TrackingConstants.GprReason @NonNull final String reason, @NonNull final Callback<ServerCredentials> callback)
    - void startVPNExceptApps(@NonNull final String countryCode, @NonNull final List<String> disallowedApps, @TrackingConstants.GprReason @NonNull final String reason, @NonNull final Callback<ServerCredentials> callback)
    - addBlacklistDomain
    - addBlacklistDomains
    - addBypassDomains

## 1.0.1

No changes

## 1.0.2

No changes

## 1.1.0

1) Supports VPN Always on feature

## 1.2.0

1) remote domain bypass lists integrated

## 1.2.1

1) updated vpn transport lib

## 2.0.0

Internal refactoring

1) Removed all deprecated versions
2) startVPN/stopVPN errors are forwarded also to vpnError callback of HydraSdk.addVpnListener(). Callbacks on startVpn/stopVpn will be called when operation is finished
3) Cannot turn off notification for PAUSED state
4) new VpnState - PAUSED - sdk moves to this state when connection was lost due to network loss, and will be restored on network connected
5) vpnError callback now gets base HydraException

## 2.2.0

Internal improvements

## 2.2.1

Removed sticky vpn service on network loss and reconnection

## 2.2.4

Internal analytics improvements

## 2.3.0

Added support for OpenVPN transport

## 2.3.1

Fix for internal account migration from older versions

## 2.4.0

1) Client network list
2) Private servers
3) Internal fixes

## 2.4.1

1) Openvpn android q support
2) Internal fixes
3) Fireshield runtime whitelist