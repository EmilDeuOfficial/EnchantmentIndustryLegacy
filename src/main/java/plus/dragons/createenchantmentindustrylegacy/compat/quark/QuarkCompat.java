package plus.dragons.createenchantmentindustrylegacy.compat.quark;

import static plus.dragons.createenchantmentindustrylegacy.EnchantmentIndustry.LANG;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;
import plus.dragons.createenchantmentindustrylegacy.EnchantmentIndustry;
import plus.dragons.createenchantmentindustrylegacy.api.PrintEntryRegisterEvent;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.enchanter.Enchanting;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.printer.PrintEntry;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.printer.Printing;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiFluids;
import plus.dragons.createenchantmentindustrylegacy.foundation.config.CeiConfigs;

public class QuarkCompat {
    public static void registerPrintEntry() {
        if (ModList.get().isLoaded("quark")) {
            NeoForge.EVENT_BUS.addListener(QuarkCompat::register);
        }
    }

    private static void register(PrintEntryRegisterEvent event) {
        event.register(new PrintEntry() {
            private final ResourceLocation id = ResourceLocation.fromNamespaceAndPath("quark", "ancient_tome");

            @Override
            public @NotNull ResourceLocation id() {
                return EnchantmentIndustry.genRL("ancient_tome");
            }

            @Override
            public boolean match(@NotNull ItemStack toPrint) {
                return id.equals(BuiltInRegistries.ITEM.getKey(toPrint.getItem()));
            }

            @Override
            public boolean valid(@NotNull ItemStack target, @NotNull ItemStack tested) {
                return tested.is(Items.ENCHANTED_BOOK);
            }

            @Override
            public int requiredInkAmount(@NotNull ItemStack target) {
                var enchantment = getTomeEnchantment(target);
                if (enchantment == null)
                    return 50;
                return enchantment.value().getMinCost(1) + Enchanting.rarityLevel(enchantment);
            }

            @Override
            public @NotNull Fluid requiredInkType(@NotNull ItemStack target) {
                return CeiFluids.HYPER_EXPERIENCE.get();
            }

            @Override
            public boolean isTooExpensive(@NotNull ItemStack target, int limit) {
                return limit < requiredInkAmount(target);
            }

            @Override
            public void addToGoggleTooltip(@NotNull List<Component> tooltip, boolean isPlayerSneaking, @NotNull ItemStack target) {
                var b = LANG.itemName(target).style(ChatFormatting.DARK_PURPLE);
                b.forGoggles(tooltip, 1);
                boolean tooExpensive = Printing.isTooExpensive(this, target, CeiConfigs.SERVER.copierTankCapacity.get());
                if (tooExpensive)
                    tooltip.add(Component.literal("     ").append(LANG.translate(
                            "gui.goggles.too_expensive").component()).withStyle(ChatFormatting.RED));
                else
                    tooltip.add(Component.literal("     ").append(LANG.translate(
                            "gui.goggles.xp_consumption",
                            String.valueOf(requiredInkAmount(target))).component()).withStyle(ChatFormatting.AQUA));
                var e = getTomeEnchantment(target);
                if (e != null) {
                    tooltip.add(Component.literal("     ").append(getFullTooltipText(e)).withStyle(ChatFormatting.GRAY));
                }
            }

            @Override
            public @NotNull MutableComponent getDisplaySourceContent(@NotNull ItemStack target) {
                var ret = LANG.itemName(target);
                var e = getTomeEnchantment(target);
                if (e != null) {
                    ret.text(" / ");
                    ret.add(getFullTooltipText(e).copy());
                }
                return ret.component();
            }

            private static Holder<Enchantment> getTomeEnchantment(ItemStack stack) {
                for (Holder<Enchantment> enchantment : EnchantmentHelper.getEnchantmentsForCrafting(stack).keySet())
                    return enchantment;
                return null;
            }

            public static Component getFullTooltipText(Holder<Enchantment> ench) {
                return Component.translatable("quark.misc.ancient_tome_tooltip",
                        ench.value().description(),
                        Component.translatable("enchantment.level." + (ench.value().getMaxLevel() + 1)));
            }
        });
    }
}
