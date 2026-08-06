package plus.dragons.createenchantmentindustrylegacy.entry;

import com.simibubi.create.AllCreativeModeTabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import plus.dragons.createenchantmentindustrylegacy.EnchantmentIndustry;

public class CeiCreativeModeTab {
    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EnchantmentIndustry.ID);

    /**
     * The tab is left empty here on purpose. Registrate fills it through
     * {@link net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent} for every entry built
     * while this tab is the registrate's default one - listing the items here as well would add each
     * of them twice and make the creative menu throw.
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB =
            REGISTER.register("base", () -> CreativeModeTab.builder()
                    .title(Component.literal("CEI"))
                    .withTabsBefore(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey(),
                            AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey())
                    .icon(CeiItems.ENCHANTING_GUIDE::asStack)
                    .build());

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
        // Registrate defaults to CreativeModeTabs.SEARCH; point it at our own tab before any entry is built.
        EnchantmentIndustry.REGISTRATE.defaultCreativeTab(CREATIVE_TAB.getKey());
    }
}
