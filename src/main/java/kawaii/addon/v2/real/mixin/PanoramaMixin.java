package kawaii.addon.v2.real.mixin;

import kawaii.addon.v2.real.modules.KawaiiTweaks;
import kawaii.addon.v2.real.util.BackGroundUtil;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Panorama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Panorama.class)
public class PanoramaMixin {

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void kawaii$overridePanorama(GuiGraphicsExtractor graphics, int width, int height, CallbackInfo ci) {
        Modules modules = Modules.get();
        if (modules == null) return;

        KawaiiTweaks t = modules.get(KawaiiTweaks.class);
        if (t == null || !t.bgToggle()) return;
        ci.cancel();
        BackGroundUtil.render(graphics, graphics.guiWidth(), graphics.guiHeight());
    }
}
