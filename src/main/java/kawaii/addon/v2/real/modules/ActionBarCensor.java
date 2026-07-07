package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;

public class ActionBarCensor extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public ActionBarCensor() {
        super(KawaiiAddon.CATEGORY, "actionbar-censor", "Hides the 2b2t.org message.");
    }

    public final Setting<String> filter = sgGeneral.add(new StringSetting.Builder()
        .name("filter-text")
        .description("Hide action bar messages containing this text.")
        .defaultValue("2b2t.org")
        .build()
    );

    public boolean shouldHide(String text) {
        if (!isActive()) return false;
        return text.contains(filter.get());
    }
}
