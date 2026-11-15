package kawaii.addon.v2.real.util;

import com.ibasco.image.gif.GifFrame;
import com.ibasco.image.gif.GifImageReader;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.textures.FilterMode;
import kawaii.addon.v2.real.mixin.accessor.AbstractTextureAccessor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public final class BackGroundUtil {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String RESOURCE_PATH = "/assets/kawaii-addon/bg/background.gif";
    private static final Identifier VIDEO_TEXTURE = Identifier.fromNamespaceAndPath("kawaii-addon", "dynamic/menu_background");
    private static final Identifier FALLBACK_TEXTURE = Identifier.fromNamespaceAndPath("kawaii-addon", "images/mainmenubg.png");

    private static final List<int[]> frames = new ArrayList<>();
    private static final List<Integer> frameDelaysMs = new ArrayList<>();
    private static int gifWidth, gifHeight;

    private static DynamicTexture texture;
    private static int frameIndex;
    private static long frameDeadline;


    private static volatile boolean active;
    private static boolean loaded;
    private static boolean loadFailed;

    private BackGroundUtil() {}

    public static void setActive(final boolean value) {
        active = value;
        if (!value) return;
        if (!loaded && !loadFailed) load();
    }

    public static void render(final GuiGraphicsExtractor context, final int width, final int height) {
        setActive(true);
        if (loadFailed) {
            drawCover(context, FALLBACK_TEXTURE, width, height, 1920, 1080);
            return;
        }
        if (!loaded) return;

        tick();
        drawCover(context, VIDEO_TEXTURE, width, height, gifWidth, gifHeight);
    }

    private static void load() {
        try (InputStream in = BackGroundUtil.class.getResourceAsStream(RESOURCE_PATH)) {
            if (in == null) throw new IOException("Missing " + RESOURCE_PATH);

            try (GifImageReader reader = new GifImageReader(in, true)) {
                gifWidth  = reader.getMetadata().getWidth();
                gifHeight = reader.getMetadata().getHeight();

                while (reader.hasRemaining()) {
                    GifFrame frame = reader.read();
                    if (frame == null) continue;

                    frames.add(frame.getData());

                    int cs = frame.getDelay();
                    if (cs < 2) cs = 10;
                    frameDelaysMs.add(cs * 10);
                }
            }

            if (frames.isEmpty()) throw new IOException("No frames decoded from " + RESOURCE_PATH);

            NativeImage image = new NativeImage(NativeImage.Format.RGBA, gifWidth, gifHeight, false);
            writeFrame(image, 0);

            texture = new DynamicTexture(() -> "kawaii-bg", image);
            ((AbstractTextureAccessor) texture).setSampler(
                RenderSystem.getSamplerCache().getRepeat(FilterMode.LINEAR)
            );
            mc.getTextureManager().register(VIDEO_TEXTURE, texture);

            frameIndex    = 0;
            frameDeadline = System.currentTimeMillis() + frameDelaysMs.getFirst();
            loaded        = true;
            LOGGER.info("kawaii-addon bg gif loaded: {}x{}, {} frames", gifWidth, gifHeight, frames.size());

        } catch (IOException e) {
            LOGGER.error("Failed to load background gif", e);
            loadFailed = true;
        }
    }

    private static void tick() {
        if (!loaded || frames.size() <= 1) return;

        long now = System.currentTimeMillis();
        if (now < frameDeadline) return;

        frameIndex = (frameIndex + 1) % frames.size();
        writeFrame(texture.getPixels(), frameIndex);
        texture.upload();
        frameDeadline = now + frameDelaysMs.get(frameIndex);
    }

    private static void writeFrame(final NativeImage image, final int index) {
        int[] data = frames.get(index);
        for (int py = 0; py < gifHeight; py++) {
            for (int px = 0; px < gifWidth; px++) {
                image.setPixelABGR(px, py, toAbgr(data[py * gifWidth + px]));
            }
        }
    }

    private static int toAbgr(final int argb) {
        int a = (argb >>> 24) & 0xFF;
        int r = (argb >>> 16) & 0xFF;
        int g = (argb >>>  8) & 0xFF;
        int b =  argb         & 0xFF;
        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    private static void drawCover(final GuiGraphicsExtractor context,
                                  final Identifier textureIdentifier,
                                  final int width, final int height,
                                  final int sourceWidth, final int sourceHeight) {
        final double screenAspect = width  / (double) Math.max(1, height);
        final double sourceAspect = sourceWidth / (double) sourceHeight;
        int   regionWidth  = sourceWidth;
        int   regionHeight = sourceHeight;
        float u = 0F, v = 0F;

        if (sourceAspect > screenAspect) {
            regionWidth = Math.max(1, (int) Math.round(sourceHeight * screenAspect));
            u = (sourceWidth - regionWidth) / 2F;
        } else if (sourceAspect < screenAspect) {
            regionHeight = Math.max(1, (int) Math.round(sourceWidth / screenAspect));
            v = (sourceHeight - regionHeight) / 2F;
        }

        context.blit(
            RenderPipelines.GUI_TEXTURED,
            textureIdentifier,
            0, 0,
            u, v,
            width, height,
            regionWidth, regionHeight,
            sourceWidth, sourceHeight
        );
    }
}
