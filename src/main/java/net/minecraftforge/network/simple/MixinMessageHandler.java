package net.minecraftforge.network.simple;

import net.minecraftforge.network.NetworkDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(IndexedMessageCodec.MessageHandler.class)
public interface MixinMessageHandler
{
    @Accessor("networkDirection")
    Optional<NetworkDirection> getNetworkDirection();
}
