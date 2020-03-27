[![](https://jitpack.io/v/AnchorFreePartner/hydra-sdk-android.svg)](https://jitpack.io/#AnchorFreePartner/hydra-sdk-android)


# General

Android SDK is a part of Anchorfree Partner SDK which contains of client-side libraries and server-side applications needed to implement custom VPN infrastructure.

## Versioning convention
Android SDK versioning is in format MAJOR.MINOR.PATCH and should be associated with version in Jira.
[See convensioning rules](https://semver.org/)

The Android SDK provides API containing
* Classes and methods to authorize client users
* Ability to connect to backend VPN service

## Compatibility

Android min sdk version 15

## Prerequisites

In order to be able to use the SDK the following steps have to be done:

1. Register an account at [developer.anchorfree.com](https://developer.anchorfree.com)
2. Create a project and use a name for your project as a Public key. Private key is optional.
3. Use SDK with a `carrierId` equals to the given *Public Key* and `baseUrl` equals to *URL* from the project details.

# Installing

To use this library you should add **jitpack** repository.

Add this to root `build.gradle`

    allprojects {
        repositories {
            ...
            maven {
                url "https://jitpack.io"
            }
        }
    }

And then add dependencies in build.gradle of your app module. Version name is available on top of this document.
```groovy
dependencies {

    implementation 'com.github.AnchorFreePartner.hydra-sdk-android:sdk:{VERSION_NAME}'

}
```

# Configuration

## Proguard Rules

Proguard rules are included in sdk, but you can use these if required:

```
    -dontwarn okio.**
    -keepattributes InnerClasses
    -dontwarn sun.misc.**
    -dontwarn java.lang.invoke.**
    -dontwarn okhttp3.**
    -dontwarn com.anchorfree.sdk.**
    -dontwarn okio.**
    -dontwarn javax.annotation.**
    -dontwarn org.conscrypt.**
    -keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
    #DNSJava
    -keep class org.xbill.DNS.** {*;}
    -dontnote org.xbill.DNS.spi.DNSJavaNameServiceDescriptor
    -dontwarn org.xbill.DNS.spi.DNSJavaNameServiceDescriptor
    -keep class * implements com.google.gson.TypeAdapterFactory
    -keep class * implements com.google.gson.JsonSerializer
    -keep class * implements com.google.gson.JsonDeserializer
    -keep class com.anchorfree.sdk.SessionConfig { *; }
    -keep class com.anchorfree.sdk.fireshield.** { *; }
    -keep class com.anchorfree.sdk.dns.** { *; }
    -keep class com.anchorfree.sdk.HydraSDKConfig { *; }
    -keep class com.anchorfree.partner.api.ClientInfo { *; }
    -keep class com.anchorfree.sdk.NotificationConfig { *; }
    -keep class com.anchorfree.sdk.NotificationConfig$Builder { *; }
    -keep class com.anchorfree.sdk.NotificationConfig$StateNotification { *; }
    -keepclassmembers public class com.anchorfree.ucr.transport.DefaultTrackerTransport {
       public <init>(...);
     }
     -keepclassmembers class com.anchorfree.ucr.SharedPrefsStorageProvider{
        public <init>(...);
     }
     -keepclassmembers class com.anchorfree.sdk.InternalReporting$InternalTrackingTransport{
     public <init>(...);
     }
     -keep class com.anchorfree.sdk.exceptions.* {
        *;
     }
      
    -keepclassmembers class * implements javax.net.ssl.SSLSocketFactory {
        final javax.net.ssl.SSLSocketFactory delegate;
    }
    
    # https://stackoverflow.com/questions/56142150/fatal-exception-java-lang-nullpointerexception-in-release-build
    -keepclassmembers,allowobfuscation class * {
      @com.google.gson.annotations.SerializedName <fields>;
    }
```

## Set VPN process name

Add this string resource to your source file to set custom process name for vpn
```xml
<string name="vpn_process_name" translatable="false">:vpn</string>
```

## Java 8

Add Java 8 support to project **build.gradle**

```groovy
compileOptions {
    sourceCompatibility 1.8
    targetCompatibility 1.8
}
```

If you cannot enable java 8 support in your project please contact us for further details

## Notifications

To configure sdk notification use **NotificationConfig.Builder** class

#### Disable notifications

```java
    NotificationConfig.Builder builder = NotificationConfig.newBuilder();
    builder = builder.disabled()
```

#### Notifications for different states

```java
//Notification to be displayed when vpn is connected
builder.inConnected("Title","Message");
//Notification to be displayed when vpn is not connected
builder.inIdle("Title","Message");
//Notification to be displayed when vpn is connecting
builder.inConnecting("Title","Message");
//Notification to be displayed when vpn is paused(waiting for network connection)
builder.inPause("Title","Message");
//Notification to be displayed if Client Network List feature is used
builder.inCnl("Title","Message");
```

#### Notification message and title fallback

By default sdk displays notification for state CONNECTED and PAUSED.

If **inConnected** was not called it tries to use value set with **title** message

If `NotificationConfig.Builder`.`title` was not set, it will use App name as in launcher

If **inConnected** was not called it will try to use string resource **default_notification_connected_message**

If **inPause** was not called it will try to use string resource **default_notification_paused_message**

### Notification click intent

To configure action when user click's on notification use **clickAction** method. You must have Activity responds to specified action.
Action will be used to create Intent.
If **clickAction** not specified, sdk will open application launch activity on notification click.

In intent extras sdk will set boolean value **UnifiedSDKNotificationManager.EXTRA_NOTIFICATION** as **true**

To handle click action, register `intent-filter` with your activity

``` xml
<intent-filter>
    <action android:name="com.sdk.notification.action"/>
    <category android:name="android.intent.category.DEFAULT"/>
</intent-filter>
```

### Update notification preferences

Notification config can be updated calling sdk method

```java
    UnifiedSDK.update(createNotificationConfig());
```

# Usage

## Set Up

To set up sdk you should call getInstance method with your specific details

```java
public class MyApplication extends Application {
   @Override
   public void onCreate() {
       super.onCreate();
       final ClientInfo info = ClientInfo.newBuilder()
                     .baseUrl("http://yourvpnbackend.com") // set base url for api calls
                     .carrierId("carrier id") // set your carrier id
                     .build();
       final UnifiedSDK sdk = UnifiedSDK.getInstance(info);
   }
}
```
## Defer VPN service initialization

Add boolean resource to disable vpn service

```
<bool name="vpn_process_enabled">false</bool>
```

To enable service later:

```
PackageManager pm = context.getPackageManager();
pm.setComponentEnabledSetting(new ComponentName(context, AFVpnService.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
```

## Authentication

Anchorfree Partner VPN Backend supports OAuth authentication with a partner's OAuth server, this is a primary authentication method.

Steps to implement OAuth:

* Deploy and configure OAuth service. Service should be publicly available in Internet.

* Configure Partner Backend to use OAuth service.

* Implement client OAuth for your application

* Retrieve access token in client app, this token will be used to initialize and sign in Android Partner

There are some auth method types:
```
AuthMethod.anonymous();
AuthMethod.firebase(token); // should be configured when creating an app
AuthMethod.customOauth(token); // specific OAuth type, should be configured when creating an app
```

Login implementation example:
```java
AuthMethod authMethod = AuthMethod.firebase(token);
sdk.getBackend().login(authMethod, new Callback<User>() {
   @Override
   public void success(User response) {
       
   }
   @Override
   public void failure(VpnException error) {
       
   }
});
```

## List available countries

```java
sdk.getBackend().countries(new Callback<AvailableCountries>() {
    @Override
    public void success(AvailableCountries response) {
        
    }

    @Override
    public void failure(HydraException error) {
        
    }
});
```

## Configure VPN session

For vpn session configuration use class `SessionConfig`

Available `Builder` methods

- `withTransport` - set transport name to start session with. Can be used in case multiple transports available
- `withPolicy` - define app policy to use. 
  - `AppPolicy.forAll` - vpn will be available for all apps
  - `AppPolicy.Builder.policy` with `AppPolicy.POLICY_FOR_LIST` - apps list allowed to use vpn
  - `AppPolicy.Builder.policy` with `AppPolicy.POLICY_EXCEPT_LIST` - apps list not allowed to use vpn
- `withReason` - define reason for starting session ` com.anchorfree.reporting.TrackingConstants.GprReasons`. Will be used in analytics 
- `withVirtualLocation` - define virtual location for session (country)
- `withPrivateGroup` - define private server for session
- `withVpnParams` - define vpn tunnel params. Like dns servers, additional routes.
- `addDnsRule` - add dns rule for session. *For Hydra transport only* 
  - `TrafficRule.block` - to block dns calls
  - `TrafficRule.vpn` - to pass dns calls through vpn
  - `TrafficRule.proxy` - to pass dns calls through vpn proxy
  - `TrafficRule.bypass` - to bypass dns calls over vpn tunnel (direct)
  - `TrafficRule.Builder.fromAssets` - specify domains with newline separated file in assets
  - `TrafficRule.Builder.fromFile` - specify domains with newline separated file on storage
  - `TrafficRule.Builder.fromDomains` - specify domains with list
  - `TrafficRule.Builder.fromResource` - specify domains with newline separated file in resources
  - `TrafficRule.Builder.fromIp` - specify ip and mask
- `addProxyRule` - add proxy rule for session. *For Hydra transport only*
  - `TrafficRule.block` - to block requests
  - `TrafficRule.vpn` - to pass requests through vpn
  - `TrafficRule.proxy` - to pass requests through vpn proxy
  - `TrafficRule.bypass` - to bypass requests over vpn tunnel (direct)
  - `TrafficRule.Builder.fromAssets` - specify domains with newline separated file in assets
  - `TrafficRule.Builder.fromFile` - specify domains with newline separated file on storage
  - `TrafficRule.Builder.fromDomains` - specify domains with list
  - `TrafficRule.Builder.fromResource` - specify domains with newline separated file in resources
  - `TrafficRule.Builder.fromIp` - specify ip and mask  
- `withFireshieldConfig` - define categorization service rules (more information on Fireshield section below). *For Hydra transport only*
  
Examples:

### Start VPN with optimal server

``` java
final SessionConfig session = new SessionConfig.Builder()
                .withVirtualLocation(UnifiedSDK.COUNTRY_OPTIMAL)
                .withReason(TrackingConstants.GprReasons.M_UI)
                .build();
```

### Start VPN for chrome only

```java
final List<String> apps = new ArrayList<String();
apps.add("com.google.chrome");
final SessionConfig session = new SessionConfig.Builder()
                .withVirtualLocation(UnifiedSDK.COUNTRY_OPTIMAL)
                .withReason(TrackingConstants.GprReasons.M_UI)
                .appPolicy(AppPolicy.newBuilder().policy(AppPolity.POLICY_FOR_LIST).appList(apps).build())
                .build(); 
```

### Analytics reasons

- `M_UI` - manually from ui
- `M_SYSTEM` - manually from system
- `M_OTHER` - manually from other place
- `A_APP_RUN` - auto on app run
- `A_RECONNECT` - auto on reconnect
- `A_ERROR` - auto after error
- `A_SLEEP` - auto after sleep
- `A_NETWORK` - auto on network event
- `A_OTHER` - auto on other reason


## Start VPN session

```java
final SessionConfig session = //create session config
sdk.getVPN().start(session, new CompletableCallback() {
    @Override
    public void complete() {
        
    }

    @Override
    public void error(@NonNull VpnException e) {

    }
});
```

## Stop vpn

```java
sdk.getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
    @Override
    public void complete() {
        //VPN was stopped
    }

    @Override
    public void error(@NonNull VpnException e) {
        //Failed to stop vpn
    }
});
```

## Restart vpn

Will restart vpn session with new config without IDLE state

```java
final SessionConfig session = //create session config
sdk.getVPN().restart(session, new CompletableCallback() {
    @Override
    public void complete() {
        
    }

    @Override
    public void error(@NonNull VpnException e) {

    }
});
```

## Update session config

Can update some vpn session configs without restarting vpn session.
Can be used to update Fireshield rules

``` java
final SessionConfig session = //create session config
sdk.getVPN().updateConfig(session, new CompletableCallback() {
    @Override
    public void complete() {
        
    }

    @Override
    public void error(@NonNull VpnException e) {

    }
});
```

## Listen for vpn status and traffic updates

```java
UnifiedSDK.addVpnStateListener(new VpnStateListener() {
    @Override
    public void vpnStateChanged(@NonNull VPNState vpnState) {
        
    }

    @Override
    public void vpnError(@NonNull VpnException e) {

    }
});

UnifiedSDK.addTrafficListener(new TrafficListener() {
    @Override
    public void onTrafficUpdate(long rx, long tx) {
        
    }
});

//stop listening for update
UnifiedSDK.removeTrafficListener(...);
UnifiedSDK.removeVpnStateListener(...);
```

## Purchases functionality

```java
sdk.getBackend().purchase("json from google", new CompletableCallback() {
   @Override
   public void complete() {
       //purchase request success
   }

   @Override
   public void error(VpnException e) {
        //failed to process purchase
   }
});
sdk.getBackend().deletePurchase(purchaseID, new CompletableCallback() {
   @Override
   public void complete() {
       //request success
   }

   @Override
   public void error(VpnException e) {
        //failed to process request
   }
});
```

## Get data about user
```java
//get information about remaining traffic for user
sdk.getBackend().remainingTraffic(new Callback<RemainingTraffic>() {
    @Override
    public void success(@NonNull RemainingTraffic remainingTraffic) {
        
    }

    @Override
    public void failure(@NonNull VpnException e) {

    }
});
//get information about current logged in user
sdk.getBackend().currentUser(new Callback<User>() {
    @Override
    public void success(@NonNull User user) {
        
    }

    @Override
    public void failure(@NonNull VpnException e) {

    }
});
```

## Get Current vpn state
```java
UnifiedSDK.getVpnState(new Callback<VPNState>() {
    @Override
    public void success(@NonNull VPNState vpnState) {
        
    }

    @Override
    public void failure(@NonNull VpnException e) {

    }
});
```

## Call VPN permission dialog without connecting to vpn

```java
VpnPermissions.request(new CompletableCallback() {
    @Override
    public void complete() {
        //permission was granted
    }

    @Override
    public void error(@NonNull VpnException e) {

    }
});
```

# SDK reconnection logic on network switch

By default sdk will automatically handle network reconnection when user switches between network.

If VPN is ON and user disable network connection SDK gets to **PAUSED** state. After network connection is enabled, sdk automatically connects with last successful config.

The same behaviour happen in case of transport error during the connection. Except of unrecoverable exceptions:

- `CODE_NOT_AUTHORIZED` - user is not authorized. Need to call HydraSdk.login
- `CODE_SESSIONS_EXCEED` - Session limit is achieved in accordance with the ToS
- `CODE_DEVICES_EXCEED` - Devices limit is achieved in accordance with the ToS
- `CODE_OAUTH_ERROR` - Returns in all cases when an authentication error occurred through OAuth-server
- `CODE_TRAFFIC_EXCEED` - Ended limitation traffic activated in accordance with the terms of the license
- `USER_SUSPENDED` - User Suspended

To control this behaviour use
```
 UnifiedSDK.setReconnectionEnabled(false);
```

# Client network list (aka CNL)

Unified SDK provides ability to automatically start/stop vpn session when user changes network.
List of network conditions are configured on dashboard. Open `https://developer.anchorfree.com/`, under your project select **Settings**->**Client Networks**
Configure list of network conditions(ssid, bssid, authorization, type) and actions(start/stop).

After vpn connection first start sdk will download configuration from server.
When user is not running active vpn session, sdk will show notification configured by CNL state of `NotificationConfigBuilder`

``` java
NotificationConfig.Builder builder = NotificationConfig.newBuilder();
builder.inCnl("Title","Message");
```

If no notification title/message configured it will use defauult one: 
- `title` - CNL
- `message` - Waiting

Rules matching:

When device changes network connection SDK will look through configured networks in CNL list and match current configuration with server

If network type is set to WiFi:
- if ssid and bsid are empty - it will match any Wifi network
- if authentication is `Does not matter` - it will match open and protected networks
- If you want to match network by SSID and/or BSSID on android 8.1+ (API 27) you need to setup and request runtime permission for use location.

```xml
ACCESS_COARSE_LOCATION
```

- If you want to match network by SSID and/or BSSID on android 10+ (API 29) you need to setup and request runtime permission for use location.

```xml
ACCESS_FINE_LOCATION
```

In case of missing permission, SDK will not be able to get network SSID and BSSID.

When user changes network when has vpn session up and running, and new network was matched with CNL rules with action **Disabled**,
sdk will not reconnect to this network and throw `CnlBlockedException` in `VpnStateListener#vpnError` callback.

# Categorization service (aka Fireshield) (Hydra transport only)

Unified SDK provides ability to categorize domains goes through VPN and perform specified action on them.
To configure you need to create instance of **FireshieldConfig** and pass it to **SessionConfig.Builder** when starting vpn session

```java
final SessionInfo session = new SessionConfig.Builder()
                .withVirtualLocation(UnifiedSDK.COUNTRY_OPTIMAL)
                .withReason(TrackingConstants.GprReasons.M_UI)
                .withFireshieldConfig(createFireshieldConfig())
                .build();
sdk.getVPN().start(session, new CompletableCallback() {
    @Override
    public void complete() {

    }

    @Override
    public void error(@NonNull VpnException e) {

    }
});
```

## Create fireshield config

Categorization configuration based on specification of categories and rules for categories

To create categories you can use one of factory methods

- **FireshieldCategory.Builder.vpn** - will create category, with action VPN ( traffic (encrypted) goes through the tunnel as IP packets )
- **FireshieldCategory.Builder.proxy** - will create category, with action proxy(traffic (encrypted) goes through the tunnel just as payload (for TCP only))
- **FireshieldCategory.Builder.bypass** - will create category, with action bypass(traffic goes directly to its destination, without vpn tunnel)
- **FireshieldCategory.Builder.block** - will create category, with action block(traffic gets blocked)
- **FireshieldCategory.Builder.blockAlertPage** - will create category, with action block(traffic gets blocked) and redirection to Alert Page specified(works for http only)

To create category rules(which domains gets to specified category) you can use one of factory methods

- **FireshieldCategoryRule.Builder.fromAssets** - create category rules from file stored in **assets** folder
- **FireshieldCategoryRule.Builder.fromDomains** - create category rules from list of domains
- **FireshieldCategoryRule.Builder.fromFile** - create category rules from file on sdcard/internal storage

To addition to category file configuration its possible to use online categorization services

Possible values defined as constants in **FireshieldConfig.Services**

Alert page configuration

**AlertPage** static method create accepts two parameters: domain and path, on categorization action user will be redirected to [https://domain/page?url=<blocked_url>]

```

FireshieldConfig createFireshieldConfig(){
        FireshieldConfig.Builder builder = new FireshieldConfig.Builder();
        builder.enabled(true);
        builder.alertPage(AlertPage.create("connect.bitdefender.net", "safe_zone"))
        builder.addService(FireshieldConfig.Services.IP);
        builder.addService(FireshieldConfig.Services.SOPHOS);
        builder.addCategory(FireshieldCategory.Builder.vpn(FireshieldConfig.Categories.SAFE));//need to add safe category to allow safe traffic
        builder.addCategory(FireshieldCategory.Builder.block(FireshieldConfig.Categories.MALWARE));
        builder.addCategoryRule(FireshieldCategoryRule.Builder.fromAssets(FireshieldConfig.Categories.MALWARE,"malware-domains.txt"));
        return builder.build();
}


## Receive information about categorized domains

SDK will fire callback when transport detect access to configure rule.

```java
UnifiedSDK.addVpnCallListener(new VpnCallback() {
    @Override
    public void onVpnCall(@NonNull Parcelable parcelable) {
        if (parcelable instanceof HydraResource){
            // handle categorization information
        }
    }
});
```

SDK provides access to some categorization stats

```java

final FireshieldStats stats = new FireshieldStatus();
//get total scanned connections count
stats.getScannedConnectionsCount(new Callback<Integer>() {
    @Override
    public void success(@NonNull Integer integer) {

    }

    @Override
    public void failure(@NonNull VpnException e) {

    }
});
//get current session scanned connections count
stats.getSessionScannedConnectionsCount();

//reset total scanned connections count
stats.resetScannedConnectionsCount();
```

# Traffic proxy Rules (Hydra transport)

Its possible to configure how Hydra will operate with dns and other traffic.
To configure it use `addDnsRule and `addProxyRule` of `SessionConfig.Builder` when starting vpn session.

To bypass local network traffic and let local network resource accessible you can configure ip mask rule
```java
final SessionConfig builder = new SessionConfig.Builder();
builder.addProxyRule(TrafficRule.Builder.bypass().fromIp("192.168.1.0", 24)); //all traffic to net 192.168.1.0 will go outside vpn tunnel
```

To block all dns requests to domains in project assets file

```java
final SessionConfig builder = new SessionConfig.Builder();
builder.addDnsRule(TrafficRule.Builder.block().fromAssets("blocklist.txt"));
```
# OpenVPN transport support

If your project targetSdk is 29 and you use Google App Bundle for application distribution. Add

```
android.bundle.enableUncompressedNativeLibs=false
```

to your **gradle.properties** to make OpenVPN transport work on Android Q. If you are not using Google App Bundle this step is not required


To add openvpn transport support:

1) Add openvpn depencency to **build.gradle**

```
com.github.AnchorFreePartner.hydra-sdk-android:openvpn:{VERSION_NAME}
```

2) Register both or one transport with sdk config

```
List<TransportConfig> transports = new ArrayList<>();
transports.add(HydraTransportConfig.create());
transports.add(OpenVpnTransportConfig.tcp());
UnifiedSDK.update(transportConfigs, callback);
```

3) Specify transport on vpn start
```
final SessionInfo session = new SessionConfig.Builder()
                .withVirtualLocation(UnifiedSDK.COUNTRY_OPTIMAL)
                .withReason(TrackingConstants.GprReasons.M_UI)
                .withTransport(CaketubeTransport.TRANSPORT_ID_TCP)
                .build();
sdk.getVPN().start(session, new CompletableCallback() {
    @Override
    public void complete() {

    }

    @Override
    public void error(@NonNull VpnException e) {

    }
});
```

# Exceptions

All exceptions sdk can throw extends **com.anchorfree.vpnsdk.exceptions.VpnException**


There are couple of main derivatives of this exception

### **PartnerApiException**

  Thrown in case of server api errors.

  **getCode** - HTTP error code

  **getContent** - response content

Possible values for getContent:

- `CODE_NOT_AUTHORIZED` - user is not authorized. Need to call HydraSdk.login
- `CODE_SESSIONS_EXCEED` - Session limit is achieved in accordance with the ToS
- `CODE_DEVICES_EXCEED` - Devices limit is achieved in accordance with the ToS
- `CODE_OAUTH_ERROR` - Returns in all cases when an authentication error occurred through OAuth-server
- `CODE_TRAFFIC_EXCEED` - Ended limitation traffic activated in accordance with the terms of the license
- `CODE_SERVER_UNAVAILABLE` - Server temporary unavailable or server not found
- `CODE_INTERNAL_SERVER_ERROR` - Internal server error
- `USER_SUSPENDED` - User Suspended
- `CODE_PARSE_EXCEPTION` - Failed to parse server response

### **VpnTransportException**

  Thrown when error happen in vpn transport

  **getCode** - code of error

  - `HydraVpnTransportException` - is thrown by **Hydra** transport
  - `CaketubeTransportException` - is thrown by **OpenVPN** transport
   
  Possible values for getCode on **Hydra** transport    
  - `HydraVpnTransportException.HYDRA_ERROR_BROKEN` - VPN connection broken
  - `HydraVpnTransportException.HYDRA_ERROR_CONNECT` - Hydra fails to connect to server
  - `HydraVpnTransportException.HYDRA_DCN_BLOCKED_BW` - Hydra server reported traffic exceed. Connection interrupted
  - `HydraVpnTransportException.HYDRA_DCN_BLOCKED_AUTH` - Hydra server reported auth failed
  
  Possible values for getCode on **OpenVPN** transport
  - `CaketubeTransportException.CONNECTION_BROKEN_ERROR` - server interrupted connection
  - `CaketubeTransportException.CONNECTION_FAILED_ERROR` - failed to connect to server
  - `CaketubeTransportException.CONNECTION_AUTH_FAILURE` - server reported auth failed
  
### **NetworkRelatedException**

  Thrown in case of network problem communicating to server

### **WrongStateException**

  Thrown when calling `start` or `stop` in wrong sdk state

### **InternalException**

  Thrown for any other unexpected cases. In **getCause** you can get original source exception

### **CnlBlockedException**

  Thrown when connecting to network that has **Disabled** state in CNL config.
      
# Sample App

You can find source code of sample app with integrated sdk on [GitHub](https://github.com/AnchorFreePartner/hydrasdk-demo-android)

# Contact US

Have problems integrating the sdk or found any issue, feel free to submit bug to [GitHub](https://github.com/AnchorFreePartner/hydrasdk-demo-android/issues/new)


# API changes history

## 3.2.0

- Fixes
  - Blast transport initialization
  - rare crashes
  - analytics fix

## 3.1.2

- Added
  - SessionConfig#keepVpnOnReconnect
  - UnifiedSDK#setReconnectionEnabled(boolean)

- Fixes
  - Fix relogin on NOT_AUTHORIZED error
  - rare crashes
  - analytics crashes

## 3.1.1

- Fixes
    - Documentation on android 10 connect openvpn

- Added
    - UnifiedSDKConfigBuilder#runCallbacksOn
    - UnifiedSDK#update(CallbackMode)

- Deprecated
    - UnifiedSDKConfigBuilder#idfaEnabled


## 3.1.0

- Added
    - SessionConfig.Builder#withTransportFallback - configure transport fallback on multiple transports configured
- Fixed
    - Always On disconnect issue
    - Captive portal detection
    - Security improvements    

## 3.0.0

- Complete sdk refactoring

## 2.4.1

- Openvpn android q support
- Internal fixes
- Fireshield runtime whitelist

## 2.4.0

- Client network list
- Private servers
- Internal fixes

## 2.3.1

- Fix for internal account migration from older versions

## 2.3.0

- Added support for OpenVPN transport

## 2.2.4

- Internal analytics improvements

## 2.2.0

- Internal improvements

## 2.2.1

- Removed sticky vpn service on network loss and reconnection

## 2.0.0

- Internal refactoring
- Removed all deprecated versions
- startVPN/stopVPN errors are forwarded also to vpnError callback of HydraSdk.addVpnListener(). Callbacks on startVpn/stopVpn will be called when operation is finished
- Cannot turn off notification for PAUSED state
- new VpnState - PAUSED - sdk moves to this state when connection was lost due to network loss, and will be restored on network connected
- vpnError callback now gets base HydraException

## 1.2.1

- updated vpn transport lib

## 1.2.0

- remote domain bypass lists integrated

## 1.1.0

- Supports VPN Always on feature

## 1.0.2

- No significant changes

## 1.0.1

- No significant changes

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
    
## 0.28.7-alpha2

- Changed
    - ServerCredentials now have multiple servers we are trying to connect
    
## 0.28.5

- Added
    - method **channelId** to **NotificationConfig** for notifications support on Android O+
        
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

# SDK version migration

## 3.0.0 - 3.1.0

- No changes required

## 2.4.1 - 3.0.0

- Use UnifiedSDK.getInstance(ClientInfo) to get sdk instance
- HydraSdk.countries - UnifiedSDK.getInstance().getBackend().countries
- HydraSdk.addTrafficListener - UnifiedSDK.addTrafficListener
- HydraSdk.addVpnListener - UnifiedSDK.addVpnStateListener
- HydraSdk.removeTrafficListener - UnifiedSDK.removeTrafficListener
- HydraSdk.removeVpnListener - UnifiedSDK.removeVpnStateListener 
- HydraSdk.deletePurchase - UnifiedSDK.getInstance().getBackend().deletePurchase
- HydraSdk.getStartVpnTimestamp - UnifiedSDK.getInstance().getVPN().getStartTimestamp
- HydraSdk.getVpnState - UnifiedSDK.getVpnState
- HydraSdk.getConnectionStatus - UnifiedSDK.getConnectionStatus
- HydraSdk.remainingTraffic - UnifiedSDK.getInstance().getBackend().remainingTraffic
- HydraSdk.restartVpn - UnifiedSDK.getInstance().getVPN().restart
- DnsRule - TrafficRule
- HydraSdk.setLoggingLevel - UnifiedSDK.setLoggingLevel
- HydraSdk.isPausedForReconnection - removed. Use UnifiedSDK.getVpnState
- HydraSdk.collectDebugInfo - removed
- HydraSdk.purchase - UnifiedSDK.getInstance().getBackend().purchase
- HydraSdk.getDeviceId - UnifiedSDK.getInstance().getInfo
- HydraSdk.currentUser - UnifiedSDK.getInstance().getBackend().currentUser
- HydraSdk.getSessionInfo - UnifiedSDK.getStatus
- HydraSdk.getAccessToken - UnifiedSDK.getInstance().getBackend().getAccessToken
- HydraSdk.requestVpnPermission - VpnPermissions.request
- HydraSdk.getConnectionAttemptId - removed, use UnifiedSDK.getStatus
- HydraSdk.getLoggingLevel - removed
- HydraSdk.isABISupported - removed
- HydraSdk.addVpnCallListener - UnifiedSDK.addVpnCallListener
- HydraSdk.removeVpnCallListener - UnifiedSDK.removeVpnCallListener
- HydraSdk.updateConfig - UnifiedSDK.getInstance().getVPN().updateConfig

## 2.4.0 - 2.4.1

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


 





