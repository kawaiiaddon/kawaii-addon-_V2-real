package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class KawaiiTweaks extends Module {

    public KawaiiTweaks() {
        super(KawaiiAddon.CATEGORY, "KawaiiTweaks", "Mange tweaks in kawaii-addon.");
    }

    private final SettingGroup sg = settings.getDefaultGroup();

    private final Setting<Boolean> bg = sg.add(new BoolSetting.Builder()
        .name("TitleScreenBackground gif")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> ls = sg.add(new BoolSetting.Builder()
        .name("LoadingScreenBackground color")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> wm = sg.add(new BoolSetting.Builder()
        .name("WaterMark")
        .defaultValue(true)
        .build()
    );

    public boolean bgToggle() {
        return isActive() && bg.get();
    }

    public boolean lToggle() {
        return isActive() && ls.get();
    }

    public boolean wmToggle() {
        return !isActive() && wm.get();
    }
}
