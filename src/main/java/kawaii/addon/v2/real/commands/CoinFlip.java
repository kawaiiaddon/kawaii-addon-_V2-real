package kawaii.addon.v2.real.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class CoinFlip extends Command {

    public CoinFlip() {
        super("coinflip", "Flip a coin.");
    }

    final String head = "Heads";
    final String tail = "Tails";

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.executes(_ -> {
            info(Math.random() < 0.5 ? head : tail);
            return SINGLE_SUCCESS;
        });
    }
}
