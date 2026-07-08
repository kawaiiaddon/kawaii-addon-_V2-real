package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class HeadPatTurn extends Module {
    private final SettingGroup sg = settings.getDefaultGroup();

    private final Setting<SpinMode> mode = sg.add(new EnumSetting.Builder<SpinMode>()
        .name("mode")
        .defaultValue(SpinMode.Server)
        .build()
    );

    private final Setting<Integer> speed = sg.add(new IntSetting.Builder()
        .name("speed")
        .defaultValue(10)
        .min(1)
        .sliderMax(100)
        .build()
    );

    private final Setting<Boolean> sound = sg.add(new BoolSetting.Builder()
        .name("sound")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> pitch = sg.add(new DoubleSetting.Builder()
        .name("pitch")
        .description("set pitch.")
        .defaultValue(1.0)
        .min(0.5)
        .sliderRange(0.5, 2.0)
        .decimalPlaces(1)
        .build()
    );

    private final Setting<Double> volume = sg.add(new DoubleSetting.Builder()
        .name("volume")
        .description("set volume.")
        .defaultValue(0.5)
        .min(0.1)
        .sliderRange(0.1, 1.0)
        .decimalPlaces(1)
        .build()
    );

    private static final ResourceLocation SPINNY_ID =
        ResourceLocation.fromNamespaceAndPath("kawaii-addon", "spinny_event");
    private static final SoundEvent SPINNY_SOUND =
        SoundEvent.createVariableRangeEvent(SPINNY_ID);

    private float yaw;
    private SimpleSoundInstance spinSound;

    public HeadPatTurn() {
        super(KawaiiAddon.CATEGORY, "HeadPatTurn", "Spins your player endlessly.");
    }

    @Override
    public void onActivate() {
        if (mc.player == null) return;
        yaw = mc.player.getYRot();
        if (sound.get()) startSound();
    }

    @Override
    public void onDeactivate() {
        stopSound();
    }

    private void startSound() {
        if (spinSound != null) return;

        spinSound = SimpleSoundInstance.forUI(
            SPINNY_SOUND,
            pitch.get().floatValue(),
            volume.get().floatValue()
        );
        mc.getSoundManager().play(spinSound);
    }

    private void stopSound() {
        if (spinSound != null) {
            mc.getSoundManager().stop(spinSound);
            spinSound = null;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (!Utils.canUpdate()) return;
        if (sound.get()) startSound();
        else stopSound();

        yaw += speed.get();

        switch (mode.get()) {
            case Client -> mc.player.setYRot(yaw);
            case Server -> Rotations.rotate(yaw, 0, -15);
        }
    }

    public enum SpinMode {
        Client,
        Server
    }
}
