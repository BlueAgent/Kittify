package kittify.vanilla.common.registry;

import kittify.Kittify;
import kittify.vanilla.common.integration.trinkets.KittifyTrinketsIntegration;
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
    public static final Item CUTE_COLLAR = new Item(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1));
    public static final Item GENTLE_RIDING_CROP = new Item(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1));
    public static final Item PURRFECT_PAW_OF_POUTY_PROTECTION = new Item(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1));
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

        if (Kittify.INSTANCE.isModLoaded(Kittify.TRINKETS_MOD_ID)) {
            KittifyTrinketsIntegration.afterRegisterItems();
        }
    }
}
