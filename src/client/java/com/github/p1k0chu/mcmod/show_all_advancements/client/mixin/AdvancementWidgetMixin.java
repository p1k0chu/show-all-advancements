package com.github.p1k0chu.mcmod.show_all_advancements.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;

@Mixin(AdvancementWidget.class)
public class AdvancementWidgetMixin {
    @Redirect(method = {"extractRenderState", "isMouseOver"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/DisplayInfo;isHidden()Z"))
    boolean neverHidden(DisplayInfo instance) {
        return false;
    }
}

