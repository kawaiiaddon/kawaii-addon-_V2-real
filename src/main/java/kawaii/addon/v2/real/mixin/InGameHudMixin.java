package kawaii.addon.v2.real.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import kawaii.addon.v2.real.modules.ActionBarCensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudMixin {

    @Inject(method = "setOverlayMessage", at = @At("HEAD"), cancellable = true)
    private void kawaii$filterActionBar(Component message, boolean tinted, CallbackInfo ci) {
        ActionBarCensor module = Modules.get().get(ActionBarCensor.class);
        if (module != null && module.isActive() && module.shouldHide(message.getString())) {
            ci.cancel();
        }
    }
}
