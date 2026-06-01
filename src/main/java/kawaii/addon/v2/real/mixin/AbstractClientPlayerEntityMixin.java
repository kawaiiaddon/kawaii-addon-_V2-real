package kawaii.addon.v2.real.mixin;

import kawaii.addon.v2.real.modules.Cape;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
        Identifier id = switch (module.capes.get()) {
            case kawaii -> Identifier.fromNamespaceAndPath("kawaii-addon", "cape/kawaii.png");
            case cat -> Identifier.fromNamespaceAndPath("kawaii-addon", "cape/cat.png");
            case idk -> Identifier.fromNamespaceAndPath("kawaii-addon", "cape/idk.png");
            case turtle -> Identifier.fromNamespaceAndPath("kawaii-addon", "cape/turtle.png");
            case hutao -> Identifier.fromNamespaceAndPath("kawaii-addon", "cape/hutao.png");
            case vape -> Identifier.fromNamespaceAndPath("kawaii-addon", "cape/vape.png");
            case RETRO -> Identifier.fromNamespaceAndPath("kawaii-addon", "cape/retro.png");
            case h0rny -> Identifier.fromNamespaceAndPath("kawaii-addon", "cape/h0rny.png");
            case astolfo -> Identifier.fromNamespaceAndPath("kawaii-addon", "cape/astolfo.png");
            default -> null;
        };

        if (id == null) return original.cape();
        return new ClientAsset.ResourceTexture(id, id);
    }
}
