package plus.dragons.createenchantmentindustry.dragonLibLegacy.advancement.critereon;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

/**
 * A trigger that accumulates a per-player counter in world-persistent data and completes once the
 * counter satisfies the configured bounds.
 */
public class AccumulativeTrigger extends SimpleCriterionTrigger<AccumulativeTrigger.TriggerInstance> {
    private static final String DATA_NAME = "create_enchantment_industry_accumulative_data";

    private final ResourceLocation id;

    public AccumulativeTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public ResourceLocation getId() {
        return id;
    }

    public void trigger(Player player, int change) {
        if (!(player instanceof ServerPlayer serverPlayer))
            return;
        this.trigger(serverPlayer, triggerInstance -> triggerInstance.matches(id, player, change));
    }

    public Criterion<TriggerInstance> atLeast(int amount) {
        return new Criterion<>(this, new TriggerInstance(Optional.empty(), MinMaxBounds.Ints.atLeast(amount)));
    }

    private static AccumulativeData get(Level level) {
        if (!(level instanceof ServerLevel))
            throw new IllegalStateException("Attempted to get the accumulative advancement data from a client world.");
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(AccumulativeData::new, AccumulativeData::load),
                DATA_NAME);
    }

    private static class AccumulativeData extends SavedData {
        private final Table<ResourceLocation, UUID, Integer> data = HashBasedTable.create();

        public void change(ResourceLocation trigger, UUID playerId, int amount) {
            Integer current = data.get(trigger, playerId);
            data.put(trigger, playerId, (current == null ? 0 : current) + amount);
            setDirty();
        }

        public int get(ResourceLocation trigger, UUID playerId) {
            Integer value = data.get(trigger, playerId);
            return value == null ? 0 : value;
        }

        public static AccumulativeData load(CompoundTag tag, HolderLookup.Provider registries) {
            AccumulativeData result = new AccumulativeData();
            if (!tag.contains("AccumulativeData"))
                return result;
            ListTag list = tag.getList("AccumulativeData", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                ResourceLocation trigger = ResourceLocation.tryParse(entry.getString("TriggerId"));
                if (trigger == null)
                    continue;
                result.data.put(trigger, entry.getUUID("PlayerId"), entry.getInt("Count"));
            }
            return result;
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
            ListTag list = new ListTag();
            for (var cell : data.cellSet()) {
                CompoundTag entry = new CompoundTag();
                entry.putString("TriggerId", cell.getRowKey().toString());
                entry.putUUID("PlayerId", cell.getColumnKey());
                entry.putInt("Count", cell.getValue());
                list.add(entry);
            }
            tag.put("AccumulativeData", list);
            return tag;
        }
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, MinMaxBounds.Ints requirement)
            implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        MinMaxBounds.Ints.CODEC.optionalFieldOf("requirement", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::requirement))
                .apply(instance, TriggerInstance::new));

        public boolean matches(@Nullable ResourceLocation trigger, Player player, int change) {
            if (trigger == null)
                return false;
            AccumulativeData data = get(player.level());
            data.change(trigger, player.getUUID(), change);
            return requirement.matches(data.get(trigger, player.getUUID()));
        }
    }
}
