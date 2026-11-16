package kawaii.addon.v2.real.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import kawaii.addon.v2.real.modules.KawaiiTweaks;
import kawaii.addon.v2.real.util.BackGroundUtil;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {

    @WrapOperation(
        method = "extractRenderState",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/TitleScreen;extractPanorama(Lnet/minecraft/client/gui/GuiGraphicsExtractor;F)V"
        )
    )
    private void kawaii$renderBackground(final TitleScreen instance, final GuiGraphicsExtractor context,
                                         final float delta, final Operation<Void> original) {
        Modules modules = Modules.get();
        if (modules == null) {
            original.call(instance, context, delta);
            return;
        }

        KawaiiTweaks t = modules.get(KawaiiTweaks.class);
        if (t == null || !t.bgToggle()) {
            original.call(instance, context, delta);
            return;
        }

        int width = context.guiWidth();
        int height = context.guiHeight();
        BackGroundUtil.render(context, width, height);
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void kawaii$stopBackground(final CallbackInfo ci) {
        BackGroundUtil.setActive(false);
    }
}
