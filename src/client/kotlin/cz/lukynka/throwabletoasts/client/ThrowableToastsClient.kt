package cz.lukynka.throwabletoasts.client

import cz.lukynka.throwabletoasts.mixins.ToastManagerAccessor
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.client.gui.components.toasts.ToastManager.ToastInstance
import net.minecraft.network.chat.Component
import org.joml.Vector2d

class ThrowableToastsClient : ClientModInitializer {

    companion object {

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
        ClientLifecycleEvents.CLIENT_STARTED.register { client ->
            client.toastManager.addToast(SystemToast(SystemToast.SystemToastId.LOW_DISK_SPACE, Component.literal("Annoying Toast"), Component.literal("wtf who asked for this")))
            client.toastManager.addToast(SystemToast(SystemToast.SystemToastId.LOW_DISK_SPACE, Component.literal("Toasting toast"), Component.literal("get your toasty toast")))
            client.toastManager.addToast(SystemToast(SystemToast.SystemToastId.LOW_DISK_SPACE, Component.literal("Hot single Steves near you!"), Component.literal("hotsinglesteavesnearyou.com")))
            client.toastManager.addToast(SystemToast(SystemToast.SystemToastId.LOW_DISK_SPACE, Component.literal("Lava Chicken!"), Component.literal("La-la-la-lava, ch-ch-ch-chicken")))
            client.toastManager.addToast(SystemToast(SystemToast.SystemToastId.LOW_DISK_SPACE, Component.literal("IRS"), Component.literal("You have failed to do your taxes")))
        }

        frameRenderHandler = { delta -> onClientTick(Minecraft.getInstance(), delta) }
    }


    fun onClientTick(client: Minecraft, deltaTracker: DeltaTracker) {

        val mouseX: Double = client.mouseHandler.getScaledXPos(client.window)
        val mouseY: Double = client.mouseHandler.getScaledYPos(client.window)


        CURSOR_LOCATION = Vector2d(mouseX, mouseY)

        val toastManager = Minecraft.getInstance().toastManager as ToastManagerAccessor
        toastManager.visibleToasts.forEachIndexed { index, vanillaToast ->

            val width = vanillaToast.toast.width()
            val height = vanillaToast.toast.height()

            val x: Int = client.window.guiScaledWidth - width
            val y: Int = 32 + (height + 2) * (index - 1)

            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                if (IS_LEFT_DOWN && HOVERED_TOAST == null) {
                    HOVERED_TOAST = vanillaToast
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