package kittify;

import kittify.common.millenair.MillenaireEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = Kittify.MOD_ID,
        name = "Kittify",
        version = Kittify.MOD_VERSION,
        acceptedMinecraftVersions = Kittify.MC_VERSION,
        clientSideOnly = false,
        serverSideOnly = false,
        dependencies = Kittify.DEPENDENCIES
)
public class Kittify {
    public static final String MOD_ID = "kittify";
    public static final String MOD_VERSION = "99999.999.999";
    public static final String MC_VERSION = "";
    public static final String DEPENDENCIES = "";
    public static final boolean DEV_ENVIRONMENT = MOD_VERSION.equals("99999.999.999");
    public static final String MILLENAIRE_MODID = "millenaire";
    public static final String NEVERMINE_MODID = "aoa3";
    public static Logger log = LogManager.getLogger(MOD_ID);
    @Instance(MOD_ID)
    public static Kittify INSTANCE;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Kittify.log.info(String.format("Pre-Init %s", MOD_ID));
        Kittify.log.info(String.format("Version %s", MOD_VERSION));
        Kittify.log = event.getModLog();
        if (Loader.isModLoaded(MILLENAIRE_MODID) && Loader.isModLoaded(NEVERMINE_MODID)) {
            log.info("Millenaire and Nevermine are installed together");
            MinecraftForge.EVENT_BUS.register(MillenaireEventHandler.class);
        }
    }
}
