package kittify.common.compat;

import kittify.Kittify;
import kittify.common.module.EntityProtection;

public class Millenaire extends CompatBase {
    public Millenaire() {
        super(Kittify.MILLENAIRE_MODID, "Millénaire");
    }

    public void init() {
        EntityProtection.addVillagerClass("org.millenaire.common.entity.MillVillager");
    }
}
