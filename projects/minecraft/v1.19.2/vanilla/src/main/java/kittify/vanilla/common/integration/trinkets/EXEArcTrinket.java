package kittify.vanilla.common.integration.trinkets;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketEnums;
import kittify.vanilla.common.integration.accessories.EXEArcAccessory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class EXEArcTrinket implements Trinket {
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        Trinket.super.tick(stack, slot, entity);
        EXEArcAccessory.tick(stack, entity);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        Multimap<Attribute, AttributeModifier> modifiers = Trinket.super.getModifiers(stack, slot, entity, uuid);
        EXEArcAccessory.addModifiers(modifiers, uuid);
        return modifiers;
    }

    @Override
    public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return TrinketEnums.DropRule.KEEP;
    }
}
