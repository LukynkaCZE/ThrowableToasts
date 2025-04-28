package cz.lukynka.throwabletoasts.mixins;

import cz.lukynka.throwabletoasts.client.ThrowableToastsClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(at = @At("HEAD"), method = "render")
    public void render(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        ThrowableToastsClient.onFrameRender(deltaTracker);
    }

}
