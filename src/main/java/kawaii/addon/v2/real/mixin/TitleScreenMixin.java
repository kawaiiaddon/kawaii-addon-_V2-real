package kawaii.addon.v2.real.mixin;

import kawaii.addon.v2.real.modules.KawaiiTweaks;
import kawaii.addon.v2.real.util.BackGroundUtil;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {

    @Inject(
        method = "renderBackground",
        at = @At("HEAD"),
        cancellable = true
    )
    private void kawaii$renderCustomBackground(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Modules modules = Modules.get();
        if (modules == null) {
            return;
        }

        KawaiiTweaks t = modules.get(KawaiiTweaks.class);
        if (t == null || !t.bgToggle()) {
            return;
        }

        int width = context.guiWidth();
        int height = context.guiHeight();
        BackGroundUtil.render(context, width, height);
        ci.cancel();
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void kawaii$stopBackground(CallbackInfo ci) {
        BackGroundUtil.setActive(false);
    }
}
