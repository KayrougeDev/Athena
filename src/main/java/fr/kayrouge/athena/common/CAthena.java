package fr.kayrouge.athena.common;

import fr.kayrouge.athena.common.artifact.Artifacts;
import fr.kayrouge.athena.common.event.CFurnaceEvent;
import fr.kayrouge.athena.common.event.CMiscEvents;
import fr.kayrouge.athena.common.event.CSpecialPlayerEvents;
import fr.kayrouge.athena.common.util.CPlatform;
import fr.kayrouge.athena.common.util.compat.PlatformCompat;
import fr.kayrouge.athena.common.util.compat.WorldCompat;
import org.bukkit.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public abstract class CAthena extends JavaPlugin {

    protected final Logger LOGGER = getLogger();

    @Override
    public void onEnable() {
        super.onEnable();
        checkDevWorldAndGenerate();
        LOGGER.info("Starting Athena on "+getPlatform());
        registerEvents();
        saveDefaultConfig();

        Artifacts.init();
    }

    protected void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new CFurnaceEvent(), this);
        pm.registerEvents(new CMiscEvents(), this);
        pm.registerEvents(new CSpecialPlayerEvents(), this);
    }

    private void checkDevWorldAndGenerate() {
        if(isDevWorldAvailable()) return;
        LOGGER.info("DevWorld don't exist, creating it...");
        WorldCreator creator = WorldCompat.newWorldCreator(new NamespacedKey(this, "devworld"));
        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.FLAT);
        creator.generateStructures(true);
        creator.generatorSettings("minecraft:bedrock,230*minecraft:stone,5*minecraft:dirt,minecraft:grass_block;minecraft:windswept_hills");
        creator.createWorld();
        LOGGER.info("DevWorld created !");
    }

    public static boolean isDevWorldAvailable() {
        return Bukkit.getWorld("devworld") != null;
    }

    public abstract CPlatform getPlatform();
}
