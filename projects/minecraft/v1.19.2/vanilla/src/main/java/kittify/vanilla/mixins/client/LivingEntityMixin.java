package kittify.vanilla.mixins.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * Allows Elytra Flight to start and stop client-side.
     */
    @Inject(method = "updateFallFlying()V", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z", opcode = Opcodes.GETFIELD, ordinal = 1), require = 1)
    private void kittify$updateFallFlying$isClientSide1(CallbackInfo ci, boolean isFallFlying) {
        if (this.level.isClientSide) {
            // Wish I could inject after the field access, instead of before without using a redirect. :(
            // It would let us not have to duplicate this.
            // I really don't like redirects.
            // Then again I guess there's not many people that are going to modify this field access anyway...
            this.setSharedFlag(FLAG_FALL_FLYING, isFallFlying);
        }
    }
}
