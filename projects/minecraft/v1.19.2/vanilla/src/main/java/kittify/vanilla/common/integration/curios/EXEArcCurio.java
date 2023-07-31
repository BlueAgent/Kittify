package kittify.vanilla.common.integration.curios;

import com.google.common.collect.Multimap;
import kittify.vanilla.common.integration.accessories.EXEArcAccessory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.Optional;
import java.util.UUID;

public class EXEArcCurio implements ICurio {
    private final ItemStack stack;

    public EXEArcCurio(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public void curioTick(SlotContext slotContext) {
        ICurio.super.curioTick(slotContext);
        LivingEntity entity = slotContext.entity();
        Optional<ItemStack> result = CuriosApi.getCuriosHelper()
                .findCurio(entity, slotContext.identifier(), slotContext.index())
                .map(SlotResult::stack);

        if (result.isEmpty()) {
            return;
        }

        EXEArcAccessory.tick(result.get(), entity);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid) {
        Multimap<Attribute, AttributeModifier> modifiers = ICurio.super.getAttributeModifiers(slotContext, uuid);
        EXEArcAccessory.addModifiers(modifiers, uuid);
        return modifiers;
    }

    @Override
    public @NotNull DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit) {
        return DropRule.ALWAYS_KEEP;
    }
}
