package kawaii.addon.v2.real.hud;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.hud.*;
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

    public enum DisplayMode {
        Coords,
        Distance,
        Both
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgColors = settings.createGroup("Colors");

    private final Setting<DisplayMode> displayMode = sgGeneral.add(new EnumSetting.Builder<DisplayMode>()
        .name("display-mode")
        .description("What information to display for each player.")
        .defaultValue(DisplayMode.Both)
        .build()
    );

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

    private final Setting<SettingColor> outlineColor = sgColors.add(new ColorSetting.Builder()
        .name("outline-color")
        .description("Color of the outline.")
        .defaultValue(new SettingColor(255, 255, 255, 255))
        .build()
    );

    private final Setting<Boolean> backgroundEnabled = sgGeneral.add(new BoolSetting.Builder()
        .name("background")
        .description("Shows a background behind the text.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> outlineEnabled = sgGeneral.add(new BoolSetting.Builder()
        .name("outline")
        .description("Shows an outline around the HUD element.")
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
            .sorted(Comparator.comparing(p -> p.getGameProfile().getName()))
            .collect(Collectors.toList());

        if (players.isEmpty()) {
            String noPlayersText = "No players nearby";
            double width = renderer.textWidth(noPlayersText, false);
            double height = renderer.textHeight(false);

            if (backgroundEnabled.get()) {
                renderer.quad(x, y, width, height, backgroundColor.get());
            }
            if (outlineEnabled.get()) {
                renderOutline(renderer, x, y, width, height);
            }
            renderer.text(noPlayersText, x, y, Color.WHITE, false);
            setSize(width, height);
            return;
        }

        double currentY = y;
        double maxWidth = 0;

        Vec3 localPos = mc.player.position();

        for (Player player : players) {
            String text = getPlayerText(player, localPos);
            maxWidth = Math.max(maxWidth, renderer.textWidth(text, false));
        }

        double totalHeight = (renderer.textHeight(false) + 2) * players.size();

        if (backgroundEnabled.get()) {
            renderer.quad(x, y, maxWidth, totalHeight, backgroundColor.get());
        }
        if (outlineEnabled.get()) {
            renderOutline(renderer, x, y, maxWidth, totalHeight);
        }

        for (Player player : players) {
            String text = getPlayerText(player, localPos);
            Color textColor = Friends.get().isFriend(player) ? friendColor.get() : playerColor.get();
            renderer.text(text, x, currentY, textColor, false);
            currentY += renderer.textHeight(false) + 2;
        }

        setSize(maxWidth, totalHeight);
    }

    private String getPlayerText(Player player, Vec3 localPos) {
        String name = player.getGameProfile().getName();
        Vec3 targetPos = player.position();

        switch (displayMode.get()) {
            case Coords: {
                int pX = (int) Math.round(targetPos.x);
                int pY = (int) Math.round(targetPos.y);
                int pZ = (int) Math.round(targetPos.z);
                return String.format("%s [%d, %d, %d]", name, pX, pY, pZ);
            }
            case Distance: {
                int distance = (int) localPos.distanceTo(targetPos);
                return String.format("%s (%d blocks)", name, distance);
            }
            case Both:
            default: {
                int pX = (int) Math.round(targetPos.x);
                int pY = (int) Math.round(targetPos.y);
                int pZ = (int) Math.round(targetPos.z);
                int distance = (int) localPos.distanceTo(targetPos);
                return String.format("%s [%d, %d, %d] (%d blocks)", name, pX, pY, pZ, distance);
            }
        }
    }

    private void renderOutline(HudRenderer renderer, double x, double y, double width, double height) {
        Color outline = outlineColor.get();
        renderer.quad(x, y, width, 1, outline);
        renderer.quad(x, y + height - 1, width, 1, outline);
        renderer.quad(x, y, 1, height, outline);
        renderer.quad(x + width - 1, y, 1, height, outline);
    }
}
