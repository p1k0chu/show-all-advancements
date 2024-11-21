package com.github.p1k0chu.mcmod.show_all_advancements;

import net.minecraft.advancements.AdvancementNode;

public interface IServerAdvancementManager {
    Iterable<AdvancementNode> show_all_advancements$getRoots();
}
