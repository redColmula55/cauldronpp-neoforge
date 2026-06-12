package rc55.mc.cauldronpp.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import rc55.mc.cauldronpp.block.CppCauldronBlock;
import rc55.mc.cauldronpp.blockEntity.CppCauldronBlockEntity;
import rc55.mc.cauldronpp.item.CauldronppItems;

import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
public interface CppCauldronBehavior {
    static InteractionResult tryInteract(Level world, BlockPos pos, BlockState state, CppCauldronBlockEntity blockEntity, Player player, InteractionHand hand, ItemStack stack) {
        return BEHAVIORS.getOrDefault(stack.getItem(), EMPTY).interact(world, pos, state, blockEntity, player, hand, stack);
    }

    //交互
    InteractionResult interact(Level world, BlockPos pos, BlockState state, CppCauldronBlockEntity blockEntity, Player player, InteractionHand hand, ItemStack stack);
    //空交互（不进行任何操作，直接返回ActionResult.PASS）
    CppCauldronBehavior EMPTY = (world, pos, state, blockEntity, player, hand, stack) -> InteractionResult.PASS;

    //常量
    int BOTTLE_LEVEL = 1;//1瓶
    int BUCKET_LEVEL = 3;//1桶（3瓶）
    int MAX_LEVEL = 3;//最大容量（1桶）

    //清洗潜影盒
    CppCauldronBehavior CLEAN_SHULKER_BOX = (world, pos, state, cauldron, player, hand, stack) -> {
        Block block = Block.byItem(stack.getItem());
        if (!(block instanceof ShulkerBoxBlock)) {
            return InteractionResult.PASS;
        } else if (!cauldron.canDecrease(1)) {
            return InteractionResult.PASS;
        } else {
            if (!world.isClientSide()) {
                ItemStack itemStack = stack.transmuteCopy(Blocks.SHULKER_BOX, 1);
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, itemStack, false));
                player.awardStat(Stats.CLEAN_SHULKER_BOX);
                cauldron.decreaseAmount(1);
                update(world, pos, state, cauldron, player);
            }
            return InteractionResult.SUCCESS;
        }
    };
    //清洗旗帜
    CppCauldronBehavior CLEAN_BANNER = (world, pos, state, cauldron, player, hand, stack) -> {
        BannerPatternLayers bannerPatternsComponent = stack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
        if (bannerPatternsComponent.layers().isEmpty()) {
            return InteractionResult.PASS;
        } else if (!cauldron.canDecrease(1)) {
            return InteractionResult.PASS;
        } else {
            if (!world.isClientSide()) {
                ItemStack itemStack = stack.copyWithCount(1);
                itemStack.set(DataComponents.BANNER_PATTERNS, bannerPatternsComponent.removeLast());
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, itemStack, false));
                cauldron.decreaseAmount(1);
                update(world, pos, state, cauldron, player);
            }

            return InteractionResult.SUCCESS;
        }
    };
    //清洗，染色可染色的皮革盔甲
    CppCauldronBehavior DYEABLE_ITEM_BEHAVIOR = (world, pos, state, cauldron, player, hand, stack) -> {
        if (cauldron.canDecrease(1)){
            if (cauldron.getLiquidType() == CppCauldronLiquidType.WATER) {//清洗
                if (!stack.has(DataComponents.DYED_COLOR)) return InteractionResult.CONSUME;
                if (!world.isClientSide()) {
                    stack.remove(DataComponents.DYED_COLOR);
                    cauldron.decreaseAmount(1);
                    update(world, pos, state, cauldron, player);
                }
            } else if (cauldron.getLiquidType() == CppCauldronLiquidType.COLORED_WATER) {//染色
                if (!world.isClientSide()) {
                    if (stack.has(DataComponents.DYED_COLOR)) {
                        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(getDyedColor(stack.get(DataComponents.DYED_COLOR).rgb(), cauldron.getLiquidData())));
                    } else {
                        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(cauldron.getLiquidData()));
                    }
                    player.setItemInHand(hand, stack);
                    cauldron.decreaseAmount(1);
                    update(world, pos, state, cauldron, player);
                }
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    };
    //染色
    CppCauldronBehavior DYE_ITEM_BEHAVIOR = (world, pos, state, cauldron, player, hand, stack) -> {
        if (!cauldron.isEmpty() && (cauldron.getLiquidType() == CppCauldronLiquidType.COLORED_WATER || cauldron.getLiquidType() == CppCauldronLiquidType.WATER)) {
            if (stack.has(DataComponents.DYE)) {
                cauldron.setLiquidData(getDyedColor(cauldron.getLiquidData(), stack.get(DataComponents.DYE)));
            }
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            if (!player.isCreative()) stack.shrink(1);
            cauldron.setLiquidType(CppCauldronLiquidType.COLORED_WATER);
            update(world, pos, state, cauldron, player);
            world.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    };

    //炼药锅交互逻辑
    Map<ItemLike, CppCauldronBehavior> BEHAVIORS = Util.make(new HashMap<>(), map -> {
        //岩浆，细雪，空桶
        map.put(Items.LAVA_BUCKET, (world, pos, state, cauldron, player, hand, stack) ->
                fill(world, pos, state, cauldron, player, hand, stack, CppCauldronLiquidType.LAVA, SoundEvents.BUCKET_EMPTY_LAVA, true));
        map.put(Items.POWDER_SNOW_BUCKET, (world, pos, state, cauldron, player, hand, stack) ->
                fill(world, pos, state, cauldron, player, hand, stack, CppCauldronLiquidType.POWDER_SNOW, SoundEvents.BUCKET_EMPTY_POWDER_SNOW, false));
        map.put(Items.BUCKET, (world, pos, state, cauldron, player, hand, stack) -> {
            if (cauldron.canDecrease(BUCKET_LEVEL) && !cauldron.isPotion()) {
                if (cauldron.getLiquidType().getBucketItem() != null) {
                    if (cauldron.getLiquidType().getBucketFillSound() != null) {
                        world.playSound(null, pos, cauldron.getLiquidType().getBucketFillSound(), SoundSource.BLOCKS);
                    }
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, cauldron.getLiquidType().getBucketItem().getDefaultInstance()));
                    cauldron.decreaseAmount(BUCKET_LEVEL);
                    player.awardStat(Stats.ITEM_USED.get(Items.BUCKET));
                    world.setBlockAndUpdate(pos, state.setValue(CppCauldronBlock.EMITS_LIGHT, false));
                    update(world, pos, state, cauldron, player);
                    return InteractionResult.SUCCESS;
                } else return InteractionResult.PASS;
            } else return InteractionResult.PASS;
        });
        //水，药水
        map.put(Items.WATER_BUCKET, (world, pos, state, cauldron, player, hand, stack) -> cauldron.updatePotion(world, pos, state, player, hand, stack));
        map.put(Items.POTION, (world, pos, state, cauldron, player, hand, stack) -> cauldron.updatePotion(world, pos, state, player, hand, stack));
        map.put(CauldronppItems.WATER_BOTTLE.get(), (world, pos, state, cauldron, player, hand, stack) -> cauldron.updatePotion(world, pos, state, player, hand, stack));
        map.put(Items.GLASS_BOTTLE, (world, pos, state, cauldron, player, hand, stack) -> cauldron.updatePotion(world, pos, state, player, hand, stack));
        map.put(Items.ARROW, (world, pos, state, cauldron, player, hand, stack) -> cauldron.updatePotion(world, pos, state, player, hand, stack));
        map.put(Items.NETHER_WART, (world, pos, state, cauldron, player, hand, stack) -> cauldron.updatePotion(world, pos, state, player, hand, stack));
        for (Item item : PotionHelper.brewingMaterial.keySet()) {
            map.put(item, (world, pos, state, cauldron, player, hand, stack) -> cauldron.updatePotion(world, pos, state, player, hand, stack));
        }
        for (Item item : PotionHelper.brewingMaterialType.keySet()) {
            map.put(item, (world, pos, state, cauldron, player, hand, stack) -> cauldron.updatePotion(world, pos, state, player, hand, stack));
        }
        //清洗
        map.put(Items.WHITE_BANNER, CLEAN_BANNER);
        map.put(Items.ORANGE_BANNER, CLEAN_BANNER);
        map.put(Items.MAGENTA_BANNER, CLEAN_BANNER);
        map.put(Items.LIGHT_BLUE_BANNER, CLEAN_BANNER);
        map.put(Items.YELLOW_BANNER, CLEAN_BANNER);
        map.put(Items.LIME_BANNER, CLEAN_BANNER);
        map.put(Items.PINK_BANNER, CLEAN_BANNER);
        map.put(Items.GRAY_BANNER, CLEAN_BANNER);
        map.put(Items.LIGHT_GRAY_BANNER, CLEAN_BANNER);
        map.put(Items.CYAN_BANNER, CLEAN_BANNER);
        map.put(Items.PURPLE_BANNER, CLEAN_BANNER);
        map.put(Items.BLUE_BANNER, CLEAN_BANNER);
        map.put(Items.BROWN_BANNER, CLEAN_BANNER);
        map.put(Items.GREEN_BANNER, CLEAN_BANNER);
        map.put(Items.RED_BANNER, CLEAN_BANNER);
        map.put(Items.BLACK_BANNER, CLEAN_BANNER);

        map.put(Items.SHIELD, (world, pos, state, cauldron, player, hand, stack) -> {
            if (cauldron.getLiquidType() == CppCauldronLiquidType.WATER && cauldron.canDecrease(1)) {
                DyeColor color = stack.get(DataComponents.BASE_COLOR);
                if (color == null) {
                    return InteractionResult.PASS;
                } else {
                    if (!world.isClientSide()) {
                        stack.remove(DataComponents.BANNER_PATTERNS);
                        stack.remove(DataComponents.BASE_COLOR);
                        player.awardStat(Stats.CLEAN_BANNER);
                        cauldron.decreaseAmount(1);
                        update(world, pos, state, cauldron, player);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });

        map.put(Items.WHITE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.ORANGE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.MAGENTA_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.LIGHT_BLUE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.YELLOW_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.LIME_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.PINK_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.GRAY_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.LIGHT_GRAY_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.CYAN_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.PURPLE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.BLUE_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.BROWN_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.GREEN_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.RED_SHULKER_BOX, CLEAN_SHULKER_BOX);
        map.put(Items.BLACK_SHULKER_BOX, CLEAN_SHULKER_BOX);
        //皮质物品清洗，染色
        map.put(Items.LEATHER_HELMET, DYEABLE_ITEM_BEHAVIOR);
        map.put(Items.LEATHER_CHESTPLATE, DYEABLE_ITEM_BEHAVIOR);
        map.put(Items.LEATHER_LEGGINGS, DYEABLE_ITEM_BEHAVIOR);
        map.put(Items.LEATHER_BOOTS, DYEABLE_ITEM_BEHAVIOR);
        map.put(Items.LEATHER_HORSE_ARMOR, DYEABLE_ITEM_BEHAVIOR);
        //水染色
        map.put(Items.WHITE_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.ORANGE_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.MAGENTA_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.LIGHT_BLUE_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.YELLOW_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.LIME_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.PINK_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.GRAY_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.LIGHT_GRAY_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.CYAN_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.PURPLE_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.BLUE_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.BROWN_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.GREEN_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.RED_DYE, DYE_ITEM_BEHAVIOR);
        map.put(Items.BLACK_DYE, DYE_ITEM_BEHAVIOR);

        map.put(Items.BONE_MEAL, DYE_ITEM_BEHAVIOR);
        map.put(Items.LAPIS_LAZULI, DYE_ITEM_BEHAVIOR);
        map.put(Items.COCOA_BEANS, DYE_ITEM_BEHAVIOR);
        map.put(Items.INK_SAC, DYE_ITEM_BEHAVIOR);
    });

    //填满
    static InteractionResult fill(Level world, BlockPos pos, BlockState state, CppCauldronBlockEntity blockEntity, Player player, InteractionHand hand, ItemStack stack, CppCauldronLiquidType liquidType, SoundEvent sound, boolean lit) {
        if (blockEntity.isEmpty()) {
            blockEntity.setLiquidType(liquidType);
            blockEntity.increaseAmount(BUCKET_LEVEL);
            world.playSound(null, pos, sound, SoundSource.BLOCKS);
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            world.setBlockAndUpdate(pos, state.setValue(CppCauldronBlock.EMITS_LIGHT, lit));
            update(world, pos, state, blockEntity, player);
            return InteractionResult.SUCCESS;
        } else return InteractionResult.PASS;
    }
    //更新
    static void update(Level world, BlockPos pos, BlockState state, CppCauldronBlockEntity blockEntity, Player player) {
        blockEntity.setChanged();
        world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        world.updateNeighbourForOutputSignal(pos, state.getBlock());
    }
    //水染色
    static int getDyedColor(int oriColor, DyeColor color) {
        int dyeColor = color.getTextureDiffuseColor();
        return getDyedColor(oriColor, dyeColor);
    }
    static int getDyedColor(int oriColor, int dyeColor) {
        int max = 0;
        int[] results = new int[3];
        oriColor = oriColor & 0xffffff;
        float oriR = (oriColor >> 16 & 0xff) / 255.0f;
        float oriG = (oriColor >> 8 & 0xff) / 255.0f;
        float oriB = (oriColor & 0xff) / 255.0f;
        results[0] += (int) (oriR * 255.0f);
        results[1] += (int) (oriG * 255.0f);
        results[2] += (int) (oriB * 255.0f);
        max += (int) (Math.max(oriR, Math.max(oriG, oriB)) * 255.0f);

        dyeColor = dyeColor & 0xffffff;
        float[] dyeColors = new float[3];
        dyeColors[0] = (dyeColor >> 16 & 0xff) / 255.0f;
        dyeColors[1] = (dyeColor >> 8 & 0xff) / 255.0f;
        dyeColors[2] = (dyeColor & 0xff) / 255.0f;
        int dyeR = (int) (dyeColors[0] * 255.0f);
        int dyeG = (int) (dyeColors[1] * 255.0f);
        int dyeB = (int) (dyeColors[2] * 255.0f);
        max += Math.max(dyeR, Math.max(dyeG, dyeB));
        results[0] += dyeR;
        results[1] += dyeG;
        results[2] += dyeB;

        int resultR = results[0] / 2;
        int resultG = results[1] / 2;
        int resultB = results[2] / 2;

        float avg = (float) max / 2;
        float max2 = Math.max(resultR, Math.max(resultG, resultB));

        resultR = (int) (resultR * avg / max2);
        resultG = (int) (resultG * avg / max2);
        resultB = (int) (resultB * avg / max2);

        return (resultR << 16) | (resultG << 8) | resultB;
    }
}
