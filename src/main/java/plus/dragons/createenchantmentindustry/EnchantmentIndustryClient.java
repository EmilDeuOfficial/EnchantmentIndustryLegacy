package plus.dragons.createenchantmentindustry;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import plus.dragons.createenchantmentindustry.content.contraptions.fluids.ink.InkRenderingCamera;
import plus.dragons.createenchantmentindustry.entry.CeiBlockPartials;
import plus.dragons.createenchantmentindustry.foundation.config.CeiConfigs;
import plus.dragons.createenchantmentindustry.foundation.ponder.CeiPonderPlugin;

public class EnchantmentIndustryClient {
    public EnchantmentIndustryClient(IEventBus modEventBus, ModContainer modContainer) {
        // Has to happen here because flywheel lied about the init timing ;(
        // Things won't work if you try to init PartialModels in FMLClientSetupEvent
        CeiBlockPartials.register();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::loadComplete);
        NeoForge.EVENT_BUS.addListener(InkRenderingCamera::handleInkFogColor);
    }

    private void setup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> PonderIndex.addPlugin(new CeiPonderPlugin()));
    }

    private void loadComplete(final FMLLoadCompleteEvent event) {
        BaseConfigScreen.setDefaultActionFor(EnchantmentIndustry.ID, screen -> screen
                .withButtonLabels(null, null, "Gameplay Settings")
                .withSpecs(null, null, CeiConfigs.SERVER_SPEC));
    }
}
