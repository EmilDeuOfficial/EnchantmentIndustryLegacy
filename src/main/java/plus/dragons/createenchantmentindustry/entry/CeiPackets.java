package plus.dragons.createenchantmentindustry.entry;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import plus.dragons.createenchantmentindustry.EnchantmentIndustry;
import plus.dragons.createenchantmentindustry.content.contraptions.enchanting.enchanter.BlazeEnchanterEditPacket;
import plus.dragons.createenchantmentindustry.content.contraptions.enchanting.enchanter.EnchantingGuideEditPacket;

/**
 * Networking. Forge's {@code SimpleChannel} was replaced by NeoForge's payload registration.
 */
public class CeiPackets {
    public static final String NETWORK_VERSION = "1";

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(CeiPackets::registerPayloads);
    }

    private static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(NETWORK_VERSION);
        registrar.playToServer(EnchantingGuideEditPacket.TYPE, EnchantingGuideEditPacket.STREAM_CODEC,
                EnchantingGuideEditPacket::handle);
        registrar.playToServer(BlazeEnchanterEditPacket.TYPE, BlazeEnchanterEditPacket.STREAM_CODEC,
                BlazeEnchanterEditPacket::handle);
    }

    public static void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    public static void sendToNear(ServerLevel level, BlockPos pos, int range, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayersNear(level, null, pos.getX(), pos.getY(), pos.getZ(), range, payload);
    }

    public static net.minecraft.resources.ResourceLocation id(String path) {
        return EnchantmentIndustry.genRL(path);
    }
}
