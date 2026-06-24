package com.github.p1k0chu.mcmod.show_all_advancements.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;

@Mixin(PlayerAdvancements.class)
public interface PlayerAdvancementsAccessor {
    @Invoker
    void invokeMarkForVisibilityUpdate(AdvancementHolder advancementHolder);
}

