package kawaii.addon.v2.real.mixin;

import kawaii.addon.v2.real.modules.KawaiiTweaks;
import kawaii.addon.v2.real.util.BackGroundUtil;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Panorama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Panorama.class)
public class PanoramaMixin {

    //26.2 & 26.3
    @Inject(
        method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void kawaii$overridePanorama3Arg(GuiGraphicsExtractor graphics, int width, int height, CallbackInfo ci) {
        kawaii$applyOverride(graphics, ci);
    }

    //26.1.2
    @Inject(
        method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIZ)V",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void kawaii$overridePanorama4Arg(GuiGraphicsExtractor graphics, int width, int height, boolean shouldSpin, CallbackInfo ci) {
        kawaii$applyOverride(graphics, ci);
    }

    @Unique
    private void kawaii$applyOverride(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        Modules modules = Modules.get();
        if (modules == null) return;

        KawaiiTweaks t = modules.get(KawaiiTweaks.class);
        if (t == null || !t.bgToggle()) return;
        ci.cancel();
        BackGroundUtil.render(graphics, graphics.guiWidth(), graphics.guiHeight());
    }
}
