package plus.dragons.createenchantmentindustry;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = EnchantmentIndustry.ID, bus = EventBusSubscriber.Bus.MOD, value = { Dist.CLIENT, Dist.DEDICATED_SERVER })
public class EnchantmentIndustryData {
    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        EnchantmentIndustry.ADVANCEMENT_FACTORY.datagen(event);
    }
}
