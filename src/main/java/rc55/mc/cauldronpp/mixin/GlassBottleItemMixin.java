package rc55.mc.cauldronpp.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import rc55.mc.cauldronpp.item.CauldronppItems;

@Mixin(BottleItem.class)
public abstract class GlassBottleItemMixin {
    @Shadow protected abstract ItemStack turnBottleIntoItem(ItemStack stack, Player player, ItemStack outputStack);

    //空瓶右键装水
    @Redirect(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/BottleItem;turnBottleIntoItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    public ItemStack cauldronpp_modifyWaterBottle(BottleItem instance, ItemStack stack, Player player, ItemStack outputStack) {
        return this.turnBottleIntoItem(stack, player, new ItemStack(CauldronppItems.WATER_BOTTLE.get()));
    }
}
