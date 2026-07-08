package kawaii.addon.v2.real.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kawaii.addon.v2.real.modules.MapCensor;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.state.MapRenderState;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapRenderer.class)
public class MapRendererMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onDraw(MapRenderState state, PoseStack matrices, MultiBufferSource vertexConsumers, boolean bool, int light, CallbackInfo ci) {
        MapCensor module = Modules.get().get(MapCensor.class);
        if (module == null || !module.isActive()) return;

        // Cancel the real map so it doesn't leak coordinates
        ci.cancel();

        // Use the vertex consumer provider to render
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.text(module.getTexture()));

        Matrix4f matrix4f = matrices.last().pose();

        // Draw the custom PNG over the map area
        vertexConsumer.addVertex(matrix4f, 0, 128, -0.01f).setUv(0, 1).setColor(255, 255, 255, 255).setLight(light);
        vertexConsumer.addVertex(matrix4f, 128, 128, -0.01f).setUv(1, 1).setColor(255, 255, 255, 255).setLight(light);
        vertexConsumer.addVertex(matrix4f, 128, 0, -0.01f).setUv(1, 0).setColor(255, 255, 255, 255).setLight(light);
        vertexConsumer.addVertex(matrix4f, 0, 0, -0.01f).setUv(0, 0).setColor(255, 255, 255, 255).setLight(light);
    }
}
