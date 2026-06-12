package rc55.mc.cauldronpp.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import rc55.mc.cauldronpp.block.CauldronppBlocks;

import java.util.Set;
import java.util.stream.Collectors;

public class BlockLootTableDataGen extends BlockLootSubProvider {
    public BlockLootTableDataGen(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return CauldronppBlocks.FORGE_REGISTRY.getEntries().stream()
                .map(DeferredHolder::get)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void generate() {
        dropSelf(CauldronppBlocks.CPP_CAULDRON.get());
    }
}
