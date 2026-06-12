package rc55.mc.cauldronpp.datagen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.block.CauldronppBlocks;

import java.util.concurrent.CompletableFuture;

public class BlockTagDataGen extends BlockTagsProvider {

    public BlockTagDataGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Cauldronpp.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CauldronppBlocks.CPP_CAULDRON.get());
    }
}
