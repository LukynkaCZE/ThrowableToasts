package cz.lukynka.throwabletoasts.client.protocol

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

class ModInstalledPacket(val version: String) : CustomPacketPayload {

    companion object {
        val RESOURCE_LOCATION = ResourceLocation.fromNamespaceAndPath("throwable_toasts", "mod_installed")
        val TYPE = CustomPacketPayload.Type<ModInstalledPacket>(RESOURCE_LOCATION)

        var STREAM_CODEC: StreamCodec<ByteBuf, ModInstalledPacket> = StreamCodec.composite<ByteBuf, ModInstalledPacket, String>(
            ByteBufCodecs.STRING_UTF8, ModInstalledPacket::version, ::ModInstalledPacket
        )

    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}