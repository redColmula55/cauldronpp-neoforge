package rc55.mc.cauldronpp.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.api.PotionHelper;
import rc55.mc.cauldronpp.block.CauldronppBlocks;

import java.util.function.Function;

public class CauldronppItems {
    public static final DeferredRegister.Items FORGE_REGISTRY = DeferredRegister.createItems(Cauldronpp.MODID);

    public static final DeferredItem<CppPotionItem> CPP_POTION = register("potion", CppPotionItem::new, CppPotionItem.SETTING);
    public static final DeferredItem<CppSplashPotionItem> CPP_SPLASH_POTION = register("splash_potion", CppSplashPotionItem::new, CppSplashPotionItem.SETTINGS);
    public static final DeferredItem<CppLingeringPotionItem> CPP_LINGERING_POTION = register("lingering_potion", CppLingeringPotionItem::new, CppLingeringPotionItem.SETTINGS);
    public static final DeferredItem<CppTippedArrowItem> CPP_TIPPED_ARROW = register("tipped_arrow", CppTippedArrowItem::new, CppTippedArrowItem.SETTINGS);
    public static final DeferredItem<WaterBottleItem> WATER_BOTTLE = register("water_bottle", WaterBottleItem::new, WaterBottleItem.SETTINGS);

    private static DeferredItem<Item> register(String id) {
        return register(id, Item::new, new Item.Properties());
    }
    private static <T extends Item> DeferredItem<@NotNull T> register(String name, Function<Item.Properties, T> function, Item.Properties settings) {
        return FORGE_REGISTRY.register(name, id -> function.apply(settings.setId(ResourceKey.create(Registries.ITEM,id))));
    }

    public static void init(IEventBus eventBus) {
        FORGE_REGISTRY.register(eventBus);
        eventBus.addListener(CauldronppItems::addItemToGroup);
        eventBus.addListener(CauldronppItems::regItemDispenserBehavior);
        Cauldronpp.LOGGER.info("Cauldron++ items registered.");
    }
    
    @SubscribeEvent
    private static void addItemToGroup(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(WATER_BOTTLE);
            event.accept(PotionHelper.getPotionItem(PotionHelper.DEFAULT_TYPE, 32767));
            event.accept(PotionHelper.getPotionItem(PotionHelper.DEFAULT_TYPE, 16123));
            event.accept(PotionHelper.getPotionItem(PotionHelper.DEFAULT_TYPE, 81621));
            event.accept(PotionHelper.getPotionItem(PotionHelper.DEFAULT_TYPE, 55577));
            event.accept(PotionHelper.getPotionItem(PotionHelper.SPLASH_TYPE, 32767));
            event.accept(PotionHelper.getPotionItem(PotionHelper.SPLASH_TYPE, 16123));
            event.accept(PotionHelper.getPotionItem(PotionHelper.SPLASH_TYPE, 81621));
            event.accept(PotionHelper.getPotionItem(PotionHelper.SPLASH_TYPE, 55577));
            event.accept(PotionHelper.getPotionItem(PotionHelper.LINGERING_TYPE, 32767));
            event.accept(PotionHelper.getPotionItem(PotionHelper.LINGERING_TYPE, 16123));
            event.accept(PotionHelper.getPotionItem(PotionHelper.LINGERING_TYPE, 81621));
            event.accept(PotionHelper.getPotionItem(PotionHelper.LINGERING_TYPE, 55577));
        }
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS || event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(CauldronppBlocks.CPP_CAULDRON);
        }
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(PotionHelper.getPotionItem(PotionHelper.ARROW_TYPE, 32767));
            event.accept(PotionHelper.getPotionItem(PotionHelper.ARROW_TYPE, 16123));
            event.accept(PotionHelper.getPotionItem(PotionHelper.ARROW_TYPE, 81621));
            event.accept(PotionHelper.getPotionItem(PotionHelper.ARROW_TYPE, 55577));
        }
    }

    @SubscribeEvent
    private static void regItemDispenserBehavior(RegisterEvent event) {
        if (!Registries.ITEM.equals(event.getRegistryKey())) return;
        DispenserBlock.registerBehavior(WATER_BOTTLE, new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior fallbackBehavior = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute(BlockSource pointer, ItemStack stack) {
                ServerLevel serverWorld = pointer.level();
                BlockPos blockPos = pointer.pos();
                BlockPos blockPos2 = pointer.pos().relative(pointer.state().getValue(DispenserBlock.FACING));
                if (!serverWorld.getBlockState(blockPos2).is(BlockTags.CONVERTABLE_TO_MUD)) {
                    return this.fallbackBehavior.dispense(pointer, stack);
                } else {
                    for (int i = 0; i < 5; i++) {
                        serverWorld.sendParticles(
                                ParticleTypes.SPLASH,
                                blockPos.getX() + serverWorld.getRandom().nextDouble(),
                                blockPos.getY() + 1,
                                blockPos.getZ() + serverWorld.getRandom().nextDouble(),
                                1,
                                0.0,
                                0.0,
                                0.0,
                                1.0
                        );
                    }

                    serverWorld.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    serverWorld.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
                    serverWorld.setBlockAndUpdate(blockPos2, Blocks.MUD.defaultBlockState());
                    return this.consumeWithRemainder(pointer, stack, new ItemStack(Items.GLASS_BOTTLE));
                }
            }
        });
    }
}
