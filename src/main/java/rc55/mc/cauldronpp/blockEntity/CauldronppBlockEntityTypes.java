package rc55.mc.cauldronpp.blockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.block.CauldronppBlocks;

import java.util.function.Supplier;

public class CauldronppBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> FORGE_REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Cauldronpp.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CppCauldronBlockEntity>> CAULDRON = register("cauldron",
            () -> new BlockEntityType<>(CppCauldronBlockEntity::new, CauldronppBlocks.CPP_CAULDRON.get())
    );

    public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String id, Supplier<BlockEntityType<T>> factory) {
        return FORGE_REGISTRY.register(id, factory);
    }
    public static void init(IEventBus eventBus) {
        FORGE_REGISTRY.register(eventBus);
        Cauldronpp.LOGGER.info("Cauldron++ block entity registered.");
    }
}
