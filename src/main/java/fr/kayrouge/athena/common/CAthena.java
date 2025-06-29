package fr.kayrouge.athena.common;

import fr.kayrouge.athena.common.artifact.Artifacts;
import fr.kayrouge.athena.common.event.CFurnaceEvent;
import fr.kayrouge.athena.common.event.CMiscEvents;
import fr.kayrouge.athena.common.event.CSpecialPlayerEvents;
import fr.kayrouge.athena.common.util.CFastAccess;
import fr.kayrouge.athena.common.util.CPlatform;
import fr.kayrouge.athena.common.util.compat.WorldCompat;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public abstract class CAthena extends JavaPlugin {

    protected final Logger LOGGER = getLogger();

    private boolean experimentalFeaturesEnabled = false;

    @Override
    public void onEnable() {
        super.onEnable();
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        experimentalFeaturesEnabled = getConfig().getBoolean("experimentalFeatures", false);

        checkDevWorldAndGenerate();
        LOGGER.info("Starting Athena on "+getPlatform());
        registerEvents();

        Artifacts.init();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        LOGGER.info("Saving SpecialPlayerManager...");
    }

    protected void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new CFurnaceEvent(), this);
        pm.registerEvents(new CMiscEvents(), this);
        pm.registerEvents(new CSpecialPlayerEvents(), this);
    }

    private void checkDevWorldAndGenerate() {
        if(!isExperimentalFeaturesEnabled() ||isDevWorldAvailable()) return;
        LOGGER.info("DevWorld don't exist, creating it...");
        WorldCreator creator = WorldCompat.newWorldCreator(new NamespacedKey(this, "devworld"));
        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.FLAT);
        creator.generateStructures(true);
        creator.generatorSettings(
                "{"
                        + "\"layers\":["
                        + "{\"block\":\"minecraft:bedrock\",\"height\":1},"
                        + "{\"block\":\"minecraft:stone\",\"height\":200},"
                        + "{\"block\":\"minecraft:dirt\",\"height\":10},"
                        + "{\"block\":\"minecraft:grass_block\",\"height\":1}"
                        + "],"
                        + "\"biome\":\"forest\""
                        + "}"
        );
        creator.createWorld();
        LOGGER.info("DevWorld created !");
    }

    public static boolean isDevWorldAvailable() {
        return Bukkit.getWorld("devworld") != null;
    }

    public boolean isExperimentalFeaturesEnabled() {
        return experimentalFeaturesEnabled;
    }

    public void sendExperimentalFeatureDisabledMessage(CommandSender sender) {
        CFastAccess.sendMessage(sender, Component.text("Experimental features are disabled !"));
    }

    public abstract CPlatform getPlatform();
}
