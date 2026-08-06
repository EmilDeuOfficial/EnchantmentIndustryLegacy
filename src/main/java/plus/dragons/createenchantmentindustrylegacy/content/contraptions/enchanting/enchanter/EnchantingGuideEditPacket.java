package plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.enchanter;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiDataComponents;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiItems;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiPackets;

public record EnchantingGuideEditPacket(int index, ItemStack itemStack) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<EnchantingGuideEditPacket> TYPE =
            new CustomPacketPayload.Type<>(CeiPackets.id("configure_enchanting_guide"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantingGuideEditPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EnchantingGuideEditPacket::index,
            ItemStack.OPTIONAL_STREAM_CODEC, EnchantingGuideEditPacket::itemStack,
            EnchantingGuideEditPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EnchantingGuideEditPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var sender = context.player();
            ItemStack mainHandItem = sender.getMainHandItem();
            if (!CeiItems.ENCHANTING_GUIDE.isIn(mainHandItem))
                return;

            mainHandItem.set(CeiDataComponents.ENCHANTING_TARGET.get(),
                    new EnchantingTarget(packet.itemStack(), packet.index()));

            sender.getCooldowns().addCooldown(mainHandItem.getItem(), 5);
        });
    }
}
