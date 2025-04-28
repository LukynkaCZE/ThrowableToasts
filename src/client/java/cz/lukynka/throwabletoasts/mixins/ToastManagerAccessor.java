package cz.lukynka.throwabletoasts.mixins;

import net.minecraft.client.gui.components.toasts.ToastManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ToastManager.class)
public interface ToastManagerAccessor {

    @Accessor
    List<ToastManager.ToastInstance<?>> getVisibleToasts();

}
