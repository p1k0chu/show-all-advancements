package com.github.p1k0chu.mcmod.show_all_advancements.mixin;

import com.github.p1k0chu.mcmod.show_all_advancements.IServerAdvancementManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.server.ServerAdvancementManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerAdvancementManager.class)
public class ServerAdvancementManagerMixin implements IServerAdvancementManager {

    @Shadow private AdvancementList advancements;

    @Override
    public Iterable<Advancement> show_all_advancements$getRoots() {
        return advancements.getRoots();
    }
}