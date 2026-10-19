package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.entity.player.Player;

//"inspired" from blackout automoan :3

public class AutoMeow extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> message = sgGeneral.add(
        new StringSetting.Builder()
            .name("message")
            .description("meows to the nearest person(or smth else idk)")
            .defaultValue("meow :3")
            .build()
    );

    private final Setting<Integer> seconds = sgGeneral.add(
        new IntSetting.Builder()
            .name("seconds")
            .description("Seconds between meows.")
            .defaultValue(5)
            .min(1)
            .sliderMin(1)
            .sliderMax(60)
            .build()
    );

    private int timer;

    public AutoMeow() {
        super(KawaiiAddon.CATEGORY, "auto-meow",
            "Whispers a message to the closest player.");
    }

    @Override
    public void onActivate() {
        timer = 0;
    }

    private Player getClosest() {
        if (mc.player == null || mc.level == null) return null;

        Player closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (Player player : mc.level.players()) {
            if (player == mc.player || player.isRemoved()) continue;

            double distance = mc.player.distanceToSqr(player);

            if (distance < closestDistance) {
                closest = player;
                closestDistance = distance;
            }
        }

        return closest;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.level == null) return;

        if (++timer < seconds.get() * 20) return;
        timer = 0;

        Player closest = getClosest();
        if (closest == null) return;

        String text = message.get().trim();
        if (text.isEmpty()) return;

        ChatUtils.sendPlayerMsg(
            "/msg " + closest.getName().getString() + " " + text
        );
    }
}
