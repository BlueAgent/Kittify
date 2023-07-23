package kittify.vanilla.common.registry;

import kittify.Kittify;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public class KittifyItems {
    public static final Item PURRFECT_PAW_OF_POUTY_PROTECTION = new Item(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
    private static final Logger LOG = LogManager.getLogger();
    private static final Marker REGISTRIES = MarkerManager.getMarker("REGISTRIES");

    public static void init(BiConsumer<ResourceLocation, Item> registerItem) {
        for (Field f : KittifyItems.class.getDeclaredFields()) {
            try {
                if (Modifier.isStatic(f.getModifiers())) {
                    if (Item.class.isAssignableFrom(f.getType())) {
                        ResourceLocation rl = new ResourceLocation(Kittify.MOD_ID, f.getName().toLowerCase(Locale.ROOT));
                        LOG.info(REGISTRIES, "Registering Item: " + rl);
                        Item item = (Item) f.get(null);
                        registerItem.accept(rl, item);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
