package kawaii.addon.v2.real.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import kawaii.addon.v2.real.modules.KawaiiTweaks;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin {

    @ModifyExpressionValue(
        method = "extractRenderState",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/function/IntSupplier;getAsInt()I"
        )
    )

    //Couldn't find a better way so it might be scuffed when launching the game but after that it's fine.
    private int kawaii$modifyBrandBackground(int original) {
        Modules modules = Modules.get();
        if (modules == null) return original;

        KawaiiTweaks t = modules.get(KawaiiTweaks.class);
        if (t == null) return original;

        if (t.lToggle()) {
            return 0xFFC800FF;
        }
        return original;
    }
}
