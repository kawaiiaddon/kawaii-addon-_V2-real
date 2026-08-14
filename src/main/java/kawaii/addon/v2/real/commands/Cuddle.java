package kawaii.addon.v2.real.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kawaii.addon.v2.real.modules.Troll;
import kawaii.addon.v2.real.util.PlayerPosition;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import java.util.Objects;

public class Cuddle extends Command {
    public Cuddle() {
        super("cuddler", "Sends ur coords in public chat :D");
    }

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.executes(_ -> {
            PlayerPosition pos = new PlayerPosition();

            if (mc.player != null) {
                //if ur seeing this is for CoOrdLeakerCommand this doesn't execute on its own!
                mc.player.connection.sendChat(String.format("Cuddle with me at coords owo: X: %d, Y: %d, Z: %d in the %s", Math.round(pos.getX()), Math.round(pos.getY()), Math.round(pos.getZ()), pos.getDimension()));
                if (Objects.requireNonNull(Modules.get().get(Troll.class)).isActive()) {
                    mc.player.setDeltaMovement(0, 9e99, 0);
                }
            } else {
                error("skill issue thb.");
            }
            return SINGLE_SUCCESS;
        });
    }
}
