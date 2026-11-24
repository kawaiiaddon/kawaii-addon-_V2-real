package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import kawaii.addon.v2.real.util.SwapUtil;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.*;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.*;

public class BlockTrap extends Module {

    public BlockTrap() {
        super(KawaiiAddon.CATEGORY, "BlockTrap", "Places obsidian blocks on chests.");
    }

    private final SettingGroup sg = settings.getDefaultGroup();
    private final SettingGroup sgExtra = settings.createGroup("Extra");

    private final Setting<Double> range = sg.add(new DoubleSetting.Builder()
        .name("range")
        .defaultValue(4.0)
        .min(0.1)
        .sliderMax(4.5)
        .build()
    );

    private final Setting<SwapMode> swapMode = sg.add(new EnumSetting.Builder<SwapMode>()
        .name("swap-mode")
        .defaultValue(SwapMode.Normal)
        .build()
    );

    private final Setting<Integer> placeDelay = sg.add(new IntSetting.Builder()
        .name("place-delay")
        .defaultValue(1)
        .min(0)
        .sliderMax(10)
        .build()
    );

    private final Setting<Boolean> chests = sg.add(new BoolSetting.Builder()
        .name("Chests")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> enderChests = sg.add(new BoolSetting.Builder()
        .name("EnderChests")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> trappedChests = sg.add(new BoolSetting.Builder()
        .name("TrappedChests")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> copperChests = sg.add(new BoolSetting.Builder()
        .name("CopperChests")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> rotate = sgExtra.add(new BoolSetting.Builder()
        .name("rotate")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> render = sgExtra.add(new BoolSetting.Builder()
        .name("render")
        .defaultValue(true)
        .build()
    );

    public enum SwapMode {
        Normal,
        Silent
    }

    private static final Color SIDE_COLOR = new Color(160, 80, 255, 40);
    private static final Color LINE_COLOR = new Color(160, 80, 255, 255);
    private static final long RENDER_TIME_MS = 500;
    private static final int ROTATION_PRIORITY = 50;

    private static class RenderBlock {
        final BlockPos pos;
        final long start;

        RenderBlock(BlockPos pos, long start) {
            this.pos = pos;
            this.start = start;
        }
    }

    private final List<RenderBlock> renderBlocks = new ArrayList<>();

    private int timer = 0;
    private boolean normalSwapped = false;

    @Override
    public void onActivate() {
        timer = 0;
        normalSwapped = false;
        renderBlocks.clear();
    }

    @Override
    public void onDeactivate() {
        finishNormalSwap();
        renderBlocks.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;

        if (timer > 0) {
            timer--;
            return;
        }

        int slot = SwapUtil.findInHotbar(Items.OBSIDIAN);
        BlockHitResult hit = slot == -1 ? null : findPlacement();

        if (hit == null) {
            finishNormalSwap();
            return;
        }

        if (swapMode.get() == SwapMode.Normal && !normalSwapped) {
            SwapUtil.swapNormal(slot);
            normalSwapped = true;
        }

        Runnable action = () -> {
            if (swapMode.get() == SwapMode.Silent) {
                SwapUtil.swapSilent(slot);
                place(hit);
                SwapUtil.swapBack();
            } else {
                place(hit);
            }
        };

        if (rotate.get()) {
            Vec3 loc = hit.getLocation();
            Rotations.rotate(Rotations.getYaw(loc), Rotations.getPitch(loc), ROTATION_PRIORITY, action);
        } else {
            action.run();
        }

        timer = placeDelay.get();
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (!render.get() || renderBlocks.isEmpty()) return;

        long now = System.currentTimeMillis();

        Iterator<RenderBlock> it = renderBlocks.iterator();
        while (it.hasNext()) {
            RenderBlock rb = it.next();
            long age = now - rb.start;

            if (age >= RENDER_TIME_MS) {
                it.remove();
                continue;
            }

            double alpha = 1.0 - (double) age / RENDER_TIME_MS;

            Color side = new Color(SIDE_COLOR.r, SIDE_COLOR.g, SIDE_COLOR.b, (int) (SIDE_COLOR.a * alpha));
            Color line = new Color(LINE_COLOR.r, LINE_COLOR.g, LINE_COLOR.b, (int) (LINE_COLOR.a * alpha));

            event.renderer.box(rb.pos, side, line, ShapeMode.Both, 0);
        }
    }

    private void finishNormalSwap() {
        if (!normalSwapped) return;
        normalSwapped = false;
        if (mc.player != null) SwapUtil.swapBackNormal();
    }

    private void place(BlockHitResult hit) {
        assert mc.player != null;
        assert mc.gameMode != null;

        Input real = mc.player.input.keyPresses;
        sendInput(new Input(real.forward(), real.backward(), real.left(), real.right(), real.jump(), true, real.sprint()));

        mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hit);

        sendInput(real);

        if (render.get()) {
            BlockPos placed = hit.getBlockPos().relative(hit.getDirection());
            renderBlocks.add(new RenderBlock(placed, System.currentTimeMillis()));
        }
    }

    private BlockHitResult findPlacement() {
        for (BlockPos chestPos : findChest()) {
            BlockHitResult hit = getPlaceHit(chestPos.above());
            if (hit != null) return hit;
        }
        return null;
    }

    private BlockHitResult getPlaceHit(BlockPos target) {
        assert mc.level != null;
        assert mc.player != null;

        if (!mc.level.getBlockState(target).canBeReplaced()) return null;
        if (!mc.level.isUnobstructed(Blocks.OBSIDIAN.defaultBlockState(), target, CollisionContext.empty())) return null;

        Vec3 eye = mc.player.getEyePosition();

        for (Direction dir : Direction.values()) {
            BlockPos neighbor = target.relative(dir);
            BlockState state = mc.level.getBlockState(neighbor);
            if (state.isAir() || state.canBeReplaced()) continue;

            Direction face = dir.getOpposite();
            Vec3 hitVec = Vec3.atCenterOf(neighbor).add(
                face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5
            );

            if (eye.distanceTo(hitVec) > range.get()) continue;

            return new BlockHitResult(hitVec, face, neighbor, false);
        }

        return null;
    }

    private void sendInput(Input input) {
        Objects.requireNonNull(mc.getConnection()).send(new ServerboundPlayerInputPacket(input));
    }

    private boolean isTargetBlock(BlockState state) {
        Block block = state.getBlock();
        if (chests.get() && block == Blocks.CHEST) return true;
        if (enderChests.get() && block == Blocks.ENDER_CHEST) return true;
        if (trappedChests.get() && block == Blocks.TRAPPED_CHEST) return true;
        if (copperChests.get() && block instanceof CopperChestBlock) return true;
        return false;
    }

    private List<BlockPos> findChest() {
        List<BlockPos> chest = new ArrayList<>();
        double r = range.get();

        assert mc.player != null;
        assert mc.level != null;

        AABB bodyBox = mc.player.getBoundingBox().inflate(r);
        AABB headBox = new AABB(
            mc.player.getX() - r, mc.player.getEyeY() - r, mc.player.getZ() - r,
            mc.player.getX() + r, mc.player.getEyeY() + r, mc.player.getZ() + r
        );

        BlockPos.betweenClosedStream(bodyBox.minmax(headBox))
            .filter(pos -> isTargetBlock(mc.level.getBlockState(pos)))
            .forEach(pos -> chest.add(pos.immutable()));

        List<BlockPos> occupied = List.of(
            BlockPos.containing(mc.player.getX(), mc.player.getY(), mc.player.getZ()),
            BlockPos.containing(mc.player.getX(), mc.player.getY() + 0.5, mc.player.getZ()),
            BlockPos.containing(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ())
        );

        for (BlockPos pos : occupied) {
            if (isTargetBlock(mc.level.getBlockState(pos)) && !chest.contains(pos)) {
                chest.add(pos);
            }
        }

        return chest;
    }
}
