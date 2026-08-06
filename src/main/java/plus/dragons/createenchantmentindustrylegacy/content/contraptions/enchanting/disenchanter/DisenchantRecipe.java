package plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.disenchanter;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiFluids;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiRecipeTypes;

public class DisenchantRecipe extends StandardProcessingRecipe<SingleRecipeInput> {
    private final int experience;

    public DisenchantRecipe(ProcessingRecipeParams params) {
        super(CeiRecipeTypes.DISENCHANTING, params);
        if (fluidResults.isEmpty())
            throw new IllegalArgumentException("Illegal Disenchanting Recipe: has no fluid output!");
        FluidStack fluid = fluidResults.getFirst();
        if (!fluid.getFluid().isSame(CeiFluids.EXPERIENCE.get().getSource()))
            throw new IllegalArgumentException("Illegal Disenchanting Recipe: has wrong type of fluid output!");
        this.experience = fluid.getAmount();
    }

    public static StandardProcessingRecipe.Builder<DisenchantRecipe> builder(ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<>(DisenchantRecipe::new, id);
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredients.getFirst().test(input.item());
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 1;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return false;
    }

    public boolean hasNoResult() {
        return results.isEmpty();
    }

    public int getExperience() {
        return experience;
    }
}
