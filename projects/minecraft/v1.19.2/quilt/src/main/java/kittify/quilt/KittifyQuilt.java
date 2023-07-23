package kittify.quilt;

import kittify.Kittify;
import kittify.quiltish.KittifyQuiltish;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class KittifyQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        Kittify.LOGGER.info(Kittify.NAME + " is on Quilt");
        KittifyQuiltish.onInitialize();
    }
}
