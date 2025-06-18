package cz.lukynka.throwabletoasts.mixins;

import cz.lukynka.throwabletoasts.client.ThrowableToastsClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("MissingUnique")
@Mixin(ToastManager.ToastInstance.class)
public class ToastInstanceMixin<T extends Toast> {

    @Shadow
    @Final
    private T toast;

    @Shadow
    private long fullyVisibleFor;

    @Shadow
    @Final
    int firstSlotIndex;

    @Shadow
    private boolean hasFinishedRendering;

    @Shadow
    Toast.Visibility visibility;

    @Shadow
    private float visiblePortion;
    @Unique
    @SuppressWarnings("unchecked")
    ToastManager.ToastInstance<T> thisClass = ((ToastManager.ToastInstance<T>) (Object) this);

    @Unique
    double animationX;

    @Unique
    double animationY;

    @Unique
    float rotation = 0f;

    @Unique
    int animationTicks = 0;

    @Unique
    DeltaTracker deltaTracker = Minecraft.getInstance().getDeltaTracker();

    double randomXModifier = ThrowableToastsClient.randomDoubleInRange(11, 17);
    double randomYModifier = ThrowableToastsClient.randomDoubleInRange(3, 5);
    float randomRotModifier = (float) ThrowableToastsClient.randomDoubleInRange(0.1, 0.3);

    private final int targetFPS = 30;
    private final float targetFrameDurationMillis = 1000f / targetFPS;
    private float accumulatedTimeMillisRender = 0f;
    private float accumulatedTimeMillisUpdate = 0f;

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void render(GuiGraphics guiGraphics, int i, CallbackInfo ci) {

        accumulatedTimeMillisRender += deltaTracker.getRealtimeDeltaTicks() * 100;
        if (accumulatedTimeMillisRender >= targetFrameDurationMillis) {

            var mousePosition = ThrowableToastsClient.getCURSOR_LOCATION();
            var hoveredToast = ThrowableToastsClient.getHOVERED_TOAST();
            var isBeingAnimated = ThrowableToastsClient.getTHROWN_AWAY_TOASTS().contains(thisClass);

            double renderPositionX = (float) i - (float) this.toast.width() * this.visiblePortion;
            double renderPositionY = (float) (this.firstSlotIndex * 32);

            if (isBeingAnimated) {
                renderPositionX = animationX;
                renderPositionY = animationY;
            } else {
                if (hoveredToast != null && hoveredToast == thisClass) {
                    renderPositionX = mousePosition.x - this.toast.width() / 2.0;
                    renderPositionY = mousePosition.y - this.toast.height() / 2.0;
                }
            }

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate((float) renderPositionX, (float) renderPositionY);
            if (rotation != 0.0) {
                guiGraphics.pose().rotateAbout(rotation, toast.width() / 2f, 0f);
            }
            animationX = renderPositionX;
            animationY = renderPositionY;
            this.toast.render(guiGraphics, Minecraft.getInstance().font, this.fullyVisibleFor);
            guiGraphics.pose().popMatrix();

            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "update", cancellable = true)
    public void update(CallbackInfo ci) {

        accumulatedTimeMillisUpdate += deltaTracker.getGameTimeDeltaTicks() * 100;

        // cap the updates at 30fps, so It's not too fast (why is the ui rendering not limited anyway)
        if (accumulatedTimeMillisUpdate >= targetFrameDurationMillis) {

            accumulatedTimeMillisUpdate -= targetFrameDurationMillis;

            if (ThrowableToastsClient.getTHROWN_AWAY_TOASTS().contains(thisClass)) {
                animationX -= randomXModifier;
                animationY += randomYModifier;
                rotation += randomRotModifier;
                animationTicks++;

                // Dispose
                if (animationTicks == 100) {
                    this.hasFinishedRendering = true;
                    this.visibility = Toast.Visibility.HIDE;
                    ThrowableToastsClient.getTHROWN_AWAY_TOASTS().remove(thisClass);
                }
                ci.cancel();
            }
        }
    }
}