package kittify.common.compat;

import kittify.common.module.EntityProtection;

public class Millenaire extends CompatBase {
    public void init() {
        EntityProtection.addVillagerClass("org.millenaire.common.entity.MillVillager");
    }
}
