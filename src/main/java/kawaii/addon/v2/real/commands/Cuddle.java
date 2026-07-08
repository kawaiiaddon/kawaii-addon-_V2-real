package kawaii.addon.v2.real.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;

public class Cuddle extends Command {
    public Cuddle() {
        super("cuddler", "Sends ur coords in public chat :D");
    }

    @Override
    public void build(LiteralArgumentBuilder<SharedSuggestionProvider> builder) {
        builder.executes(context -> {
            Minecraft client = Minecraft.getInstance();

            if (client.player != null) {
                //if ur seeing this is for CoOrdLeakerCommand this doesn't execute on its own!
                int x = client.player.getBlockX();
                int y = client.player.getBlockY();
                int z = client.player.getBlockZ();
                String message = String.format("Cuddle with me at coords owo: X: %d, Y: %d, Z: %d", x, y, z);
                client.player.connection.sendChat(message);
            } else {
                error("skill issue thb.");
            }

            return SINGLE_SUCCESS;
        });
    }
}
