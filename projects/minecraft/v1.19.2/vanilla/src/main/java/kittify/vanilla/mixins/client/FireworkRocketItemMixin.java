package kittify.vanilla.mixins.client;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FireworkRocketItem.class)
public abstract class FireworkRocketItemMixin extends Item {
    public FireworkRocketItemMixin(Properties properties) {
        super(properties);
    }

    /**
     * Allows using fireworks with an elytra client-side.
     */
    @Inject(method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z", opcode = Opcodes.GETFIELD, ordinal = 0), require = 1)
    private void kittify$use$isClientSide0(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, ItemStack itemStack) {
        if (level.isClientSide) {
            FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(level, itemStack, player);
            level.addFreshEntity(fireworkRocketEntity);
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
        }
    }
}
