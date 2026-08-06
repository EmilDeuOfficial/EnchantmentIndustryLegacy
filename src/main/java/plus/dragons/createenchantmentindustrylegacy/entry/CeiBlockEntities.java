package plus.dragons.createenchantmentindustrylegacy.entry;

import static plus.dragons.createenchantmentindustrylegacy.EnchantmentIndustry.REGISTRATE;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.disenchanter.DisenchanterBlockEntity;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.disenchanter.DisenchanterRenderer;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.enchanter.BlazeEnchanterBlockEntity;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.enchanter.BlazeEnchanterRenderer;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.printer.PrinterBlockEntity;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.printer.PrinterRenderer;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.fluids.experience.FurnaceExpExtractor;
import plus.dragons.createenchantmentindustrylegacy.foundation.mixin.AbstractFurnaceBlockEntityAccessor;

public class CeiBlockEntities {
    public static final BlockEntityEntry<DisenchanterBlockEntity> DISENCHANTER = REGISTRATE
            .blockEntity("disenchanter", DisenchanterBlockEntity::new)
            .validBlocks(CeiBlocks.DISENCHANTER)
            .renderer(() -> DisenchanterRenderer::new)
            .register();

    public static final BlockEntityEntry<PrinterBlockEntity> PRINTER = REGISTRATE
            .blockEntity("printer", PrinterBlockEntity::new)
            .validBlocks(CeiBlocks.PRINTER)
            .renderer(() -> PrinterRenderer::new)
            .register();

    public static final BlockEntityEntry<BlazeEnchanterBlockEntity> BLAZE_ENCHANTER = REGISTRATE
            .blockEntity("blaze_enchanter", BlazeEnchanterBlockEntity::new)
            .validBlocks(CeiBlocks.BLAZE_ENCHANTER)
            .renderer(() -> BlazeEnchanterRenderer::new)
            .register();

    public static void register() {}

    public static void registerCapabilityListener(IEventBus modEventBus) {
        modEventBus.register(CeiBlockEntities.class);
    }

    /**
     * Block capabilities replaced Forge's per-block-entity {@code getCapability} on NeoForge, so the
     * vanilla furnaces' experience output is registered here instead of through a mixin.
     */
    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                DISENCHANTER.get(), DisenchanterBlockEntity::getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                DISENCHANTER.get(), DisenchanterBlockEntity::getFluidHandler);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                PRINTER.get(), PrinterBlockEntity::getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                PRINTER.get(), PrinterBlockEntity::getFluidHandler);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                BLAZE_ENCHANTER.get(), BlazeEnchanterBlockEntity::getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                BLAZE_ENCHANTER.get(), BlazeEnchanterBlockEntity::getFluidHandler);

        for (BlockEntityType<? extends AbstractFurnaceBlockEntity> type : java.util.List.of(
                BlockEntityType.FURNACE, BlockEntityType.SMOKER, BlockEntityType.BLAST_FURNACE)) {
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, type, (furnace, side) -> {
                if (side == null)
                    return null;
                var recipesUsed = ((AbstractFurnaceBlockEntityAccessor) furnace).create_enchantment_industry_legacy$getRecipesUsed();
                return new FurnaceExpExtractor(recipesUsed, furnace);
            });
        }
    }
}
