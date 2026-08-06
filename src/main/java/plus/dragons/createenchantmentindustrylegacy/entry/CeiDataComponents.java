package plus.dragons.createenchantmentindustrylegacy.entry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import plus.dragons.createenchantmentindustrylegacy.EnchantmentIndustry;
import plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.enchanter.EnchantingTarget;

/**
 * Item data that lived in free-form NBT before 1.20.5.
 */
public class CeiDataComponents {
    private static final DeferredRegister<DataComponentType<?>> REGISTER =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, EnchantmentIndustry.ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EnchantingTarget>> ENCHANTING_TARGET =
            REGISTER.register("enchanting_target", () -> DataComponentType.<EnchantingTarget>builder()
                    .persistent(EnchantingTarget.CODEC)
                    .networkSynchronized(EnchantingTarget.STREAM_CODEC)
                    .build());

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
