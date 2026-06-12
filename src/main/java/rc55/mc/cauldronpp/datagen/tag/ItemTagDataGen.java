package rc55.mc.cauldronpp.datagen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.item.CauldronppItemTags;
import rc55.mc.cauldronpp.item.CauldronppItems;

import java.util.concurrent.CompletableFuture;

public class ItemTagDataGen extends ItemTagsProvider {

    public ItemTagDataGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Cauldronpp.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        tag(CauldronppItemTags.SPLASH_POTIONS).add(Items.SPLASH_POTION, CauldronppItems.CPP_SPLASH_POTION.get());
        tag(CauldronppItemTags.LINGERING_POTIONS).add(Items.LINGERING_POTION, CauldronppItems.CPP_LINGERING_POTION.get());
        tag(CauldronppItemTags.TIPPED_ARROWS).add(Items.TIPPED_ARROW, CauldronppItems.CPP_TIPPED_ARROW.get());
        tag(Tags.Items.POTIONS).add(CauldronppItems.CPP_POTION.get()).addTag(CauldronppItemTags.SPLASH_POTIONS).addTag(CauldronppItemTags.LINGERING_POTIONS);
        tag(ItemTags.ARROWS).addTag(CauldronppItemTags.TIPPED_ARROWS);
        tag(CauldronppItemTags.POTION_MATERIALS)
                .add(Items.NETHER_WART, Items.SUGAR, Items.GHAST_TEAR,Items.SPIDER_EYE, Items.FERMENTED_SPIDER_EYE, Items.BLAZE_POWDER, Items.MAGMA_CREAM,
                Items.RABBIT_FOOT,Items.GLOWSTONE_DUST, Items.REDSTONE, Items.GOLDEN_CARROT, Items.PHANTOM_MEMBRANE, Items.SLIME_BALL);
        tag(CauldronppItemTags.POTION_TYPE_MATERIALS).add(Items.GLISTERING_MELON_SLICE, Items.GUNPOWDER, Items.DRAGON_BREATH);
        tag(CauldronppItemTags.CAULDRON_BREWING_MATERIALS).addTag(CauldronppItemTags.POTION_MATERIALS).addTag(CauldronppItemTags.POTION_TYPE_MATERIALS);
    }
}
