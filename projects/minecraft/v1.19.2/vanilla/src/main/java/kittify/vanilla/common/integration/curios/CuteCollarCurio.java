package kittify.vanilla.common.integration.curios;

import com.google.common.collect.Multimap;
import kittify.vanilla.common.integration.accessories.CuteCollarAccessory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.UUID;

public class CuteCollarCurio implements ICurio {
    private final ItemStack stack;

    public CuteCollarCurio(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid) {
        Multimap<Attribute, AttributeModifier> modifiers = ICurio.super.getAttributeModifiers(slotContext, uuid);
        CuteCollarAccessory.addModifiers(modifiers, uuid);
        return modifiers;
    }

    @Override
    public @NotNull DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit) {
        return DropRule.ALWAYS_KEEP;
    }
}
