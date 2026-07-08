package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.resources.ResourceLocation;

public class MapCensor extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public MapCensor() {
        super(KawaiiAddon.CATEGORY, "map-censor", "Replaces maps with a picture.");
    }

    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("picture")
        .description("Which image to display on the map.")
        .defaultValue(Mode.pileton)
        .build()
    );

    public enum Mode {
        Rem, rip, punkalopi, pileton, catgirl, smoke
    }

    public ResourceLocation getTexture() {
        return switch (mode.get()) {
            case catgirl -> ResourceLocation.fromNamespaceAndPath("kawaii-addon", "censor/catgirl.png");
            case pileton -> ResourceLocation.fromNamespaceAndPath("kawaii-addon", "censor/pileton.png");
            case punkalopi -> ResourceLocation.fromNamespaceAndPath("kawaii-addon", "censor/punkalopi.png");
            case rip -> ResourceLocation.fromNamespaceAndPath("kawaii-addon", "censor/rip.png");
            case Rem -> ResourceLocation.fromNamespaceAndPath("kawaii-addon", "censor/rem.png");
            case smoke -> ResourceLocation.fromNamespaceAndPath("kawaii-addon", "censor/smoke.png");
        };
    }
}
