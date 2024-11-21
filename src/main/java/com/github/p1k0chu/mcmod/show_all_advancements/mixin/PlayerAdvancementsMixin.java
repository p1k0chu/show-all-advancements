package com.github.p1k0chu.mcmod.show_all_advancements.mixin;

import com.github.p1k0chu.mcmod.show_all_advancements.IServerAdvancementManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {

    @Shadow protected abstract void ensureVisibility(Advancement advancement);

    @Inject(method = "shouldBeVisible", at = @At("HEAD"), cancellable = true)
    private void shouldBeVisible(Advancement advancement, CallbackInfoReturnable<Boolean> cir) {
        DisplayInfo display = advancement.getDisplay();

        if(display != null && !display.isHidden()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void ensureRootsVisible(ServerAdvancementManager serverAdvancementManager, CallbackInfo ci) {
        Iterable<Advancement> roots = ((IServerAdvancementManager) serverAdvancementManager).show_all_advancements$getRoots();

        roots.forEach(this::ensureVisibility);
    }
}
