package plus.dragons.createenchantmentindustrylegacy.foundation.mixin;

import com.simibubi.create.content.fluids.potion.PotionMixingRecipes;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createenchantmentindustrylegacy.compat.apotheosis.ApotheosisCompat;

/**
 * Appends the Potion of Knowledge mixing recipes, which Apotheosis' potion enables.
 */
@Mixin(value = PotionMixingRecipes.class, remap = false)
public class PotionMixingRecipesMixin {
    @Inject(method = "createRecipes", at = @At("RETURN"), cancellable = true)
    private static void create_enchantment_industry_legacy$addKnowledgeRecipes(Level level,
            CallbackInfoReturnable<List<RecipeHolder<MixingRecipe>>> cir) {
        List<RecipeHolder<MixingRecipe>> extra = ApotheosisCompat.createPotionMixingRecipes(level);
        if (extra.isEmpty())
            return;
        List<RecipeHolder<MixingRecipe>> combined = new ArrayList<>(cir.getReturnValue());
        combined.addAll(extra);
        cir.setReturnValue(combined);
    }
}
