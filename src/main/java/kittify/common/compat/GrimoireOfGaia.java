package kittify.common.compat;

import gaia.entity.EntityMobHostileBase;
import kittify.Kittify;
import kittify.common.module.EntityProtection;

public class GrimoireOfGaia {
    public static void init() {
        Kittify.log.info("Grimoire of Gaia is installed");
        EntityProtection.VILLAGER_ATTACKER_BASE_CLASSES.add(EntityMobHostileBase.class);
    }
}
