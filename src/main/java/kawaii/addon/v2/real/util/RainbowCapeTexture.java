package kawaii.addon.v2.real.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.awt.Color;

import static kawaii.addon.v2.real.util.FilePath.space;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class RainbowCapeTexture {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(space, "cape/rgb_dynamic");

    private static NativeImage image;
    private static DynamicTexture texture;

    public static void init() {
        if (texture != null) return;
        image = new NativeImage(64, 32, true);
        texture = new DynamicTexture(() -> "kawaii_rgb_cape", image);
        mc.getTextureManager().register(ID, texture);
    }

    public static void update(float speed, int alpha, float spread) {
        init();
        double t = (System.nanoTime() / 1e9 * speed) % 1.0;

        for (int y = 0; y < image.getHeight(); y++) {
            int rgb = Color.HSBtoRGB((float) t + y * spread, 0.8f, 1f) & 0xFFFFFF;
            int argb = (alpha << 24) | rgb;
            for (int x = 0; x < image.getWidth(); x++) image.setPixel(x, y, argb);
        }
        texture.upload();
    }
}
