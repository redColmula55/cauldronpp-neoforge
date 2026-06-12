package rc55.mc.cauldronpp.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Arrow.class)
public abstract class ArrowEntityMixin {
    @Shadow protected abstract PotionContents getPotionContents();
    @Shadow @Final private static EntityDataAccessor<Integer> ID_EFFECT_COLOR;

    //药箭颜色
    @Inject(at = @At("TAIL"), method = "updateColor")
    public void cauldronpp_setTippedArrowColor(CallbackInfo ci) {
        final Arrow self = ((Arrow)(Object)this);
        if (this.getPotionContents().customColor().isPresent()) {
            self.getEntityData().set(ID_EFFECT_COLOR, ARGB.opaque(this.getPotionContents().getColor()));
        }
    }
}
