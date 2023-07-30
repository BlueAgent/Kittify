package kittify.vanilla.common.integration.curios;

import kittify.Kittify;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import java.util.function.Supplier;

public class KittifyCuriosIntegration {
    public static final String COLLAR_SLOT_TYPE = "collar";
    public static final ResourceLocation COLLAR_TEXTURE = new ResourceLocation("kittify:gui/slots/collar");

    private KittifyCuriosIntegration() {
    }

    public static void sendIMCs(IMC imc) {
        imc.send(Kittify.CURIOS_MOD_ID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder(COLLAR_SLOT_TYPE)
                .icon(COLLAR_TEXTURE)
                .priority(SlotTypePreset.NECKLACE.getMessageBuilder().build().getPriority())
                .build());
    }

    public static void addSprites(TextureSitchPre event) {
        event.addSprite(COLLAR_TEXTURE);
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
