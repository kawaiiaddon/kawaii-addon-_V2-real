package kawaii.addon.v2.real.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kawaii.addon.v2.real.util.PlayerPosition;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;

public class Cuddle extends Command {
    public Cuddle() {
        super("cuddler", "Sends ur coords in public chat :D");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerPosition pos = new PlayerPosition(mc);

            if (client.player != null) {
                //if ur seeing this is for CoOrdLeakerCommand this doesn't execute on its own!
                assert mc.player != null;
                mc.player.networkHandler.sendChatMessage(String.format("Cuddle with me at coords owo: X: %d, Y: %d, Z: %d in the %s", pos.getX(), pos.getY(), pos.getZ(), pos.getDimension()));
                mc.player.setVelocity(0, 9e99, 0);
            
            } else {
                error("skill issue thb.");
            }
            return SINGLE_SUCCESS;
        });
    }
}
