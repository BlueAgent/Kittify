package kittify.forge;

import kittify.Kittify;
import kittify.vanilla.common.registry.KittifyItems;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod(Kittify.MOD_ID)
public class KittifyForge {
    public KittifyForge() {
        Kittify.LOGGER.info(Kittify.NAME + " is on Forge.");
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::onRegister);
    }

    public void onRegister(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ITEMS, this::onRegisterItems);
    }

    public void onRegisterItems(RegisterEvent.RegisterHelper<Item> helper) {
        KittifyItems.init(helper::register);
    }
}
