package kawaii.addon.v2.real.hud;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.resources.Identifier;

import static kawaii.addon.v2.real.util.FilePath.space;

public class Cat extends HudElement {
    public static final HudElementInfo<Cat> INFO = new HudElementInfo<>(KawaiiAddon.HUD_GROUP, "cat-hud", "Displays a cat icon.", Cat::new);

    static final int imgTotal = 51;

    private static final Identifier[] TEXTURES = new Identifier[imgTotal];

    static {
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = Identifier.fromNamespaceAndPath(space, "hud/cat" + (i + 1) + ".png");
        }
    }

    public Cat() {
        super(INFO);
    }

    private final SettingGroup sg = settings.getDefaultGroup();

    private final Setting<Double> size = sg.add(new DoubleSetting.Builder()
        .name("size")
        .description("set how big the cat is.")
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

    public enum SetMode {
        Modern, OG
    }

    private final Setting<SetMode> modes = sg.add(new EnumSetting.Builder<SetMode>()
        .name("Picture select mode")
        .description("Which method you want to use for selecting a catgirl.")
        .defaultValue(SetMode.Modern)
        .build()
    );

    private final Setting<Picture> mode = sg.add(new EnumSetting.Builder<Picture>()
        .name("picture")
        .description("set the picture you want.")
        .defaultValue(Picture.Cat1)
        .visible(() -> modes.get() == SetMode.Modern)
        .build()
    );

    public enum Picture {
        Cat1, Cat2, Cat3, Cat4, Cat5, Cat6,
        Cat7, Cat8, Cat9, Cat10, Cat11, Cat12,
        Cat13, Cat14, Cat15, Cat16, Cat17, Cat18,
        Cat19, Cat20, Cat21, Cat22, Cat23, Cat24,
        Cat25, Cat26, Cat27, Cat28, Cat29, Cat30,
        Cat31, Cat32, Cat33, Cat34, Cat35, Cat36,
        Cat37, Cat38, Cat39, Cat40, Cat41, Cat42,
        Cat43, Cat44, Cat45, Cat46, Cat47, Cat48,
        Cat49, Cat50, Cat51
    }

    private final Setting<Integer> picture = sg.add(new IntSetting.Builder()
        .name("og-picture")
        .description("Select different pictures of catgirls.")
        .defaultValue(1)
        .min(1)
        .max(imgTotal)
        .sliderRange(1, imgTotal)
        .visible(() -> modes.get() == SetMode.OG)
        .build()
    );

    @Override
    public void render(HudRenderer renderer) {
        switch (modes.get()) {
            case Modern -> ModernRender(renderer);
            case OG -> OGRender(renderer);
        }
    }

    private void ModernRender(HudRenderer renderer) {
        int index = mode.get().ordinal();
        if (index >= TEXTURES.length) index = 0;

        drawTexture(renderer, TEXTURES[index]);
    }

    private void OGRender(HudRenderer renderer) {
        int index = picture.get() - 1;
        if (index < 0 || index >= TEXTURES.length) index = 0;

        drawTexture(renderer, TEXTURES[index]);
    }

    private void drawTexture(HudRenderer renderer, Identifier texture) {
        double n = size.get();
        double x_width = width.get();
        double y_height = height.get();
        setSize(64 * n * x_width, 64 * n * y_height);

        renderer.texture(texture, x, y, getWidth(), getHeight(), Color.WHITE);
    }
}
