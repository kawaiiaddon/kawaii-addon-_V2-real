package kawaii.addon.v2.real.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kawaii.addon.v2.real.modules.MapCensor;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapRenderer.class)
public class MapRendererMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onDraw(MapRenderState state, PoseStack matrices, SubmitNodeCollector queue, boolean renderDecorations, int light, CallbackInfo ci) {
        MapCensor module = Modules.get().get(MapCensor.class);
        if (module == null || !module.isActive()) return;

        // Cancel the real map so it doesn't leak coordinates
        ci.cancel();

        //vertex stuff
        MultiBufferSource.BufferSource consumers = Minecraft.getInstance().renderBuffers().bufferSource();

        //layer stuff
        RenderType layer = RenderType.text(module.getTexture());
        VertexConsumer vertexConsumer = consumers.getBuffer(layer);

        Matrix4f matrix4f = matrices.last().pose();

        int overlay = OverlayTexture.NO_OVERLAY;
        int lightU = light & 0xFFFF;
        int lightV = (light >> 16) & 0xFFFF;

        // Draw the custom PNG over the map area
        vertexConsumer.addVertex(matrix4f, 0f, 128f, -0.01f).setUv(0f, 1f).setOverlay(overlay).setUv2(lightU, lightV).setColor(255, 255, 255, 255).setLight(light);
        vertexConsumer.addVertex(matrix4f, 128f, 128f, -0.01f).setUv(1f, 1f).setOverlay(overlay).setUv2(lightU, lightV).setColor(255, 255, 255, 255).setLight(light);
        vertexConsumer.addVertex(matrix4f, 128f, 0f, -0.01f).setUv(1f, 0f).setOverlay(overlay).setUv2(lightU, lightV).setColor(255, 255, 255, 255).setLight(light);
        vertexConsumer.addVertex(matrix4f, 0f, 0f, -0.01f).setUv(0f, 0f).setOverlay(overlay).setUv2(lightU, lightV).setColor(255, 255, 255, 255).setLight(light);
        consumers.endBatch();
    }
}
