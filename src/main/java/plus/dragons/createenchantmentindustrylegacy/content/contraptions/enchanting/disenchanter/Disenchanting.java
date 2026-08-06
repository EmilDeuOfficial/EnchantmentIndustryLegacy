package plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.disenchanter;

import javax.annotation.Nullable;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiFluids;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiRecipeTypes;

public class Disenchanting {
    public static ItemStack disenchantAndInsert(DisenchanterBlockEntity be, ItemStack itemStack, boolean simulate) {
        Level level = be.getLevel();
        if (level == null)
            return itemStack;
        return CeiRecipeTypes.DISENCHANTING.<SingleRecipeInput, DisenchantRecipe>find(new SingleRecipeInput(itemStack), level)
                .map(holder -> {
                    DisenchantRecipe recipe = holder.value();
                    if (!recipe.hasNoResult())
                        return itemStack;
                    var tank = be.getInternalTank();
                    tank.allowInsertion();
                    int amount = recipe.getExperience();
                    var fluidStack = new FluidStack(CeiFluids.EXPERIENCE.get().getSource(), itemStack.getCount() * amount);
                    int inserted = tank.getPrimaryHandler().fill(fluidStack, IFluidHandler.FluidAction.SIMULATE) / amount;
                    ItemStack ret = itemStack.copy();
                    if (!simulate) {
                        fluidStack = new FluidStack(CeiFluids.EXPERIENCE.get().getSource(), inserted * amount);
                        tank.getPrimaryHandler().fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    }
                    ret.shrink(inserted);
                    tank.forbidInsertion();
                    return ret;
                }).orElse(itemStack);
    }

    // Produce result only. Do not modify stack.
    // stack always has count of 1.
    @Nullable
    public static Pair<FluidStack, ItemStack> disenchantResult(ItemStack itemStack, Level level) {
        if (hasNonCurseEnchantment(itemStack)) {
            var xp = new FluidStack(CeiFluids.EXPERIENCE.get().getSource(), getDisenchantExperience(itemStack));
            ItemStack result = disenchant(itemStack);
            return Pair.of(xp, result);
        }
        var recipe = CeiRecipeTypes.DISENCHANTING.<SingleRecipeInput, DisenchantRecipe>find(new SingleRecipeInput(itemStack), level)
                .map(net.minecraft.world.item.crafting.RecipeHolder::value)
                .orElse(null);
        if (recipe != null && !recipe.hasNoResult()) {
            var xp = new FluidStack(CeiFluids.EXPERIENCE.get().getSource(), recipe.getExperience());
            var result = recipe.getResultItem(level.registryAccess()).copy();
            return Pair.of(xp, result);
        }
        return null;
    }

    public static boolean hasNonCurseEnchantment(ItemStack itemStack) {
        return EnchantmentHelper.getEnchantmentsForCrafting(itemStack)
                .keySet().stream()
                .anyMatch(enchantment -> !enchantment.is(EnchantmentTags.CURSE));
    }

    public static ItemStack disenchant(ItemStack itemStack) {
        ItemStack result = itemStack.copy();
        ItemEnchantments.Mutable curses = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        for (var entry : EnchantmentHelper.getEnchantmentsForCrafting(itemStack).entrySet()) {
            if (entry.getKey().is(EnchantmentTags.CURSE))
                curses.set(entry.getKey(), entry.getIntValue());
        }
        ItemEnchantments remaining = curses.toImmutable();

        if (result.is(Items.ENCHANTED_BOOK) && remaining.isEmpty()) {
            result = result.transmuteCopy(Items.BOOK);
            result.remove(DataComponents.STORED_ENCHANTMENTS);
            result.remove(DataComponents.REPAIR_COST);
        } else {
            EnchantmentHelper.setEnchantments(result, remaining);
            int repairCost = 0;
            for (int i = 0; i < remaining.size(); ++i)
                repairCost = AnvilMenu.calculateIncreasedRepairCost(repairCost);
            result.set(DataComponents.REPAIR_COST, repairCost);
        }
        return result;
    }

    private static int getDisenchantExperience(ItemStack itemStack) {
        int xp = EnchantmentHelper.getEnchantmentsForCrafting(itemStack)
                .entrySet().stream()
                .filter(entry -> !entry.getKey().is(EnchantmentTags.CURSE))
                .map(entry -> entry.getKey().value().getMinCost(entry.getIntValue()))
                .reduce(0, Integer::sum);
        return xp == 0 ? 0 : Mth.ceil(xp * 0.75);
    }
}
