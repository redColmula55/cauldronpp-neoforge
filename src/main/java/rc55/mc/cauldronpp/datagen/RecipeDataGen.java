package rc55.mc.cauldronpp.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.block.CauldronppBlocks;

import java.util.concurrent.CompletableFuture;

public class RecipeDataGen extends RecipeProvider {

    protected RecipeDataGen(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shapeless(RecipeCategory.MISC, CauldronppBlocks.CPP_CAULDRON).requires(Items.CAULDRON).requires(Blocks.BREWING_STAND).group("cppcauldron")
                .unlockedBy(getHasName(Blocks.BREWING_STAND), has(Blocks.BREWING_STAND)).save(this.output, recipeKey("cppcauldron"));
        shaped(RecipeCategory.MISC, CauldronppBlocks.CPP_CAULDRON).define('a', Items.IRON_INGOT).define('b', Blocks.BREWING_STAND)
                .pattern("a a").pattern("aba").pattern("aaa").group("cppcauldron")
                .unlockedBy(getHasName(Blocks.BREWING_STAND), has(Blocks.BREWING_STAND)).save(this.output, recipeKey("cppcauldron2"));
    }

    private static ResourceKey<Recipe<?>> recipeKey(String id) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(Cauldronpp.MODID, id));
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new RecipeDataGen(registries, output);
        }

        @Override
        public String getName() {
            return "Cauldron++ Recipe generator";
        }
    }
}
