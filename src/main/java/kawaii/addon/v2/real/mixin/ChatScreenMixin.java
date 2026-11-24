package kawaii.addon.v2.real.mixin;

import kawaii.addon.v2.real.modules.CatFacts;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    @Shadow protected EditBox input;

    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void kawaii$addCatFactButton(CallbackInfo ci) {
        CatFacts module = Modules.get().get(CatFacts.class);

        if (module == null || !module.isActive() || module.mode.get() != CatFacts.Mode.Manual) return;

        this.addRenderableWidget(
            Button.builder(Component.literal("cat-fact"), _ ->
                    CatFacts.fetchFact(fact -> {
                        if (this.input == null) return;
                        this.setFocused(this.input);
                        this.input.insertText(fact);
                    }))
                .bounds(2, 2, 60, 20)
                .build()
        );
    }
}
