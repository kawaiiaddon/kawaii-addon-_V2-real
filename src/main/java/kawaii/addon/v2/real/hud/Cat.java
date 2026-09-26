package kawaii.addon.v2.real.hud;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.resources.Identifier;

import static kawaii.addon.v2.real.util.FilePath.space;

public class Cat extends HudElement {
    public static final HudElementInfo<Cat> INFO = new HudElementInfo<>(KawaiiAddon.HUD_GROUP, "cat-hud", "Displays a cat icon.", Cat::new);

    static int imgTotal = 36;

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

    private final Setting<Integer> size = sg.add(new IntSetting.Builder()
        .name("size")
        .description("set how big the cat is.")
        .defaultValue(1)
        .min(1)
        .sliderMin(1)
        .sliderMax(10)
        .build()
    );

    private final Setting<Integer> width = sg.add(new IntSetting.Builder()
        .name("width")
        .description("Stretch the cat in the x axis.")
        .defaultValue(5)
        .min(1)
        .sliderMin(1)
        .sliderMax(20)
        .build()
    );


    private final Setting<Integer> height = sg.add(new IntSetting.Builder()
        .name("height")
        .description("Stretch the cat in the y axis.")
        .defaultValue(5)
        .min(1)
        .sliderMin(1)
        .sliderMax(20)
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
        Cat31, Cat32, Cat33, Cat34, Cat35, Cat36
    }

    private final Setting<Integer> picture = sg.add(new IntSetting.Builder()
        .name("picture")
        .description("Select different pictures of catgirls.")
        .defaultValue(1)
        .min(1)
        .sliderMin(1)
        .sliderMax(imgTotal)
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
        Identifier TEXTURE = null;

        switch (mode.get()) {
            case Cat1 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat1.png");
            case Cat2 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat2.png");
            case Cat3 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat3.png");
            case Cat4 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat4.png");
            case Cat5 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat5.png");
            case Cat6 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat6.png");
            case Cat7 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat7.png");
            case Cat8 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat8.png");
            case Cat9 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat9.png");
            case Cat10 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat10.png");
            case Cat11 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat11.png");
            case Cat12 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat12.png");
            case Cat13 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat13.png");
            case Cat14 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat14.png");
            case Cat15 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat15.png");
            case Cat16 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat16.png");
            case Cat17 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat17.png");
            case Cat18 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat18.png");
            case Cat19 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat19.png");
            case Cat20 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat20.png");
            case Cat21 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat21.png");
            case Cat22 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat22.png");
            case Cat23 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat23.png");
            case Cat24 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat24.png");
            case Cat25 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat25.png");
            case Cat26 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat26.png");
            case Cat27 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat27.png");
            case Cat28 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat28.png");
            case Cat29 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat29.png");
            case Cat30 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat30.png");
            case Cat31 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat31.png");
            case Cat32 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat32.png");
            case Cat33 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat33.png");
            case Cat34 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat34.png");
            case Cat35 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat35.png");
            case Cat36 -> TEXTURE = Identifier.fromNamespaceAndPath(space, "hud/cat36.png");
        }
        int n = size.get();
        int x_width = width.get();
        int y_height = height.get();
        setSize(64 * x_width * n, 64 * y_height * n);
        renderer.texture(TEXTURE, x, y, getWidth(), getHeight(), Color.WHITE);
    }

    private void OGRender(HudRenderer renderer) {
        int n = size.get();
        int x_width = width.get();
        int y_height = height.get();
        setSize(64 * x_width * n, 64 * y_height * n);

        int index = picture.get() - 1;
        if (index < 0 || index >= TEXTURES.length) index = 0;

        renderer.texture(TEXTURES[index], x, y, getWidth(), getHeight(), Color.WHITE);
    }
}
