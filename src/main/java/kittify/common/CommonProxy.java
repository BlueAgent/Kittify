package kittify.common;

import kittify.common.compat.GrimoireOfGaia;
import kittify.common.compat.Millenaire;
import kittify.common.compat.MinecraftComesAlive;
import kittify.common.compat.Nevermine;
import kittify.common.module.EntityProtection;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static kittify.Kittify.*;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        if (Loader.isModLoaded(GRIMOIRE_OF_GAIA_MODID)) GrimoireOfGaia.init();
        if (Loader.isModLoaded(MILLENAIRE_MODID)) Millenaire.init();
        if (Loader.isModLoaded(MCA_MODID)) MinecraftComesAlive.init();
        if (Loader.isModLoaded(NEVERMINE_MODID)) Nevermine.init();
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
        EntityProtection.rebuildVillagerClasses();
    }
}
