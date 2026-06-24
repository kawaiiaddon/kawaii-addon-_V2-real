package kawaii.addon.v2.real.util;

import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class SwapUtil {

    private static int savedSlot = -1;

    public static int findInHotbar(TagKey<Item> tag) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getItem(i).is(tag)) return i;
        }
        return -1;
    }

    public static int findInHotbar(Item item) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getItem(i).getItem() == item) return i;
        }
        return -1;
    }

    public static void swapSilent(int slot) {
        if (slot == -1) return;
        savedSlot = mc.player.getInventory().getSelectedSlot();
        mc.getConnection().send(new ServerboundSetCarriedItemPacket(slot));
    }

    public static void swapBack() {
        if (savedSlot == -1) return;
        mc.getConnection().send(new ServerboundSetCarriedItemPacket(savedSlot));
        savedSlot = -1;
    }

    public static void swapNormal(int slot) {
        if (slot == -1) return;
        savedSlot = mc.player.getInventory().getSelectedSlot();
        mc.player.getInventory().setSelectedSlot(slot);
        mc.getConnection().send(new ServerboundSetCarriedItemPacket(slot));
    }

    public static void swapBackNormal() {
        if (savedSlot == -1) return;
        mc.player.getInventory().setSelectedSlot(savedSlot);
        mc.getConnection().send(new ServerboundSetCarriedItemPacket(savedSlot));
        savedSlot = -1;
    }

    public static int getSavedSlot() {
        return savedSlot;
    }

    public static boolean isSwapped() {
        return savedSlot != -1;
    }
}
