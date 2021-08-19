package com.northghost.hydraclient.deps

import android.content.Context
import com.anchorfree.partner.UrlRotator
import com.anchorfree.partner.UrlRotatorFactory
import com.anchorfree.partner.api.ClientInfo
import com.anchorfree.partner.exceptions.PartnerRequestException

class CustomUrlProvider:UrlRotator {
    override fun failure(p0: String, p1: PartnerRequestException) {
        TODO("Not yet implemented")
    }

    override fun success(p0: String) {
        TODO("Not yet implemented")
    }

    override fun provide(): String {
        TODO("Not yet implemented")
    }

    override fun size(): Int {
        TODO("Not yet implemented")
    }
}

class CustomUrlRotatorFactory:UrlRotatorFactory{
    override fun create(p0: Context, p1: ClientInfo): UrlRotator {
        TODO("Not yet implemented")
    }

}