package kawaii.addon.v2.real.modules;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.orbit.EventHandler;

import java.util.function.Consumer;

import static kawaii.addon.v2.real.KawaiiAddon.CATEGORY;

public class CatFacts extends Module {
    public enum Mode {
        Manual,
        Automatic
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Manual: use the catfact button in chat. Automatic: sends facts on a timer.")
        .defaultValue(Mode.Automatic)
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("delay :3")
        .defaultValue(200)
        .min(200)
        .sliderMax(10000)
        .visible(() -> mode.get() == Mode.Automatic)
        .build()
    );

    private int timer;

    public CatFacts() {
        super(CATEGORY, "cat-facts", "cat fact :3");
    }

    @Override
    public void onActivate() {
        timer = 0;
    }

    @SuppressWarnings("unused")
    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mode.get() != Mode.Automatic) return;

        if (timer <= 0) {
            fetchFact(fact -> {
                if (mc.player != null) mc.player.connection.sendChat(fact);
            });
            timer = delay.get();
        } else {
            timer--;
        }
    }

    public static void fetchFact(Consumer<String> callback) {
        new Thread(() -> {
            try {
                String jsonResponse = Http.get("https://catfact.ninja/fact").sendString();
                if (jsonResponse != null) {
                    JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                    String fact = jsonObject.get("fact").getAsString();
                    MeteorClient.mc.execute(() -> callback.accept(fact));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
