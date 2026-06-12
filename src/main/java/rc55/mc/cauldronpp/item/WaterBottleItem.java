package rc55.mc.cauldronpp.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.function.Consumer;

public class WaterBottleItem extends PotionItem {

    public static final Properties SETTINGS = new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).usingConvertsTo(Items.GLASS_BOTTLE);

    public WaterBottleItem(Properties settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultInstance() {
        return new ItemStack(this);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        Player playerEntity = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();
        BlockState blockState = world.getBlockState(blockPos);
        if (context.getClickedFace() != Direction.DOWN && blockState.is(BlockTags.CONVERTABLE_TO_MUD)) {
            world.playSound(null, blockPos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
            playerEntity.setItemInHand(context.getHand(), ItemUtils.createFilledResult(itemStack, playerEntity, new ItemStack(Items.GLASS_BOTTLE)));
            playerEntity.awardStat(Stats.ITEM_USED.get(this));
            if (!world.isClientSide()) {
                ServerLevel serverWorld = (ServerLevel)world;
                for (int i = 0; i < 5; i++) {
                    serverWorld.sendParticles(
                            ParticleTypes.SPLASH,
                            blockPos.getX() + world.getRandom().nextDouble(),
                            blockPos.getY() + 1,
                            blockPos.getZ() + world.getRandom().nextDouble(),
                            1,
                            0.0,
                            0.0,
                            0.0,
                            1.0
                    );
                }
            }
            world.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
            world.setBlockAndUpdate(blockPos, Blocks.MUD.defaultBlockState());
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId());
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor() : -1;
    }
}
