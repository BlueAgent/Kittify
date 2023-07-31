package kittify.vanilla.common.integration.accessories;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class EXEArcAccessory {
    private static final int TICKS_PER_SECOND = 20;
    private static final int FULL_HEAL_SECONDS = 20;
    private static final int FULL_HEAL_TICKS = FULL_HEAL_SECONDS * TICKS_PER_SECOND;
    private static final int HEAL_INTERVAL_TICKS = 20;
    private static final float HEAL_PERCENT_PER_INTERVAL = (float) HEAL_INTERVAL_TICKS / FULL_HEAL_TICKS;

    private EXEArcAccessory() {
    }

    public static void addModifiers(Multimap<Attribute, AttributeModifier> modifiers, UUID uuid) {
    }

    public static void tick(ItemStack stack, LivingEntity entity) {
        Level level = entity.level;
        if (level.isClientSide) {
            return;
        }

        long ticks = level.getGameTime();
        if ((ticks + entity.getId()) % HEAL_INTERVAL_TICKS != 0) {
            return;
        }

        entity.heal(HEAL_PERCENT_PER_INTERVAL * entity.getMaxHealth());
    }
}
