package kawaii.addon.v2.real.hud;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.*;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.resources.Identifier;

import static kawaii.addon.v2.real.util.FilePath.space;

public class Cat extends HudElement {
    public static final HudElementInfo<Cat> INFO = new HudElementInfo<>(KawaiiAddon.HUD_GROUP, "cat-hud", "Displays a cat icon.", Cat::new);

    public Cat() {
        super(INFO);
    }

    private final int images = CatImage.values().length;

    private final SettingGroup sg = settings.getDefaultGroup();

    private final Setting<Double> scale = sg.add(new DoubleSetting.Builder()
        .name("Scale")
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 10)
        .build()
    );

    private final Setting<Double> scaleY = sg.add(new DoubleSetting.Builder()
        .name("Scale Height")
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 10)
        .build()
    );

    private final Setting<Double> scaleX = sg.add(new DoubleSetting.Builder()
        .name("Scale Width")
        .defaultValue(1)
        .min(0)
        .sliderRange(0, 10)
        .build()
    );

    private final Setting<SetMode> mode = sg.add(new EnumSetting.Builder<SetMode>()
        .name("IMG selector-type")
        .defaultValue(SetMode.Enum)
        .build()
    );

    private final Setting<Integer> sliderSet = sg.add(new IntSetting.Builder()
        .name("IMG selector")
        .defaultValue(1)
        .min(1)
        .sliderRange(1, images)
        .visible(() -> mode.get() == SetMode.Slider)
        .build()
    );

    private final Setting<CatImage> enumSet = sg.add(new EnumSetting.Builder<CatImage>()
        .name("IMG enum")
        .defaultValue(CatImage.Cat1)
        .visible(() -> mode.get() == SetMode.Enum)
        .build()
    );

    public enum SetMode {
        Slider, Enum
    }

    public enum CatImage {
        Cat1(1), Cat2(2), Cat3(3), Cat4(4),
        Cat5(5), Cat6(6), Cat7(7), Cat8(8),
        Cat9(9), Cat10(10), Cat11(11), Cat12(12),
        Cat13(13), Cat14(14), Cat15(15), Cat16(16),
        Cat17(17), Cat18(18), Cat19(19), Cat20(20),
        Cat21(21), Cat22(22), Cat23(23), Cat24(24),
        Cat25(25), Cat26(26), Cat27(27), Cat28(28),
        Cat29(29), Cat30(30), Cat31(31), Cat32(32),
        Cat33(33), Cat34(34), Cat35(35), Cat36(36),
        Cat37(37), Cat38(38), Cat39(39), Cat40(40),
        Cat41(41), Cat42(42), Cat43(43), Cat44(44),
        Cat45(45), Cat46(46), Cat47(47), Cat48(48),
        Cat49(49), Cat50(50), Cat51(51);

        public final int index;

        CatImage(int index) {
            this.index = index;
        }
    }

    private int getIndex() {
        return switch (mode.get()) {
            case Slider -> sliderSet.get();
            case Enum -> enumSet.get().index;
        };
    }

    private Identifier getImage() {
        return Identifier.fromNamespaceAndPath(space, "hud/cat" + getIndex() + ".png");
    }

    @Override
    public void render(HudRenderer renderer) {
        setSize(64 * scale.get() * scaleX.get(), 64 * scale.get() * scaleY.get());
        renderer.texture(getImage(), getX(), getY(), getWidth(), getHeight(), Color.WHITE);
    }
}
