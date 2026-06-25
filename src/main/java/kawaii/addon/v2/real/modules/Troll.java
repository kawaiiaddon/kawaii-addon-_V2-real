package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.events.meteor.MouseScrollEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import java.util.concurrent.ThreadLocalRandom;

public class Troll extends Module {
    public Troll() {
        super(KawaiiAddon.CATEGORY, "troll", "Changes some stuff. :)");
    }

    private int ticksSinceLastInput = 0;
    private static final int IDLE_THRESHOLD = 2400;

    @EventHandler
    private void onKeyInput(KeyEvent event) {
        ticksSinceLastInput = 0;
    }

    @EventHandler
    private void onMouseClick(MouseClickEvent event) {
        ticksSinceLastInput = 0;
    }

    @EventHandler
    private void onMouseScroll(MouseScrollEvent event) {
        ticksSinceLastInput = 0;
    }

    public enum Sound {
        TAKING_TOO_LONG("your_taking_too_long_event"),
        YOUR_LONG("your_long_event");

        public final SoundEvent sound;

        Sound(String soundEventName) {
            Identifier id = Identifier.fromNamespaceAndPath("kawaii-addon", soundEventName);
            this.sound = SoundEvent.createVariableRangeEvent(id);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null) return;

        ticksSinceLastInput++;

        if (ticksSinceLastInput >= IDLE_THRESHOLD) {
            if (ticksSinceLastInput == IDLE_THRESHOLD) {
                Sound[] values = Sound.values();
                Sound soundToPlay = values[ThreadLocalRandom.current().nextInt(values.length)];

                double volume = 1.0;
                double pitch = 1.0;

                mc.getSoundManager().play(
                    SimpleSoundInstance.forUI(
                        soundToPlay.sound,
                        (float) pitch,
                        (float) volume
                    )
                );
            }
        }
    }
}
