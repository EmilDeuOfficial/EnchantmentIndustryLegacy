package plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.printer;

import static plus.dragons.createenchantmentindustrylegacy.EnchantmentIndustry.LANG;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.NeoForge;
import plus.dragons.createenchantmentindustrylegacy.EnchantmentIndustry;
import plus.dragons.createenchantmentindustrylegacy.api.PrintEntryRegisterEvent;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.EnchantmentLevelCapUtil;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.EnchantmentLevelUtil;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.enchanter.Enchanting;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiFluids;
import plus.dragons.createenchantmentindustrylegacy.foundation.config.CeiConfigs;

public class PrintEntries {
    public static Map<ResourceLocation, PrintEntry> ENTRIES = new HashMap<>();

    static ItemEnchantments storedEnchantments(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentsForCrafting(stack);
    }

    static int writtenBookPageCount(ItemStack stack) {
        WrittenBookContent content = stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
        return content == null ? 0 : content.pages().size();
    }

    static {
        var e1 = new EnchantedBook();
        var e2 = new WrittenBook();
        var e3 = new NameTag();
        var e4 = new Schedule();
        var e5 = new ClipBoard();
        ENTRIES.put(e1.id(), e1);
        ENTRIES.put(e2.id(), e2);
        ENTRIES.put(e3.id(), e3);
        ENTRIES.put(e4.id(), e4);
        ENTRIES.put(e5.id(), e5);

        var event = new PrintEntryRegisterEvent();
        NeoForge.EVENT_BUS.post(event);
    }

    static class EnchantedBook implements PrintEntry {
        @Override
        public ResourceLocation id() {
            return EnchantmentIndustry.genRL("enchanted_book");
        }

        @Override
        public boolean match(ItemStack toPrint) {
            return toPrint.is(Items.ENCHANTED_BOOK);
        }

        @Override
        public boolean valid(ItemStack target, ItemStack tested) {
            return tested.is(Items.BOOK);
        }

        @Override
        public int requiredInkAmount(ItemStack target) {
            if (hasEnchantmentsAboveConfiguredCap(target))
                return -1;
            return (int) (getExperienceFromItem(target) *
                    (requiredInkType(target).isSame(CeiFluids.HYPER_EXPERIENCE.get()) ? CeiConfigs.SERVER.copyEnchantedBookWithHyperExperienceCostCoefficient.get() : CeiConfigs.SERVER.copyEnchantedBookCostCoefficient.get()));
        }

        @Override
        public Fluid requiredInkType(ItemStack target) {
            return isHyper(target) ? CeiFluids.HYPER_EXPERIENCE.get() : CeiFluids.EXPERIENCE.get();
        }

        private static boolean isHyper(ItemStack target) {
            for (var entry : storedEnchantments(target).entrySet()) {
                if (entry.getIntValue() > EnchantmentLevelUtil.getMaxLevel(entry.getKey()))
                    return true;
            }
            return false;
        }

        @Override
        public boolean isTooExpensive(ItemStack target, int limit) {
            if (hasEnchantmentsAboveConfiguredCap(target))
                return true;
            return (int) (getExperienceFromItem(target) * (requiredInkType(target).isSame(CeiFluids.HYPER_EXPERIENCE.get()) ? CeiConfigs.SERVER.copyEnchantedBookWithHyperExperienceCostCoefficient.get() : CeiConfigs.SERVER.copyEnchantedBookCostCoefficient.get())) > limit;
        }

        @Override
        public void addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking, ItemStack target) {
            var b = LANG.itemName(target).style(ChatFormatting.LIGHT_PURPLE);
            b.forGoggles(tooltip, 1);
            boolean tooExpensive = Printing.isTooExpensive(this, target, CeiConfigs.SERVER.copierTankCapacity.get());
            if (tooExpensive)
                tooltip.add(Component.literal("     ").append(LANG.translate(
                        "gui.goggles.too_expensive").component()).withStyle(ChatFormatting.RED));
            else {
                var hyper = isHyper(target);
                tooltip.add(Component.literal("     ").append(LANG.translate(
                        hyper ? "gui.goggles.hyper_xp_consumption" : "gui.goggles.xp_consumption",
                        String.valueOf((int) (getExperienceFromItem(target) * (requiredInkType(target).isSame(CeiFluids.HYPER_EXPERIENCE.get()) ? CeiConfigs.SERVER.copyEnchantedBookWithHyperExperienceCostCoefficient.get() : CeiConfigs.SERVER.copyEnchantedBookCostCoefficient.get())))).component()).withStyle(hyper ? ChatFormatting.AQUA : ChatFormatting.GREEN));
            }
            for (var e : storedEnchantments(target).entrySet()) {
                Holder<Enchantment> enchantment = e.getKey();
                Component name = Enchantment.getFullname(enchantment, e.getIntValue());
                tooltip.add(Component.literal("     ").append(name).withStyle(name.getStyle()));
            }
        }

        @Override
        public MutableComponent getDisplaySourceContent(ItemStack target) {
            var ret = LANG.itemName(target).text(" / ");
            for (var e : storedEnchantments(target).entrySet()) {
                Holder<Enchantment> enchantment = e.getKey();
                ret.add(Enchantment.getFullname(enchantment, e.getIntValue()).copy()).text(" ");
            }
            return ret.component();
        }

        public static int getExperienceFromItem(ItemStack itemStack) {
            int total = 0;
            for (var entry : storedEnchantments(itemStack).entrySet())
                total += Enchanting.getExperienceConsumption(entry.getKey(), entry.getIntValue());
            return total;
        }

        private static boolean hasEnchantmentsAboveConfiguredCap(ItemStack itemStack) {
            for (var entry : storedEnchantments(itemStack).entrySet()) {
                if (EnchantmentLevelCapUtil.exceedsConfiguredCap(entry.getKey(), entry.getIntValue()))
                    return true;
            }
            return false;
        }
    }

    static class WrittenBook implements PrintEntry {
        @Override
        public ResourceLocation id() {
            return EnchantmentIndustry.genRL("written_book");
        }

        @Override
        public boolean match(ItemStack toPrint) {
            return toPrint.is(Items.WRITTEN_BOOK);
        }

        @Override
        public boolean valid(ItemStack target, ItemStack tested) {
            return tested.is(Items.BOOK);
        }

        @Override
        public int requiredInkAmount(ItemStack target) {
            return writtenBookPageCount(target) * CeiConfigs.SERVER.copyWrittenBookCostPerPage.get();
        }

        @Override
        public Fluid requiredInkType(ItemStack target) {
            return CeiFluids.INK.get();
        }

        @Override
        public ItemStack print(ItemStack target, ItemStack material) {
            var ret = target.copy();
            if (!CeiConfigs.SERVER.copyingWrittenBookAlwaysGetOriginalVersion.get()) {
                WrittenBookContent content = ret.get(DataComponents.WRITTEN_BOOK_CONTENT);
                if (content != null) {
                    WrittenBookContent copy = content.tryCraftCopy();
                    if (copy != null)
                        ret.set(DataComponents.WRITTEN_BOOK_CONTENT, copy);
                }
            }
            return ret;
        }

        @Override
        public boolean isTooExpensive(ItemStack target, int limit) {
            return writtenBookPageCount(target) * CeiConfigs.SERVER.copyWrittenBookCostPerPage.get() > limit;
        }

        @Override
        public void addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking, ItemStack target) {
            var page = writtenBookPageCount(target);
            var b = LANG.builder()
                    .add(LANG.itemName(target)
                            .style(ChatFormatting.BLUE))
                    .text(ChatFormatting.GRAY, " / ")
                    .add(LANG.number(page)
                            .text(" ")
                            .add(page == 1 ? LANG.translate("generic.unit.page") : LANG.translate("generic.unit.pages"))
                            .style(ChatFormatting.DARK_GRAY));
            b.forGoggles(tooltip, 1);
            if (Printing.isTooExpensive(this, target, CeiConfigs.SERVER.copierTankCapacity.get()))
                tooltip.add(Component.literal("     ").append(LANG.translate(
                        "gui.goggles.too_expensive").component()).withStyle(ChatFormatting.RED));
            else
                tooltip.add(Component.literal("     ").append(LANG.translate(
                        "gui.goggles.ink_consumption",
                        String.valueOf(CeiConfigs.SERVER.copyWrittenBookCostPerPage.get() * page)).component()).withStyle(ChatFormatting.DARK_GRAY));
        }

        @Override
        public MutableComponent getDisplaySourceContent(ItemStack target) {
            var page = writtenBookPageCount(target);
            return LANG.builder()
                    .add(LANG.itemName(target))
                    .text(" / ")
                    .add(LANG.number(page)
                            .text(" ")
                            .add(page == 1 ? LANG.translate("generic.unit.page") : LANG.translate("generic.unit.pages")))
                    .component();
        }
    }

    static class NameTag implements PrintEntry {
        @Override
        public ResourceLocation id() {
            return EnchantmentIndustry.genRL("name_tag");
        }

        @Override
        public boolean match(ItemStack toPrint) {
            return toPrint.is(Items.NAME_TAG);
        }

        @Override
        public boolean valid(ItemStack target, ItemStack tested) {
            return !target.getHoverName().equals(tested.getHoverName());
        }

        @Override
        public int requiredInkAmount(ItemStack target) {
            return CeiConfigs.SERVER.copyNameTagCost.get();
        }

        @Override
        public ItemStack print(ItemStack target, ItemStack material) {
            if (material.is(Items.NAME_TAG))
                return target.copy();
            material.set(DataComponents.CUSTOM_NAME, target.getHoverName());
            return material;
        }

        @Override
        public boolean isTooExpensive(ItemStack target, int limit) {
            return CeiConfigs.SERVER.copyNameTagCost.get() > limit;
        }

        @Override
        public void addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking, ItemStack target) {
            var b = LANG.builder()
                    .add(Component.translatable(target.getDescriptionId()).withStyle(ChatFormatting.LIGHT_PURPLE))
                    .text(ChatFormatting.GREEN, " / ")
                    .add(LANG.itemName(target)
                            .style(ChatFormatting.GREEN));
            b.forGoggles(tooltip, 1);
            boolean tooExpensive = Printing.isTooExpensive(this, target, CeiConfigs.SERVER.copierTankCapacity.get());
            if (tooExpensive)
                tooltip.add(Component.literal("     ").append(LANG.translate(
                        "gui.goggles.too_expensive").component()).withStyle(ChatFormatting.RED));
            else
                tooltip.add(Component.literal("     ").append(LANG.translate(
                        "gui.goggles.xp_consumption",
                        String.valueOf(CeiConfigs.SERVER.copyNameTagCost.get())).component()).withStyle(ChatFormatting.GREEN));
        }

        @Override
        public MutableComponent getDisplaySourceContent(ItemStack target) {
            return LANG.builder()
                    .add(Component.translatable(target.getDescriptionId()))
                    .text(" / ")
                    .add(LANG.itemName(target)).component();
        }
    }

    static class Schedule implements PrintEntry {
        @Override
        public ResourceLocation id() {
            return EnchantmentIndustry.genRL("schedule");
        }

        @Override
        public boolean match(ItemStack toPrint) {
            return toPrint.is(AllItems.SCHEDULE.get());
        }

        @Override
        public boolean valid(ItemStack target, ItemStack tested) {
            return tested.is(target.getItem()) && !ItemStack.isSameItemSameComponents(target, tested);
        }

        @Override
        public int requiredInkAmount(ItemStack target) {
            return CeiConfigs.SERVER.copyTrainScheduleCost.get();
        }

        @Override
        public Fluid requiredInkType(ItemStack target) {
            return CeiFluids.INK.get();
        }

        @Override
        public boolean isTooExpensive(ItemStack target, int limit) {
            return CeiConfigs.SERVER.copyTrainScheduleCost.get() > limit;
        }

        @Override
        public void addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking, ItemStack target) {
            var b = LANG.itemName(target).style(ChatFormatting.BLUE);
            b.forGoggles(tooltip, 1);
            boolean tooExpensive = Printing.isTooExpensive(this, target, CeiConfigs.SERVER.copierTankCapacity.get());
            if (tooExpensive)
                tooltip.add(Component.literal("     ").append(LANG.translate(
                        "gui.goggles.too_expensive").component()).withStyle(ChatFormatting.RED));
            else
                tooltip.add(Component.literal("     ").append(LANG.translate(
                        "gui.goggles.ink_consumption",
                        String.valueOf(CeiConfigs.SERVER.copyTrainScheduleCost.get())).component()).withStyle(ChatFormatting.DARK_GRAY));
        }

        @Override
        public MutableComponent getDisplaySourceContent(ItemStack target) {
            return LANG.itemName(target).component();
        }
    }

    static class ClipBoard implements PrintEntry {
        @Override
        public ResourceLocation id() {
            return EnchantmentIndustry.genRL("clipboard");
        }

        @Override
        public boolean match(ItemStack toPrint) {
            return toPrint.is(AllBlocks.CLIPBOARD.get().asItem());
        }

        @Override
        public boolean valid(ItemStack target, ItemStack tested) {
            return tested.is(target.getItem()) && !ItemStack.isSameItemSameComponents(target, tested);
        }

        @Override
        public int requiredInkAmount(ItemStack target) {
            return CeiConfigs.SERVER.copyClipboardCost.get();
        }

        @Override
        public Fluid requiredInkType(ItemStack target) {
            return CeiFluids.INK.get();
        }

        @Override
        public boolean isTooExpensive(ItemStack target, int limit) {
            return CeiConfigs.SERVER.copyClipboardCost.get() > limit;
        }

        @Override
        public void addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking, ItemStack target) {
            var b = LANG.itemName(target).style(ChatFormatting.BLUE);
            b.forGoggles(tooltip, 1);
            boolean tooExpensive = Printing.isTooExpensive(this, target, CeiConfigs.SERVER.copierTankCapacity.get());
            if (tooExpensive)
                tooltip.add(Component.literal("     ").append(LANG.translate(
                        "gui.goggles.too_expensive").component()).withStyle(ChatFormatting.RED));
            else
                tooltip.add(Component.literal("     ").append(LANG.translate(
                        "gui.goggles.ink_consumption",
                        String.valueOf(CeiConfigs.SERVER.copyClipboardCost.get())).component()).withStyle(ChatFormatting.DARK_GRAY));
        }

        @Override
        public MutableComponent getDisplaySourceContent(ItemStack target) {
            return LANG.itemName(target).component();
        }
    }
}
