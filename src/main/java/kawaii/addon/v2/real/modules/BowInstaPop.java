package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BowItem;
// claude fix pls
public class BowInstaPop extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> charge = sgGeneral.add(new IntSetting.Builder()
        .name("charge-ticks")
        .defaultValue(5)
        .range(3, 20)
        .sliderRange(3, 20)
        .build()
    );

    public BowInstaPop() {
        super(KawaiiAddon.CATEGORY, "BowInstaPop", "pops people using a bow or smth idk.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.gameMode == null) return;
        if (!mc.options.keyUse.isDown()) return;

        if (!mc.player.isUsingItem()) return;
        if (!(mc.player.getUseItem().getItem() instanceof BowItem)) return;
        if (mc.player.getTicksUsingItem() < charge.get()) return;

        InteractionHand hand = mc.player.getUsedItemHand();
        mc.gameMode.releaseUsingItem(mc.player);
        mc.gameMode.useItem(mc.player, hand);
    }
}
