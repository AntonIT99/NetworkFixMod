package networkfix.mixin;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkInstance;
import net.minecraftforge.network.simple.IndexedMessageCodec;
import net.minecraftforge.network.simple.SimpleChannel;
import networkfix.IMixinIndexedMessageCodec;
import networkfix.NetworkFix;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(SimpleChannel.class)
public class MixinSimpleChannel
{
    @Shadow @Final
    private IndexedMessageCodec indexedCodec;

    @Shadow @Final
    private Optional<Consumer<NetworkEvent.ChannelRegistrationChangeEvent>> registryChangeConsumer;

    /**
     * @author Wolff
     * @reason log packet errors and prevent disconnection
     */
    @Overwrite(remap = false)
    private void networkEventListener(final NetworkEvent networkEvent)
    {
        try
        {
            if (networkEvent instanceof NetworkEvent.ChannelRegistrationChangeEvent channelRegistrationChangeEvent)
            {
                registryChangeConsumer.ifPresent(l->l.accept(channelRegistrationChangeEvent));
            }
            else
            {
                ((IMixinIndexedMessageCodec) indexedCodec).consumeEvent(networkEvent);
            }
        }
        catch (Throwable t)
        {
            NetworkFix.log.error("Suppressed exception during decoding on channel {}",
                    Optional.ofNullable(((IMixinIndexedMessageCodec) indexedCodec).getNetworkInstance())
                            .map(NetworkInstance::getChannelName)
                            .map(Objects::toString)
                            .orElse("MISSING CHANNEL"), t);
            networkEvent.getSource().get().setPacketHandled(true);
        }
    }
}
