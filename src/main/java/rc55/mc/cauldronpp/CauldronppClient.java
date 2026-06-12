package rc55.mc.cauldronpp;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import rc55.mc.cauldronpp.client.render.CauldronppRenderer;

@Mod(value = Cauldronpp.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Cauldronpp.MODID, value = Dist.CLIENT)
public class CauldronppClient {
    public CauldronppClient(IEventBus eventBus, ModContainer container) {
        CauldronppRenderer.init(eventBus);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
    }
}
