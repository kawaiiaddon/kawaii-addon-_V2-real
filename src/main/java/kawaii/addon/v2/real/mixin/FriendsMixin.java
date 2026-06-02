package kawaii.addon.v2.real.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mixin(value = Friends.class, remap = false)
public abstract class FriendsMixin {

    @Shadow public abstract boolean add(Friend friend);
    @Shadow public abstract Friend get(String name);

    @Unique
    public int importFromMio() throws Exception {
        Path path = Paths.get(
            Minecraft.getInstance().gameDirectory.getAbsolutePath(),
            "mio-fabric",
            "socials.json"
        );

        if (!Files.exists(path)) return -1;

        String json = Files.readString(path);
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonArray socials = root.getAsJsonArray("socials");

        int added = 0;

        for (JsonElement element : socials) {
            JsonObject obj = element.getAsJsonObject();
            String role = obj.get("role").getAsString();
            if (!role.equalsIgnoreCase("friend")) continue;

            String name = obj.get("name").getAsString();
            if (get(name) == null) {
                add(new Friend(name));
                added++;
            }
        }

        return added;
    }

    @Unique
    public int importFromWurst() throws Exception {
        Path path = Paths.get(
            Minecraft.getInstance().gameDirectory.getAbsolutePath(),
            "wurst",
            "friends.json"
        );

        if (!Files.exists(path)) return -1;

        String json = Files.readString(path);
        JsonArray array = JsonParser.parseString(json).getAsJsonArray();

        int added = 0;
        for (JsonElement element : array) {
            String name = element.getAsString();
            if (get(name) == null) {
                add(new Friend(name));
                added++;
            }
        }

        return added;
    }
}
