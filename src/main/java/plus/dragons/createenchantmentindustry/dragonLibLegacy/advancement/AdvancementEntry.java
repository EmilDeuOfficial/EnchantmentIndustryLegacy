package plus.dragons.createenchantmentindustry.dragonLibLegacy.advancement;

import com.google.gson.JsonObject;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import plus.dragons.createenchantmentindustry.dragonLibLegacy.advancement.critereon.SimpleTrigger;
import plus.dragons.createenchantmentindustry.dragonLibLegacy.advancement.critereon.TriggerFactory;
import plus.dragons.createenchantmentindustry.foundation.mixin.dragonLibLegacy.CreateAdvancementConstructor;

/**
 * A mod-owned advancement definition. Named {@code AdvancementEntry} rather than
 * {@code AdvancementHolder} because 1.21 introduced a vanilla class under the latter name.
 */
public class AdvancementEntry {
    public static final Map<String, List<AdvancementEntry>> ENTRIES_MAP = new HashMap<>();

    protected final ResourceLocation id;
    protected final Advancement.Builder builder;
    @Nullable
    protected final SimpleTrigger builtinTrigger;
    protected final String titleKey;
    protected final String descriptionKey;
    protected final String title;
    protected final String description;
    @Nullable
    protected final AdvancementEntry parent;
    /**
     * Created on demand: Create's {@code CreateAdvancement} touches its own registry entries in a
     * static initialiser, which is not populated yet while this class is being loaded.
     */
    @Nullable
    protected CreateAdvancement createAdvancement;
    /** Display and external criteria are resolved lazily, they may reference registry objects. */
    protected Runnable deferredSetup = () -> {};
    protected AdvancementHolder advancement;

    protected AdvancementEntry(String modid, String id, Advancement.Builder builder, @Nullable AdvancementEntry parent,
            boolean builtin, String title, String description, TriggerFactory triggerFactory) {
        this.id = ResourceLocation.fromNamespaceAndPath(modid, id);
        this.builder = builder;
        this.parent = parent;
        if (builtin) {
            this.builtinTrigger = triggerFactory.simple(ResourceLocation.fromNamespaceAndPath(modid, "builtin/" + id));
            this.builder.addCriterion("builtin", builtinTrigger.instance());
        } else {
            this.builtinTrigger = null;
        }
        this.titleKey = new StringJoiner(".").add("advancement").add(modid).add(id).toString();
        this.descriptionKey = titleKey + ".desc";
        this.title = title;
        this.description = description;
    }

    public ResourceLocation id() {
        return id;
    }

    public String titleKey() {
        return titleKey;
    }

    public String descriptionKey() {
        return descriptionKey;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    @Nullable
    public SimpleTrigger getTrigger() {
        return builtinTrigger;
    }

    public CreateAdvancement asCreateAdvancement() {
        if (createAdvancement == null) {
            createAdvancement = CreateAdvancementConstructor.createInstance(id.getPath(), UnaryOperator.identity());
            ((CreateAdvancementAccess) createAdvancement).fromAdvancementEntry(this);
        }
        return createAdvancement;
    }

    public boolean isAlreadyAwardedTo(Player player) {
        if (!(player instanceof ServerPlayer sp))
            return true;
        AdvancementHolder holder = sp.getServer().getAdvancements().get(id);
        if (holder == null)
            return true;
        return sp.getAdvancements().getOrStartProgress(holder).isDone();
    }

    public void awardTo(Player player) {
        if (!(player instanceof ServerPlayer sp))
            return;
        if (builtinTrigger == null)
            throw new UnsupportedOperationException("Advancement [" + id + "] uses external Triggers, it cannot be awarded directly");
        builtinTrigger.trigger(sp);
    }

    public void save(Consumer<AdvancementHolder> consumer) {
        deferredSetup.run();
        if (parent != null)
            builder.parent(parent.advancement);
        advancement = builder.save(consumer, id.toString());
    }

    public void appendToLang(JsonObject object) {
        object.addProperty(titleKey(), title());
        object.addProperty(descriptionKey(), description());
    }

    public static JsonObject provideLangEntries(String modid) {
        JsonObject object = new JsonObject();
        var advancements = ENTRIES_MAP.get(modid);
        if (advancements == null)
            return object;
        for (var advancement : advancements)
            advancement.appendToLang(object);
        return object;
    }

    public static class Builder {
        private final String modid;
        @Nullable
        private final ResourceLocation background;
        private final String id;
        private final Advancement.Builder builder = Advancement.Builder.advancement();
        @Nullable
        private AdvancementEntry parent;
        private boolean builtin = true;
        private String title = "Untitled";
        private String description = "No Description";
        private Supplier<ItemStack> icon = () -> ItemStack.EMPTY;
        private final Map<String, Supplier<Criterion<?>>> externalCriteria = new LinkedHashMap<>();
        private AdvancementType frame = AdvancementType.TASK;
        private boolean toast = true;
        private boolean announce = false;
        private boolean hide = false;
        private final TriggerFactory factory;

        public Builder(String modid, String id, TriggerFactory factory) {
            this.modid = modid;
            this.id = id;
            this.background = "root".equals(id)
                    ? ResourceLocation.fromNamespaceAndPath(modid, "textures/gui/advancements.png")
                    : null;
            this.factory = factory;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder icon(ItemStack stack) {
            this.icon = () -> stack;
            return this;
        }

        public Builder icon(Supplier<ItemStack> stack) {
            this.icon = stack;
            return this;
        }

        public Builder icon(ItemProviderEntry<?, ?> item) {
            this.icon = item::asStack;
            return this;
        }

        public Builder icon(ItemLike item) {
            this.icon = () -> new ItemStack(item);
            return this;
        }

        public Builder frame(AdvancementType frame) {
            this.frame = frame;
            return this;
        }

        public Builder toast(boolean bl) {
            this.toast = bl;
            return this;
        }

        public Builder announce(boolean bl) {
            this.announce = bl;
            return this;
        }

        public Builder hidden() {
            this.hide = true;
            return this;
        }

        public Builder externalTrigger(String key, Criterion<?> criterion) {
            return externalTrigger(key, () -> criterion);
        }

        public Builder externalTrigger(String key, Supplier<Criterion<?>> criterion) {
            externalCriteria.put(key, criterion);
            this.builtin = false;
            return this;
        }

        public Builder parent(ResourceLocation id) {
            builder.parent(id);
            return this;
        }

        public Builder parent(AdvancementEntry advancement) {
            this.parent = advancement;
            return this;
        }

        public Builder transform(UnaryOperator<Advancement.Builder> transform) {
            transform.apply(builder);
            return this;
        }

        public AdvancementEntry build() {
            if (hide)
                description += "§7\n(Hidden Advancement)";
            AdvancementEntry advancement = new AdvancementEntry(modid, id, builder, parent, builtin, title, description, factory);
            advancement.deferredSetup = () -> {
                externalCriteria.forEach((key, criterion) -> builder.addCriterion(key, criterion.get()));
                builder.display(
                        icon.get(),
                        Component.translatable(advancement.titleKey),
                        Component.translatable(advancement.descriptionKey).withStyle(s -> s.withColor(0xDBA213)),
                        background,
                        frame,
                        toast,
                        announce,
                        hide);
            };
            ENTRIES_MAP.computeIfAbsent(modid, $ -> new ArrayList<>()).add(advancement);
            return advancement;
        }
    }
}
