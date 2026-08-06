package plus.dragons.createenchantmentindustry.content.contraptions.enchanting;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import plus.dragons.createenchantmentindustry.EnchantmentIndustry;

/**
 * Resolves an enchantment's maximum level, honouring Apothic Enchanting's raised caps when that mod
 * is present. On 1.21 enchantments are data driven and always addressed through their {@link Holder}.
 */
public class EnchantmentLevelUtil {
    /** {@code EnchHooks#getMaxLevel}, or null when Apothic Enchanting is absent. */
    @Nullable
    private static final MethodHandle APOTHIC_GET_MAX_LEVEL;
    /** Whether the located hook takes a {@link Holder} rather than a raw {@link Enchantment}. */
    private static final boolean APOTHIC_TAKES_HOLDER;

    static {
        MethodHandle handle = null;
        boolean takesHolder = false;
        for (String className : new String[] {
                "dev.shadowsoffire.apothic_enchanting.asm.EnchHooks",
                "dev.shadowsoffire.apotheosis.ench.asm.EnchHooks" }) {
            try {
                Class<?> enchHooks = Class.forName(className);
                Method method;
                try {
                    method = enchHooks.getMethod("getMaxLevel", Holder.class);
                    takesHolder = true;
                } catch (NoSuchMethodException ignored) {
                    method = enchHooks.getMethod("getMaxLevel", Enchantment.class);
                    takesHolder = false;
                }
                method.setAccessible(true);
                handle = MethodHandles.lookup().unreflect(method);
                break;
            } catch (Throwable ignored) {
                // try the next candidate
            }
        }
        if (handle == null)
            EnchantmentIndustry.LOGGER.debug("No Apothic Enchanting hook found, using vanilla enchantment level caps");
        APOTHIC_GET_MAX_LEVEL = handle;
        APOTHIC_TAKES_HOLDER = takesHolder;
    }

    public static int getMaxLevel(Holder<Enchantment> enchantment) {
        if (APOTHIC_GET_MAX_LEVEL != null) {
            try {
                Object argument = APOTHIC_TAKES_HOLDER ? enchantment : enchantment.value();
                return (Integer) APOTHIC_GET_MAX_LEVEL.invoke(argument);
            } catch (Throwable throwable) {
                EnchantmentIndustry.LOGGER.warn("Failed to invoke getMaxLevel", throwable);
            }
        }
        return enchantment.value().getMaxLevel();
    }
}
