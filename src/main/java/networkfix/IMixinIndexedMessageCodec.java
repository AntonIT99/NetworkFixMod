package networkfix;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkInstance;

public interface IMixinIndexedMessageCodec
{
    NetworkInstance getNetworkInstance();

    void consumeEvent(NetworkEvent networkEvent);
}
