package com.github.p1k0chu.mcmod.show_all_advancements.mixin;

import com.github.p1k0chu.mcmod.show_all_advancements.ShowAllAdvsEntry;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DisplayInfo.class)
class DisplayInfoMixin {
    @Shadow
    @Final
    private Component title;

    @Definition(id = "hidden", field = "Lnet/minecraft/advancements/DisplayInfo;hidden:Z")
    @Expression("?.hidden")
    @ModifyExpressionValue(method = "serializeToNetwork", at = @At("MIXINEXTRAS:EXPRESSION"))
    boolean modifyHidden(boolean original) {
        if (!original) {
            // This should be hot.
            return false;
        }

        var id = ShowAllAdvsEntry.SERIALIZED_ADV_ID.get();
        if (id == null) {
            ShowAllAdvsEntry.LOGGER.warn("DisplayInfoMixin: id is null for advancement \"{}\"", title);
            return true;
        }

        return !ShowAllAdvsEntry.getInstance().showsThisHidden(id.toString());
    }
}
