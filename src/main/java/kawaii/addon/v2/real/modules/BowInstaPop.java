package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import kawaii.addon.v2.real.util.PlayerPosition;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.level.Level;

public class BowInstaPop extends Module {

    public BowInstaPop() {super(KawaiiAddon.CATEGORY, "BowInstaPop", "pops people using a bow or smth idk");}

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player != null) {
            PlayerPosition pos = new PlayerPosition();
            int y = pos.getY();

            System.out.println(y);

        }
    }

}
