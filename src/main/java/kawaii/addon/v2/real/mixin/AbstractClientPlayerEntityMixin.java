package kawaii.addon.v2.real.mixin;

import kawaii.addon.v2.real.modules.Cape;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
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
        ResourceLocation cape = switch (module.capes.get()) {
            case kawaii -> ResourceLocation.fromNamespaceAndPath("kawaii-addon", "cape/kawaii.png");
            case cat -> ResourceLocation.fromNamespaceAndPath("kawaii-addon", "cape/cat.png");
            case idk -> ResourceLocation.fromNamespaceAndPath("kawaii-addon", "cape/idk.png");
            case turtle -> ResourceLocation.fromNamespaceAndPath("kawaii-addon", "cape/turtle.png");
            case hutao -> ResourceLocation.fromNamespaceAndPath("kawaii-addon", "cape/hutao.png");
            case vape -> ResourceLocation.fromNamespaceAndPath("kawaii-addon", "cape/vape.png");
            case RETRO -> ResourceLocation.fromNamespaceAndPath("kawaii-addon", "cape/retro.png");
            case h0rny -> ResourceLocation.fromNamespaceAndPath("kawaii-addon", "cape/h0rny.png");
            case astolfo -> ResourceLocation.fromNamespaceAndPath("kawaii-addon", "cape/astolfo.png");
            default -> original.capeTexture();
        };
        PlayerSkin modified = new PlayerSkin(
            original.texture(), original.textureUrl(), cape, original.elytraTexture(), original.model(), original.secure()
        );
        cir.setReturnValue(modified);
    }
}
