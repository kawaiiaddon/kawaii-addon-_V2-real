package kawaii.addon.v2.real.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;

public class CrashOut extends Command {

    public CrashOut() {
        super("crashout", "No more ragebait.");
    }

    @Override
    public void build(LiteralArgumentBuilder<SharedSuggestionProvider> builder) {
        builder.executes(ctx -> {
            Minecraft.getInstance().stop();
            return SINGLE_SUCCESS;
        });
    }
}
