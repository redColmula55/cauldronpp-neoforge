package rc55.mc.cauldronpp;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rc55.mc.cauldronpp.block.CauldronppBlocks;
import rc55.mc.cauldronpp.blockEntity.CauldronppBlockEntityTypes;
import rc55.mc.cauldronpp.datagen.RecipeDataGen;
import rc55.mc.cauldronpp.datagen.lang.LangEnDataGen;
import rc55.mc.cauldronpp.datagen.lang.LangZhCnDataGen;
import rc55.mc.cauldronpp.datagen.loot.BlockLootTableDataGen;
import rc55.mc.cauldronpp.datagen.tag.BlockTagDataGen;
import rc55.mc.cauldronpp.datagen.tag.ItemTagDataGen;
import rc55.mc.cauldronpp.item.CauldronppItems;

import java.util.List;
import java.util.Set;

@Mod(Cauldronpp.MODID)
public class Cauldronpp {
	//mod id
	public static final String MODID = "cauldronpp";
	//日志
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public Cauldronpp(IEventBus modEventBus, ModContainer modContainer) {
		CauldronppItems.init(modEventBus);
		CauldronppBlocks.init(modEventBus);
        CauldronppBlockEntityTypes.init(modEventBus);

        modEventBus.addListener(Cauldronpp::generateData);

		LOGGER.info("Cauldron++ loaded.");
	}

    //数据生成
    @SubscribeEvent
    private static void generateData(GatherDataEvent.Client event) {
        //配方
        event.createProvider(RecipeDataGen.Runner::new);
        //标签
        event.createProvider(BlockTagDataGen::new);
        event.createProvider(ItemTagDataGen::new);
        //战利品表
        event.createProvider((output, lookupProvider) -> new LootTableProvider(
                output,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(BlockLootTableDataGen::new, LootContextParamSets.BLOCK)),
                lookupProvider
        ));
        //语言文件
        event.createProvider(LangEnDataGen::new);
        event.createProvider(LangZhCnDataGen::new);
    }
}