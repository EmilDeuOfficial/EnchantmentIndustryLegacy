package plus.dragons.createenchantmentindustrylegacy.dragonLibLegacy.advancement.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

/**
 * A trigger without any additional condition; firing it once completes the criterion.
 */
public class SimpleTrigger extends SimpleCriterionTrigger<SimpleTrigger.Instance> {
    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> true);
    }

    public Criterion<Instance> instance() {
        return new Criterion<>(this, new Instance(Optional.empty()));
    }

    public record Instance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player))
                .apply(instance, Instance::new));
    }
}
