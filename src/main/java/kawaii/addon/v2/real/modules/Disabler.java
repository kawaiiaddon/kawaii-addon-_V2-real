package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Disabler extends Module {

    public Disabler() {
        super(KawaiiAddon.CATEGORY, "Disabler", "bypass nothing.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Modes> mode = sgGeneral.add(new EnumSetting.Builder<Modes>()
        .name("mode")
        .defaultValue(Modes.grim)
        .build()
    );

    public enum Modes {
        grim, grimV2, vulcan, hypixel, cubecraft, NCP, oldNCP
    }

    private void onTick(TickEvent.Pre event) {
        switch (mode.get()) {
            case grim, hypixel, oldNCP, NCP, cubecraft, vulcan, grimV2 -> info("Get baited Dumb Ass!");
        };
    }
}
