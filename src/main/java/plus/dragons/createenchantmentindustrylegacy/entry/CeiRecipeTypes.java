package plus.dragons.createenchantmentindustrylegacy.entry;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import java.util.Optional;
import java.util.function.Supplier;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import plus.dragons.createenchantmentindustrylegacy.EnchantmentIndustry;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.disenchanter.DisenchantRecipe;

public enum CeiRecipeTypes implements IRecipeTypeInfo {
    DISENCHANTING(() -> new StandardProcessingRecipe.Serializer<>(DisenchantRecipe::new));

    private final ResourceLocation id;
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerObject;
    private final DeferredHolder<RecipeType<?>, RecipeType<?>> typeObject;

    CeiRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = Lang.asId(name());
        id = EnchantmentIndustry.genRL(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        typeObject = Registers.TYPE_REGISTER.register(name, () -> simpleType(id));
    }

    public static <T extends Recipe<?>> RecipeType<T> simpleType(ResourceLocation id) {
        String stringId = id.toString();
        return new RecipeType<>() {
            @Override
            public String toString() {
                return stringId;
            }
        };
    }

    public static void register(IEventBus modEventBus) {
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    }

    public <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>> find(I input, Level level) {
        return level.getRecipeManager().getRecipeFor(this.getType(), input, level);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) typeObject.get();
    }

    private static class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER =
                DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, EnchantmentIndustry.ID);
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER =
                DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, EnchantmentIndustry.ID);
    }
}
