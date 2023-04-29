package kittify.common.compat;

import kittify.common.module.EntityProtection;

public class MinecraftComesAlive extends CompatBase {
    public void init() {
        EntityProtection.addVillagerClass("mca.entity.EntityVillagerMCA");
    }
}
