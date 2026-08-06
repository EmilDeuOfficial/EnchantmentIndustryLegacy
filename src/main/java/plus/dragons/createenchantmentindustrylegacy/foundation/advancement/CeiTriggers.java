package plus.dragons.createenchantmentindustrylegacy.foundation.advancement;

import static plus.dragons.createenchantmentindustrylegacy.EnchantmentIndustry.ADVANCEMENT_FACTORY;

import plus.dragons.createenchantmentindustrylegacy.EnchantmentIndustry;
import plus.dragons.createenchantmentindustrylegacy.dragonLibLegacy.advancement.critereon.AccumulativeTrigger;
import plus.dragons.createenchantmentindustrylegacy.dragonLibLegacy.advancement.critereon.TriggerFactory;

public class CeiTriggers {
    private static final TriggerFactory FACTORY = ADVANCEMENT_FACTORY.getTriggerFactory();

    public static final AccumulativeTrigger BOOK_PRINTED = FACTORY.accumulative(EnchantmentIndustry.genRL("book_printed"));
    public static final AccumulativeTrigger DISENCHANTED = FACTORY.accumulative(EnchantmentIndustry.genRL("disenchanted"));

    static void init() {}
}
