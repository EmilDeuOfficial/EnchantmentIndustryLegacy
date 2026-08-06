package plus.dragons.createenchantmentindustry.dragonLibLegacy.advancement.critereon;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Creates and registers the mod's criterion triggers.
 * On 1.21 triggers live in the {@code minecraft:trigger_type} registry instead of a static map.
 */
public class TriggerFactory {
    private final DeferredRegister<CriterionTrigger<?>> register;

    public TriggerFactory(String modid) {
        this.register = DeferredRegister.create(Registries.TRIGGER_TYPE, modid);
    }

    public SimpleTrigger simple(ResourceLocation id) {
        SimpleTrigger trigger = new SimpleTrigger();
        register.register(id.getPath(), () -> trigger);
        return trigger;
    }

    public AccumulativeTrigger accumulative(ResourceLocation id) {
        AccumulativeTrigger trigger = new AccumulativeTrigger(id);
        register.register(id.getPath(), () -> trigger);
        return trigger;
    }

    public void register(IEventBus modEventBus) {
        register.register(modEventBus);
    }
}
