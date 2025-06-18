package cz.lukynka.throwabletoasts.client

import cz.lukynka.throwabletoasts.client.protocol.ModInstalledPacket
import cz.lukynka.throwabletoasts.mixins.ToastManagerAccessor
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.toasts.ToastManager.ToastInstance
import org.joml.Vector2d
import java.util.function.Consumer

class ThrowableToastsClient : ClientModInitializer {

    companion object {

        lateinit var VERSION: String

        private var frameRenderHandler: ((DeltaTracker) -> Unit)? = null

        @JvmStatic
        var CURSOR_LOCATION = Vector2d(0.0, 0.0)

        @JvmStatic
        var HOVERED_TOAST: ToastInstance<*>? = null

        @JvmStatic
        var IS_LEFT_DOWN: Boolean = false

        @JvmStatic
        fun onMouseDown() {
            IS_LEFT_DOWN = true
        }

        @JvmStatic
        fun onMouseUp() {
            IS_LEFT_DOWN = false
        }

        @JvmStatic
        var THROWN_AWAY_TOASTS: MutableList<ToastInstance<*>> = mutableListOf()

        @JvmStatic
        fun randomDoubleInRange(min: Double, max: Double): Double {
            val random: java.util.Random = java.util.Random()
            return min + (max - min) * random.nextDouble()
        }

        @JvmStatic
        fun onFrameRender(tracker: DeltaTracker) {
            frameRenderHandler?.invoke(tracker)
        }
    }

    override fun onInitializeClient() {
        val container = FabricLoader.getInstance().getModContainer("throwabletoasts")
        container.ifPresent(Consumer<ModContainer> { modContainer: ModContainer -> VERSION = modContainer.metadata.version.friendlyString })

        frameRenderHandler = { _ -> onClientTick(Minecraft.getInstance()) }
        PayloadTypeRegistry.playC2S().register(ModInstalledPacket.TYPE, ModInstalledPacket.STREAM_CODEC)

        ClientPlayConnectionEvents.JOIN.register { _, sender, _ ->
            sender.sendPacket(ModInstalledPacket(VERSION))
        }
    }

    private fun onClientTick(client: Minecraft) {

        val mouseX: Double = client.mouseHandler.getScaledXPos(client.window)
        val mouseY: Double = client.mouseHandler.getScaledYPos(client.window)

        CURSOR_LOCATION = Vector2d(mouseX, mouseY)

        val toastManager = client.toastManager as ToastManagerAccessor
        toastManager.visibleToasts.forEachIndexed { index, toast ->

            val width = toast.toast.width()
            val height = toast.toast.height()

            val x: Int = client.window.guiScaledWidth - width
            val y: Int = 32 + (height + 2) * (index - 1)

            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                if (IS_LEFT_DOWN && HOVERED_TOAST == null) {
                    HOVERED_TOAST = toast
                }
            }
        }

        if (!IS_LEFT_DOWN) {
            if (HOVERED_TOAST != null) {
                THROWN_AWAY_TOASTS.add(HOVERED_TOAST!!)
            }
            HOVERED_TOAST = null
        }
    }
}