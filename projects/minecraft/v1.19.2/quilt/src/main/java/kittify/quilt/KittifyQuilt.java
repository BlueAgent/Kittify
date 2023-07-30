package kittify.quilt;

import kittify.Kittify;
import kittify.quiltish.KittifyQuiltish;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class KittifyQuilt extends KittifyQuiltish implements ModInitializer {
    public static KittifyQuilt INSTANCE;

    public KittifyQuilt() {
        if (INSTANCE != null) {
            throw new RuntimeException("Expected only one KittifyQuilt to be instantiated.");
        }

        INSTANCE = this;
        Kittify.LOGGER.info(Kittify.NAME + " is on Quilt");
    }

    @Override
    public void onInitialize(ModContainer mod) {
        super.onInitialize();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return QuiltLoader.isModLoaded(modId);
    }
}
