package com.github.p1k0chu.mcmod.show_all_advancements.mixin;

import com.github.p1k0chu.mcmod.show_all_advancements.IServerAdvancementManager;
import net.minecraft.advancements.*;
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
import java.util.Optional;
import java.util.function.Predicate;


@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {
    @Shadow
    protected abstract void markForVisibilityUpdate(AdvancementHolder advancementHolder);

    @Shadow
    public abstract AdvancementProgress getOrStartProgress(AdvancementHolder advancementHolder);

    @Redirect(method = "updateTreeVisibility", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator;evaluateVisibility(Lnet/minecraft/advancements/AdvancementNode;Ljava/util/function/Predicate;Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator$Output;)V"))
    private void updateTreeVisibility(AdvancementNode advancementNode, Predicate<AdvancementNode> predicate, AdvancementVisibilityEvaluator.Output output) {
        AdvancementNode root = advancementNode.root();
        updateTreeRecursive(root, output);
    }

    @Unique
    private boolean updateTreeRecursive(AdvancementNode node, AdvancementVisibilityEvaluator.Output output) {
        Iterator<AdvancementNode> children = node.children().iterator();
        Optional<DisplayInfo> displayInfo = node.advancement().display();

        boolean bl = displayInfo.isPresent() && (!displayInfo.get().isHidden() || getOrStartProgress(node.holder()).isDone());
        while (children.hasNext()) {
            bl = updateTreeRecursive(children.next(), output) || bl;
        }

        output.accept(node, bl);

        return bl;
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void ensureRootsVisible(ServerAdvancementManager serverAdvancementManager, CallbackInfo ci) {
        Iterable<AdvancementNode> roots = ((IServerAdvancementManager) serverAdvancementManager).show_all_advancements$getRoots();

        roots.forEach(node -> markForVisibilityUpdate(node.holder()));
    }
}
