package kittify.vanilla.common.integration.accessories;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class CuteCollarAccessory {
    private CuteCollarAccessory() {
    }

    public static void addModifiers(Multimap<Attribute, AttributeModifier> modifiers, UUID uuid) {
        modifiers.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "kittify:cute_collar_speed", 1.0, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }
}
