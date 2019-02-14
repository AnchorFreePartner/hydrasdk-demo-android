# Versions

## 0.21.3 - 9 Nov 2017

- Updated config to fix SA block

## 0.21.4 - 9 Nov 2017

- UCR fixes (for crashes reported by partners)

## 0.21.6 - 15 Nov 2017

- UCR fixes (for crashes reported by partners)
- Added API for reporting cause of start/stop connection into analytics

## 0.21.7 - 16 Nov 2017

- UMSA-156
- UMSA-152

## 0.21.8 - 17 Nov 2017

- Fixed restoring vpn connection after coming back from airplane mode (issue on some android versions)

## 0.21.9 - 20 Nov 2017

- Fixed SocketTimeout for HydraSDK api calls after establishing vpn connection

## 0.22.1 - 22 Nov 2017

- Fixes in internal sdk analytics

## 0.22.2 - 23 Nov 2017

- Fixed network loss and restore issues
- Fixed restore connection when task was removed and aiplane mode toggled

## 0.22.3 - 4 Dec 2017

- Removed adb logs

## 0.22.4 - 6 Dec 2017

- Social field in Subscriber

## 0.22.5 - 6 Dec 2017

- Revised error code in UCR analytics

## 0.22.7 - 12 Dec 2017

- Handle disabled user
- Handle hydra connection lost errors with its reconnection

## 0.22.8 - 18 Dec 2017

- UCR fixes
- Crash fixes
- Handle creds after country change

## 0.22.9 - 21 Dec 2017

- Fixed 2 rare crashes in UCR

## 0.23.0 - 23 Dec 2017

- Fixed crashes in UCR
- Removed x86 support with AbiNotSupportedException

## 0.23.2 - 9 Jan 2018

- Removed requirement to call HydraSDK.init on each process if sdk is in multiprocess mode

## 0.23.5 - 16 Jan 2018

- fixed restore after kill main process while vpn is connected

## 0.23.6 - 17 Jan 2018

- updated ucr lib
- ability to change global analytics prop

## 0.23.7 - 5 Feb 2018

- fix abi check
- updated vpn module version

## 0.23.8 - 8 Feb 2018

- updated vpn module to 0.7.6
- updated ucr module to 0.6.5-beta

## 0.23.9 - 8 Feb 2018

- send VpnException TrafficExceed on vpn Start

## 0.24.0 - 16 Feb 2018

- updated hydra with armv7 support
- method to get device id

## 0.24.1 - 21 Feb 2018

- added debug info method to sdk to collect information abot sdk issue

## 0.24.2 - 28 Feb 2018

- Changes in device id hashing

## 0.24.3 - 5 Mar 2018

- fixed crash on vpn error on initialization

## 0.24.4 - 7 Mar 2018

- removed Runtime.exec call
- fix socket timeout after vpn connected

## 0.24.5 - 15 Mar 2018

- added api to show vpn permission dialog without vpn connection
- fix restore vpn connection after network change
- use username and password in hydra config

## 0.24.6 - 21 Mar 2018

- fixes in handling network change
- updated hydra lib
- removed unused native libs
- updated readme

## 0.24.7 - 21 Mar 2018

- removed fabric references
- removed android backup config
- fix crashes

## 0.24.8 - 23 Mar 2018

- added ability to turn off network observation

## 0.24.9 - 27 Mar 2018

- fix crash in device id generation

## 0.25.0 - 30 Mar 2018

- internal analytics improvements
- proguard fix
- crash fixes

## 0.25.1 - 9 Apr 2018

- dont notify multiple times on the same state
- removed http url for captive portal check
- make hydra with PKI by default

## 0.25.2 - 16 Apr 2018

- removed Quantcast

## 0.25.3 - 18 Apr 2018

- removed allowBackup from lib
- fix in analytics

## 0.25.4 - 19 Apr 2018

- updated api client
- stop vpn on network loss(fix sd issue)
- torrents traffic forwarding enabled
- sd enabled

## 0.25.6 - 10 May 2018

- updated hydra

## 0.25.7 - 16 May 2018

- fix notify on idle state after sdk initialization

## 0.25.8 - 16 May 2018

- fix app_start analytics

## 0.25.9 - 16 May 2018

- changed vpn module to release tag

## 0.27.0 - 16 May 2018

- bugfix
- traffic speed optimization
- updated hydra

## 0.27.1 - 20 May 2018

- bugfix
- speed degradation fix
- updated hydra

## 0.27.2 - 21 May 2018

- fix for reporting connected country in ServerCredentials

## 0.27.3 - 21 May 2018

- fix for hydra cache

## 0.27.4 - 22 May 2018

- fix for bq reporting

## 0.27.5 - 23 May 2018

- fix for proguard rules

## 0.27.6 - 23 May 2018

- fix for credentials cache

## 0.27.7 - 30 May 2018

- bugfix

## 0.27.8 - 13 June 2018

- fix use a lot of alarm manager
- another fix for app_start event
- updated hydra lib
- updated proguard
- fix ucr crash
- Provide app_version and sdk_version into sessions
- fix crash in unregister online observer

## 0.28.0 - 19 June 2018

- encrypt stored data

## 0.28.2 - 16 July 2018

- hydra module updated to 4.0.38
- x86 fixes
- analytics improvements
- bug fixes

## 0.28.3 - 13 August 2018

- crashes in data encryption
- crashes in analytics
- fix infinite loop on api calls without internet connection

## 0.28.4

- move TrackingConstants to com.anchorfree.reporting package
- changed exceptions on api calls
- refactored exceptions reporting
- fix reconnection issue
- country/language in login request

## 1.0.0

- Refactored API interface
- Added categorization service. aka Fireshield

## API changes history

### 0.28.3

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

### 0.28.4

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

### 0.28.5

- Added

    - method **channelId** to **NotificationConfig** for notifications support on Android O+

### 0.28.7-alpha2

- Changed

    - ServerCredentials now have multiple servers we are trying to connect

### 1.0.0

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

### 1.0.1

- No changes

### 1.0.2

- No changes

### 1.1.0

- Supports VPN Always on feature

### 1.2.0

- remote domain bypass lists integrated

### 1.2.1

- updated vpn transport lib
<<<<<<< HEAD

### 2.0.0

Internal refactoring

#### Behaviour Changes:

1) Removed all deprecated methods
2) StartVPN/stopVPN errors are forwarded also to vpnError callback of HydraSdk.addVpnListener(). Callbacks on startVpn/stopVpn will be called when operation is finished
3) All vpnError callback now gets base HydraException
