package net.minecraftforge.network.simple;

import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkInstance;
import networkfix.NetworkFix;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Mixin(IndexedMessageCodec.class)
public class MixinIndexedMessageCodec
{
    @Final
    @Shadow
    private NetworkInstance networkInstance;

    @Final
    @Shadow
    private Short2ObjectArrayMap<IndexedMessageCodec.MessageHandler<?>> indicies;

    /**
     * @author Wolff
     * @reason log packet errors and prevent disconnection
     */
    @Overwrite(remap = false)
    void consume(FriendlyByteBuf payload, int payloadIndex, Supplier<NetworkEvent.Context> context) {
        if (payload == null || !payload.isReadable())
        {
            NetworkFix.log.error("Received empty payload on channel {}", Optional.ofNullable(networkInstance).map(NetworkInstance::getChannelName).map(Objects::toString).orElse("MISSING CHANNEL"));
            if (!net.minecraftforge.network.HandshakeHandler.packetNeedsResponse(context.get().getNetworkManager(), payloadIndex))
            {
                context.get().setPacketHandled(true);
            }
            return;
        }

        short discriminator = payload.readUnsignedByte();
        IndexedMessageCodec.MessageHandler<?> messageHandler = indicies.get(discriminator);

        if (messageHandler == null)
        {
            NetworkFix.log.error("Received invalid discriminator byte {} on channel {}", discriminator, Optional.ofNullable(networkInstance).map(NetworkInstance::getChannelName).map(Objects::toString).orElse("MISSING CHANNEL"));
            return;
        }

        NetworkHooks.validatePacketDirection(context.get().getDirection(), ((MixinMessageHandler) messageHandler).getNetworkDirection(), context.get().getNetworkManager());

        try
        {
            tryDecode(payload, context, payloadIndex, messageHandler);
        }
        catch (Throwable t)
        {
            NetworkFix.log.error("Suppressed exception during decoding on channel {}", Optional.ofNullable(networkInstance).map(NetworkInstance::getChannelName).map(Objects::toString).orElse("MISSING CHANNEL"), t);
            context.get().setPacketHandled(true);
        }
    }

    @Shadow
    private static <M> void tryDecode(FriendlyByteBuf payload, Supplier<NetworkEvent.Context> context, int payloadIndex, IndexedMessageCodec.MessageHandler<M> codec) {}
}
