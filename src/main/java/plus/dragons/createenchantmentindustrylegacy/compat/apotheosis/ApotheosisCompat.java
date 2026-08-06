package plus.dragons.createenchantmentindustrylegacy.compat.apotheosis;

import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.fluids.potion.PotionMixingRecipes;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;
import plus.dragons.createenchantmentindustrylegacy.EnchantmentIndustry;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.enchanter.Enchanting;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiFluids;

public class ApotheosisCompat {
    public static final ResourceKey<Potion> KNOWLEDGE = ResourceKey.create(Registries.POTION,
            ResourceLocation.fromNamespaceAndPath("apotheosis", "knowledge"));

    /**
     * Liquid Experience + Awkward Potion = Potion of Knowledge, mixed in a heated Basin.
     * <p>
     * Create no longer keeps a mutable list of potion mixing recipes, they are rebuilt per level,
     * so these are appended through {@code PotionMixingRecipesMixin} instead of registered once.
     */
    public static List<RecipeHolder<MixingRecipe>> createPotionMixingRecipes(Level level) {
        Optional<Holder.Reference<Potion>> knowledge = BuiltInRegistries.POTION.getHolder(KNOWLEDGE);
        if (knowledge.isEmpty())
            return List.of();

        List<RecipeHolder<MixingRecipe>> recipes = new ArrayList<>();
        PotionMixingRecipes.SUPPORTED_CONTAINERS
                .stream()
                .filter(container -> level.potionBrewing().isInput(new ItemStack(container)))
                .map(PotionFluidHandler::bottleTypeFromItem)
                .distinct()
                .sorted()
                .forEachOrdered(bottle -> {
                    String prefix = switch (bottle) {
                        case REGULAR -> "";
                        case SPLASH -> "splash_";
                        case LINGERING -> "lingering_";
                    };
                    FluidStack knowledgeFluid = PotionFluidHandler.getFluidFromPotion(
                            new PotionContents(knowledge.get()), bottle, 1000);
                    ResourceLocation id = EnchantmentIndustry
                            .genRL("compat/apotheosis/potion_mixing/" + prefix + "knowledge");
                    MixingRecipe recipe = new StandardProcessingRecipe.Builder<>(MixingRecipe::new, id)
                            .require(CeiFluids.EXPERIENCE.get(), 10)
                            .require(PotionFluidHandler.potionIngredient(Potions.AWKWARD, 1000))
                            .output(knowledgeFluid)
                            .requiresHeat(HeatCondition.HEATED)
                            .build();
                    recipes.add(new RecipeHolder<>(id, recipe));
                });
        return recipes;
    }

    public static void banTomeFromEnchanter() {
        if (ModList.get().isLoaded("apotheosis") || ModList.get().isLoaded("apothic_enchanting")) {
            Enchanting.UNENCHANTABLE_CONDITIONS.add(itemStack -> {
                var id = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
                return (id.getNamespace().equals("apotheosis") || id.getNamespace().equals("apothic_enchanting"))
                        && id.getPath().contains("tome");
            });
        }
    }
}
