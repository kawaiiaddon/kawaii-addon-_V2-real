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

    private final Setting<Picture> mode = sg.add(new EnumSetting.Builder<Picture>()
        .name("picture")
        .description("set the picture you want.")
        .defaultValue(Picture.Cat1)
        .build()
    );

    public enum Picture {
        Cat1, Cat2, Cat3, Cat4, Cat5, Cat6, Cat7, Cat8, Cat9, Cat10, Cat11, Cat12, Cat13, Cat14
    }

    private Identifier TEXTURE;

    @Override
    public void render(HudRenderer renderer) {
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
        }
        int n = size.get();
        int x_width = width.get();
        int y_height = height.get();
        setSize(64 * x_width * n, 64 * y_height * n);
        renderer.texture(TEXTURE, x, y, getWidth(), getHeight(), Color.WHITE);
    }
}
