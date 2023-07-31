package kittify.vanilla.common.integration.curios;

import kittify.Kittify;
import kittify.vanilla.common.registry.KittifyItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.function.Supplier;

public class KittifyCuriosIntegration {
    public static final String HEADBAND_SLOT_TYPE = "headband";
    public static final ResourceLocation HEADBAND_TEXTURE = new ResourceLocation("kittify:gui/slots/headband");
    public static final String COLLAR_SLOT_TYPE = "collar";
    public static final ResourceLocation COLLAR_TEXTURE = new ResourceLocation("kittify:gui/slots/collar");

    private KittifyCuriosIntegration() {
    }

    public static void sendIMCs(IMC imc) {
        imc.send(Kittify.CURIOS_MOD_ID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder(HEADBAND_SLOT_TYPE)
                .icon(HEADBAND_TEXTURE)
                .priority(SlotTypePreset.HEAD.getMessageBuilder().build().getPriority())
                .build());
        imc.send(Kittify.CURIOS_MOD_ID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder(COLLAR_SLOT_TYPE)
                .icon(COLLAR_TEXTURE)
                .priority(SlotTypePreset.NECKLACE.getMessageBuilder().build().getPriority())
                .build());
    }

    public static void addSprites(TextureSitchPre event) {
        event.addSprite(HEADBAND_TEXTURE);
        event.addSprite(COLLAR_TEXTURE);
    }

    public static ICurio createCapability(ItemStack stack) {
        Item item = stack.getItem();

        if (item == KittifyItems.EXE_ARC) {
            return new EXEArcCurio(stack);
        }

        if (item == KittifyItems.CUTE_COLLAR) {
            return new CuteCollarCurio(stack);
        }

        return null;
    }

    @FunctionalInterface
    public interface IMC {
        void send(final String modId, final String method, final Supplier<?> thing);
    }

    @FunctionalInterface
    public interface TextureSitchPre {
        void addSprite(final ResourceLocation sprite);
    }
}
