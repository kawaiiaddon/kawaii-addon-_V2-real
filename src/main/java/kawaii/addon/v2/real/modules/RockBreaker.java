package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import kawaii.addon.v2.real.util.PlayerPosition;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import meteordevelopment.meteorclient.events.world.TickEvent;

public class RockBreaker extends Module {

    public RockBreaker() {
        super(KawaiiAddon.CATEGORY, "RockBreaker", "might break bedrock on some servers.");
    }

    Minecraft client = Minecraft.getInstance();

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player != null) {
            PlayerPosition pos = new PlayerPosition();
            int y = pos.getY();

            if (mc.level.dimension() == Level.NETHER & y == 5) {
                assert client.player != null;
                String currentPrefix = Config.get().prefix.get();
                String message = String.format(currentPrefix + "vclip -15");
                client.player.connection.sendChat(message);
            }
        }
    }
}
