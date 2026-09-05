package kawaii.addon.v2.real.hud;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.resources.Identifier;

import static kawaii.addon.v2.real.util.FilePath.space;

public class WaterMark extends HudElement {

    public static final HudElementInfo<WaterMark> INFO = new HudElementInfo<>(KawaiiAddon.HUD_GROUP, "WaterMark", "Displays a WaterMark.", WaterMark::new);
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(space, "/icon/watermark.png");

    public WaterMark() {
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

    @Override
    public void render(HudRenderer renderer) {
        int n = size.get();
        setSize(160 * n, 64 * n);
        renderer.texture(TEXTURE, x, y, getWidth(), getHeight(), Color.WHITE);
    }
}
