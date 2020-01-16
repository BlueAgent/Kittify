package kittify.common.registry;

import com.google.common.collect.ImmutableMap;
import kittify.Kittify;
import kittify.common.compat.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

import java.util.Map;
import java.util.function.Supplier;

public class RegistryCompat {
    public static final ImmutableMap<String, CompatEntry> modules = ImmutableMap.<String, CompatEntry>builder()
            .put(new CompatEntry(Kittify.ANGEL_OF_VENGEANCE_MODID, "Angel of Vengeance", AngelOfVengeance::new))
            .put(new CompatEntry(Kittify.APPLECORE_MODID, "AppleCore", AppleCore::new))
            .put(new CompatEntry(Kittify.GRIMOIRE_OF_GAIA_MODID, "Grimoire of Gaia", GrimoireOfGaia::new))
            .put(new CompatEntry(Kittify.MILLENAIRE_MODID, "Millénaire", Millenaire::new))
            .put(new CompatEntry(Kittify.MINECRAFT_COMES_ALIVE_MODID, "Minecraft Comes Alive", MinecraftComesAlive::new))
            .put(new CompatEntry(Kittify.NEVERMINE_MODID, "Advent of Ascension", Nevermine::new))
            .build();

    public static void init() {
        int loaded = 0;
        int total = 0;
        for (CompatEntry entry : modules.values()) {
            total++;
            if (entry.isModLoaded()) {
                Kittify.log.info("Loading " + entry.getName() + " compatibility...");
                final CompatBase module = entry.getInstance();
                module.init();
                MinecraftForge.EVENT_BUS.register(module);
                Kittify.log.info("Finished loading " + entry.getName() + " compatibility");
                loaded++;
            } else {
                Kittify.log.info(entry.getName() + " is not loaded. Skipping compatibility");
            }
        }
        Kittify.log.info("Loaded " + loaded + " (out of " + total + ") compatibility modules");
    }

    public static class CompatEntry implements Map.Entry<String, CompatEntry> {
        private final String id;
        private final String name;
        private final Supplier<CompatBase> newInstance;
        private CompatBase instance = null;

        public CompatEntry(String id, String name, Supplier<CompatBase> newInstance) {
            this.id = id;
            this.name = name;
            this.newInstance = newInstance;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isModLoaded() {
            return Loader.isModLoaded(id);
        }

        public CompatBase getInstance() {
            if (instance != null) return instance;
            if (!Loader.isModLoaded(id))
                throw new RuntimeException(id + " (" + name + ") is not loaded!");
            return instance = newInstance.get();
        }

        @Override
        public String getKey() {
            return this.id;
        }

        @Override
        public CompatEntry getValue() {
            return this;
        }

        @Override
        public CompatEntry setValue(CompatEntry value) {
            throw new UnsupportedOperationException("Value cannot be set.");
        }
    }
}
