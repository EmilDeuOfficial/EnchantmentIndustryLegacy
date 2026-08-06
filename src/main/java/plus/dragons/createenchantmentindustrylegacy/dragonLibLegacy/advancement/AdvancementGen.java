package plus.dragons.createenchantmentindustrylegacy.dragonLibLegacy.advancement;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

class AdvancementGen implements DataProvider {
    private final String name;
    private final String modid;
    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;

    AdvancementGen(String name, String modid, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.name = name;
        this.modid = modid;
        this.pathProvider = output.createRegistryElementsPathProvider(Registries.ADVANCEMENT);
        this.registries = registries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return registries.thenCompose(provider -> {
            Set<ResourceLocation> seen = new HashSet<>();
            List<CompletableFuture<?>> futures = new ArrayList<>();
            Consumer<AdvancementHolder> consumer = holder -> {
                if (!seen.add(holder.id()))
                    throw new IllegalStateException("Duplicate advancement " + holder.id());
                Path path = pathProvider.json(holder.id());
                futures.add(DataProvider.saveStable(cache, provider, Advancement.CODEC, holder.value(), path));
            };
            var advancements = AdvancementEntry.ENTRIES_MAP.get(modid);
            if (advancements != null)
                for (var advancement : advancements)
                    advancement.save(consumer);
            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return name + " Advancements";
    }
}
