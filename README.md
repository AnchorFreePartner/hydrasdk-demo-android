[![](https://jitpack.io/v/AnchorFreePartner/hydra-sdk-android.svg)](https://jitpack.io/#AnchorFreePartner/hydra-sdk-android)

# HydraVPN SDK for Android

Android SDK is a part of Anchorfree Partner SDK which contains of client-side libraries and server-side applications needed to implement custom VPN infrastructure.

## Versioning convention
Android SDK versioning is in format MAJOR.MINOR.PATCH and should be associated with version in Jira.
[See convensioning rules](https://semver.org/)

## Changelog

- [HydraSDK for Android changelog](https://raw.githubusercontent.com/AnchorFreePartner/hydrasdk-demo-android/master/SDK-CHANGES.MD)

The Android SDK provides API containing
* Classes and methods to authorize client users
* Ability to connect to backend VPN service

# Compatibility

Android min sdk version 15

# Prerequisites

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

    compile 'com.github.AnchorFreePartner.hydra-sdk-android:sdk:{VERSION_NAME}'

}
```

In case of **startVpn** called on unsupported ABI you'll get **AbiNotSupportedException**

# Proguard Rules

Proguard rules are included in sdk, but you can use these if required:

```
    #HYDRASDK
    -dontwarn okio.**
    -keep class com.anchorfree.hydrasdk.api.data.** { *; }
    -keep class com.anchorfree.hydrasdk.api.response.** { *; }
    -keep class com.anchorfree.hdr.HydraHeaderListener { *; }
    -keep class com.anchorfree.hdr.HydraConnInfo { *; }
    -keep class com.anchorfree.hdr.AFHydra { *; }
    -keepclassmembers class com.anchorfree.vpnsdk.transporthydra.HydraTransport {
         public *** protect(...);
    }
    -dontwarn com.anchorfree.vpnsdk.transporthydra.proxyservice.*
    -keepattributes InnerClasses
    -dontwarn sun.misc.**
    -dontwarn java.lang.invoke.**
    -dontwarn okhttp3.**
    -dontwarn okio.**
    -dontwarn javax.annotation.**
    -dontwarn org.conscrypt.**
    -keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
    #DNSJava
    -dontnote org.xbill.DNS.spi.DNSJavaNameServiceDescriptor
    -dontwarn org.xbill.DNS.spi.DNSJavaNameServiceDescriptor
    -keep class com.northghost.ucr.UCRTracker$User { *; }
    -keep class * implements com.google.gson.TypeAdapterFactory
    -keep class * implements com.google.gson.JsonSerializer
    -keep class * implements com.google.gson.JsonDeserializer
    -keep class com.anchorfree.hydrasdk.SessionConfig { *; }
    -keep class com.anchorfree.hydrasdk.fireshield.** { *; }
    -keep class com.anchorfree.hydrasdk.dns.** { *; }
    -keep class com.anchorfree.hydrasdk.HydraSDKConfig { *; }
    -keep class com.anchorfree.hydrasdk.api.ClientInfo { *; }
    -keep class com.anchorfree.hydrasdk.vpnservice.connectivity.NotificationConfig { *; }
```

# Set VPN process name

Add this string resource to your source file
```xml
<string name="vpn_process_name" translatable="false">:vpn</string>
```

# Java 8

Add Java 8 support to project **build.gradle**

```groovy
compileOptions {
    sourceCompatibility 1.8
    targetCompatibility 1.8
}
```

If you cannot enable java 8 support in your project please contact us for further details

## Configure notifications

To configure sdk notification use **NotificationConfig.Builder** class

- completely disable sdk notifications

```java
NotificationConfig.Builder builder = NotificationConfig.newBuilder();
builder = builder.disabled()
```

- Configure notifications for different states
```java
builder.inConnected("Title","Message");//Notification to be displayed when vpn is connected
builder.inIdle("Title","Message");//Notification to be displayed when vpn is not connected
builder.inConnecting("Title","Message");//Notification to be displayed when vpn is connecting
builder.inPause("Title","Message");//Notification to be displayed when vpn is paused(waiting for network connection)
```

- Notification message with fallback


By default sdk displays notification for state connected and inPause.
- fallback for title

If **inConnected** was not called it tries to use value set with **title** message

If **title** was not called on Builder, it will use App name as in launcher

- fallback for message

If **inConnected** was not called it will try to use string resource **state_connected**

If **inPause** was not called it will try to use string resource **state_nonetwork**

### Update notification preferences

Notification config can be updated calling sdk method

```java
HydraSdk.update(createNotificationConfig());
```

### Notification click intent

To configure action when user click's on notification use **clickAction** method. You must have Activity responds to specified action.
Action will be used to create Intent.
If **clickAction** not specified, sdk will open application launch activity on notification click.

In intent extras sdk will set boolean value **HydraSdk.EXTRA_FROM_NOTIFICATION** as **true**

# Set Up
To set up sdk you should call init() method with your specific details for all process(each process has unique Application instance)

```java
public class MyApplication extends Application {
   @Override
   public void onCreate() {
       super.onCreate();
       initHydraSDK();
   }
   private void initHydraSDK(){
       HydraSdk.init(this, ClientInfo.newBuilder()
                     .baseUrl("http://yourvpnbackend.com") // set base url for api calls
                     .carrierId("carrier id") // set your carrier id
                     .build(),
               NotificationConfig.newBuilder()
                     .title("Your custom vpn notification title") // notification title to display in status bar
                     .enableConnectionLost() //enabled show notification when no network connection
                     .build(),
                     HydraSDKConfig.newBuilder()
                             .observeNetworkChanges(false) // turn on/off handling network changes by sdk
                             .unsafeClient(false)// set true if want to use unsafe client instead
                             .captivePortal(true)//control if sdk should check if device is behind captive portal
                             .build());
   }
}
```

Each Android process has unique Application instance. If you have some specific initialization in Application onCreate, and don`t want it to init on vpn process you can use:

```java
//...

initHydraSDK();

if (!ProcessUtils.isVpnProcess(this)){
    //do your initialization stuff
}

//...
```

# Authentication

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
AuthMethod.customOath(token); // specific OAuth type, should be configured when creating an app
```
For more AuthMethod types, see API reference.

Login implementation example:
```java
AuthMethod authMethod = AuthMethod.firebase(token);
HydraSdk.login(authMethod, new Callback<User>() {
   @Override
   public void success(User response) {
       showMessage("Logged in successfully");
   }
   @Override
   public void failure(HydraException error) {
       showMessage("Fail to login");
   }
});
```

# List available countries

```java
HydraSdk.countries(new Callback<List<Country>>() {
                @Override
                public void success(List<Country> response) {
                    for (Country country:response){
                        //country.getCountry()
                    }
                }

                @Override
                public void failure(HydraException error) {
                    //request failed
                }
            });
```

# Start VPN with optimal server

```java
HydraSdk.startVPN(new SessionConfig.Builder()
                .withVirtualLocation(HydraSdk.COUNTRY_OPTIMAL)
                .withReason(TrackingConstants.GprReasons.M_UI)
                .build(), new Callback<ServerCredentials>() {
                @Override
                public void success(@NonNull ServerCredentials serverCredentials) {
                    //VPN connected
                }

                @Override
                public void failure(@NonNull HydraException e) {
                    //Failed to connect vpn
                }
            });
```

For **withVirtualLocation** its possible to use **HydraSdk.COUNTRY_OPTIMAL** to detect optimal location automatically or use country value from **HydraSdk.countries**

For **withReason** param refer to **TrackingConstants.GprReasons**

Possible values:

- M_UI - manually from ui
- M_SYSTEM - manually from system
- M_OTHER - manually from other place
- A_APP_RUN - auto on app run
- A_RECONNECT - auto on reconnect
- A_ERROR - auto after error
- A_SLEEP - auto after sleep
- A_NETWORK - auto on network event
- A_OTHER - auto on other reason

# Start VPN for specified apps

```java
final List<String> allowedApps = new LinkedList<String();
allowedApps.add("com.google.chrome");
HydraSdk.startVPN(new SessionConfig.Builder()
                .withVirtualLocation(HydraSdk.COUNTRY_OPTIMAL)
                .withReason(TrackingConstants.GprReasons.M_UI)
                .forApps(allowedApps)
                .build(), new Callback<ServerCredentials>() {
                @Override
                public void success(@NonNull ServerCredentials serverCredentials) {
                    //VPN connected
                }

                @Override
                public void failure(@NonNull HydraException e) {
                    //Failed to connect vpn
                }
            });
```

# Start VPN and deny specified apps access to it

```java
final List<String> exteptApps = new LinkedList<String();
exteptApps.add("com.google.chrome");
HydraSdk.startVPN(new SessionConfig.Builder()
                .withVirtualLocation(HydraSdk.COUNTRY_OPTIMAL)
                .withReason(TrackingConstants.GprReasons.M_UI)
                .exceptApps(exteptApps)
                .build(), new Callback<ServerCredentials>() {
                @Override
                public void success(@NonNull ServerCredentials serverCredentials) {
                    //VPN connected
                }

                @Override
                public void failure(@NonNull HydraException e) {
                    //Failed to connect vpn
                }
            });
```

# Stop vpn

```java
HydraSdk.stopVPN(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
    @Override
    public void complete() {
        //VPN was stopped
    }

    @Override
    public void error(HydraException e) {
        //Failed to stop vpn
    }
});
```

# Listen for vpn status and traffic updates

```java
 HydraSdk.addVpnListener(new VpnStateListener() {
    @Override
    public void vpnStateChanged(VPNState vpnState) {
        //handle state change
    }

    @Override
    public void vpnError(VPNException e) {
        //handle vpn error
    }
});
HydraSdk.addTrafficListener(new TrafficListener() {
    @Override
    public void onTrafficUpdate(long tx, long rx) {
        //handle used traffic update
        //tx - bytes transfered
        //rx - bytes received
    }
});

//stop listening for update
HydraSdk.removeTrafficListener(...);
HydraSdk.removeVpnListener(...);
```

# Purchases functionality

```java
HydraSdk.purchase("json from google", new CompletableCallback() {
   @Override
   public void complete() {
       //purchase request success
   }

   @Override
   public void error(HydraException e) {
        //failed to process purchase
   }
});
HydraSdk.deletePurchase(purchaseID, new CompletableCallback() {
   @Override
   public void complete() {
       //request success
   }

   @Override
   public void error(HydraException e) {
        //failed to process request
   }
});
```

# Get data about user
```java
//get information about remaining traffic for user
HydraSdk.remainingTraffic(new Callback<RemainingTraffic>() {
   @Override
   public void success(ApiRequest request, RemainingTraffic response) {
       //handle response
   }

   @Override
   public void failure(HydraException error) {
        //failed to send request
   }
});
//get information about current logged in user
HydraSdk.currentUser(new Callback<User>() {
   @Override
   public void success(ApiRequest request, User response) {
       //handle response
   }

   @Override
   public void failure(HydraException error) {
        //failed to send request
   }
});
```

# Get Current vpn state
```java
HydraSdk.getVpnState(new Callback<VPNState>() {
   @Override
   public void success(@NonNull VPNState state) {
        //handle current state
   }

   @Override
   public void failure(@NonNull HydraException e) {
        //failed to get current vpn state
   }
});
```

# Call VPN permission dialog without connecting to vpn

```java
if (HydraSdk.requestVpnPermission(new CompletableCallback() {
    @Override
    public void complete() {
        //dialog was shown and permission was granted
    }

    @Override
    public void error(HydraException e) {
        //dialog was shown and permission was NOT granted
    }
})){
    //will show vpn permissions dialog
}else{
    //already have vpn permissions, no need to request
}
```

# Collecting Debug information before submitting sdk issue

After reproducing the issue, call **HydraSdk.collectDebugInfo** and attach result to your issue

```java
HydraSdk.collectDebugInfo(context, new Callback<String>() {
    @Override
    public void success(String debugInfo) {
        //attach value of debugInfo to your issue description
    }

    @Override
    public void failure(HydraException e) {

    }
});
```

## Categorization service (aka Fireshield)

Hydra SDK provides ability to categorize domains goes through VPN and perform specified action on them.
To configure you need to create instance of **FireshieldConfig** and pass it to **SessionConfig.Builder** when starting vpn session

```java
HydraSdk.startVPN(new SessionConfig.Builder()
                .withVirtualLocation(HydraSdk.COUNTRY_OPTIMAL)
                .withReason(TrackingConstants.GprReasons.M_UI)
                .withFireshieldConfig(createFireshieldConfig())
                .build(), new Callback<ServerCredentials>() {
                @Override
                public void success(@NonNull ServerCredentials serverCredentials) {
                    //VPN connected
                }

                @Override
                public void failure(@NonNull HydraException e) {
                    //Failed to connect vpn
                }
            });
```

# Create fireshield config

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

```java

FireshieldConfig createFireshieldConfig(){
        FireshieldConfig.Builder builder = new FireshieldConfig.Builder();
        builder.enabled(true);
        builder.alertPage(AlertPage.create("connect.bitdefender.net", "safe_zone.html"))
        builder.addService(FireshieldConfig.Services.IP);
        builder.addService(FireshieldConfig.Services.SOPHOS);
        builder.addCategory(FireshieldCategory.Builder.vpn(FireshieldConfig.Categories.SAFE));//need to add safe category to allow safe traffic
        builder.addCategory(FireshieldCategory.Builder.block(FireshieldConfig.Categories.MALWARE));
        builder.addCategoryRule(FireshieldCategoryRule.Builder.fromAssets(FireshieldConfig.Categories.MALWARE,"malware-domains.txt"));
        return builder.build();
}

```
## Exceptions

All exceptions sdk can throw extends **com.anchorfree.hydrasdk.exceptions.HydraException**

There are couple of main derivatives of this exception

- **ApiHydraException**

  Thrown in case of server api errors.

  **getCode** - HTTP error code

  **getContent** - response content

  Possible values for getContent:
  - ApiException.CODE_NOT_AUTHORIZED("NOT_AUTHORIZED") - user is not authorized. Need to call HydraSdk.login
  - ApiException.CODE_SESSIONS_EXCEED("SESSIONS_EXCEED") - Session limit is achieved in accordance with the ToS
  - ApiException.CODE_DEVICES_EXCEED("DEVICES_EXCEED") - Devices limit is achieved in accordance with the ToS
  - ApiException.CODE_OAUTH_ERROR("OAUTH_ERROR") - Returns in all cases when an authentication error occurred through OAuth-server
  - ApiException.CODE_TRAFFIC_EXCEED("TRAFFIC_EXCEED") - Ended limitation traffic activated in accordance with the terms of the license
  - ApiException.CODE_SERVER_UNAVAILABLE("SERVER_UNAVAILABLE") - Server temporary unavailable or server not found
  - ApiException.CODE_INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR") - Internal server error
  - ApiException.USER_SUSPENDED("USER_SUSPENDED") - User Suspended
  - ApiException.CODE_PARSE_EXCEPTION("PARSE_EXCEPTION") - Failed to parse server response

- **NetworkRelatedException**

  Thrown in case of network problem communicating to server

- **VPNException**

  Thrown when error happen in vpn

  **getCode** - code of error

  Possible values for getCode
  - VPNException.VPN_FD_NULL_NO_PERMISSIONS(-4) - Android fatal error. VpnService.Builder.establish returns null. Not possible to continue
  - VPNException.REVOKED(-5) - Use revoked vpn permission
  - VPNException.VPN_PERMISSION_DENIED_BY_USER(-7) - Use not granted vpn permission in dialog
  - VPNException.CANCELLED(-10) - Start was cancelled
  - VPNException.HYDRA_ERROR_START_TIMEOUT(-11) - Timeout of start
  - VPNException.HYDRA_ERROR_BROKEN(181) - VPN connection broken
  - VPNException.HYDRA_ERROR_CONNECT(182) - Hydra fails to connect to server
  - VPNException.HYDRA_DCN_BLOCKED_BW(191) - Hydra server reported traffic exceed. Connection interrupted
  - VPNException.HYDRA_DCN_BLOCKED_AUTH(196) - Hydra server reported auth failed

- **WrongStateException**

  Thrown when calling startVpn or stopVpn in wrong sdk state

- **InternalException**

    Thrown for any other unexpected cases. In **getCause** you can get original source exception


Note: In case your device is running Android 5.0 or 5.0.1 you must restart it after receiving VPN_FD_NULL_NO_PERMISSIONS.
Reference: https://issuetracker.google.com/issues/37011385

## Sample App

You can find source code of sample app with integrated sdk on [GitHub](https://github.com/AnchorFreePartner/hydrasdk-demo-android)

## Contact US

Have problems integrating the sdk or found any issue, feel free to submit bug to [GitHub](https://github.com/AnchorFreePartner/hydrasdk-demo-android/issues/new)
