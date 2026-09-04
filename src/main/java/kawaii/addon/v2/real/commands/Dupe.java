package kawaii.addon.v2.real.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.commands.SharedSuggestionProvider;

public class Dupe extends Command {
    public Dupe() {
        super("dupe", "Client-side fake dupe command");
    }

    @Override
    public void build(LiteralArgumentBuilder<SharedSuggestionProvider> builder) {
        builder.executes(ctx -> {
            info("Get baited Dumb Ass!");
            return SINGLE_SUCCESS;
        });
    }
}
