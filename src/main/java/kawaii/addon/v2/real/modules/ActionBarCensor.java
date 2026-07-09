package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;

public class ActionBarCensor extends Module {

    public ActionBarCensor() {
        super(KawaiiAddon.CATEGORY, "actionbar-censor", "Hides the 2b2t.org message.");
    }

    String filter = "";

    public boolean shouldHide(String text) {
        if (!isActive()) return false;
        return text.contains(filter);
    }
}
