package kawaii.addon.v2.real.mixin;

import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin {

    //red
    @ModifyConstant(
        method = "<clinit>",
        constant = @Constant(intValue = 239)
    )
    private static int modifyLightRed(int original) {
        return 194;
    }

    //green
    @ModifyConstant(
        method = "<clinit>",
        constant = @Constant(intValue = 50)
    )
    private static int modifyLightGreen(int original) {
        return 28;
    }

    //blue
    @ModifyConstant(
        method = "<clinit>",
        constant = @Constant(intValue = 61)
    )
    private static int modifyLightBlue(int original) {
        return 240;
    }
}
