package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import kawaii.addon.v2.real.util.PlayerPosition;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.entity.player.Player;

//"inspired" from blackout automoan :3 

public class AutoMeow extends Module {

    public AutoMeow() {super(KawaiiAddon.CATEGORY, "AutoMeow", "Meows to everyone in render distance");}

    private Player getClosest() {
        if (mc.player == null || mc.level == null) {
            return null;
        }

        Player closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (Player player : mc.level.players()) {
            if (player == mc.player) continue;
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
        System.out.println(getClosest());
    }

}
