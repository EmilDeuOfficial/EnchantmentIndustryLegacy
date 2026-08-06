package plus.dragons.createenchantmentindustry.foundation.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import plus.dragons.createenchantmentindustry.EnchantmentIndustry;

@EventBusSubscriber(modid = EnchantmentIndustry.ID, bus = EventBusSubscriber.Bus.MOD)
public class CeiConfigs {
    public static CeiServerConfig SERVER;
    public static ModConfigSpec SERVER_SPEC;

    public static void register(ModContainer container) {
        Pair<CeiServerConfig, ModConfigSpec> serverConfigPair = new ModConfigSpec.Builder().configure(builder -> {
            CeiServerConfig config = new CeiServerConfig();
            config.registerAll(builder);
            return config;
        });
        SERVER = serverConfigPair.getKey();
        SERVER_SPEC = serverConfigPair.getValue();
        container.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        if (SERVER_SPEC == event.getConfig().getSpec())
            SERVER.onLoad();
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        if (SERVER_SPEC == event.getConfig().getSpec())
            SERVER.onReload();
    }
}
