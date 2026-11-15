package kawaii.addon.v2.real.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import kawaii.addon.v2.real.util.BackGroundUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @WrapOperation(
        method = "extractRenderState",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/TitleScreen;extractPanorama(Lnet/minecraft/client/gui/GuiGraphicsExtractor;F)V"
        )
    )
    private void renderBackground(final TitleScreen instance, final GuiGraphicsExtractor context,
                                  final float delta, final Operation<Void> original) {
        int width  = context.guiWidth();
        int height = context.guiHeight();
        BackGroundUtil.render(context, width, height);
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void stopBackground(final CallbackInfo ci) {
        BackGroundUtil.setActive(false);
    }
}
