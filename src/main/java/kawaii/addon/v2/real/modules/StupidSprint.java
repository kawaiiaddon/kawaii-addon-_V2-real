package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;

public class StupidSprint extends Module {

    public StupidSprint() {
        super(KawaiiAddon.CATEGORY, "StupidSprint", "Bad AutoSprint Module");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> nowatersprint = sgGeneral.add(new BoolSetting.Builder()
        .name("NoWaterSprint")
        .description("Do not sprint in thas water.")
        .build()
    );

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGH)
    private void onTickMovement(TickEvent.Post event) {
        mc.options.keySprint.setDown(mc.player != null && mc.isWindowActive() && !(nowatersprint.get() && mc.player.isInWater()));
    }
}
