package kawaii.addon.v2.real.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import kawaii.addon.v2.real.modules.CoordSpoofer;
import kawaii.addon.v2.real.util.MathSecret;

import java.util.List;
import java.util.Locale;

@Mixin(DebugScreenOverlay.class)
public class DebugHudMixin {

    @Unique
    private float spoof(float num, float multiplier) {
        CoordSpoofer spoofer = Modules.get().get(CoordSpoofer.class);
        if (spoofer == null) return num;

        int seed = spoofer.seed.get();
        float offset = 0;

        if (spoofer.SpoofMode.get() == CoordSpoofer.mode.Static) {
            offset = MathSecret.transform(seed, multiplier);
        } else if (spoofer.SpoofMode.get() == CoordSpoofer.mode.Random) {
            offset = MathSecret.RandomTransform(seed);
        }

        return seed >= 0 ? num + offset : num - offset;
    }

    @Unique
    private void kawaii$spoofLines(List<String> lines) {
        CoordSpoofer mod = Modules.get().get(CoordSpoofer.class);
        if (mod == null || !mod.isActive()) return;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.contains("XYZ: ")) continue;

            String coords = line.substring(line.indexOf("XYZ: ") + 5);
            String[] pos = coords.split(" / ");
            if (pos.length < 3) continue;

            try {
                float x = Float.parseFloat(pos[0]);
                float y = Float.parseFloat(pos[1]);
                float z = Float.parseFloat(pos[2]);

                lines.set(i, String.format(
                    Locale.ROOT,
                    "XYZ: %.3f / %.5f / %.3f",
                    spoof(x, 0.75f),
                    y,
                    spoof(z, 1.25f)
                ));
            } catch (NumberFormatException ignored) {}
        }
    }

   //26.2 & 26.1.2
    @Inject(
        method = "extractLines(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Ljava/util/List;Z)V",
        at = @At("HEAD"),
        require = 0
    )
    private void kawaii$spoofCoordLines3Arg(GuiGraphicsExtractor graphics, List<String> lines, boolean alignLeft, CallbackInfo ci) {
        kawaii$spoofLines(lines);
    }

    //26.3
    @Inject(
        method = "extractLines(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Ljava/util/List;ZI)V",
        at = @At("HEAD"),
        require = 0
    )
    private void kawaii$spoofCoordLines4Arg(GuiGraphicsExtractor graphics, List<String> lines, boolean alignLeft, int scaledScreenWidth, CallbackInfo ci) {
        kawaii$spoofLines(lines);
    }
}
