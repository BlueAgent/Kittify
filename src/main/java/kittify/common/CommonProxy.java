package kittify.common;

import kittify.common.command.CommandKittify;
import kittify.common.module.EntityProtection;
import kittify.common.registry.RegistryCompat;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        RegistryCompat.init();
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
        EntityProtection.rebuildVillagerClasses();
    }

    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandKittify());
    }
}
