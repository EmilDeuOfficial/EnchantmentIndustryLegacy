package plus.dragons.createenchantmentindustrylegacy.content.contraptions.fluids.experience;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * On 1.21 enchantments are data driven, so {@link Enchantments#MENDING} is only a registry key.
 * Levels have to be looked up through the stack's own enchantment component instead.
 */
public class MendingHelper {
    public static boolean hasMending(ItemStack stack) {
        for (Holder<Enchantment> enchantment : EnchantmentHelper.getEnchantmentsForCrafting(stack).keySet()) {
            if (enchantment.is(Enchantments.MENDING))
                return true;
        }
        return false;
    }

    public static int getMendingLevel(ItemStack stack) {
        for (var entry : EnchantmentHelper.getEnchantmentsForCrafting(stack).entrySet()) {
            if (entry.getKey().is(Enchantments.MENDING))
                return entry.getIntValue();
        }
        return 0;
    }

    private MendingHelper() {}
}
