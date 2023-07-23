package kittify.fabric;

import kittify.Kittify;
import kittify.quiltish.KittifyQuiltish;
import net.fabricmc.api.ModInitializer;

public class KittifyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Kittify.LOGGER.info(Kittify.NAME + " is on Fabric.");
        KittifyQuiltish.onInitialize();
    }
}
