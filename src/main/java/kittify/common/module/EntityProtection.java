package kittify.common.module;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;

import java.util.HashSet;
import java.util.Set;

public class EntityProtection {
    public static final Set<Class<?>> VILLAGER_BASE_CLASSES = new HashSet<>();
    public static final Set<Class<?>> VILLAGER_ATTACKER_BASE_CLASSES = new HashSet<>();
    public static final Set<Class<?>> VILLAGER_CLASSES = new HashSet<>();
    public static final Set<Class<?>> VILLAGER_ATTACKER_CLASSES = new HashSet<>();

    static {
        VILLAGER_BASE_CLASSES.add(EntityVillager.class);
    }

    public static void rebuildVillagerClasses() {
        VILLAGER_CLASSES.clear();
        VILLAGER_ATTACKER_CLASSES.clear();
        EntityList.getEntityNameList().forEach(rl -> {
            Class<?> clazz = EntityList.getClass(rl);
            if (clazz == null)
                return;
            if (VILLAGER_BASE_CLASSES.stream().anyMatch(vc -> vc.isAssignableFrom(clazz))) {
                VILLAGER_CLASSES.add(clazz);
            }
            if (VILLAGER_ATTACKER_BASE_CLASSES.stream().anyMatch(vc -> vc.isAssignableFrom(clazz))) {
                VILLAGER_ATTACKER_CLASSES.add(clazz);
            }
        });
    }

    public static boolean shouldPreventTargeting(EntityLiving attacker, EntityLivingBase target) {
        // Villagers
        if (VILLAGER_CLASSES.contains(target.getClass()) && VILLAGER_ATTACKER_CLASSES.contains(attacker.getClass())) {
            return true;
        }
        return false;
    }
}
