package kawaii.addon.v2.real.modules;

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.resources.Identifier;

import static kawaii.addon.v2.real.util.FilePath.space;

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
        Rem, rip, punkalopi, pileton, catgirl, smoke, icon
    }

    public Identifier getTexture() {
        return switch (mode.get()) {
            case catgirl -> Identifier.fromNamespaceAndPath(space, "censor/catgirl.png");
            case pileton -> Identifier.fromNamespaceAndPath(space, "censor/pileton.png");
            case punkalopi -> Identifier.fromNamespaceAndPath(space, "censor/punkalopi.png");
            case rip -> Identifier.fromNamespaceAndPath(space, "censor/rip.png");
            case Rem -> Identifier.fromNamespaceAndPath(space, "censor/rem.png");
            case smoke -> Identifier.fromNamespaceAndPath(space, "censor/smoke.png");
            case icon -> Identifier.fromNamespaceAndPath(space, "icon/icon.png");
        };
    }
}
