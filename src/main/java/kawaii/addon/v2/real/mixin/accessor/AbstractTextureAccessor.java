package kawaii.addon.v2.real.mixin.accessor;

import com.mojang.renderpearl.api.textures.GpuSampler;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractTexture.class)
public interface AbstractTextureAccessor {

    @Accessor("sampler")
    void setSampler(GpuSampler sampler);

    @Accessor("sampler")
    GpuSampler getSampler();
}
