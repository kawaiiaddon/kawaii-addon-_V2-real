package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import kawaii.addon.v2.real.util.PlayerPosition;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.level.Level;
import meteordevelopment.meteorclient.events.world.TickEvent;
//patched on 6b :( i think soooooo imma keep the code and just not register it :3
public class RockBreaker extends Module {

    public RockBreaker() {
        super(KawaiiAddon.CATEGORY, "RockBreaker", "might break bedrock on some servers.");
    }

    @SuppressWarnings("unused")
    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player != null) {
            PlayerPosition pos = new PlayerPosition();
            int y = pos.getY();

            assert mc.level != null;
            if (mc.level.dimension() == Level.NETHER && y == 5) {
                assert mc.player != null;
                String message = Config.get().prefix.get() + "vclip -15";
                mc.player.connection.sendChat(message);
            }
        }
    }
}
