package kawaii.addon.v2.real.hud;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayerSeekerHud extends HudElement {

    public static final HudElementInfo<PlayerSeekerHud> INFO = new HudElementInfo<>(KawaiiAddon.HUD_GROUP, "PlayerSeeker", "Displays info about nearby players.", PlayerSeekerHud::new);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgColors = settings.createGroup("Colors");

    private final Setting<SettingColor> friendColor = sgColors.add(new ColorSetting.Builder()
        .name("friend-color")
        .description("Color for friends.")
        .defaultValue(new SettingColor(0, 255, 0, 255))
        .build()
    );

    private final Setting<SettingColor> playerColor = sgColors.add(new ColorSetting.Builder()
        .name("player-color")
        .description("Color for regular players.")
        .defaultValue(new SettingColor(255, 255, 255, 255))
        .build()
    );

    private final Setting<SettingColor> backgroundColor = sgColors.add(new ColorSetting.Builder()
        .name("background-color")
        .description("Color of the background.")
        .defaultValue(new SettingColor(0, 0, 0, 128))
        .build()
    );

    private final Setting<Boolean> backgroundEnabled = sgGeneral.add(new BoolSetting.Builder()
        .name("background")
        .description("Shows a background behind the text.")
        .defaultValue(true)
        .build()
    );

    public PlayerSeekerHud() {
        super(INFO);
    }

    @Override
    public void render(HudRenderer renderer) {
        if (mc.level == null || mc.player == null) return;

        List<Player> players = mc.level.players().stream()
            .filter(p -> p != mc.player)
            .sorted(Comparator.comparing(p -> p.getGameProfile().name()))
            .collect(Collectors.toList());

        if (players.isEmpty()) {
            String noPlayersText = "No players nearby";
            double width = renderer.textWidth(noPlayersText, false);
            double height = renderer.textHeight(false);

            if (backgroundEnabled.get()) {
                renderer.quad(x, y, width, height, backgroundColor.get());
            }
            renderer.text(noPlayersText, x, y, Color.WHITE, false);
            setSize(width, height);
            return;
        }

        double currentY = y;
        double maxWidth = 0;

        Vec3 localPos = mc.player.position();

        for (Player player : players) {
            String name = player.getGameProfile().name();
            Vec3 targetPos = player.position();

            int distance = (int) localPos.distanceTo(targetPos);
            int pX = (int) targetPos.x;
            int pY = (int) targetPos.y;
            int pZ = (int) targetPos.z;

            String text = String.format("%s [%d, %d, %d] (%d blocks)", name, pX, pY, pZ, distance);
            maxWidth = Math.max(maxWidth, renderer.textWidth(text, false));
        }

        double totalHeight = (renderer.textHeight(false) + 2) * players.size();

        if (backgroundEnabled.get()) {
            renderer.quad(x, y, maxWidth, totalHeight, backgroundColor.get());
        }

        for (Player player : players) {
            String name = player.getGameProfile().name();
            Vec3 targetPos = player.position();

            int distance = (int) localPos.distanceTo(targetPos);
            int pX = (int) targetPos.x;
            int pY = (int) targetPos.y;
            int pZ = (int) targetPos.z;

            String text = String.format("%s [%d, %d, %d] (%d blocks)", name, pX, pY, pZ, distance);

            Color textColor = Friends.get().isFriend(player) ? friendColor.get() : playerColor.get();

            renderer.text(text, x, currentY, textColor, false);
            currentY += renderer.textHeight(false) + 2;
        }

        setSize(maxWidth, totalHeight);
    }
}
