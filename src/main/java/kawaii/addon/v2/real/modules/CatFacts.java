package kawaii.addon.v2.real.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.orbit.EventHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import static kawaii.addon.v2.real.KawaiiAddon.CATEGORY;

public class CatFacts extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("delay :3")
        .defaultValue(200)
        .min(200)
        .sliderMax(10000)
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

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (timer <= 0) {
            sendFact();
            timer = delay.get();
        } else {
            timer--;
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void sendFact() {
        new Thread(() -> {
            try {
                String jsonResponse = Http.get("https://catfact.ninja/fact").sendString();
                if (jsonResponse != null) {
                    JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                    String fact = jsonObject.get("fact").getAsString();
                    mc.execute(() -> {
                        if (mc.player != null) {
                            mc.player.connection.sendChat(fact);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
