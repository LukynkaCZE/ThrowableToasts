package cz.lukynka.throwabletoasts.mixins;

import cz.lukynka.throwabletoasts.client.ThrowableToastsClient;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(at = @At("HEAD"), method = "onPress")
    private void onPress(long l, int i, int j, int k, CallbackInfo ci) {
        if(j == 1) {
            ThrowableToastsClient.onMouseDown() ;
        } else {
            ThrowableToastsClient.onMouseUp();
        }
    }
}
