package rc55.mc.cauldronpp.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import rc55.mc.cauldronpp.api.CppCauldronBehavior;
import rc55.mc.cauldronpp.api.CppCauldronLiquidType;
import rc55.mc.cauldronpp.api.PotionHelper;
import rc55.mc.cauldronpp.blockEntity.CauldronppBlockEntityTypes;
import rc55.mc.cauldronpp.blockEntity.CppCauldronBlockEntity;

import java.util.Optional;

public class CppCauldronBlock extends BaseEntityBlock {

    private static final VoxelShape RAYCAST_SHAPE = box(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape OUTLINE_SHAPE = Shapes.join(
            Shapes.block(),
            Shapes.or(
                    box(0.0, 0.0, 4.0, 16.0, 3.0, 12.0),
                    box(4.0, 0.0, 0.0, 12.0, 3.0, 16.0),
                    box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
                    RAYCAST_SHAPE
            ),
            BooleanOp.ONLY_FIRST
    );

    public static final BooleanProperty EMITS_LIGHT = BooleanProperty.create("emits_light");

    protected CppCauldronBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(EMITS_LIGHT, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CppCauldronBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(EMITS_LIGHT);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CppCauldronBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return RAYCAST_SHAPE;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CauldronppBlockEntityTypes.CAULDRON.get(), CppCauldronBlockEntity::tick);
    }
    //比较器
    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return world.getBlockEntity(pos) instanceof CppCauldronBlockEntity cauldron ? cauldron.getComparatorOutput() : 0;
    }

    //右键
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionHand hand = player.getUsedItemHand();
        Optional<CppCauldronBlockEntity> optional = world.getBlockEntity(pos, CauldronppBlockEntityTypes.CAULDRON.get());
        if (optional.isPresent()) {
            CppCauldronBlockEntity cauldron = optional.get();
            return cauldron.updateLiquid(world, pos, state, player, hand, player.getItemInHand(hand));
        }
        return InteractionResult.FAIL;
    }

    //实体碰撞
    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        Optional<CppCauldronBlockEntity> optional = world.getBlockEntity(pos, CauldronppBlockEntityTypes.CAULDRON.get());
        if (optional.isPresent()) {
            CppCauldronBlockEntity cauldron = optional.get();
            if (cauldron.isEntityTouchingFluid(pos, entity)) {
                switch (cauldron.getLiquidType()) {
                    case POWDER_SNOW -> {
                        effectApplier.runBefore(InsideBlockEffectType.EXTINGUISH, e -> {
                            if (e.isOnFire() && cauldron.canDecrease(1) && !world.isClientSide()) {
                                e.extinguishFire();
                                if (world instanceof ServerLevel serverWorld) {
                                    if (e.mayInteract(serverWorld, pos)) {
                                        cauldron.decreaseAmount(1);
                                        cauldron.setLiquidType(CppCauldronLiquidType.WATER);
                                        world.gameEvent(e, GameEvent.BLOCK_CHANGE, pos);
                                        world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                                        world.updateNeighbourForOutputSignal(pos, this);
                                    }
                                }
                            }
                        });
                        effectApplier.apply(InsideBlockEffectType.FREEZE);
                        effectApplier.apply(InsideBlockEffectType.EXTINGUISH);
                    }
                    case LAVA -> effectApplier.apply(InsideBlockEffectType.LAVA_IGNITE);//entity.lavaHurt();
                    case WATER, COLORED_WATER -> {
                        if (entity.isOnFire() && cauldron.canDecrease(1) && !world.isClientSide()) {
                            entity.extinguishFire();
                            if (world instanceof ServerLevel serverWorld) {
                                if (entity.mayInteract(serverWorld, pos)) {
                                    cauldron.decreaseAmount(1);
                                    world.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
                                    world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                                    world.updateNeighbourForOutputSignal(pos, this);
                                }
                            }
                        }
                    }
                    case POTION -> {
                        if (entity instanceof LivingEntity livingEntity) {
                            for (final var effect : PotionHelper.getEffects(cauldron.getLiquidData())) {
                                livingEntity.addEffect(effect.withScaledDuration(0.01f));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void handlePrecipitation(BlockState state, Level world, BlockPos pos, Biome.Precipitation precipitation) {
        Optional<CppCauldronBlockEntity> optional = world.getBlockEntity(pos, CauldronppBlockEntityTypes.CAULDRON.get());
        if (optional.isPresent()) {
            CppCauldronBlockEntity cauldron = optional.get();
            if (canFillWithPrecipitation(world, precipitation)) {
                if (precipitation == Biome.Precipitation.RAIN) {
                    if (cauldron.canIncrease(CppCauldronBehavior.BOTTLE_LEVEL) && (cauldron.isEmpty() || cauldron.getLiquidType() == CppCauldronLiquidType.WATER)) {
                        cauldron.setLiquidType(CppCauldronLiquidType.WATER);
                        cauldron.increaseAmount(CppCauldronBehavior.BOTTLE_LEVEL);
                    }
                    world.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
                    world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                    world.updateNeighbourForOutputSignal(pos, this);
                } else if (precipitation == Biome.Precipitation.SNOW) {
                    if (cauldron.canIncrease(CppCauldronBehavior.BOTTLE_LEVEL) && (cauldron.isEmpty() || cauldron.getLiquidType() == CppCauldronLiquidType.POWDER_SNOW)) {
                        cauldron.setLiquidType(CppCauldronLiquidType.POWDER_SNOW);
                        cauldron.increaseAmount(CppCauldronBehavior.BOTTLE_LEVEL);
                    }
                    world.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
                    world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                    world.updateNeighbourForOutputSignal(pos, this);
                }
            }
        }
    }
    //计划刻
    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        BlockPos dripPos = PointedDripstoneBlock.findStalactiteTipAboveCauldron(world, pos);
        if (dripPos != null) {
            Optional<CppCauldronBlockEntity> optional = world.getBlockEntity(pos, CauldronppBlockEntityTypes.CAULDRON.get());
            if (optional.isPresent()) {
                Fluid fluid = PointedDripstoneBlock.getCauldronFillFluidType(world, dripPos);//滴水石锥
                if (fluid.is(FluidTags.WATER)) {//水
                    CppCauldronBlockEntity cauldron = optional.get();
                    if (cauldron.isEmpty() || (cauldron.getLiquidType() == CppCauldronLiquidType.WATER && cauldron.canIncrease(CppCauldronBehavior.BOTTLE_LEVEL))) cauldron.increaseAmount(CppCauldronBehavior.BOTTLE_LEVEL);
                    cauldron.setLiquidType(CppCauldronLiquidType.WATER);
                    world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
                    world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                    world.updateNeighbourForOutputSignal(pos, this);
                } else if (fluid.is(FluidTags.LAVA)) {//岩浆
                    CppCauldronBlockEntity cauldron = optional.get();
                    if (cauldron.isEmpty() || (cauldron.getLiquidType() == CppCauldronLiquidType.LAVA && cauldron.canIncrease(CppCauldronBehavior.BOTTLE_LEVEL))) cauldron.increaseAmount(CppCauldronBehavior.BOTTLE_LEVEL);
                    cauldron.setLiquidType(CppCauldronLiquidType.LAVA);
                    world.setBlockAndUpdate(pos, state.setValue(EMITS_LIGHT, true));
                    world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
                    world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
                    world.updateNeighbourForOutputSignal(pos, this);
                }
            }
        }
    }
    private static boolean canFillWithPrecipitation(Level world, Biome.Precipitation precipitation) {
        if (precipitation == Biome.Precipitation.RAIN) {
            return world.getRandom().nextFloat() < 0.05F;
        } else {
            return precipitation == Biome.Precipitation.SNOW && world.getRandom().nextFloat() < 0.1F;
        }
    }
}
