package com.github.p1k0chu.mcmod.show_all_advancements;

import net.minecraft.advancements.Advancement;

public interface IServerAdvancementManager {
    Iterable<Advancement> show_all_advancements$getRoots();
}
