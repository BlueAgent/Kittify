package kittify.common.compat;

import kittify.Kittify;
import kittify.common.module.EntityProtection;
import mca.entity.EntityVillagerMCA;

public class MinecraftComesAlive {
    public static void init() {
        Kittify.log.info("Minecraft Comes Alive is installed");
        EntityProtection.VILLAGER_BASE_CLASSES.add(EntityVillagerMCA.class);
    }
}
