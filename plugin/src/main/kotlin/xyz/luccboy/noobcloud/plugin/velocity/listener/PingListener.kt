package xyz.luccboy.noobcloud.plugin.velocity.listener

import xyz.luccboy.noobcloud.api.NoobCloudAPI
import xyz.luccboy.noobcloud.plugin.velocity.NoobCloudVelocityPlugin
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyPingEvent
import net.kyori.adventure.text.Component

class PingListener {

    @Subscribe
    fun onProxyPing(event: ProxyPingEvent) {
        val motd: String = NoobCloudAPI.instance.getMotd(NoobCloudVelocityPlugin.instance.name)
        event.ping = event.ping.asBuilder().description(Component.text(motd)).build()
    }

}