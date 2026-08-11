package kawaii.addon.v2.real.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.Hud;
import net.minecraft.network.chat.Component;
import kawaii.addon.v2.real.modules.ActionBarCensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class InGameHudMixin {

    @Inject(method = "setOverlayMessage", at = @At("HEAD"), cancellable = true)
    private void kawaii$filterActionBar(Component string, boolean animate, CallbackInfo ci) {
        ActionBarCensor module = Modules.get().get(ActionBarCensor.class);
        if (module != null && module.shouldHide(string.getString())) {
            ci.cancel();
        }
    }
}
