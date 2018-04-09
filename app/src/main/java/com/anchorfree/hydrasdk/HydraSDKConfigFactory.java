package com.anchorfree.hydrasdk;

/**
 * Created by Alexandr Timoshenko <thick.tav@gmail.com> on 4/9/18.
 */

public class HydraSDKConfigFactory {
    public static HydraSDKConfig create() {
        HydraSDKConfig config = HydraSDKConfig.newBuilder()
                //traffic to these domains will not go through VPN
                .addBypassDomain("*facebook.com")
                .addBypassDomain("*wtfismyip.com")
                .serverAuth(2)
                .build();
        return config;
    }
}
