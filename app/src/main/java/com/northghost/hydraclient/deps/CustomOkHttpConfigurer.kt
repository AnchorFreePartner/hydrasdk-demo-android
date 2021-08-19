package com.northghost.hydraclient.deps

import com.anchorfree.partner.api.network.OkHttpNetworkLayer
import okhttp3.OkHttpClient

class CustomOkHttpConfigurer : OkHttpNetworkLayer.HttpClientConfigurer {
    override fun configure(p0: OkHttpClient.Builder) {
        TODO("Not yet implemented")
    }
}