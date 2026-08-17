package kawaii.addon.v2.real;

import kawaii.addon.v2.real.commands.*;
import kawaii.addon.v2.real.hud.*;
import kawaii.addon.v2.real.modules.*;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;

public class KawaiiAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Kawaii", Items.PINK_TULIP::getDefaultInstance);
    public static final HudGroup HUD_GROUP = new HudGroup("Kawaii");

    @Override
    public void onInitialize() {
        LOG.info("Loading kawaii-addon-_V2-real...");

        // Modules
        Modules.get().add(new ChinaHat());
        Modules.get().add(new CatFacts());
        Modules.get().add(new HeadPatTurn());
        Modules.get().add(new Cape());
        Modules.get().add(new MapCensor());
        Modules.get().add(new Troll());
        Modules.get().add(new RockBreaker());
        Modules.get().add(new CoordSpoofer());
        Modules.get().add(new AntiWeb());
        Modules.get().add(new SwordSwap());
        Modules.get().add(new OnDeathSFX());
        Modules.get().add(new ActionBarCensor());
        //todo: make AntiPearlPhase

        // Commands
        Commands.add(new Cuddle());
        Commands.add(new CrashOut());
        Commands.add(new Dupe());
        Commands.add(new Rat());
        Commands.add(new FriendSync());

        // HUD
        Hud.get().register(Cat.INFO);
        Hud.get().register(WaterMark.INFO);
        Hud.get().register(PlayerSeekerHud.INFO);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "kawaii.addon.v2.real";
    }
}
