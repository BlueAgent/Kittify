package kittify.forge;

import kittify.Kittify;
import kittify.forge.client.KittifyForgeClient;
import kittify.vanilla.common.integration.curios.KittifyCuriosIntegration;
import kittify.vanilla.common.registry.KittifyItems;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

@Mod(Kittify.MOD_ID)
public class KittifyForge extends Kittify {
    private static final ResourceLocation CURIO_CAPABILITY_ID = new ResourceLocation(MOD_ID, "curio");
    public static KittifyForge INSTANCE;

    public KittifyForge() {
        if (INSTANCE != null) {
            throw new RuntimeException("Expected only one KittifyForge to be instantiated.");
        }

        INSTANCE = this;
        LOGGER.info(Kittify.NAME + " is on Forge.");
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::onInterModCommunication);
        forgeEventBus.addGenericListener(ItemStack.class, this::onAttachCapabilitiesToItemStack);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> KittifyForgeClient.INSTANCE.registerEvents(modEventBus));
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public void onRegister(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ITEMS, this::onRegisterItems);
    }

    public void onRegisterItems(RegisterEvent.RegisterHelper<Item> helper) {
        KittifyItems.init(helper::register);
    }

    public void onInterModCommunication(InterModEnqueueEvent event) {
        if (isModLoaded(CURIOS_MOD_ID)) {
            KittifyCuriosIntegration.sendIMCs(InterModComms::sendTo);
        }
    }

    public void onAttachCapabilitiesToItemStack(AttachCapabilitiesEvent<ItemStack> event) {
        if (isModLoaded(CURIOS_MOD_ID)) {
            ItemStack stack = event.getObject();
            ICurio curioCapability = KittifyCuriosIntegration.createCapability(stack);
            if (curioCapability != null) {
                LazyOptional<ICurio> lazyCap = LazyOptional.of(() -> curioCapability);

                ICapabilityProvider provider = new ICapabilityProvider() {
                    @Override
                    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction direction) {
                        if (cap == CuriosCapability.ITEM) {
                            return lazyCap.cast();
                        }
                        return LazyOptional.empty();
                    }
                };

                event.addCapability(CURIO_CAPABILITY_ID, provider);
            }
        }
    }
}
