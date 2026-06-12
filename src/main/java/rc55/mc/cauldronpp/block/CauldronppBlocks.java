package rc55.mc.cauldronpp.block;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import rc55.mc.cauldronpp.Cauldronpp;

import java.util.function.Function;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import rc55.mc.cauldronpp.item.CauldronppItems;

public class CauldronppBlocks {
    public static final DeferredRegister.Blocks FORGE_REGISTRY = DeferredRegister.createBlocks(Cauldronpp.MODID);

    public static final DeferredBlock<CppCauldronBlock> CPP_CAULDRON = register("cauldron", CppCauldronBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)
            .lightLevel(state -> state.getValue(CppCauldronBlock.EMITS_LIGHT) ? 15 : 0), true);

    private static <T extends Block> DeferredBlock<@NotNull T> register(
            String name,
            Function<BlockBehaviour.Properties, T> function,
            BlockBehaviour.Properties settings,
            boolean hasItem
    ) {
        final var block = FORGE_REGISTRY.register(name, id -> function.apply(settings.setId(ResourceKey.create(Registries.BLOCK, id))));
        if (hasItem) {
            CauldronppItems.FORGE_REGISTRY.registerSimpleBlockItem(block);
        }
        return block;
    }
    public static void init(IEventBus eventBus) {
        FORGE_REGISTRY.register(eventBus);
        Cauldronpp.LOGGER.info("Cauldron++ blocks registered.");
    }
}
