package plus.dragons.createenchantmentindustry.content.contraptions.enchanting.enchanter;

import net.createmod.catnip.data.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import plus.dragons.createenchantmentindustry.EnchantmentIndustry;
import plus.dragons.createenchantmentindustry.content.contraptions.enchanting.EnchantmentLevelCapUtil;
import plus.dragons.createenchantmentindustry.content.contraptions.enchanting.EnchantmentLevelUtil;
import plus.dragons.createenchantmentindustry.foundation.config.CeiConfigs;

public class EnchantmentEntry extends Pair<Holder<Enchantment>, Integer> {
    public static final TagKey<Enchantment> HYPER_ENCHANTABLE = TagKey.create(Registries.ENCHANTMENT, EnchantmentIndustry.genRL("hyper_enchantable"));
    public static final TagKey<Enchantment> HYPER_ENCHANTABLE_BLACKLIST = TagKey.create(Registries.ENCHANTMENT, EnchantmentIndustry.genRL("hyper_enchantable_blacklist"));

    protected EnchantmentEntry(Holder<Enchantment> first, Integer second) {
        super(first, second);
    }

    public static EnchantmentEntry of(Holder<Enchantment> enchantment, Integer level) {
        return new EnchantmentEntry(enchantment, level);
    }

    public static EnchantmentEntry of(Holder<Enchantment> enchantment, int level) {
        return new EnchantmentEntry(enchantment, level);
    }

    public boolean valid() {
        Holder<Enchantment> enchantment = getFirst();
        int level = getSecond();
        int maxLevel = EnchantmentLevelUtil.getMaxLevel(enchantment);
        var configuredCap = EnchantmentLevelCapUtil.getConfiguredCap(enchantment);
        if (configuredCap.isPresent())
            return level <= configuredCap.getAsInt();
        if (enchantment.is(HYPER_ENCHANTABLE_BLACKLIST)) {
            return level <= maxLevel;
        } else if (maxLevel == 1 && level > 1) {
            return enchantment.is(HYPER_ENCHANTABLE)
                    && CeiConfigs.SERVER.enableHyperEnchant.get()
                    && level <= maxLevel + CeiConfigs.SERVER.maxHyperEnchantingLevelExtension.get();
        }
        return level <= maxLevel
                + (CeiConfigs.SERVER.enableHyperEnchant.get() ? CeiConfigs.SERVER.maxHyperEnchantingLevelExtension.get() : 0);
    }
}
