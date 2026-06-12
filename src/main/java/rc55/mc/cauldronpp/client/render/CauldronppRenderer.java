package rc55.mc.cauldronpp.client.render;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import rc55.mc.cauldronpp.Cauldronpp;
import rc55.mc.cauldronpp.blockEntity.CauldronppBlockEntityTypes;

public class CauldronppRenderer {
    public static void init(IEventBus eventBus) {
        eventBus.addListener(CauldronppRenderer::registerEntityRenderers);
        eventBus.addListener(CauldronppRenderer::registerModelLayer);
        Cauldronpp.LOGGER.info("Cauldron++ renderer added.");
    }

    public static ModelLayerLocation CAULDRON_INNER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Cauldronpp.MODID, "cauldron"), "main");

    @SubscribeEvent
    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CauldronppBlockEntityTypes.CAULDRON.get(),
                CppCauldronRenderer::new
        );
    }

    @SubscribeEvent
    private static void registerModelLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(
                CauldronppRenderer.CAULDRON_INNER, CppCauldronModel::getTexturedModelData
        );
    }
}
