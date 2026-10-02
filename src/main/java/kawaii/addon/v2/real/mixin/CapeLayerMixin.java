package kawaii.addon.v2.real.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import kawaii.addon.v2.real.util.RainbowCapeTexture;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin {

    @WrapOperation(
        method = "submit*",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;entitySolid(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"
        )
    )
    private RenderType kawaii$translucentCape(Identifier texture, Operation<RenderType> original) {
        if (texture.equals(RainbowCapeTexture.ID)) {
            return RenderTypes.entityTranslucent(texture);
        }
        return original.call(texture);
    }
}
