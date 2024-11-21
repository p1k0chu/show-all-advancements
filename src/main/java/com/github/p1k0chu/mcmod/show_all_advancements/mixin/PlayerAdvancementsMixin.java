package com.github.p1k0chu.mcmod.show_all_advancements.mixin;

import com.github.p1k0chu.mcmod.show_all_advancements.IServerAdvancementManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.advancements.AdvancementVisibilityEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.function.Predicate;


@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {
    @Shadow
    protected abstract void markForVisibilityUpdate(Advancement advancement);

    @Shadow
    public abstract AdvancementProgress getOrStartProgress(Advancement advancement);

    @Redirect(method = "updateTreeVisibility", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator;evaluateVisibility(Lnet/minecraft/advancements/Advancement;Ljava/util/function/Predicate;Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator$Output;)V"))
    private void updateTreeVisibility(Advancement advancement, Predicate<Advancement> predicate, AdvancementVisibilityEvaluator.Output output) {
        Advancement root = advancement.getRoot();
        updateTreeRecursive(root, output);
    }

    @Unique
    private void updateTreeRecursive(Advancement advancement, AdvancementVisibilityEvaluator.Output output) {
        Iterator<Advancement> children = advancement.getChildren().iterator();
        boolean childrenExist = children.hasNext();

        while (children.hasNext()) {
            updateTreeRecursive(children.next(), output);
        }

        DisplayInfo displayInfo = advancement.getDisplay();
        output.accept(advancement, childrenExist || displayInfo != null && (!displayInfo.isHidden() || getOrStartProgress(advancement).isDone()));
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void ensureRootsVisible(ServerAdvancementManager serverAdvancementManager, CallbackInfo ci) {
        Iterable<Advancement> roots = ((IServerAdvancementManager) serverAdvancementManager).show_all_advancements$getRoots();

        roots.forEach(this::markForVisibilityUpdate);
    }
}
