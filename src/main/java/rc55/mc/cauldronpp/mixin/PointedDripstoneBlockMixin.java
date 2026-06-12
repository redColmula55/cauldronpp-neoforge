package rc55.mc.cauldronpp.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rc55.mc.cauldronpp.block.CauldronppBlocks;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

@Mixin(PointedDripstoneBlock.class)
public abstract class PointedDripstoneBlockMixin {
    @Shadow
    private static boolean canDripThrough(BlockGetter world, BlockPos pos, BlockState state) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private static Optional<BlockPos> findBlockVertical(
            LevelAccessor world,
            BlockPos pos,
            Direction.AxisDirection direction,
            BiPredicate<BlockPos, BlockState> continuePredicate,
            Predicate<BlockState> stopPredicate,
            int range
    ) {
        throw new UnsupportedOperationException();
    }

    //滴水石锥滴水灌满炼药锅
    @Inject(at = @At("HEAD"), method = "findFillableCauldronBelowStalactiteTip", cancellable = true)
    private static void cauldronpp_findCppCauldronToFill(Level world, BlockPos pos, Fluid fluid, CallbackInfoReturnable<BlockPos> cir) {
        Predicate<BlockState> predicate = state -> state.is(CauldronppBlocks.CPP_CAULDRON);
        BiPredicate<BlockPos, BlockState> biPredicate = (posx, state) -> canDripThrough(world, posx, state);
        findBlockVertical(world, pos, Direction.DOWN.getAxisDirection(), biPredicate, predicate, 11).ifPresent(cir::setReturnValue);
    }
}
