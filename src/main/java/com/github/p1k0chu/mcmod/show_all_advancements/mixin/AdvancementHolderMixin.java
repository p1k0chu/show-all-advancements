package com.github.p1k0chu.mcmod.show_all_advancements.mixin;

import com.github.p1k0chu.mcmod.show_all_advancements.ShowAllAdvsEntry;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Function;

@Mixin(AdvancementHolder.class)
class AdvancementHolderMixin {
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Ljava/util/function/BiFunction;)Lnet/minecraft/network/codec/StreamCodec;"), index = 1)
    private static Function<AdvancementHolder, Identifier> setAdvIdThreadLocal(Function<AdvancementHolder, Identifier> getter1) {
        return holder -> {
            var id = getter1.apply(holder);
            ShowAllAdvsEntry.SERIALIZED_ADV_ID.set(id);
            return id;
        };
    }
}
