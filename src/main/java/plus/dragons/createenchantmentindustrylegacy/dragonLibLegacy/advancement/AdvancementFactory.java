package plus.dragons.createenchantmentindustrylegacy.dragonLibLegacy.advancement;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import plus.dragons.createenchantmentindustrylegacy.dragonLibLegacy.advancement.critereon.TriggerFactory;

public class AdvancementFactory {
    private final String name;
    private final String modid;
    private final TriggerFactory triggerFactory;
    private final Runnable preTask;

    private AdvancementFactory(String name, String modid, Runnable preTask) {
        this.name = name;
        this.modid = modid;
        this.triggerFactory = new TriggerFactory(modid);
        this.preTask = preTask;
    }

    public static AdvancementFactory create(String name, String modid, Runnable preTask) {
        return new AdvancementFactory(name, modid, preTask);
    }

    public AdvancementEntry.Builder builder(String id) {
        return new AdvancementEntry.Builder(modid, id, triggerFactory);
    }

    public TriggerFactory getTriggerFactory() {
        return triggerFactory;
    }

    public void datagen(final GatherDataEvent event) {
        preTask.run();
        var generator = event.getGenerator();
        generator.addProvider(event.includeServer(),
                new AdvancementGen(name, modid, generator.getPackOutput(), event.getLookupProvider()));
    }

    public void register(IEventBus modEventBus) {
        triggerFactory.register(modEventBus);
    }
}
