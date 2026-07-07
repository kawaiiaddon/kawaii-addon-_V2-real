package kawaii.addon.v2.real.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import kawaii.addon.v2.real.modules.ActionBarCensor;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "setOverlayMessage", at = @At("HEAD"), cancellable = true)
    private void kawaii$filterActionBar(Text message, boolean tinted, CallbackInfo ci) {
        ActionBarCensor module = Modules.get().get(ActionBarCensor.class);
        if (module != null && module.shouldHide(message.getString())) {
            ci.cancel();
        }
    }
}
