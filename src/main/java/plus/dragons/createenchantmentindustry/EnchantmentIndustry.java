package plus.dragons.createenchantmentindustry;

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
import plus.dragons.createenchantmentindustry.compat.apotheosis.ApotheosisCompat;
import plus.dragons.createenchantmentindustry.compat.quark.QuarkCompat;
import plus.dragons.createenchantmentindustry.content.contraptions.fluids.experience.ExperienceOpenPipeEffectHandler;
import plus.dragons.createenchantmentindustry.dragonLibLegacy.advancement.AdvancementFactory;
import plus.dragons.createenchantmentindustry.dragonLibLegacy.init.SafeRegistrate;
import plus.dragons.createenchantmentindustry.dragonLibLegacy.lang.Lang;
import plus.dragons.createenchantmentindustry.entry.CeiBlockEntities;
import plus.dragons.createenchantmentindustry.entry.CeiBlocks;
import plus.dragons.createenchantmentindustry.entry.CeiContainerTypes;
import plus.dragons.createenchantmentindustry.entry.CeiCreativeModeTab;
import plus.dragons.createenchantmentindustry.entry.CeiDataComponents;
import plus.dragons.createenchantmentindustry.entry.CeiDisplaySources;
import plus.dragons.createenchantmentindustry.entry.CeiEntityTypes;
import plus.dragons.createenchantmentindustry.entry.CeiFluids;
import plus.dragons.createenchantmentindustry.entry.CeiItems;
import plus.dragons.createenchantmentindustry.entry.CeiPackets;
import plus.dragons.createenchantmentindustry.entry.CeiRecipeTypes;
import plus.dragons.createenchantmentindustry.entry.CeiTags;
import plus.dragons.createenchantmentindustry.foundation.advancement.CeiAdvancements;
import plus.dragons.createenchantmentindustry.foundation.config.CeiConfigs;
import plus.dragons.createenchantmentindustry.foundation.data.CeiLangGen;

@Mod(EnchantmentIndustry.ID)
public class EnchantmentIndustry {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String NAME = "Create: Enchantment Industry";
    public static final String ID = "create_enchantment_industry";
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
