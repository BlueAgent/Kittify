package kittify.common.registry;

import com.google.common.collect.ImmutableMap;
import kittify.Kittify;
import kittify.common.compat.*;
import net.minecraftforge.fml.common.Loader;

public class RegistryCompat {
    public static final ImmutableMap<String, CompatBase> modules = ImmutableMap.<String, CompatBase>builder()
            .put(new GrimoireOfGaia())
            .put(new Millenaire())
            .put(new MinecraftComesAlive())
            .put(new Nevermine())
            .build();

    public static void init() {
        int loaded = 0;
        int total = 0;
        for (CompatBase module : modules.values()) {
            total++;
            if (Loader.isModLoaded(module.getId())) {
                Kittify.log.info("Loading " + module.getName() + " compatibility...");
                module.init();
                Kittify.log.info("Finished loading " + module.getName() + " compatibility");
                loaded++;
            } else {
                Kittify.log.info(module.getName() + " is not loaded. Skipping compatibility");
            }
        }
        Kittify.log.info("Loaded " + loaded + " (out of " + total + ") compatibility modules");
    }
}
