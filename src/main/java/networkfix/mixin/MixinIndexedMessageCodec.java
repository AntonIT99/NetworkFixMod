package networkfix.mixin;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkInstance;
import net.minecraftforge.network.simple.IndexedMessageCodec;
import networkfix.IMixinIndexedMessageCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

@Mixin(IndexedMessageCodec.class)
public abstract class MixinIndexedMessageCodec implements IMixinIndexedMessageCodec
{
    @Accessor("networkInstance")
    public abstract NetworkInstance getNetworkInstance();

    @Shadow
    abstract void consume(FriendlyByteBuf payload, int payloadIndex, Supplier<NetworkEvent.Context> context);

    @Unique
    public void consumeEvent(NetworkEvent networkEvent)
    {
        consume(networkEvent.getPayload(), networkEvent.getLoginIndex(), networkEvent.getSource());
    }
}
