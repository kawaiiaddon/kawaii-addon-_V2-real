package kawaii.addon.v2.real.hud;

import com.ibasco.image.gif.GifFrame;
import com.ibasco.image.gif.GifImageReader;
import com.mojang.blaze3d.platform.NativeImage;
import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
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
public class GIF extends HudElement {

    public static final HudElementInfo<GIF> INFO = new HudElementInfo<>(KawaiiAddon.HUD_GROUP, "GIF-hud", "Displays a gif.", GIF::new);

    public GIF() {
        super(INFO);
    }

    private final SettingGroup sg = settings.getDefaultGroup();

    private final Setting<Double> size = sg.add(new DoubleSetting.Builder()
        .name("size")
        .description("how big.")
        .defaultValue(1.0)
        .min(0.5)
        .max(10.0)
        .sliderRange(0.5, 10.0)
        .decimalPlaces(1)
        .build()
    );

    private final Setting<Double> width = sg.add(new DoubleSetting.Builder()
        .name("width")
        .description("Stretch the cat in the x axis.")
        .defaultValue(5.0)
        .min(0.5)
        .max(10.0)
        .sliderRange(0.5, 10.0)
        .decimalPlaces(1)
        .build()
    );


    private final Setting<Double> height = sg.add(new DoubleSetting.Builder()
        .name("height")
        .description("Stretch the cat in the y axis.")
        .defaultValue(5.0)
        .min(0.5)
        .max(10.0)
        .sliderRange(0.5, 10.0)
        .decimalPlaces(1)
        .build()
    );

    private final Setting<gifs> mode = sg.add(new EnumSetting.Builder<gifs>()
        .name("gifs")
        .description("set the gif you want.")
        .defaultValue(gifs.uwu)
        .build()
    );

    private String gifPath;
    private Identifier textureId;
    private String currentFile;

    public enum gifs {
        uwu, cat, meow1, meow2
    }

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
        String file = "";
        switch (mode.get()) {
            case uwu   -> file = "blush-anime.gif";
            case cat   -> file = "cat-patting.gif";
            case meow1 -> file = "meow1.gif";
            case meow2 -> file = "meow2.gif";
        }
        gifPath   = "/assets/" + space + "/gif/" + file;
        textureId = Identifier.fromNamespaceAndPath(space, "gif/" + file);

        if (!file.equals(currentFile)) {
            reset();
            currentFile = file;
        }

        double n = size.get();
        double x_width = width.get();
        double y_height = height.get();
        setSize(64 * n * x_width, 64 * n * y_height);

        if (!loaded && !loadFailed) load();
        if (loadFailed) return;

        tick();
        renderer.texture(textureId, x, y, getWidth(), getHeight(), Color.WHITE);
    }

    private void reset() {
        if (texture != null) {
            Minecraft.getInstance().getTextureManager().release(textureId);
            texture.close();
            texture = null;
        }
        frames.clear();
        frameDelaysMs.clear();
        frameIndex = 0;
        loaded = false;
        loadFailed = false;
    }

    private void load() {
        try (InputStream in = GIF.class.getResourceAsStream(gifPath)) {
            if (in == null) throw new IOException("Couldn't find " + gifPath + " on the classpath");
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

            if (frames.isEmpty()) throw new IOException("No frames decoded from " + gifPath);

            NativeImage image = new NativeImage(NativeImage.Format.RGBA, gifWidth, gifHeight, false);
            writeFrame(image, 0);

            texture = new DynamicTexture(() -> "uwu-blush-anime", image);
            Minecraft.getInstance().getTextureManager().register(textureId, texture);

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
