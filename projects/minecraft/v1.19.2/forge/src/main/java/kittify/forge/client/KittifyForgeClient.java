package kittify.forge.client;

import kittify.Kittify;
import kittify.vanilla.common.integration.curios.KittifyCuriosIntegration;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class KittifyForgeClient {
    public static final KittifyForgeClient INSTANCE = new KittifyForgeClient();

    public void registerEvents(IEventBus modEventBus) {
        modEventBus.addListener(this::onTextureSitchPre);
    }

    public void onTextureSitchPre(TextureStitchEvent.Pre event) {
        if (event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS) && Kittify.INSTANCE.isModLoaded(Kittify.CURIOS_MOD_ID)) {
            KittifyCuriosIntegration.addSprites(event::addSprite);
        }
    }
}
