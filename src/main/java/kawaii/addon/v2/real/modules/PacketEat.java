package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import kawaii.addon.v2.real.util.SwapUtil;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

public class PacketEat extends Module {

    public enum ThresholdMode {
        Health,
        Hunger,
        Any,
        Both
    }

    public PacketEat() {
        super(KawaiiAddon.CATEGORY, "PacketEat", "Packets are yummy.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgThreshold = settings.createGroup("Threshold");

    private final Setting<ThresholdMode> thresholdMode = sgThreshold.add(new EnumSetting.Builder<ThresholdMode>()
        .name("threshold-mode")
        .description("The threshold mode to trigger auto eat.")
        .defaultValue(ThresholdMode.Any)
        .build()
    );

    private final Setting<Double> healthThreshold = sgThreshold.add(new DoubleSetting.Builder()
        .name("health-threshold")
        .description("The level of health you eat at.")
        .defaultValue(10)
        .range(1, 19)
        .sliderRange(1, 19)
        .visible(() -> thresholdMode.get() != ThresholdMode.Hunger)
        .build()
    );

    private final Setting<Double> hungerThreshold = sgThreshold.add(new DoubleSetting.Builder()
        .name("hunger-threshold")
        .description("The level of hunger you eat at.")
        .defaultValue(16)
        .range(1, 20)
        .sliderRange(1, 20)
        .visible(() -> thresholdMode.get() != ThresholdMode.Health)
        .build()
    );

    private final Setting<Boolean> eatGoldenApple = sgGeneral.add(new BoolSetting.Builder()
        .name("GApple")
        .description("Eat GApple.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> eatEnchantedGoldenApple = sgGeneral.add(new BoolSetting.Builder()
        .name("EGApple")
        .description("Eat EGApple.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> pauseOnCombat = sgGeneral.add(new BoolSetting.Builder()
        .name("combat pause")
        .description("have a break.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> combatCheckDelay = sgGeneral.add(new IntSetting.Builder()
        .name("combat delay")
        .description("Checks?")
        .defaultValue(20)
        .range(1, 100)
        .sliderRange(1, 100)
        .visible(pauseOnCombat::get)
        .build()
    );

    private boolean isEating = false;
    private int eatingSlot = -1;
    private long lastCombatCheck = 0;
    private boolean inCombat = false;
    private int eatingTicks = 0;

    @Override
    public void onDeactivate() {
        if (isEating) {
            stopEating();
        }
        inCombat = false;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.level == null) return;

        if (pauseOnCombat.get() && System.currentTimeMillis() - lastCombatCheck > combatCheckDelay.get() * 50L) {
            inCombat = checkCombat();
            lastCombatCheck = System.currentTimeMillis();
        }

        if (inCombat) {
            if (isEating) stopEating();
            return;
        }

        if (shouldEat() && !isEating) {
            startEating();
        } else if (!shouldEat() && isEating) {
            stopEating();
        }

        if (isEating) {
            eatingTicks++;

            if (!mc.options.keyUse.isDown()) {
                mc.options.keyUse.setDown(true);
            }

            if (mc.player.getFoodData().getFoodLevel() >= 20 || eatingTicks > 100) {
                stopEating();
            }
        }
    }

    private boolean shouldEat() {
        if (mc.player == null) return false;
        if (mc.player.isCreative() || mc.player.isSpectator()) return false;
        if (mc.player.isUsingItem()) return false;

        boolean healthLow = mc.player.getHealth() <= healthThreshold.get();
        boolean hungerLow = mc.player.getFoodData().getFoodLevel() <= hungerThreshold.get();

        return switch (thresholdMode.get()) {
            case Health -> healthLow;
            case Hunger -> hungerLow;
            case Any -> healthLow || hungerLow;
            case Both -> healthLow && hungerLow;
        };
    }

    private void startEating() {
        FindItemResult food = findFood();
        if (!food.found()) return;

        eatingSlot = food.slot();
        eatingTicks = 0;

        SwapUtil.swapSilent(eatingSlot);

        if (mc.getConnection() != null) {
            assert mc.player != null;
            mc.getConnection().send(new ServerboundUseItemPacket(
                InteractionHand.MAIN_HAND,
                mc.player.getId(),
                mc.player.getYRot(),
                mc.player.getXRot()
            ));
        }

        mc.options.keyUse.setDown(true);
        isEating = true;
    }

    private void stopEating() {
        if (mc.player == null) return;
        mc.options.keyUse.setDown(false);
        mc.player.stopUsingItem();

        if (mc.getConnection() != null) {
            mc.getConnection().send(new ServerboundUseItemPacket(
                InteractionHand.MAIN_HAND,
                mc.player.getId(),
                mc.player.getYRot(),
                mc.player.getXRot()
            ));
        }

        if (eatingSlot != -1) {
            SwapUtil.swapBack();
            eatingSlot = -1;
        }

        eatingTicks = 0;
        isEating = false;
    }

    private FindItemResult findFood() {
        if (eatEnchantedGoldenApple.get()) {
            FindItemResult enchantedApple = InvUtils.find(itemStack ->
                itemStack.getItem() == Items.ENCHANTED_GOLDEN_APPLE);
            if (enchantedApple.found()) return enchantedApple;
        }

        if (eatGoldenApple.get()) {
            FindItemResult goldenApple = InvUtils.find(itemStack ->
                itemStack.getItem() == Items.GOLDEN_APPLE);
            if (goldenApple.found()) return goldenApple;
        }

        return InvUtils.find(Utils::isFood);
    }

    private boolean checkCombat() {
        if (mc.player == null) return false;

        long lastDamageTime = mc.player.getLastHurtByMobTimestamp();
        assert mc.level != null;
        long currentTime = mc.level.getGameTime();

        return currentTime - lastDamageTime < 100;
    }
}
