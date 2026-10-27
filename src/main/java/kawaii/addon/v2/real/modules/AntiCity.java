package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import kawaii.addon.v2.real.mixin.MultiPlayerGameModeAccessor;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AntiCity extends Module {

    public enum SwapMode { None, Normal, Silent }

    public enum UpdateMode { None, Place, Interact, Both }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgTargeting = settings.createGroup("Targeting");
    private final SettingGroup sgMining = settings.createGroup("Mining");
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("blocks")
        .defaultValue(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.ENDER_CHEST, Blocks.RESPAWN_ANCHOR, Blocks.BEDROCK)
        .build()
    );

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .defaultValue(5.0)
        .min(1)
        .sliderMax(6)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> strictDirection = sgGeneral.add(new BoolSetting.Builder()
        .name("strict-direction")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> grimDelay = sgGeneral.add(new IntSetting.Builder()
        .name("grim-delay")
        .defaultValue(0)
        .min(0)
        .sliderMax(5)
        .build()
    );

    private final Setting<Double> targetRange = sgTargeting.add(new DoubleSetting.Builder()
        .name("target-range")
        .defaultValue(7.0)
        .min(1)
        .sliderMax(12)
        .build()
    );

    private final Setting<Boolean> ignoreNaked = sgTargeting.add(new BoolSetting.Builder()
        .name("ignore-naked")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> ignoreFriends = sgTargeting.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> burrow = sgTargeting.add(new BoolSetting.Builder()
        .name("burrow")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> surround = sgTargeting.add(new BoolSetting.Builder()
        .name("surround")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> face = sgTargeting.add(new BoolSetting.Builder()
        .name("face")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> head = sgTargeting.add(new BoolSetting.Builder()
        .name("head")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> selfProtect = sgTargeting.add(new BoolSetting.Builder()
        .name("self-protect")
        .defaultValue(true)
        .build()
    );

    private final Setting<SwapMode> swapMode = sgMining.add(new EnumSetting.Builder<SwapMode>()
        .name("swap")
        .defaultValue(SwapMode.Silent)
        .build()
    );

    private final Setting<UpdateMode> updateMode = sgMining.add(new EnumSetting.Builder<UpdateMode>()
        .name("update-mode")
        .defaultValue(UpdateMode.Both)
        .build()
    );

    private final Setting<Double> damage = sgMining.add(new DoubleSetting.Builder()
        .name("damage")
        .defaultValue(1.0)
        .min(0.5)
        .sliderMax(1.0)
        .build()
    );

    private final Setting<Integer> retryDelay = sgMining.add(new IntSetting.Builder()
        .name("retry-delay")
        .defaultValue(5)
        .min(1)
        .sliderMax(20)
        .build()
    );

    private final Setting<Boolean> doubleStart = sgMining.add(new BoolSetting.Builder()
        .name("start-stop")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> rebreak = sgMining.add(new BoolSetting.Builder()
        .name("rebreak")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> instantRebreak = sgMining.add(new BoolSetting.Builder()
        .name("instant-rebreak")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> realBedrockMining = sgMining.add(new BoolSetting.Builder()
        .name("real-bedrock-mining")
        .description("Mines bedrock-id blocks normally with a fixed timer instead of the bedrock exploit. Use for custom blocks that look like bedrock but are breakable.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> realBedrockTicks = sgMining.add(new IntSetting.Builder()
        .name("real-bedrock-ticks")
        .description("Ticks to wait before sending the stop packet. Must match the server's custom block hardness.")
        .defaultValue(20)
        .min(1)
        .sliderMax(200)
        .visible(realBedrockMining::get)
        .build()
    );

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .defaultValue(ShapeMode.Both)
        .visible(render::get)
        .build()
    );

    private final Setting<SettingColor> startColor = sgRender.add(new ColorSetting.Builder()
        .name("start-color")
        .defaultValue(new SettingColor(255, 40, 40, 60))
        .visible(render::get)
        .build()
    );

    private final Setting<SettingColor> endColor = sgRender.add(new ColorSetting.Builder()
        .name("end-color")
        .defaultValue(new SettingColor(40, 255, 40, 60))
        .visible(render::get)
        .build()
    );

    private BlockPos miningPos;
    private BlockPos pendingBreak;
    private Direction miningDir = Direction.UP;
    private double progress;
    private int startDelay;
    private int sinceSend;
    private boolean startPending;
    private boolean primed;
    private boolean wasAir;
    private boolean bedrockTarget;

    public AntiCity() {
        super(KawaiiAddon.CATEGORY, "anti-city", "Mines blocks around enemies (surround, burrow, trap) using silent packet mining.");
    }

    @Override
    public void onActivate() {
        reset();
    }

    @Override
    public void onDeactivate() {
        reset();
    }

    @Override
    public String getInfoString() {
        return miningPos == null ? null : String.format("%.2f", Mth.clamp(progress, 0, 1));
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;
        if (mc.player.isCreative() || mc.player.isSpectator()) return;

        if (pendingBreak != null) {
            BlockPos p = pendingBreak;
            pendingBreak = null;
            sendAction(Action.START_DESTROY_BLOCK, p, Direction.UP);
            sendAction(Action.STOP_DESTROY_BLOCK, p, Direction.UP);
        }

        if (sinceSend < 1000) sinceSend++;

        if (miningPos == null) {
            BlockPos target = findTarget();
            if (target != null) beginMining(target);
            return;
        }

        if (mc.player.getEyePosition().distanceToSqr(Vec3.atCenterOf(miningPos)) > range.get() * range.get()) {
            abort();
            return;
        }

        if (startPending) {
            if (startDelay-- <= 0) {
                startPending = false;
                sendStart();
            }
            return;
        }

        BlockState state = mc.level.getBlockState(miningPos);

        if (state.isAir()) {
            wasAir = true;
            if (!(instantRebreak.get() && primed)) progress = 0;
            if (!rebreak.get() || bedrockTarget) miningPos = null;
            return;
        }

        if (!blocks.get().contains(state.getBlock())) {
            abort();
            return;
        }

        int slot = bestSlot(state);

        if (instantRebreak.get() && primed) {
            progress = damage.get();
            if (wasAir || sinceSend >= retryDelay.get()) {
                wasAir = false;
                finishMining(slot);
            }
            return;
        }

        if (bedrockTarget) {
            progress = damage.get();
        } else if (state.is(Blocks.BEDROCK)) {
            progress += damage.get() / realBedrockTicks.get();
        } else {
            progress += BlockUtils.getBreakDelta(slot, state);
        }

        if (progress >= damage.get() && sinceSend >= retryDelay.get()) finishMining(slot);
    }

    private void beginMining(BlockPos pos) {
        miningPos = pos;
        miningDir = getDirection(pos);
        progress = 0;
        primed = false;
        wasAir = false;
        sinceSend = 1000;
        assert mc.level != null;
        bedrockTarget = mc.level.getBlockState(pos).is(Blocks.BEDROCK) && !realBedrockMining.get();

        if (grimDelay.get() > 0) {
            startPending = true;
            startDelay = grimDelay.get();
        } else {
            sendStart();
        }
    }

    private void sendStart() {
        if (miningPos == null) return;
        BlockPos pos = miningPos;
        Direction dir = miningDir;

        Runnable action = () -> {
            sendAction(Action.START_DESTROY_BLOCK, pos, dir);
            if (doubleStart.get() || bedrockTarget) sendAction(Action.STOP_DESTROY_BLOCK, pos, dir);
            if (bedrockTarget) triggerUpdate(pos);
        };
        withRotation(pos, action);
        sinceSend = 0;
    }

    private void finishMining(int slot) {
        BlockPos pos = miningPos;
        Direction dir = miningDir;

        if (bedrockTarget) {
            withRotation(pos, () -> {
                sendAction(Action.START_DESTROY_BLOCK, pos, dir);
                sendAction(Action.STOP_DESTROY_BLOCK, pos, dir);
                triggerUpdate(pos);
            });
            sinceSend = 0;
            return;
        }

        Runnable action = () -> {
            assert mc.player != null;
            int previous = mc.player.getInventory().getSelectedSlot();
            boolean swapped = slot != previous && swapMode.get() != SwapMode.None;

            if (swapped) InvUtils.swap(slot, swapMode.get() == SwapMode.Silent);

            sendAction(Action.STOP_DESTROY_BLOCK, pos, dir);

            if (swapped && swapMode.get() == SwapMode.Silent) InvUtils.swapBack();
        };
        withRotation(pos, action);
        sinceSend = 0;

        if (rebreak.get()) {
            primed = true;
            if (!instantRebreak.get()) {
                progress = 0;
                sendAction(Action.START_DESTROY_BLOCK, pos, dir);
            }
        } else {
            progress = 0;
            miningPos = null;
        }
    }

    private void triggerUpdate(BlockPos pos) {
        UpdateMode mode = updateMode.get();
        if (mode == UpdateMode.None) return;
        if (mode == UpdateMode.Interact || mode == UpdateMode.Both) interactNear(pos);
        if (mode == UpdateMode.Place || mode == UpdateMode.Both) placeNear(pos);
    }

    private void interactNear(BlockPos pos) {
        assert mc.level != null && mc.player != null;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    BlockPos p = pos.offset(x, y, z);
                    Block b = mc.level.getBlockState(p).getBlock();
                    if (b == Blocks.IRON_DOOR || b == Blocks.IRON_TRAPDOOR) continue;
                    boolean interactable = b instanceof DoorBlock || b instanceof TrapDoorBlock
                        || b instanceof FenceGateBlock || b instanceof ButtonBlock
                        || b instanceof LeverBlock || b instanceof NoteBlock;
                    if (!interactable) continue;
                    if (mc.player.getEyePosition().distanceToSqr(Vec3.atCenterOf(p)) > range.get() * range.get()) continue;

                    BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(p), Direction.UP, p, false);
                    assert mc.gameMode != null;
                    ((MultiPlayerGameModeAccessor) mc.gameMode).kawaii$startPrediction(mc.level, sequence -> new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, hit, sequence));
                    return;
                }
            }
        }
    }

    private void placeNear(BlockPos pos) {
        assert mc.level != null && mc.player != null;

        FindItemResult item = InvUtils.findInHotbar(Items.REDSTONE_TORCH, Items.TORCH);
        if (!item.found()) item = InvUtils.findInHotbar(stack -> stack.getItem() instanceof BlockItem);
        if (!item.found()) return;

        BlockPos me = mc.player.blockPosition();
        List<Direction> order = new ArrayList<>();
        for (Direction d : Direction.Plane.HORIZONTAL) order.add(d);
        order.add(Direction.UP);
        order.add(Direction.DOWN);

        for (Direction d : order) {
            BlockPos target = pos.relative(d);
            if (!mc.level.getBlockState(target).canBeReplaced()) continue;
            if (target.equals(me) || target.equals(me.above())) continue;

            Vec3 loc = Vec3.atCenterOf(pos).add(d.getStepX() * 0.5, d.getStepY() * 0.5, d.getStepZ() * 0.5);
            BlockHitResult hit = new BlockHitResult(loc, d, pos, false);

            InvUtils.swap(item.slot(), true);
            assert mc.gameMode != null;
            ((MultiPlayerGameModeAccessor) mc.gameMode).kawaii$startPrediction(mc.level, sequence -> new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, hit, sequence));
            InvUtils.swapBack();

            pendingBreak = target;
            return;
        }
    }

    private void abort() {
        if (miningPos != null && !startPending) {
            sendAction(Action.ABORT_DESTROY_BLOCK, miningPos, miningDir);
        }
        reset();
    }

    private void reset() {
        miningPos = null;
        pendingBreak = null;
        progress = 0;
        startPending = false;
        startDelay = 0;
        primed = false;
        wasAir = false;
        bedrockTarget = false;
        sinceSend = 1000;
    }

    private void sendAction(Action action, BlockPos pos, Direction dir) {
        if (mc.gameMode == null || mc.level == null) return;
        ((MultiPlayerGameModeAccessor) mc.gameMode).kawaii$startPrediction(mc.level, sequence -> new ServerboundPlayerActionPacket(action, pos, dir, sequence));
    }

    private void withRotation(BlockPos pos, Runnable action) {
        if (rotate.get()) {
            Vec3 c = Vec3.atCenterOf(pos);
            Rotations.rotate(Rotations.getYaw(c), Rotations.getPitch(c), 100, action);
        } else {
            action.run();
        }
    }

    private int bestSlot(BlockState state) {
        assert mc.player != null;
        int held = mc.player.getInventory().getSelectedSlot();
        if (swapMode.get() == SwapMode.None) return held;

        int best = held;
        double bestDelta = BlockUtils.getBreakDelta(held, state);
        for (int i = 0; i < 9; i++) {
            double d = BlockUtils.getBreakDelta(i, state);
            if (d > bestDelta) {
                bestDelta = d;
                best = i;
            }
        }
        return best;
    }

    private Direction getDirection(BlockPos pos) {
        if (!strictDirection.get()) return Direction.UP;
        assert mc.player != null;
        Vec3 eye = mc.player.getEyePosition();
        Vec3 c = Vec3.atCenterOf(pos);
        return Direction.getApproximateNearest(eye.x - c.x, eye.y - c.y, eye.z - c.z);
    }

    private BlockPos findTarget() {
        Player best = null;
        double bestDist = Double.MAX_VALUE;

        assert mc.level != null;
        for (Player player : mc.level.players()) {
            if (player == mc.player || player.isDeadOrDying() || player.isSpectator()) continue;
            if (ignoreFriends.get() && Friends.get().isFriend(player)) continue;
            if (ignoreNaked.get() && isNaked(player)) continue;

            assert mc.player != null;
            double dist = mc.player.distanceToSqr(player);
            if (dist > targetRange.get() * targetRange.get()) continue;

            if (dist < bestDist) {
                bestDist = dist;
                best = player;
            }
        }
        if (best == null) return null;

        for (BlockPos pos : candidates(best)) {
            if (isMineable(pos)) return pos;
        }
        return null;
    }

    private List<BlockPos> candidates(Player target) {
        List<BlockPos> list = new ArrayList<>();
        BlockPos feet = target.blockPosition();

        if (burrow.get()) list.add(feet);

        if (surround.get()) addSides(list, feet);
        if (face.get()) addSides(list, feet.above());
        if (head.get()) list.add(feet.above(2));

        return list;
    }

    private void addSides(List<BlockPos> list, BlockPos origin) {
        List<BlockPos> sides = new ArrayList<>(4);
        for (Direction d : Direction.Plane.HORIZONTAL) sides.add(origin.relative(d));
        assert mc.player != null;
        Vec3 eye = mc.player.getEyePosition();
        sides.sort(Comparator.comparingDouble(a -> eye.distanceToSqr(Vec3.atCenterOf(a))));
        list.addAll(sides);
    }

    private boolean isMineable(BlockPos pos) {
        assert mc.level != null;
        BlockState state = mc.level.getBlockState(pos);
        if (state.isAir() || !blocks.get().contains(state.getBlock())) return false;
        if (state.getDestroySpeed(mc.level, pos) < 0 && !state.is(Blocks.BEDROCK)) return false;

        assert mc.player != null;
        if (mc.player.getEyePosition().distanceToSqr(Vec3.atCenterOf(pos)) > range.get() * range.get()) return false;

        if (selfProtect.get()) {
            BlockPos me = mc.player.blockPosition();
            if (pos.equals(me) || pos.equals(me.above()) || pos.equals(me.above(2))) return false;
            for (Direction d : Direction.Plane.HORIZONTAL) {
                if (pos.equals(me.relative(d)) || pos.equals(me.above().relative(d))) return false;
            }
        }
        return true;
    }

    private boolean isNaked(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
            && player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()
            && player.getItemBySlot(EquipmentSlot.LEGS).isEmpty()
            && player.getItemBySlot(EquipmentSlot.FEET).isEmpty();
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (!render.get() || miningPos == null || mc.level == null) return;

        float t = (float) Mth.clamp(progress / damage.get(), 0, 1);
        SettingColor a = startColor.get();
        SettingColor b = endColor.get();

        Color side = new Color(
            (int) Mth.lerp(t, a.r, b.r), (int) Mth.lerp(t, a.g, b.g),
            (int) Mth.lerp(t, a.b, b.b), (int) Mth.lerp(t, a.a, b.a));
        Color line = new Color(side.r, side.g, side.b, 255);

        event.renderer.box(miningPos, side, line, shapeMode.get(), 0);
    }
}
