package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import kawaii.addon.v2.real.util.SwapUtil;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.*;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import java.util.*;

public class AntiWeb extends Module {

    public enum SwapMode {
        Normal,
        Silent,
        None
    }

    private static final int ROTATION_PRIORITY = 50;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<SwapMode> swapMode = sgGeneral.add(new EnumSetting.Builder<SwapMode>()
        .name("swap-mode")
        .description("300 iq swap code.")
        .defaultValue(SwapMode.Normal)
        .build()
    );

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("are we there yet?")
        .defaultValue(0.5)
        .min(0.1)
        .sliderMax(2.0)
        .build()
    );

    private final Setting<Double> breakThreshold = sgGeneral.add(new DoubleSetting.Builder()
        .name("break-threshold")
        .description("Progress before done.")
        .defaultValue(0.72)
        .min(0.5)
        .sliderMax(1.0)
        .build()
    );

    private final Setting<Double> RETRY_COOLDOWN = sgGeneral.add(new DoubleSetting.Builder()
        .name("retry-cooldown")
        .description("Retries amount.")
        .defaultValue(10)
        .min(1)
        .sliderMax(20)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> notify = sgGeneral.add(new BoolSetting.Builder()
        .name("notify")
        .description("snitches.")
        .defaultValue(false)
        .build()
    );

    private final Map<BlockPos, Integer> minedCooldowns = new HashMap<>();
    private boolean swapped = false;

    private BlockPos target = null;
    private Direction targetFace = Direction.UP;
    private int elapsed = 0;

    public AntiWeb() {
        super(KawaiiAddon.CATEGORY, "AntiWeb", "funny packet mine module because NoSlow not work for some reason on servers that use NCP ac fucking retards.");
    }

    @Override
    public void onDeactivate() {
        minedCooldowns.clear();
        target = null;
        elapsed = 0;
        restoreIfSwapped();
    }

    @SuppressWarnings("unused")
    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.level == null || mc.getConnection() == null) return;

        minedCooldowns.replaceAll((_, ticks) -> ticks - 1);
        minedCooldowns.entrySet().removeIf(e -> e.getValue() <= 0);

        if (target != null && !isWeb(target)) {
            target = null;
            elapsed = 0;
        }

        if (target == null) {
            List<BlockPos> webs = findWebs();
            webs.removeIf(minedCooldowns::containsKey);

            if (webs.isEmpty()) {
                restoreIfSwapped();
                return;
            }

            Vec3 eye = mc.player.getEyePosition();
            webs.sort(Comparator.comparingDouble(p -> Vec3.atCenterOf(p).distanceToSqr(eye)));

            target = webs.get(0);
            targetFace = getClosestFace(target);
            elapsed = 0;

            int swordSlot = SwapUtil.findInHotbar(ItemTags.SWORDS);
            if (swapMode.get() != SwapMode.None && swordSlot != -1 && !swapped) {
                if (swapMode.get() == SwapMode.Silent) SwapUtil.swapSilent(swordSlot);
                else SwapUtil.swapNormal(swordSlot);
                swapped = true;
            }

            BlockPos pos = target;
            Direction face = targetFace;
            runMaybeRotated(pos, () -> sendAction(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, pos, face));
        } else {
            elapsed++;
        }

        float perTick = getProgressPerTick(target);
        if (perTick > 0 && perTick * (elapsed + 1) >= breakThreshold.get()) {
            BlockPos pos = target;
            Direction face = targetFace;
            runMaybeRotated(pos, () -> sendAction(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, pos, face));

            minedCooldowns.put(pos, RETRY_COOLDOWN.get().intValue());
            target = null;
            elapsed = 0;

            if (notify.get()) info("Broke cobweb.");
        }
    }

    private void runMaybeRotated(BlockPos pos, Runnable action) {
        if (rotate.get()) {
            Vec3 t = Vec3.atCenterOf(pos);
            Rotations.rotate(Rotations.getYaw(t), Rotations.getPitch(t), ROTATION_PRIORITY, action);
        } else {
            action.run();
        }
    }

    private void sendAction(ServerboundPlayerActionPacket.Action action, BlockPos pos, Direction face) {
        if (mc.getConnection() == null) return;
        mc.getConnection().send(new ServerboundPlayerActionPacket(action, pos, face, 0));
    }

    private float getProgressPerTick(BlockPos pos) {
        if (mc.player == null || mc.level == null) return 0;

        int swordSlot = SwapUtil.findInHotbar(ItemTags.SWORDS);
        boolean useSword = swapMode.get() != SwapMode.None && swordSlot != -1;

        var inv = mc.player.getInventory();
        int old = inv.getSelectedSlot();
        if (useSword) inv.setSelectedSlot(swordSlot);
        float p = mc.level.getBlockState(pos).getDestroyProgress(mc.player, mc.level, pos);
        if (useSword) inv.setSelectedSlot(old);
        return p;
    }

    private boolean isWeb(BlockPos pos) {
        return mc.level != null && mc.level.getBlockState(pos).getBlock() == Blocks.COBWEB;
    }

    private List<BlockPos> findWebs() {
        List<BlockPos> webs = new ArrayList<>();
        double r = range.get();

        assert mc.player != null;
        AABB bodyBox = mc.player.getBoundingBox().inflate(r);
        AABB headBox = new AABB(
            mc.player.getX() - r, mc.player.getEyeY() - r, mc.player.getZ() - r,
            mc.player.getX() + r, mc.player.getEyeY() + r, mc.player.getZ() + r
        );

        List<BlockPos> occupied = List.of(
            BlockPos.containing(mc.player.getX(), mc.player.getY(), mc.player.getZ()),
            BlockPos.containing(mc.player.getX(), mc.player.getY() + 0.5, mc.player.getZ()),
            BlockPos.containing(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ())
        );

        BlockPos.betweenClosedStream(bodyBox.minmax(headBox))
            .filter(this::isWeb)
            .forEach(pos -> webs.add(pos.immutable()));

        for (BlockPos pos : occupied) {
            if (isWeb(pos) && !webs.contains(pos)) webs.add(pos);
        }

        return webs;
    }

    private void restoreIfSwapped() {
        if (!swapped) return;
        if (swapMode.get() == SwapMode.Silent) {
            SwapUtil.swapBack();
        } else {
            SwapUtil.swapBackNormal();
        }
        swapped = false;
    }

    private Direction getClosestFace(BlockPos pos) {
        assert mc.player != null;
        double dx = mc.player.getX()    - (pos.getX() + 0.5);
        double dy = mc.player.getEyeY() - (pos.getY() + 0.5);
        double dz = mc.player.getZ()    - (pos.getZ() + 0.5);
        double ax = Math.abs(dx), ay = Math.abs(dy), az = Math.abs(dz);
        if (ax > ay && ax > az) return dx > 0 ? Direction.EAST  : Direction.WEST;
        if (ay > ax && ay > az) return dy > 0 ? Direction.UP    : Direction.DOWN;
        return dz > 0 ? Direction.SOUTH : Direction.NORTH;
    }
}
