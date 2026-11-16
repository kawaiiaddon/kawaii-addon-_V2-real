package kawaii.addon.v2.real.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import kawaii.addon.v2.real.modules.ActionBarCensor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "setActionBarText", at = @At("HEAD"), cancellable = true)
    private void kawaii$onSetActionBarText(ClientboundSetActionBarTextPacket packet, CallbackInfo ci) {
        ActionBarCensor module = Modules.get().get(ActionBarCensor.class);
        if (module != null && module.isActive() && module.shouldHide(packet.text().getString())) {
            ci.cancel();
        }
    }
}
