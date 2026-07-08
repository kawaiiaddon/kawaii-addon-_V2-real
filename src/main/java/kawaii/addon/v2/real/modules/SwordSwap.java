package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import kawaii.addon.v2.real.util.SwapUtil;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.tags.ItemTags;

public class SwordSwap extends Module {

    public enum SwapMode {
        Normal,
        Silent,
        GhostHand
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<SwapMode> mode = sgGeneral.add(new EnumSetting.Builder<SwapMode>()
        .name("mode")
        .description("Swap mode.")
        .defaultValue(SwapMode.GhostHand)
        .build()
    );

    private boolean swapped = false;
    private int lastSlot = -1;
    private int swordSlot = -1;
    private int realSlot = -1;

    private int useTicks = 0;
    private static final int USE_DURATION = 4;

    public SwordSwap() {
        super(KawaiiAddon.CATEGORY, "SwordSwap", "Auto-switches to sword in hotbar.");
    }

    @Override
    public void onDeactivate() {
        restore();
        swapped = false;
        lastSlot = -1;
        swordSlot = -1;
        realSlot = -1;
        useTicks = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.getConnection() == null) return;

        int currentSlot = mc.player.getInventory().selected;
        swordSlot = InvUtils.findInHotbar(stack -> stack.is(ItemTags.SWORDS)).slot();

        if (swordSlot == -1) {
            if (swapped) restore();
            swapped = false;
            lastSlot = currentSlot;
            return;
        }

        if (useTicks > 0) {
            useTicks--;
            if (useTicks == 0) {
                mc.getConnection().send(new ServerboundSetCarriedItemPacket(swordSlot));
                realSlot = -1;
            }
            lastSlot = currentSlot;
            return;
        }

        switch (mode.get()) {
            case GhostHand -> handleGhostHand(currentSlot);
            case Silent    -> handleSilent(currentSlot);
            case Normal    -> handleNormal(currentSlot);
        }
    }

    private void handleGhostHand(int currentSlot) {
        if (!swapped) {
            lastSlot = currentSlot;
            SwapUtil.swapSilent(swordSlot);
            swapped = true;
            return;
        }

        if (currentSlot != lastSlot) {
            if (useTicks == 0) {
                mc.getConnection().send(new ServerboundSetCarriedItemPacket(swordSlot));
            }
            lastSlot = currentSlot;
        }
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (mode.get() != SwapMode.GhostHand || !swapped) return;
        if (mc.player == null || mc.getConnection() == null) return;

        int currentSlot = mc.player.getInventory().selected;

        if (currentSlot == swordSlot) return;

        boolean isUsing = event.packet instanceof ServerboundUseItemPacket
            || event.packet instanceof ServerboundUseItemOnPacket
            || event.packet instanceof ServerboundPlayerActionPacket;

        if (isUsing && useTicks == 0) {
            realSlot = currentSlot;
            mc.getConnection().send(new ServerboundSetCarriedItemPacket(realSlot));
            useTicks = USE_DURATION;
        }
    }

    private void handleSilent(int currentSlot) {
        if (swapped && currentSlot != lastSlot) {
            restore();
            swapped = false;
            lastSlot = currentSlot;
            return;
        }

        if (!swapped) {
            lastSlot = currentSlot;
            SwapUtil.swapSilent(swordSlot);
            swapped = true;
        }
    }

    private void handleNormal(int currentSlot) {
        if (currentSlot != swordSlot) {
            if (swapped) restore();
            SwapUtil.swapNormal(swordSlot);
            swapped = true;
            lastSlot = swordSlot;
            return;
        }

        if (!swapped) {
            lastSlot = currentSlot;
            swapped = true;
        }
    }

    private void restore() {
        if (!swapped) return;

        if (mode.get() == SwapMode.Normal) {
            SwapUtil.swapBackNormal();
        } else {
            SwapUtil.swapBack();
        }
        swapped = false;
    }
}
