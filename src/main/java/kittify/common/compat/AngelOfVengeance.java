package kittify.common.compat;

import kittify.Kittify;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tamaized.aov.common.capabilities.aov.IAoVCapability;
import tamaized.aov.common.core.abilities.Ability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AngelOfVengeance extends CompatBase {
    public static final int MINUTES_TILL_FULL_CHARGES = 10;
    public static final int TICKS_TILL_FULL_CHARGES = MINUTES_TILL_FULL_CHARGES * 60 * 20;
    @CapabilityInject(IAoVCapability.class)
    public static Capability<IAoVCapability> AOV_CAP = null;
    @CapabilityInject(IAbilitiesRechargeData.class)
    public static Capability<IAbilitiesRechargeData> ABILITIES_RECHARGE_DATA_CAP = null;
    public static final ResourceLocation ABILITIES_RECHARGE_DATA_RESOURCE_LOCATION = new ResourceLocation(Kittify.MOD_ID, Kittify.ANGEL_OF_VENGEANCE_MODID + "_abilities_recharge");

    public AngelOfVengeance() {
        super(Kittify.ANGEL_OF_VENGEANCE_MODID, "Angel of Vengeance");
    }

    @Override
    public void init() {
        //CapabilityManager.INSTANCE.register(IAbilitiesRechargeData.class, new AbilitiesRechargeDataStorage(), AbilitiesRechargeData::new);
    }

    @SubscribeEvent
    public void attachPlayerCapabilities(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof EntityPlayer) {
            //e.addCapability(ABILITIES_RECHARGE_DATA_RESOURCE_LOCATION, new AbilitiesRechargeDataProvider());
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        IAoVCapability aov = e.player.getCapability(AOV_CAP, null);
        if (aov == null) return;
        //IAbilitiesRechargeData rechargeData = e.player.getCapability(ABILITIES_RECHARGE_DATA_CAP, null);
        long totalWorldTime = e.player.world.getTotalWorldTime();
        // Recharge abilities
        for (Ability a : aov.getAbilities()) {
            int chargeLimit = a.getAbility().getMaxCharges() + aov.getExtraCharges(e.player, a);
            // Should probably do this with capabilities... It'd be more consistent but also more laggy.
            if (a.getCharges() >= chargeLimit) continue;
            int ticksPerCharge = TICKS_TILL_FULL_CHARGES / chargeLimit;
            if (totalWorldTime % ticksPerCharge == 0) {
                a.restoreCharge(e.player, aov, 1);
            }
        }
    }

    public static class AbilityRechargeData {

    }

    public interface IAbilitiesRechargeData {
    }

    public static class AbilitiesRechargeData implements IAbilitiesRechargeData {
    }

    public class AbilitiesRechargeDataStorage implements Capability.IStorage<IAbilitiesRechargeData> {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IAbilitiesRechargeData> capability, IAbilitiesRechargeData instance, EnumFacing side) {
            // TODO: Implement
            return null;
        }

        @Override
        public void readNBT(Capability<IAbilitiesRechargeData> capability, IAbilitiesRechargeData instance, EnumFacing side, NBTBase nbt) {
            // TODO: Implement
        }
    }

    public static class AbilitiesRechargeDataProvider implements ICapabilitySerializable<NBTBase> {

        public static final Capability.IStorage<IAbilitiesRechargeData> storage = ABILITIES_RECHARGE_DATA_CAP.getStorage();
        public final IAbilitiesRechargeData instance = ABILITIES_RECHARGE_DATA_CAP.getDefaultInstance();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == ABILITIES_RECHARGE_DATA_CAP;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == ABILITIES_RECHARGE_DATA_CAP ? ABILITIES_RECHARGE_DATA_CAP.cast(instance) : null;
        }

        @Override
        public NBTBase serializeNBT() {
            return storage.writeNBT(ABILITIES_RECHARGE_DATA_CAP, this.instance, null);
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            storage.readNBT(ABILITIES_RECHARGE_DATA_CAP, this.instance, null, nbt);
        }
    }
}
