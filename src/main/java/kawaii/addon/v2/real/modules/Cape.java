package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import kawaii.addon.v2.real.util.RainbowCapeTexture;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class Cape extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<Capes> capes = sgGeneral.add(new EnumSetting.Builder<Capes>()
        .name("Cape")
        .defaultValue(Capes.kawaii)
        .build()
    );

    public Cape() {
        super(KawaiiAddon.CATEGORY, "Capes", "Get a cape (client-side only).");
    }

    public enum Capes{
        kawaii, cat, idk, turtle, hutao, vape, RETRO, h0rny, astolfo, RusherHack, phobos, Shoreline, future, RGB
    }

    public final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("speed")
        .description("Color cycles per second.")
        .defaultValue(0.1)
        .min(0.01)
        .sliderMax(1)
        .visible(() -> capes.get() == Capes.RGB)
        .build()
    );

    public final Setting<Double> spread = sgGeneral.add(new DoubleSetting.Builder()
        .name("spread")
        .defaultValue(0.01)
        .min(0)
        .sliderMax(0.05)
        .visible(() -> capes.get() == Capes.RGB)
        .build()
    );

    public final Setting<Integer> alpha = sgGeneral.add(new IntSetting.Builder()
        .name("alpha")
        .defaultValue(200)
        .range(0, 255)
        .sliderRange(0, 255)
        .visible(() -> capes.get() == Capes.RGB)
        .build()
    );

    @SuppressWarnings("unused")
    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (capes.get() == Capes.RGB)
            RainbowCapeTexture.update(speed.get().floatValue(), alpha.get(), spread.get().floatValue());
    }
}
