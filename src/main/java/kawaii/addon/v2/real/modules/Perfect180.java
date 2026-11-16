package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Perfect180 extends Module {

    public Perfect180() {
        super(KawaiiAddon.CATEGORY, "PERFECT 180", "Razer basilisk v4 pro mouse at home.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Double> deg = sgGeneral.add(new DoubleSetting.Builder()
        .name("Degrees")
        .defaultValue(180.0)
        .min(0.01)
        .sliderMax(360.0)
        .build()
    );

    @Override
    public void onActivate() {
        super.onActivate();
        assert mc.player != null;
        mc.player.setYRot(mc.player.getYRot() + deg.get().floatValue());
        toggle();
    }
}
