package plus.dragons.createenchantmentindustrylegacy.foundation.mixin;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ExperienceOrb.class)
public interface ExperienceOrbAccessor {
    @Accessor("count")
    int create_enchantment_industry_legacy$getCount();

    @Accessor("count")
    void create_enchantment_industry_legacy$setCount(int count);

    @Invoker("repairPlayerItems")
    int create_enchantment_industry_legacy$repairPlayerItems(ServerPlayer player, int value);
}
