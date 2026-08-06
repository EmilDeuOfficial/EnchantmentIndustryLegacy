package plus.dragons.createenchantmentindustry.content.contraptions.enchanting.enchanter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import plus.dragons.createenchantmentindustry.EnchantmentIndustry;
import plus.dragons.createenchantmentindustry.entry.CeiItems;

public class Enchanting {
    public static final TagKey<Item> UNENCHANTABLE = TagKey.create(Registries.ITEM, EnchantmentIndustry.genRL("unenchantable"));
    public static final List<Predicate<ItemStack>> UNENCHANTABLE_CONDITIONS = new ArrayList<>();

    @Nullable
    public static EnchantmentEntry getTargetEnchantment(ItemStack itemStack, boolean hyper) {
        if (itemStack.is(CeiItems.ENCHANTING_GUIDE.get())) {
            var result = EnchantingGuideItem.getEnchantment(itemStack);
            if (!hyper || result == null)
                return result;
            return EnchantmentEntry.of(result.getFirst(), result.getSecond() + 1);
        }
        throw new IllegalStateException("TargetItem is not an enchanting guide for blaze!");
    }

    @Nullable
    public static EnchantmentEntry getValidEnchantment(ItemStack itemStack, ItemStack targetItem, boolean hyper) {
        if (itemStack.is(UNENCHANTABLE))
            return null;
        for (Predicate<ItemStack> condition : UNENCHANTABLE_CONDITIONS) {
            if (condition.test(itemStack))
                return null;
        }

        var entry = getTargetEnchantment(targetItem, hyper);
        if (entry == null || !entry.valid())
            return null;
        Holder<Enchantment> enchantment = entry.getFirst();

        ItemStack toCheck = itemStack.copy();
        ItemEnchantments present = EnchantmentHelper.getEnchantmentsForCrafting(toCheck);

        if (present.getLevel(enchantment) >= entry.getSecond())
            return null;

        // If the item already has the enchantment remove it to pass the checks
        ItemEnchantments.Mutable without = new ItemEnchantments.Mutable(present);
        without.set(enchantment, 0);
        ItemEnchantments remaining = without.toImmutable();
        EnchantmentHelper.setEnchantments(toCheck, remaining);

        if (!enchantment.value().canEnchant(toCheck))
            return null;
        for (Holder<Enchantment> other : remaining.keySet()) {
            if (!Enchantment.areCompatible(other, enchantment))
                return null;
        }
        return entry;
    }

    public static void enchantItem(ItemStack itemStack, Pair<Holder<Enchantment>, Integer> enchantment) {
        EnchantmentHelper.updateEnchantments(itemStack,
                mutable -> mutable.set(enchantment.getFirst(), enchantment.getSecond()));
    }

    public static int expPointFromLevel(int level) {
        if (level > 31) {
            return (int) (4.5 * level * level - 162.5 * level + 2220);
        } else {
            return level > 16
                    ? (int) (2.5 * level * level - 40.5 * level + 360)
                    : level * level + 6 * level;
        }
    }

    public static int expPointForNextLevel(int level) {
        if (level > 30) {
            return 9 * level - 158;
        } else {
            return level > 15
                    ? 5 * level - 38
                    : 2 * level + 7;
        }
    }

    /**
     * 1.21 replaced {@code Enchantment.Rarity} with a plain weight. The vanilla weights of the old
     * rarities were 10/5/2/1, so they are mapped back onto the original 1-4 cost multiplier.
     */
    public static int rarityLevel(Holder<Enchantment> enchantment) {
        int weight = enchantment.value().getWeight();
        if (weight >= 10)
            return 1;
        if (weight >= 5)
            return 2;
        if (weight >= 2)
            return 3;
        return 4;
    }

    public static int getExperienceConsumption(Holder<Enchantment> enchantment, int level) {
        int xpLevel = enchantment.value().getMinCost(level) + level * rarityLevel(enchantment);
        return expPointForNextLevel(xpLevel);
    }
}
