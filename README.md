[![](https://jitpack.io/v/AnchorFreePartner/hydra-sdk-android.svg)](https://jitpack.io/#AnchorFreePartner/hydra-sdk-android)

GitHub project: https://github.com/AnchorFreePartner/hydrasdk-demo-android

# Anchorfree Hydra VPN SDK demo for Android
This is a demo application for Android with basic usage of Hydra VPN SDK.

## Compatibility

Minimum Android SDK version 15

## Installation

1. Add the JitPack repository to your root gradle.build file:

```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```

2. Add the dependency. VERSION_NAME is a version from JitPack badge: [![](https://jitpack.io/v/AnchorFreePartner/hydra-sdk-android.svg)](https://jitpack.io/#AnchorFreePartner/hydra-sdk-android)


```groovy
dependencies {

    compile 'com.github.AnchorFreePartner.hydra-sdk-android:hydrasdk:VERSION_NAME'

}
```

## Set VPN process name

Add this string resource to your source file
```groovy
<string name="vpn_process_name" translatable="false">my.custom.vpn.process.name</string>
```
## Set Up VPN content provider authorities

Add this string resource to your source file
```groovy
<string name="vpn_provider_authorities" translatable="false">%e.g your package name%</string>
```

# Usage and core classes

## Set Up

To set up sdk you should call init() method with your specific details.

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HydraSdk.init(this, ClientInfo.newBuilder()
            .baseUrl("http://yourvpnserver.com")
            .carrierId("carrier id")
            .appKey("<app key to connect to hydra servers>")	
            .build(),
        NotificationConfig.newBuilder()
            .title("Your custom vpn notification title")
            .enableConnectionLost() //enabled show notification when no network connection
            .build());
   }
}
```
Note: HydraSdk is a singleton, and must be initialized before accessing its methods, 
otherwise NotInitializedException will be thrown.

## Authentication

Anchorfree Partner VPN Backend supports OAuth authentication with a partner's OAuth server, this is a primary authentication method. 

Steps to implement OAuth:

* Deploy and configure OAuth service. Service should be publicly available in Internet.

* Configure Partner Backend to use OAuth service.

* Implement client OAuth for your application

* Retrieve access token in client app, this token will be used to initialize and sign in Android Partner

Example of login:
```java
    AuthMethod authMethod = AuthMethod.anonymous();
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
AuthMethod also can be: anonymous, custom OAuth, github, facebook, twitter, firebase.  
Appropriate OAuth AccessToken must be provided.

## Connection to VPN

```java
    HydraSdk.startVPN(country, new Callback<ServerCredentials>() {
        @Override
        public void success(ServerCredentials serverCredentials) {
            update UI;
        }

        @Override
        public void failure(HydraException e) {
            handleError(e);
        }
    });
    
    HydraSdk.stopVPN(new CompletableCallback() {
        @Override
        public void complete() {
            update UI;
        }
    
        @Override
        public void error(HydraException e) {
            handleError(e);
        }
    });
```

VPN can be started with country from provided list, or null for default value.

## Change country
Getting server list:

```java
    HydraSdk.countries(new ApiCallback<List<Country>>() {
        @Override
        public void success(ApiRequest apiRequest, List<Country> countries) {
            ...
        }

        @Override
        public void failure(ApiException e) {
            handleError(e);
        }
    });
```

In order to set new country you must reconnect to Hydra Sdk.

## Example of error handling

```java
    private void handleError(HydraException e) {
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

Note: In case your device is running Android 5.0 or 5.0.1 you must restart it after receiving SystemPermissionsErrorException.  
Reference: https://issuetracker.google.com/issues/37011385
