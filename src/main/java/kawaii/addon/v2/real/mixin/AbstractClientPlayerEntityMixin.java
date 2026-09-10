package kawaii.addon.v2.real.mixin;

import kawaii.addon.v2.real.modules.Cape;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static kawaii.addon.v2.real.util.FilePath.space;
import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntityMixin extends Entity {

    public AbstractClientPlayerEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void onGetSkinTextures(CallbackInfoReturnable<PlayerSkin> cir) {
        Cape module = Modules.get().get(Cape.class);
        if (module == null || !module.isActive()) return;
        if (!this.getUUID().equals(mc.getUser().getProfileId())) return;

        PlayerSkin original = cir.getReturnValue();
        if (original == null) return;

        ClientAsset.Texture capeAsset = getCapeAsset(module, original);
        if (capeAsset == null) return;

        cir.setReturnValue(new PlayerSkin(
            original.body(),
            capeAsset,
            original.elytra(),
            original.model(),
            original.secure()
        ));
    }

    @Unique
    private ClientAsset.Texture getCapeAsset(Cape module, PlayerSkin original) {
        ResourceLocation id = switch (module.capes.get()) {
            case kawaii -> ResourceLocation.fromNamespaceAndPath(space, "cape/kawaii.png");
            case cat -> ResourceLocation.fromNamespaceAndPath(space, "cape/cat.png");
            case idk -> ResourceLocation.fromNamespaceAndPath(space, "cape/idk.png");
            case turtle -> ResourceLocation.fromNamespaceAndPath(space, "cape/turtle.png");
            case hutao -> ResourceLocation.fromNamespaceAndPath(space, "cape/hutao.png");
            case vape -> ResourceLocation.fromNamespaceAndPath(space, "cape/vape.png");
            case RETRO -> ResourceLocation.fromNamespaceAndPath(space, "cape/retro.png");
            case h0rny -> ResourceLocation.fromNamespaceAndPath(space, "cape/h0rny.png");
            case astolfo -> ResourceLocation.fromNamespaceAndPath(space, "cape/astolfo.png");
            case RusherHack -> ResourceLocation.fromNamespaceAndPath(space, "cape/rusherhack.png");
            case phobos -> ResourceLocation.fromNamespaceAndPath(space, "cape/phobos.png");
            case Shoreline -> ResourceLocation.fromNamespaceAndPath(space, "cape/shoreline.png");
            case future -> ResourceLocation.fromNamespaceAndPath(space, "cape/future.png");
            default -> null;
        };

        if (id == null) return original.cape();
        return new ClientAsset.ResourceTexture(id, id);
    }
}
