package plus.dragons.createenchantmentindustrylegacy;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plus.dragons.createenchantmentindustrylegacy.compat.apotheosis.ApotheosisCompat;
import plus.dragons.createenchantmentindustrylegacy.compat.quark.QuarkCompat;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.fluids.experience.ExperienceOpenPipeEffectHandler;
import plus.dragons.createenchantmentindustrylegacy.dragonLibLegacy.advancement.AdvancementFactory;
import plus.dragons.createenchantmentindustrylegacy.dragonLibLegacy.init.SafeRegistrate;
import plus.dragons.createenchantmentindustrylegacy.dragonLibLegacy.lang.Lang;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiBlockEntities;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiBlocks;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiContainerTypes;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiCreativeModeTab;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiDataComponents;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiDisplaySources;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiEntityTypes;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiFluids;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiItems;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiPackets;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiRecipeTypes;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiTags;
import plus.dragons.createenchantmentindustrylegacy.foundation.advancement.CeiAdvancements;
import plus.dragons.createenchantmentindustrylegacy.foundation.config.CeiConfigs;
import plus.dragons.createenchantmentindustrylegacy.foundation.data.CeiLangGen;

@Mod(EnchantmentIndustry.ID)
public class EnchantmentIndustry {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String NAME = "Create: Enchantment Industry";
    public static final String ID = "create_enchantment_industry_legacy";
    public static final SafeRegistrate REGISTRATE = new SafeRegistrate(ID);
    public static final Lang LANG = new Lang(ID);
    public static final AdvancementFactory ADVANCEMENT_FACTORY = AdvancementFactory.create(NAME, ID,
            CeiAdvancements::register);

    public EnchantmentIndustry(IEventBus modEventBus, ModContainer modContainer) {
        IEventBus gameEventBus = NeoForge.EVENT_BUS;

        CeiConfigs.register(modContainer);

        registerEntries(modEventBus);
        modEventBus.addListener(this::setup);
        registerGameEvents(gameEventBus);

        if (FMLEnvironment.dist == Dist.CLIENT)
            new EnchantmentIndustryClient(modEventBus, modContainer);
    }

    private void registerEntries(IEventBus modEventBus) {
        CeiDataComponents.register(modEventBus);
        CeiBlocks.register();
        CeiBlockEntities.register();
        CeiBlockEntities.registerCapabilityListener(modEventBus);
        CeiContainerTypes.register();
        CeiEntityTypes.register();
        CeiFluids.register();
        CeiItems.register();
        CeiRecipeTypes.register(modEventBus);
        CeiTags.register();
        CeiCreativeModeTab.register(modEventBus);
        CeiDisplaySources.register();
        CeiPackets.register(modEventBus);
        CeiAdvancements.registerTriggers(modEventBus);
        CeiLangGen.register();
        REGISTRATE.registerEventListeners(modEventBus);
    }

    private void registerGameEvents(IEventBus gameEventBus) {
        gameEventBus.addListener(CeiFluids::handleInkEffect);
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CeiAdvancements.register();
            CeiFluids.registerLavaReaction();
            ExperienceOpenPipeEffectHandler.register();
            ApotheosisCompat.banTomeFromEnchanter();
            QuarkCompat.registerPrintEntry();
        });
    }

    public static ResourceLocation genRL(String name) {
        return ResourceLocation.fromNamespaceAndPath(ID, name);
    }
}
