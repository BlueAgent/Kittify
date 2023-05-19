package kittify.vanilla.mixins.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
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
    @Inject(method = "updateFallFlying()V", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "RETURN"))
    private void kittify$updateFallFlying$isClientSide1(CallbackInfo ci, boolean isFallFlying) {
        // Ideally we'd remove the condition so that it always updates the flag, but this is okay as well.
        if (this.level.isClientSide) {
            this.setSharedFlag(FLAG_FALL_FLYING, isFallFlying);
        }
    }
}
