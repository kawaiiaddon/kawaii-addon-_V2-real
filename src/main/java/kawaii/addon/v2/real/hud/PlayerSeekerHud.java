package kawaii.addon.v2.real.hud;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayerSeekerHud extends HudElement {

    public static final HudElementInfo<PlayerSeekerHud> INFO = new HudElementInfo<>(KawaiiAddon.HUD_GROUP, "PlayerSeeker", "Displays info about nearby players.", PlayerSeekerHud::new);
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
            renderer.text("No players nearby", x, y, Color.WHITE, false);
            setSize(renderer.textWidth("No players nearby", false), renderer.textHeight(false));
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
            renderer.text(text, x, currentY, Color.WHITE, false);

            maxWidth = Math.max(maxWidth, renderer.textWidth(text, false));
            currentY += renderer.textHeight(false) + 2;
        }
        setSize(maxWidth, currentY - y);
    }
}
