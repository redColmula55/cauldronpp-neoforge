package rc55.mc.cauldronpp.blockEntity;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import rc55.mc.cauldronpp.api.CppCauldronBehavior;
import rc55.mc.cauldronpp.api.CppCauldronLiquidType;
import rc55.mc.cauldronpp.api.PotionHelper;
import rc55.mc.cauldronpp.item.CauldronppItems;

public class CppCauldronBlockEntity extends BlockEntity {
    public CppCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(CauldronppBlockEntityTypes.CAULDRON.get(), pos, state);
    }

    private CppCauldronLiquidType liquidType = CppCauldronLiquidType.NONE;
    private int liquidData;
    private byte potionType;
    private int amount;

    public static void tick(Level world, BlockPos pos, BlockState state, CppCauldronBlockEntity blockEntity) {
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("LiquidData", this.liquidData);
        output.putByte("PotionType", this.potionType);
        output.putInt("Level", this.amount);
        output.putInt("LiquidType", this.liquidType.getId());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.liquidData = input.getIntOr("LiquidData", 0);
        this.potionType = input.getByteOr("PotionType", (byte) 0);
        this.amount = input.getIntOr("Level", 0);
        this.liquidType = CppCauldronLiquidType.byId(input.getIntOr("LiquidType", 0));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return this.saveWithoutMetadata(registryLookup);
    }
    //更新锅内流体状态
    public InteractionResult updateLiquid(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, ItemStack stack) {
        return CppCauldronBehavior.tryInteract(world, pos, state, this, player, hand, stack);
    }
    //水，药水
    public InteractionResult updatePotion(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, ItemStack stack) {
        String materialProperty = PotionHelper.brewingMaterial.get(stack.getItem());
        byte typeMaterialProperty = PotionHelper.brewingMaterialType.getOrDefault(stack.getItem(), ((byte) -1));
        if (stack.is(Items.WATER_BUCKET) && !this.isFull() && (this.canBrew() || this.isEmpty())) {
            //加水（水桶）
            if (this.isEmpty()) this.liquidType = CppCauldronLiquidType.WATER;
            this.amount = CppCauldronBehavior.BUCKET_LEVEL;
            this.liquidData = PotionHelper.applyMaterial(this.liquidData, PotionHelper.WATER_MATERIAL);
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, Items.BUCKET.getDefaultInstance()));
            world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS);
            this.setChanged();
            return InteractionResult.SUCCESS;
        } else if (PotionHelper.isWaterBottle(stack) && !this.isFull() && (this.canBrew() || this.isEmpty())) {
            //加水（水瓶）
            if (this.isEmpty()) this.liquidType = CppCauldronLiquidType.WATER;
            this.increaseAmount(CppCauldronBehavior.BOTTLE_LEVEL);
            this.liquidData = PotionHelper.applyMaterial(this.liquidData, PotionHelper.WATER_MATERIAL);
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, Items.GLASS_BOTTLE.getDefaultInstance()));
            world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS);
            this.setChanged();
            return InteractionResult.SUCCESS;
        }
        if (!this.isEmpty() && this.canBrew()) {
            if (materialProperty != null) {//酿造材料
                this.liquidType = CppCauldronLiquidType.POTION;
                this.liquidData = PotionHelper.applyMaterial(this.liquidData, materialProperty);
                if (!player.isCreative()) stack.shrink(1);
                world.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS);
            } else if (typeMaterialProperty != -1) {//药水类型
                if (this.potionType == typeMaterialProperty) return InteractionResult.PASS;
                this.liquidType = CppCauldronLiquidType.POTION;
                this.potionType = typeMaterialProperty;
                if (stack.is(Items.DRAGON_BREATH)) {
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, Items.GLASS_BOTTLE.getDefaultInstance()));
                    world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS);
                } else {
                    if (!player.isCreative()) stack.shrink(1);
                    world.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS);
                }
            } else if (stack.is(Items.NETHER_WART)) {//地狱疣
                this.liquidType = CppCauldronLiquidType.POTION;
                this.liquidData = PotionHelper.applyMaterialNetherWart(this.liquidData);
                if (!player.isCreative()) stack.shrink(1);
                world.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS);
            } else if (stack.is(Items.GLASS_BOTTLE) && this.canDecrease(CppCauldronBehavior.BOTTLE_LEVEL)) {//取出药水
                if (this.isPotion()) {//药水
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, PotionHelper.getPotionItem(this.potionType, this.liquidData)));
                } else if (this.liquidType == CppCauldronLiquidType.WATER) {//水瓶
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, CauldronppItems.WATER_BOTTLE.toStack()));
                }
                this.decreaseAmount(CppCauldronBehavior.BOTTLE_LEVEL);
                world.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS);
            } else if (stack.is(Items.ARROW) && this.isPotion()) {//药箭
                if ((stack.getCount()-16 >= 0 || player.isCreative()) && this.canDecrease(CppCauldronBehavior.BOTTLE_LEVEL)) {
                    if (!player.isCreative()) stack.shrink(15);
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, PotionHelper.getPotionItem(PotionHelper.ARROW_TYPE, this.liquidData, 16)));
                    this.decreaseAmount(CppCauldronBehavior.BOTTLE_LEVEL);
                    world.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS);
                }
            } else return InteractionResult.PASS;
            this.setChanged();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    //获取信息（getter）
    public CppCauldronLiquidType getLiquidType() {
        return this.liquidType;
    }
    public int getAmount() {
        return this.amount;
    }
    public int getPotionType() {
        return this.potionType;
    }
    public int getLiquidData() {
        return this.liquidData;
    }

    //设置信息（setter）
    public void setLiquidData(int liquidData) {
        this.liquidData = liquidData;
    }
    public void setLiquidType(CppCauldronLiquidType type) {
        this.liquidType = type;
    }

    //是否能倒入
    public boolean canIncrease(int amount) {
        return this.amount + amount <= CppCauldronBehavior.MAX_LEVEL;
    }
    //倒入
    public void increaseAmount(int amount) {
        if (this.canIncrease(amount)) {
            this.amount += amount;
            if (this.isPotion()) this.liquidData = PotionHelper.applyMaterial(this.liquidData, PotionHelper.WATER_MATERIAL);
        }
    }
    //是否能取出
    public boolean canDecrease(int amount) {
        return this.amount - amount >= 0;
    }
    //取出
    public void decreaseAmount(int amount) {
        if (this.canDecrease(amount)) {
            this.amount -= amount;
            if (this.amount == 0) this.empty();
        }
    }
    //变为空
    public void empty() {
        this.amount = 0;
        this.potionType = 0;
        this.liquidData = 0;
        this.liquidType = CppCauldronLiquidType.NONE;
    }
    //是否为满
    public boolean isFull() {
        return this.amount >= CppCauldronBehavior.MAX_LEVEL;
    }
    //是否为空
    public boolean isEmpty() {
        return this.amount == 0;
    }
    //是否为药水
    public boolean isPotion() {
        return this.liquidType == CppCauldronLiquidType.POTION;
    }
    //是否能制作药水
    public boolean canBrew() {
        return this.isPotion() || this.liquidType == CppCauldronLiquidType.WATER;
    }
    //实体是否接触水面
    public boolean isEntityTouchingFluid(BlockPos pos, Entity entity) {
        return entity.getY() < pos.getY() + this.getFluidHeight() && entity.getBoundingBox().maxY > pos.getY() + 0.25 && !this.isEmpty();
    }
    //液面高度
    public double getFluidHeight() {
        return this.amount * 0.1875 + 0.375;
    }
    //颜色
    public int getLiquidRenderColor(BlockAndTintGetter world, BlockPos pos) {
        return switch (this.liquidType) {
            case POTION -> PotionHelper.getPotionColor(this.liquidData);
            case WATER -> BiomeColors.getAverageWaterColor(world, pos);
            case COLORED_WATER -> this.liquidData;
            default -> -1;
        };
    }
    //比较器
    public int getComparatorOutput() {
        return this.amount + (this.liquidType.getId() - 1) * CppCauldronBehavior.MAX_LEVEL;
    }
}
