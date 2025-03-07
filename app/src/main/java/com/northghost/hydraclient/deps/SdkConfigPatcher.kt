package unified.vpn.sdk

import android.content.Context
import com.anchorfree.toolkit.clz.ClassSpec
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import unified.vpn.sdk.SdkConfigPatcher.Companion.EXTRA_SERVER_ADDRESS
import unified.vpn.sdk.SessionConfig.Builder

class SdkConfigPatcherFactory : ConfigPatcherFactory {
    override fun create(context: Context): ConfigPatcher {
        return SdkConfigPatcher()
    }

    companion object {
        fun addPatcherToSessionConfig(
            sessionConfigBuilder: Builder,
            serverAddress: String
        ): Builder {
            sessionConfigBuilder.addExtra(
                SwitcherParametersReader.EXTRA_CONFIG_PATCHER,
                SwitchableCredentialsSource.getGson()
                    .toJson(ClassSpec.createClassSpec(SdkConfigPatcherFactory::class.java)),
            )
            sessionConfigBuilder.addExtra(
                EXTRA_SERVER_ADDRESS,
                serverAddress
            )
            return sessionConfigBuilder
        }
    }
}

internal class SdkConfigPatcher() : ConfigPatcher {
    companion object {
        const val EXTRA_SERVER_ADDRESS = "hydrasdk:extra:server_address"
    }

    private val gson = Gson()
    override fun patch(
        patchHelper: JsonPatchHelper,
        credentialsResponse: PartnerApiCredentials,
        sessionConfig: SessionConfig,
    ) {
        runBlocking {
            val transport = sessionConfig.transport
            val serverAddress = sessionConfig.extras.get(Companion.EXTRA_SERVER_ADDRESS)
            serverAddress?.let {
                when (transport) {
                    WireguardTransport.TRANSPORT_ID -> {
                        val original: WireguardConfigData = gson.fromJson(patchHelper.patched, WireguardConfigData::class.java)
                        patchHelper.replace(
                            gson.toJson(
                                WireguardConfigData(
                                    original.username,
                                    original.password,
                                    original.token,
                                    listOf(WireguardConfigServer(serverAddress, serverAddress)),
                                ),
                            ),
                        )
                    }

                    HydraTransport.TRANSPORT_ID -> {
                            val serverList = serverAddress.split(",")
                            sdPatchServer(patchHelper, serverList)
                    }

                    OpenVpnTransport.TRANSPORT_ID_TCP, OpenVpnTransport.TRANSPORT_ID_UDP -> {
                        val openVpnData = JSONObject(patchHelper.patched)
                        val openVpnConfig = openVpnData.getString("config")
                        val configParts = openVpnConfig.split("\n").toMutableList()
                        val patchedConfig = configParts.map { configPart ->
                            if(configPart.contains("remote")) {
                                if (transport == OpenVpnTransport.TRANSPORT_ID_TCP) {
                                    "remote $serverAddress 443 tcp-client"
                                } else {
                                    "remote $serverAddress 1194 udp"
                                }

                            } else {
                                configPart
                            }
                        }.distinct().joinToString("\n")
                        patchHelper.patch("config", patchedConfig)
                    }
                    else -> {}
                }
            }
        }
    }

    override fun patchCert(credentials: PartnerApiCredentials,
                           sessionConfig: SessionConfig,): String {
        return runBlocking {
            credentials.hydraCert!!
        }
    }

    override fun patchCredentials(
        credentials: PartnerApiCredentials,
        sessionConfig: SessionConfig,
    ): PartnerApiCredentials {
        return runBlocking {
                credentials
        }
    }

    private fun sdPatchServer(
        helper: JsonPatchHelper,
        ips: List<String?>
    ): String {
        try {
            val sections = JSONArray().apply {
                ips.map { ip ->
                    JSONObject().apply {
                        put("servers", JSONArray().apply {
                            put(JSONObject().apply {
                                put("ips", JSONArray().apply { put(ip) })
                                put("sni_tag", JSONArray().apply { put("default") })
                            })
                        })
                    }
                }.forEach { sectionItem -> put(sectionItem) }
            }
            helper.patch("sd\\routes\\default\\sections", sections)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return helper.patched
    }
}