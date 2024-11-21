package com.github.p1k0chu.mcmod.show_all_advancements.mixin;

import com.github.p1k0chu.mcmod.show_all_advancements.IServerAdvancementManager;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.server.ServerAdvancementManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerAdvancementManager.class)
public class ServerAdvancementManagerMixin implements IServerAdvancementManager {
    @Shadow private AdvancementTree tree;

    @Override
    public Iterable<AdvancementNode> show_all_advancements$getRoots() {
        return tree.roots();
    }
}
