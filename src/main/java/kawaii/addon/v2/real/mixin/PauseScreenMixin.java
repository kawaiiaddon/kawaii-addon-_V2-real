package kawaii.addon.v2.real.mixin;

import kawaii.addon.v2.real.modules.KawaiiTweaks;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static kawaii.addon.v2.real.util.FilePath.space;

@Mixin(PauseScreen.class)
public class PauseScreenMixin {

    @Unique
    private static final Identifier MY_IMAGE = Identifier.fromNamespaceAndPath(space, "/icon/watermark.png");
    @Unique
    private static final int IMAGE_WIDTH = 160;
    @Unique
    private static final int IMAGE_HEIGHT = 64;
    @Unique
    private static final int MARGIN = 8;

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void kawaii$renderBottomRightImage(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        KawaiiTweaks tweaks = Modules.get().get(KawaiiTweaks.class);
        if (tweaks == null || tweaks.wmToggle()) {
            return;
        }

        PauseScreen self = (PauseScreen) (Object) this;
        if (!self.showsPauseMenu()) {
            return;
        }

        int x = self.width  - IMAGE_WIDTH  - MARGIN;
        int y = self.height - IMAGE_HEIGHT - MARGIN;

        graphics.blit(
            net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
            MY_IMAGE,
            x, y,
            0.0F, 0.0F,
            IMAGE_WIDTH, IMAGE_HEIGHT,
            IMAGE_WIDTH, IMAGE_HEIGHT,
            IMAGE_WIDTH, IMAGE_HEIGHT
        );
    }
}
