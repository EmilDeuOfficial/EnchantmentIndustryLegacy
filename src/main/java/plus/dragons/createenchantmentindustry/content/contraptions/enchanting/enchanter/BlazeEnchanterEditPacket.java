package plus.dragons.createenchantmentindustry.content.contraptions.enchanting.enchanter;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import plus.dragons.createenchantmentindustry.entry.CeiDataComponents;
import plus.dragons.createenchantmentindustry.entry.CeiPackets;

public record BlazeEnchanterEditPacket(int index, ItemStack itemStack, BlockPos blockPos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BlazeEnchanterEditPacket> TYPE =
            new CustomPacketPayload.Type<>(CeiPackets.id("configure_blaze_enchanter"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlazeEnchanterEditPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, BlazeEnchanterEditPacket::index,
            ItemStack.OPTIONAL_STREAM_CODEC, BlazeEnchanterEditPacket::itemStack,
            BlockPos.STREAM_CODEC, BlazeEnchanterEditPacket::blockPos,
            BlazeEnchanterEditPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BlazeEnchanterEditPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var sender = context.player();
            if (!(sender.level().getBlockEntity(packet.blockPos()) instanceof BlazeEnchanterBlockEntity blazeEnchanter))
                return;

            blazeEnchanter.targetItem.set(CeiDataComponents.ENCHANTING_TARGET.get(),
                    new EnchantingTarget(packet.itemStack(), packet.index()));

            if (blazeEnchanter.processingTicks > 5)
                blazeEnchanter.processingTicks = BlazeEnchanterBlockEntity.ENCHANTING_TIME;

            blazeEnchanter.notifyUpdate();
        });
    }
}
