package com.github.p1k0chu.mcmod.show_all_advancements.mixin;

import com.github.p1k0chu.mcmod.show_all_advancements.IServerAdvancementManager;
import com.github.p1k0chu.mcmod.show_all_advancements.ShowAllAdvsEntry;
import com.github.p1k0chu.mcmod.show_all_advancements.ducks.PlayerAdvancementsDuck;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.advancements.*;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.advancements.AdvancementVisibilityEvaluator;
import net.minecraft.server.level.ServerPlayer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin implements PlayerAdvancementsDuck {
    @Shadow
    @Final
    private Set<AdvancementHolder> visible;

    @Shadow
    private boolean isFirstPacket;

    @Shadow
    protected abstract void markForVisibilityUpdate(AdvancementHolder advancementHolder);

    @Shadow
    public abstract AdvancementProgress getOrStartProgress(AdvancementHolder advancementHolder);

    @WrapOperation(method = "updateTreeVisibility", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator;evaluateVisibility(Lnet/minecraft/advancements/AdvancementNode;Ljava/util/function/Predicate;Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator$Output;)V"))
    private void updateTreeVisibility(AdvancementNode advancementNode, Predicate<AdvancementNode> predicate, AdvancementVisibilityEvaluator.Output output, Operation<Void> original) {
        Predicate<AdvancementNode> pred = node -> {
            Optional<DisplayInfo> displayInfo = node.advancement().display();
            if (displayInfo.isEmpty()) return false;

            boolean nonHidden = !displayInfo.get().isHidden();
            return nonHidden || predicate.test(node) || ShowAllAdvsEntry.getInstance().showsThisHidden(node.holder().id().toString());
        };
        original.call(advancementNode, pred, output);
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void ensureRootsVisible(ServerAdvancementManager serverAdvancementManager, CallbackInfo ci) {
        Iterable<AdvancementNode> roots = ((IServerAdvancementManager) serverAdvancementManager).show_all_advancements$getRoots();

        roots.forEach(node -> markForVisibilityUpdate(node.holder()));
    }

    @Override
    public void show_all_advancements$clearVisible() {
        if (this.visible.isEmpty()) {
            return;
        }

        this.isFirstPacket = true;
        this.visible.clear();
    }
}
