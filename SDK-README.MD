[![](https://jitpack.io/v/AnchorFreePartner/hydra-sdk-android.svg)](https://jitpack.io/#AnchorFreePartner/hydra-sdk-android)

# Anchorfree Hydra VPN SDK for Android

Android SDK is a part of Anchorfree Partner SDK which contains of client-side libraries and server-side applications needed to implement custom VPN infrastructure.

**Attention!** Public api can be changed in future releases.

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

    compile 'com.github.AnchorFreePartner:hydra-sdk-android:{VERSION_NAME}'

}
```

SDK support only arm architecture. To check if ABI is supported you can use **HydraSDK.isABISupported**.

In case of **startVpn** called on unsupported ABI you'll get **AbiNotSupportedException**

# Proguard Rules

-dontwarn okio.**

-keep class com.anchorfree.hydrasdk.api.data.** { *; }

-keep class com.anchorfree.hydrasdk.api.response.** { *; }

-keep class com.anchorfree.hdr.** { *; }

-keepclassmembers class com.anchorfree.vpnsdk.transporthydra.HydraTransport {
     public *** protect(...);
}

-dontwarn com.anchorfree.vpnsdk.transporthydra.proxyservice.*

-keep class com.northghost.ucr.** { *; }

-keep class com.squareup.okhttp.** { *; }

-keep interface com.squareup.okhttp.** { *; }

-dontwarn com.squareup.okhttp.**

-keepattributes InnerClasses

# Set VPN process name

Add this string resource to your source file
```groovy
<string name="vpn_process_name" translatable="false">my.custom.vpn.process.name</string>
```
# Set Up VPN content provider authorities

Add this string resource to your source file
```groovy
<string name="vpn_provider_authorities" translatable="false">%e.g your package name%</string>
```
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
                     .baseUrl("http://yourvpnbackend.com")
                     .carrierId("carrier id")
                      .appKey("<app key to connect to hydra servers>")
                     .build(),
               NotificationConfig.newBuilder()
                     .title("Your custom vpn notification title")
                     .enableConnectionLost()//enabled show notification when no network connection
                     .build(),
                     HydraSDKConfig.newBuilder()
                             .addBypassDomain("*www.domaintobypass.com") // add domain to bypass vpn
                             .addBypassDomains(R.raw.bypass_domains) // add domains to bypass vpn, accepts raw resource, newline separated list
                             .addBlacklistDomain("*facebook.com")//block domain access
                             .addBlacklistDomains(R.raw.blocklist)//block domains access, accepts raw resource, newline separated list
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
HydraSdk.login(authMethod, new ApiCallback<User>() {
   @Override
   public void success(ApiRequest request, User response) {
       showMessage("Logged in successfully");
   }
   @Override
   public void failure(ApiException error) {
       showMessage("Fail to login");
   }
});
```

# SDK Api

### public class **HydraSdk**

This class is core interface to work with user account and to control vpn connection

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Method and Description</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>addTrafficListener(TrafficListener trafficListener)
Listen updates of input and output traffic</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>addVpnListener(VpnStateListener vpnListener)
Listen VPN connection state changes</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>countries(ApiCallback<java.util.List<Country>> callback)
Get list of countries where vpn servers are exist</td>
  </tr>
  <tr>
    <td>static java.lang.String</td>
    <td>country()
Using server country</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>current(ApiCallback<User> callback)
Get details of current user</td>
  </tr>
  <tr>
    <td>static java.lang.String</td>
    <td>getAccessToken()
Api access token</td>
  </tr>
  <tr>
    <td>static <T> void</td>
    <td>getRequest(java.lang.String method, java.util.Map<java.lang.String,java.lang.String> params, java.lang.Class<T> responseClass, ApiCallback<T> callback)
Custom GET request, put getAccessToken() to params for authorized access</td>
  </tr>
  <tr>
    <td>static long</td>
    <td>getStartVpnTimestamp()
Get timestamp of VPN start event</td>
  </tr>
  <tr>
    <td>static TrafficStats</td>
    <td>getTrafficStats()
Get traffic statistics of current VPN session</td>
  </tr>
  <tr>
    <td>static VPNState</td>
    <td>getVpnState()
Get state of VPN Service</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>init(android.content.Context context, ClientInfo clientInfo)
Set up client details and init internal state of sdk</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>init(android.content.Context context, ClientInfo clientInfo,NotificationConfig notificationConfig)
Set up client details and init internal state of sdk</td>
  </tr>
  <tr>
    <td>static boolean</td>
    <td>isLoggedIn()
Check if user is logged in</td>
  </tr>
  <tr>
    <td>static boolean</td>
    <td>isLoggingEnabled()
Check if logging is enabled</td>
  </tr>
  <tr>
    <td>static boolean</td>
    <td>isVpnStarted()
Check if VPN Service is started</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>login(AuthMethod auth, ApiCallback<User> callback)
Login user with OAuth token</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>logout()
Log out current user</td>
  </tr>
  <tr>
    <td>static <T> void</td>
    <td>postRequest(java.lang.String method, java.util.Map<java.lang.String,java.lang.String> params, java.lang.Class<T> responseClass, ApiCallback<T> callback)
Custom POST request, put getAccessToken() to params for authorized access</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>postRequest(java.lang.String method, java.util.Map<java.lang.String,java.lang.String> params,CompletableCallback callback)
Custom POST request, put getAccessToken() to params for authorized access</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>purchase(java.lang.String rawJson, CompletableCallback callback)
Send purchase raw json to server and check it</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>purchase(java.lang.String rawJson, java.lang.String type,CompletableCallback callback)
Send purchase raw json to server and check it</td>
  </tr>
  <tr>
  <td>static void</td>
  <td>deletePurchase(int purchaseId, ApiCompletableCallback callback) {
  To delete purchases closed manually. This lets server know about this and disable unlimited traffic
  </tr>
  <tr>
    <td>static void</td>
    <td>remainingTraffic(ApiCallback<RemainingTraffic> callback)
Get remaining traffic for current user</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>removeTrafficListener(TrafficListener trafficListener)
Remove traffic listener</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>removeVpnListener(VpnStateListener vpnListener)
Remove VPN state listener</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>setLoggingEnabled(boolean enabled)
Enable or disable sdk internal logging</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>startVPN(HydraSDK.ReasonInfo startReason, Callback<ServerCredentials> callback)
Start VPN service with optimal server</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>startVPN(java.lang.String countryCode, HydraSDK.ReasonInfo startReason, Callback<ServerCredentials> callback)
Start VPN service</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>startVPNExceptApps(java.util.List<java.lang.String> disallowedApps,HydraSDK.ReasonInfo startReason, Callback<ServerCredentials> callback)
Start VPN service except the list of apps with optimal server</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>startVPNExceptApps(java.lang.String countryCode, java.util.List<java.lang.String> disallowedApps,HydraSDK.ReasonInfo startReason,Callback<ServerCredentials> callback)
Start VPN service except the list of apps</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>startVPNForApps(java.util.List<java.lang.String> allowedApps,HydraSDK.ReasonInfo startReason,Callback<ServerCredentials> callback)
Start VPN service for specific apps with optimal server</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>startVPNForApps(java.lang.String countryCode, java.util.List<java.lang.String> allowedApps, HydraSDK.ReasonInfo startReason, Callback<ServerCredentials> callback)
Start VPN service for specific apps</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>stopVPN(HydraSDK.ReasonInfo startReason, CompletableCallback callback)
Stop VPN service

</td>
  </tr>
  <tr>
    <td>static void</td>
    <td>forceStopVPN()
Force Stop VPN service and set state to IDLE

</td>
  </tr>
</table>

### public class **HydraSDK.ReasonInfo**

This class represents reason for starting and stopping vpn. Reason is used for analytics purpose. You can use one of static methods to get different reasons.

<table>
  <tr>
    <td>Method</td>
    <td>Description</td>
  </tr>
  <tr>
  <td>manual(String details)</td>
  <td>Connection start & end triggered by user action, such as tapping button or notification, connection as a result of launching the other app</td>
  </tr>
  <tr>
    <td>auto(String details)</td>
    <td>Triggered automatically without user interaction with UI, other than errors above. Network switch or loss, coming back from sleep, changing virtual location will be here</td>
    </tr>
    <tr>
        <td>error(String details)</td>
        <td>Reconnection & connection loss due to an error</td>
        </tr>
        <tr>
                <td>other(String details)</td>
                <td>Others, but this should not happen, or very small %</td>
                </tr>
 </table>


### public class **ClientInfo**

This is container for client data

Nested Class Summary

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Class and Description</td>
  </tr>
  <tr>
    <td>static class </td>
    <td>ClientInfo.Builder
Builder for ClientInfo</td>
  </tr>
</table>


Method Summary

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Method and Description</td>
  </tr>
  <tr>
    <td>static ClientInfo.Builder</td>
    <td>newBuilder()
Factory method for ClientInfo.Builder</td>
  </tr>
</table>


#### public class **ClientInfo.Builder**

Method Summary

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Method and Description</td>
  </tr>
  <tr>
    <td>ClientInfo.Builder</td>
    <td>baseUrl(java.lang.String val)
Base url for user web api</td>
  </tr>
  <tr>
    <td>ClientInfo</td>
    <td>build() </td>
  </tr>
  <tr>
    <td>ClientInfo.Builder</td>
    <td>carrierId(java.lang.String val)
Unique carrier id</td>
  </tr>
  <tr>
    <td>ClientInfo.Builder</td>
    <td>email(java.lang.String val)
Email is optional parameter</td>
  </tr>
</table>


### public class **AuthMethod**

Data class for OAuth token and provider name. There is no public constructor, use factory methods. For anonymous login use anonymous() For popular OAuth providers like Facebook, Twitter and other use named factories facebook(String), twitter(String) and etc. For other providers use customOauth(String) or custom(token, customMethodName).

Method Summary

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Method and Description</td>
  </tr>
  <tr>
    <td>static AuthMethod</td>
    <td>anonymous() 
Factory for anonymous auth type</td>
  </tr>
  <tr>
    <td>static AuthMethod</td>
    <td>customOauth(java.lang.String accessToken) 
Factory for custom OAuth auth type</td>
  </tr>
    <tr>
    <td>static AuthMethod</td>
    <td>custom(java.lang.String accessToken, java.lang.String customMethodName) 
Factory for custom auth type. If you are using custom authentication scheme, use this method.</td>
  </tr>
  <tr>
    <td>static AuthMethod</td>
    <td>facebook(java.lang.String accessToken) </td>
  </tr>
  <tr>
    <td>java.lang.String</td>
    <td>getAccessToken() </td>
  </tr>
  <tr>
    <td>java.lang.String</td>
    <td>getType() </td>
  </tr>
  <tr>
    <td>static AuthMethod</td>
    <td>github(java.lang.String accessToken) </td>
  </tr>
  <tr>
    <td>static AuthMethod</td>
    <td>google(java.lang.String accessToken) </td>
  </tr>
  <tr>
    <td>static AuthMethod</td>
    <td>twitter(java.lang.String accessToken) </td>
  </tr>
</table>


### public interface **TrafficListener**

Listener for input and output traffic changes

Method Summary

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Method and Description</td>
  </tr>
  <tr>
    <td>void</td>
    <td>onTrafficUpdate(long bytesTx, long bytesRx) </td>
  </tr>
</table>


### public interface **VpnStateListener**

Listener for VPN connection state changes

Method Summary

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Method and Description</td>
  </tr>
  <tr>
    <td>void</td>
    <td>vpnError(java.lang.String message)
Called if error occurred</td>
  </tr>
  <tr>
    <td>void</td>
    <td>vpnStateChanged(VPNState state)
Called if state changed</td>
  </tr>
</table>


### public interface **ApiCallback<T>**

Callback for web api methods

Field Summary

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Field and Description</td>
  </tr>
  <tr>
    <td>static ApiCallback</td>
    <td>EMPTY
Empty implementation</td>
  </tr>
</table>


Method Summary

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Method and Description</td>
  </tr>
  <tr>
    <td>void</td>
    <td>failure(HydraException error)
Call if request if failed</td>
  </tr>
  <tr>
    <td>void</td>
    <td>success(ApiRequest request, T response)
Called if request is successful</td>
  </tr>
</table>


### public interface **CompletableCallback**

Callback for cases if no data to return

Field Summary

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Field and Description</td>
  </tr>
  <tr>
    <td>static CompletableCallback</td>
    <td>EMPTY
Empty implementation</td>
  </tr>
</table>


Method Summary

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Method and Description</td>
  </tr>
  <tr>
    <td>void</td>
    <td>complete()
Called when action is finished</td>
  </tr>
  <tr>
    <td>void</td>
    <td>error(HydraException e)
Call if request if failed</td>
  </tr>
</table>


### public class **User**

Data class for user data

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Method and Description</td>
  </tr>
  <tr>
    <td>java.lang.String</td>
    <td>getAccessToken()
A key used for other api methods</td>
  </tr>
  <tr>
    <td>Subscriber</td>
    <td>getSubscriber()
Subscribers information</td>
  </tr>
</table>


#### public class **Subscriber**

Subscribers information

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Method and Description</td>
  </tr>
  <tr>
    <td>long</td>
    <td>getActivatedDevices()
Activated devices</td>
  </tr>
  <tr>
    <td>long</td>
    <td>getActiveSessions()
Active sessions</td>
  </tr>
  <tr>
    <td>Bundle</td>
    <td>getBundle()
License information</td>
  </tr>
  <tr>
    <td>java.lang.String</td>
    <td>getCarrierId()
Carrier that is used by device</td>
  </tr>
  <tr>
    <td>long</td>
    <td>getCondition()
Represent value 0/1 which enable/disable subscriber</td>
  </tr>
  <tr>
    <td>long</td>
    <td>getConnectionTime()
Timestamp for connection time</td>
  </tr>
  <tr>
    <td>java.lang.String</td>
    <td>getExtref()
External reference</td>
  </tr>
  <tr>
    <td>long</td>
    <td>getId()
Generated user ID</td>
  </tr>
  <tr>
    <td>long</td>
    <td>getRegistrationTime()
Timestamp for registration time</td>
  </tr>
</table>


#### public class **Bundle**

Data class for licence information

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Method and Description</td>
  </tr>
  <tr>
    <td>long</td>
    <td>getDevicesLimit()
Devices limit</td>
  </tr>
  <tr>
    <td>long</td>
    <td>getId()
License ID</td>
  </tr>
  <tr>
    <td>java.lang.String</td>
    <td>getName()
License name</td>
  </tr>
  <tr>
    <td>long</td>
    <td>getSessionsLimit()
Sessions limit</td>
  </tr>
</table>


### public class **RemainingTraffic**

Data class of user traffic limitations

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Method and Description</td>
  </tr>
  <tr>
    <td>long</td>
    <td>getTrafficLimit()
Limit for traffic usage in bytes</td>
  </tr>
  <tr>
    <td>long</td>
    <td>getTrafficRemaining()
Remaining traffic in bytes traffic</td>
  </tr>
  <tr>
    <td>long</td>
    <td>getTrafficStart()
Beginning session time</td>
  </tr>
  <tr>
    <td>long</td>
    <td>getTrafficUsed()
Used traffic for subscriber</td>
  </tr>
  <tr>
    <td>boolean</td>
    <td>isUnlimited()
Is no traffic limits</td>
  </tr>
</table>


## Handling Errors

All methods have two callback functions: success, error. Error callback comes with HydraException object which is a good start to check what went wrong. There are several types of exceptions which may be generated during API methods request.

HydraException is base class for other sdk exceptions:

<table>
  <tr>
    <td>Exception</td>
    <td>Description</td>
  </tr>
  <tr>
    <td>ApiException</td>
    <td>Request specific or user account error</td>
  </tr>
  <tr>
    <td>HttpException</td>
    <td>Exception of http request level</td>
  </tr>
  <tr>
    <td>HydraException</td>
    <td>Base exception class of sdk</td>
  </tr>
  <tr>
    <td>InternalException</td>
    <td>Something went wrong on the server side.</td>
  </tr>
  <tr>
    <td>NetworkException</td>
    <td>There was a network error during request: timeout, no connection, etc.</td>
  </tr>
  <tr>
    <td>VPNException</td>
    <td>Error of VPN Service</td>
  </tr>
</table>


### public class **HydraException**

Base exception class of sdk

### public class **ApiException** extends HydraException

Request specific or user account error

Field Summary

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Field and Description</td>
  </tr>
  <tr>
    <td>static java.lang.String</td>
    <td>CODE_INTERNAL_SERVER_ERROR
Internal server error</td>
  </tr>
  <tr>
    <td>static java.lang.String</td>
    <td>CODE_INVALID
Password and username doesn’t match records on /user/verify request</td>
  </tr>
  <tr>
    <td>static java.lang.String</td>
    <td>CODE_OAUTH_ERROR
Returns in all cases when an authentication error occurred through OAuth-server</td>
  </tr>
  <tr>
    <td>static java.lang.String</td>
    <td>CODE_SERVER_UNAVAILABLE
Server temporary unavailable or server not found</td>
  </tr>
  <tr>
    <td>static java.lang.String</td>
    <td>CODE_SESSIONS_EXCEED
Ended limitation sessions activated in accordance with the terms of the license</td>
  </tr>
  <tr>
    <td>static java.lang.String</td>
    <td>CODE_TRAFFIC_EXCEED
Ended limitation traffic activated in accordance with the terms of the license</td>
  </tr>
  <tr>
    <td>static java.lang.String</td>
    <td>CODE_UNAUTHORIZED
Invalid access token</td>
  </tr>
</table>


Method Summary

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Method and Description</td>
  </tr>
  <tr>
    <td>ApiRequest</td>
    <td>getRequest()
Request details</td>
  </tr>
  <tr>
    <td>java.lang.String</td>
    <td>getResult()
Response code</td>
  </tr>
</table>


### public class **HttpException** extends HydraException

Exception of http request level

Field Summary

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Field and Description</td>
  </tr>
  <tr>
    <td>static int</td>
    <td>CODE_FORBIDDEN
Forbidden</td>
  </tr>
  <tr>
    <td>static int</td>
    <td>CODE_NOT_FOUND
Wrong url</td>
  </tr>
</table>


Method Summary

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Method and Description</td>
  </tr>
  <tr>
    <td>int</td>
    <td>httpCode()
Http response code</td>
  </tr>
  <tr>
    <td>ApiRequest</td>
    <td>request()
Request details</td>
  </tr>
</table>


### public class **NetworkException** extends HydraException

There was a network error during request: timeout, no connection, etc.

### public class **VPNException** extends HydraException

Error of VPN Service

### public class **InternalException** extends HydraException

Something went wrong on the server side.

### Example of error handling

```java
private void handleStartVpnError(HydraException e) {
           if (e instanceof NetworkException) {
            showMessage("Check internet connection");
        } else if (e instanceof VPNException) {
            switch (((VPNException) e).getCode()) {
                case VPNException.CRASH_FORCE:
                    showMessage("Hydra called forceStop");
                    break;
                case VPNException.CRASH_TIMEOUT:
                    showMessage("Hydra connect timeout");
                    break;
                default:
                    showMessage("Error in VPN Service");
                    break;
            }
        } else if (e instanceof HttpException) {
            showMessage("Wrong web api request while start vpn");
        } else if (e instanceof InternalException) {
            if (e.getCause() instanceof SystemPermissionsErrorException) {
                showMessage("VPN Permission error. Reboot device");
            } else if (e.getCause() instanceof CaptivePortalErrorException) {
                showMessage("Captive portal detected");
            } else if (e.getCause() instanceof NetworkException) {
                showMessage("Network exception");
            } else {
                showMessage("Unexpected error");
            }
        } else if (e instanceof ApiHydraException) {
            switch (((ApiHydraException) e).getCode()) {
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    showMessage("User unauthorized");
                    break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    showMessage("Server unavailable");
                    break;
            }
        }
}
```
