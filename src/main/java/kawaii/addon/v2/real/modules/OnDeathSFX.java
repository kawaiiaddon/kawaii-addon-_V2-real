package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import java.util.concurrent.ThreadLocalRandom;

import static kawaii.addon.v2.real.util.FilePath.space;

public class OnDeathSFX extends Module {

    public OnDeathSFX() {
        super(KawaiiAddon.CATEGORY, "OnDeathSFX", "Plays a sound when you die (in Minecraft).");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> random = sgGeneral.add(new BoolSetting.Builder()
        .name("randomize")
        .description("changes the sound to be random")
        .build()
    );

    private final Setting<DeathSound> deathSound = sgGeneral.add(
        new EnumSetting.Builder<DeathSound>()
            .name("death-sound")
            .description("funny sounds.")
            .defaultValue(DeathSound.FAHHH)
            .visible(() -> !random.get()) // 👈 hide when random = true
            .build()
    );

    private final Setting<Double> pitch = sgGeneral.add(new DoubleSetting.Builder()
        .name("pitch")
        .description("set pitch.")
        .defaultValue(1.0)
        .min(0.5)
        .sliderRange(0.5, 2.0)
        .decimalPlaces(1)
        .build()
    );

    private final Setting<Double> volume = sgGeneral.add(new DoubleSetting.Builder()
        .name("volume")
        .description("set volume.")
        .defaultValue(0.5)
        .min(0.1)
        .sliderRange(0.1, 1.0)
        .decimalPlaces(1)
        .build()
    );



    public enum DeathSound {
        FAHHH(space, "fahhh_event"),
        VINEBOOM(space, "vine_boom_event"),
        METAL_PIPE(space, "metal-pipe_drop_event"),
        ACK(space, "ack_event"),
        error(space, "error_event"),
        fn_death(space, "fn_death_event"),
        lego_breaking(space, "lego_breaking_event"),
        sad_instrument(space, "sad_instrument_event");
        public final SoundEvent sound;
        DeathSound(String namespace, String path) {
            Identifier id = Identifier.fromNamespaceAndPath(namespace, path);
            this.sound = SoundEvent.createVariableRangeEvent(id);
        }
    }

    private boolean wasDead = false;

    @EventHandler
    private void onTick(TickEvent.Post event) {
        assert mc.player != null;
        boolean dead = mc.player.isDeadOrDying();

        if (dead && !wasDead) {
            DeathSound soundToPlay;

            if (random.get()) {
                DeathSound[] values = DeathSound.values();
                soundToPlay = values[ThreadLocalRandom.current().nextInt(values.length)];
            } else {
                soundToPlay = deathSound.get();
            }

            mc.getSoundManager().play(
                SimpleSoundInstance.forUI(
                    soundToPlay.sound,
                    pitch.get().floatValue(),
                    volume.get().floatValue()
                )
            );
        }
        wasDead = dead;
    }
}
