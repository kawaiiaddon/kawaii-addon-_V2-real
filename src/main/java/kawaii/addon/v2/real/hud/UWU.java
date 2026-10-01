package kawaii.addon.v2.real.hud;

import com.ibasco.image.gif.GifFrame;
import com.ibasco.image.gif.GifImageReader;
import com.mojang.blaze3d.platform.NativeImage;
import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static kawaii.addon.v2.real.util.FilePath.space;

/*
 * DON'T TOUCH THIS FUCKING CODE IT MIGHT EXPLODE!!!!!!!!!!!!!!
 */
public class UWU extends HudElement {

    public static final HudElementInfo<UWU> INFO = new HudElementInfo<>(KawaiiAddon.HUD_GROUP, "UwU-hud", "Testing gifs.", UWU::new);
    private static final String GIF_PATH = "/assets/" + space + "/uwu/blush-anime.gif";
    private static final Identifier TEXTURE_ID = Identifier.fromNamespaceAndPath(space, "uwu/blush-anime-animated");

    public UWU() {
        super(INFO);
    }

    private final SettingGroup sg = settings.getDefaultGroup();

    private final Setting<Integer> size = sg.add(new IntSetting.Builder()
        .name("size")
        .description("how big.")
        .defaultValue(5)
        .min(1)
        .sliderMin(1)
        .sliderMax(10)
        .build()
    );

    private final List<int[]> frames = new ArrayList<>();
    private final List<Integer> frameDelaysMs = new ArrayList<>();
    private int gifWidth, gifHeight;

    private DynamicTexture texture;
    private boolean loaded;
    private boolean loadFailed;
    private int frameIndex;
    private long frameDeadline;

    @Override
    public void render(HudRenderer renderer) {
        int n = size.get();
        setSize(64 * n, 64 * n);

        if (!loaded && !loadFailed) load();
        if (loadFailed) return;

        tick();
        renderer.texture(TEXTURE_ID, x, y, getWidth(), getHeight(), Color.WHITE);
    }

    private void load() {
        try (InputStream in = UWU.class.getResourceAsStream(GIF_PATH)) {
            if (in == null) throw new IOException("Couldn't find " + GIF_PATH + " on the classpath");
            try (GifImageReader reader = new GifImageReader(in, true)) {
                gifWidth = reader.getMetadata().getWidth();
                gifHeight = reader.getMetadata().getHeight();

                while (reader.hasRemaining()) {
                    GifFrame frame = reader.read();
                    if (frame == null) continue;

                    frames.add(frame.getData());

                    int centiseconds = frame.getDelay();
                    if (centiseconds < 2) centiseconds = 10;
                    frameDelaysMs.add(centiseconds * 10);
                }
            }

            if (frames.isEmpty()) throw new IOException("No frames decoded from " + GIF_PATH);

            NativeImage image = new NativeImage(NativeImage.Format.RGBA, gifWidth, gifHeight, false);
            writeFrame(image, 0);

            texture = new DynamicTexture(() -> "uwu-blush-anime", image);
            Minecraft.getInstance().getTextureManager().register(TEXTURE_ID, texture);

            frameIndex = 0;
            frameDeadline = System.currentTimeMillis() + frameDelaysMs.getFirst();
            loaded = true;
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            loadFailed = true;
        }
    }

    private void tick() {
        if (frames.size() <= 1) return;

        long now = System.currentTimeMillis();
        if (now < frameDeadline) return;

        frameIndex = (frameIndex + 1) % frames.size();
        writeFrame(texture.getPixels(), frameIndex);
        texture.upload();
        frameDeadline = now + frameDelaysMs.get(frameIndex);
    }

    private void writeFrame(NativeImage image, int index) {
        int[] data = frames.get(index);
        for (int py = 0; py < gifHeight; py++) {
            for (int px = 0; px < gifWidth; px++) {
                image.setPixelABGR(px, py, toAbgr(data[py * gifWidth + px]));
            }
        }
    }

    private static int toAbgr(int argb) {
        int a = (argb >>> 24) & 0xFF;
        int r = (argb >>> 16) & 0xFF;
        int g = (argb >>> 8) & 0xFF;
        int b = argb & 0xFF;
        return (a << 24) | (b << 16) | (g << 8) | r;
    }
}
