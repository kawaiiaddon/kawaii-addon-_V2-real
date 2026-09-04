package kawaii.addon.v2.real.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.GuiGraphics;
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
    private float lastSpoofedX = 0;
    @Unique
    private float lastSpoofedZ = 0;
    @Unique
    private float lastRealX = 0;
    @Unique
    private float lastRealZ = 0;
    @Unique
    private boolean offsetsCalculated = false;
    @Unique
    private float xOffset = 0;
    @Unique
    private float zOffset = 0;

    @Unique
    private CoordSpoofer.mode lastMode = null;

    @Unique
    private float spoof(float num, boolean isX) {
        CoordSpoofer mod = Modules.get().get(CoordSpoofer.class);

        if (mod == null || !mod.isActive()) return num;

        int seed = mod.seed.get();
        CoordSpoofer.mode currentMode = mod.SpoofMode.get();

        if (currentMode != lastMode) {
            offsetsCalculated = false;
            lastMode = currentMode;
        }

        if (!offsetsCalculated) {
            if (currentMode == CoordSpoofer.mode.Static) {
                xOffset = MathSecret.transform(seed, 0.75f);
                zOffset = MathSecret.transform(seed, 1.25f);
            } else {
                xOffset = MathSecret.RandomTransform(seed);
                zOffset = MathSecret.RandomTransform(seed);
                lastRealX = 0;
                lastRealZ = 0;
            }
            offsetsCalculated = true;
        }

        if (currentMode == CoordSpoofer.mode.Random) {
            if (isX) {
                if (num != lastRealX) {
                    lastSpoofedX = num + (seed >= 0 ? xOffset : -xOffset);
                    lastRealX = num;
                }
                return lastSpoofedX;
            } else {
                if (num != lastRealZ) {
                    lastSpoofedZ = num + (seed >= 0 ? zOffset : -zOffset);
                    lastRealZ = num;
                }
                return lastSpoofedZ;
            }
        }

        float offset = isX ? xOffset : zOffset;
        return seed >= 0 ? num + offset : num - offset;
    }
    @Inject(method = "renderLines", at = @At("HEAD"))
    private void spoofCoordLines(GuiGraphics graphics, List<String> lines, boolean alignLeft, CallbackInfo ci) {
        CoordSpoofer mod = Modules.get().get(CoordSpoofer.class);

        if (mod == null || !mod.isActive()) {
            offsetsCalculated = false;
            return;
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.contains("XYZ: ")) {
                String coords = line.substring(line.indexOf("XYZ: ") + 5);
                String[] pos = coords.split(" / ");

                if (pos.length >= 3) {
                    try {
                        float x = Float.parseFloat(pos[0]);
                        float y = Float.parseFloat(pos[1]);
                        float z = Float.parseFloat(pos[2]);

                        lines.set(i, String.format(
                            Locale.ROOT,
                            "XYZ: %.3f / %.5f / %.3f",
                            spoof(x, true),
                            y,
                            spoof(z, false)
                        ));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
    }
}
