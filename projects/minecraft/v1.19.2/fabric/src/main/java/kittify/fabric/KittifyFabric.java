package kittify.fabric;

import kittify.Kittify;
import kittify.quiltish.KittifyQuiltish;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class KittifyFabric extends KittifyQuiltish implements ModInitializer {
    public static KittifyFabric INSTANCE;

    public KittifyFabric() {
        if (INSTANCE != null) {
            throw new RuntimeException("Expected only one KittifyFabric to be instantiated.");
        }

        INSTANCE = this;
        Kittify.LOGGER.info(Kittify.NAME + " is on Fabric.");
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
