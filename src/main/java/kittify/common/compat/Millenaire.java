package kittify.common.compat;

import kittify.Kittify;
import kittify.common.module.EntityProtection;
import org.millenaire.common.entity.MillVillager;

public class Millenaire {
    public static void init() {
        Kittify.log.info("Millenaire is installed");
        EntityProtection.VILLAGER_BASE_CLASSES.add(MillVillager.class);
    }
}
