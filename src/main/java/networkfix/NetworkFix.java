package networkfix;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

@Mod(NetworkFix.MODID)
public class NetworkFix
{
    public static final String MODID = "networkfix";
    public static final Logger log = LogUtils.getLogger();

    public NetworkFix()
    {
        Mixins.addConfiguration(MODID + ".mixins.json");
    }
}
