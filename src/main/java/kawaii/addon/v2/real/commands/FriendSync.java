package kawaii.addon.v2.real.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kawaii.addon.v2.real.util.ClientArgumentType;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class FriendSync extends Command {

    public FriendSync() {
        super("FriendSync", "Copy friends list from other clients to meteor with ez.");
    }

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.executes(_ -> {

                ChatUtils.sendMsg(0, ChatFormatting.GRAY, "Supported clients: mio, wurst, all");
                return SINGLE_SUCCESS;
            })
            .then(argument("client", ClientArgumentType.create())
                .suggests((context, suggestionsBuilder) -> {
                    ClientArgumentType clientArg = ClientArgumentType.create();
                    for (String client : clientArg.getExamples()) {
                        suggestionsBuilder.suggest(client);
                    }
                    return suggestionsBuilder.buildFuture();
                })
                .executes(context -> {
                    String client = ClientArgumentType.get(context, "client");
                    int added = 0;

                    try {
                        Friends friendsInstance = Friends.get();

                        // Dynamically find and invoke the mixin method based on the string input
                        String methodName = "importFrom" + client.substring(0, 1).toUpperCase() + client.substring(1).toLowerCase(); // e.g., "importFromMio"

                        java.lang.reflect.Method method = friendsInstance.getClass().getMethod(methodName);
                        added = (int) method.invoke(friendsInstance);

                        if (added == -1) {
                            error(client + " friend file not found.");
                        } else {
                            ChatUtils.sendMsg(0, ChatFormatting.GRAY, "Imported " + added + " friends from " + client + ".");
                        }

                    } catch (NoSuchMethodException e) {
                        error("Unsupported client: " + client);
                    } catch (Exception e) {
                        e.printStackTrace();
                        error("Failed to sync " + client + " friends.");
                    }

                    return SINGLE_SUCCESS;
                }));
    }
}
