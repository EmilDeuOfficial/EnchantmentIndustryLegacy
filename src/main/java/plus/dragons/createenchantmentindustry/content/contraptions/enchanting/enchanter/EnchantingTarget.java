package plus.dragons.createenchantmentindustry.content.contraptions.enchanting.enchanter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * The enchantment selection stored on an Enchanting Guide.
 * Replaces the {@code target}/{@code index} NBT keys used before data components existed.
 *
 * @param book  the enchanted book the selection was taken from
 * @param index which of the book's enchantments is selected
 */
public record EnchantingTarget(ItemStack book, int index) {
    public static final EnchantingTarget EMPTY = new EnchantingTarget(ItemStack.EMPTY, 0);

    public static final Codec<EnchantingTarget> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    ItemStack.OPTIONAL_CODEC.fieldOf("book").forGetter(EnchantingTarget::book),
                    Codec.INT.optionalFieldOf("index", 0).forGetter(EnchantingTarget::index))
            .apply(instance, EnchantingTarget::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantingTarget> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, EnchantingTarget::book,
            ByteBufCodecs.VAR_INT, EnchantingTarget::index,
            EnchantingTarget::new);

    public boolean isEmpty() {
        return book.isEmpty();
    }
}
