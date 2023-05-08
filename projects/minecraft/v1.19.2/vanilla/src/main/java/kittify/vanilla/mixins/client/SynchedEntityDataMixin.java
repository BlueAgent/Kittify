package kittify.vanilla.mixins.client;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(SynchedEntityData.class)
public abstract class SynchedEntityDataMixin {
    /**
     * Ignore server updates to Elytra flying status, instead using the client-side one.
     */
    @Inject(method = "assignValues(Ljava/util/List;)V", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;assignValue(Lnet/minecraft/network/syncher/SynchedEntityData$DataItem;Lnet/minecraft/network/syncher/SynchedEntityData$DataItem;)V", opcode = Opcodes.INVOKEVIRTUAL, ordinal = 0), require = 1)
    private <T> void kittify$assignValues$beforeAssignValue(List<SynchedEntityData.DataItem<?>> entries, CallbackInfo ci, Iterator<T> entriesIterator, SynchedEntityData.DataItem<T> sourceRaw, SynchedEntityData.DataItem<T> destRaw) {
        if (sourceRaw.getAccessor() == Entity.DATA_SHARED_FLAGS_ID) {
            //noinspection unchecked
            SynchedEntityData.DataItem<Byte> source = (SynchedEntityData.DataItem<Byte>) sourceRaw;
            //noinspection unchecked
            SynchedEntityData.DataItem<Byte> dest = (SynchedEntityData.DataItem<Byte>) destRaw;
            byte currentFlags = dest.getValue();
            byte newFlags = source.getValue();
            int fallFlyingFlagMask = 1 << Entity.FLAG_FALL_FLYING;
            byte mergedFlags = (byte) ((newFlags & ~fallFlyingFlagMask) | (currentFlags & fallFlyingFlagMask));
            source.setValue(mergedFlags);
        }
    }
}
