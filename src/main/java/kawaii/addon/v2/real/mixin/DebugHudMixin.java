package kawaii.addon.v2.real.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;

import net.minecraft.client.gui.hud.DebugHud;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import kawaii.addon.v2.real.modules.CoordSpoofer;
import kawaii.addon.v2.real.util.MathSecret;

import java.util.List;
import java.util.Locale;

@Mixin(DebugHud.class)
public class DebugHudMixin {

    @Unique
    private float spoof(float num, float multiplier) {
        CoordSpoofer mod = Modules.get().get(CoordSpoofer.class);

        if (mod == null) return num;

        int seed = mod.seed.get();
        float offset = MathSecret.transform(seed, multiplier);

        return seed >= 0 ? num + offset : num - offset;
    }

    @Inject(method = "getLeftText", at = @At("RETURN"), cancellable = true)
    private void modifyDebug(CallbackInfoReturnable<List<String>> cir) {

        CoordSpoofer mod = Modules.get().get(CoordSpoofer.class);

        if (mod == null || !mod.isActive()) return;

        List<String> text = cir.getReturnValue();

        for (int i = 0; i < text.size(); i++) {

            String line = text.get(i);

            if (line.contains("XYZ: ")) {

                String coords = line.substring(line.indexOf("XYZ: ") + 5);
                String[] pos = coords.split(" / ");

                float x = Float.parseFloat(pos[0]);
                float y = Float.parseFloat(pos[1]);
                float z = Float.parseFloat(pos[2]);

                text.set(i, String.format(
                    Locale.ROOT,
                    "XYZ: %.3f / %.5f / %.3f",
                    spoof(x, 0.75f),
                    y,
                    spoof(z, 1.25f)
                ));
            }
        }

        cir.setReturnValue(text);
    }
}
