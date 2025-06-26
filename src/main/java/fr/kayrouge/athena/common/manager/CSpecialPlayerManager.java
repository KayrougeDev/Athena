package fr.kayrouge.athena.common.manager;

import fr.kayrouge.athena.common.util.compat.PlatformCompat;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class CSpecialPlayerManager {

    private static final Map<UUID, CSpecialPlayerManager> PLAYERS = new HashMap<>();

    public final File CONFIG_FILE;
    public final YamlConfiguration CONFIG;

    private final Player player;

    public CSpecialPlayerManager(Player player) {
        this.player = player;
        this.CONFIG_FILE = new File(PlatformCompat.INSTANCE.getDataFolder(), "specialplayers/"+player.getUniqueId()+".yml");
        this.CONFIG = YamlConfiguration.loadConfiguration(CONFIG_FILE);
        PLAYERS.put(player.getUniqueId(), this);
    }

    @Nullable
    public static CSpecialPlayerManager get(Player player) {
        return PLAYERS.get(player.getUniqueId());
    }

    public static CSpecialPlayerManager getOrCreate(Player player) {
        CSpecialPlayerManager spm = get(player);
        if(spm == null) return new CSpecialPlayerManager(player);
        return spm;
    }

    public void onJoin() {
    }


    public void onQuit() {
        PLAYERS.remove(this.player.getUniqueId());
        if(isInDevWorld()) {
            moveToOverworld();
        }
    }

    // START DEV WORLD LOGIC

    public void moveToOverworld() {
        PlayerData.of(player, getDevWorld()).save(this.CONFIG, this.CONFIG_FILE);
        PlayerData data = PlayerData.load(getOverworld(),this.CONFIG, this.CONFIG_FILE);
        if(data == null) {
            PlatformCompat.LOGGER.warning("Can't load overworld data for "+player.getName());
        }else {
            data.applyOn(player);
        }
    }

    public void moveToDevWorld() {
        PlayerData devWorldData = PlayerData.load(getDevWorld(),this.CONFIG, this.CONFIG_FILE);
        PlayerData.of(this.player, getOverworld()).save(this.CONFIG, CONFIG_FILE);
        if(devWorldData == null) {
            PlatformCompat.LOGGER.info("No devworld data, using default...");
            this.player.getInventory().clear();
            this.player.setHealth(20);
            this.player.teleport(getDevWorld().getSpawnLocation());
        }
        else {
            devWorldData.applyOn(this.player);
        }
    }



    public boolean isInDevWorld() {
        return this.player.getWorld().getName().equals("devworld");
    }
    public World getDevWorld() { return Bukkit.getWorld("devworld"); }
    public World getOverworld() { return Bukkit.getWorld("world"); }

    // END DEV WORLD LOGIC

    public record PlayerData(ItemStack[] inventory, double health, float xp, int xpLevel, float saturation, int foodLevel, Location location, GameMode gameMode, World world) {

        public static PlayerData of(Player player, World world) {
            return new PlayerData(player.getInventory().getContents(),
                    player.getHealth(), player.getExp(), player.getLevel(), player.getSaturation(), player.getFoodLevel(),
                    player.getLocation(), player.getGameMode(), world);
        }

        public void applyOn(Player player) {
            PlatformCompat.LOGGER.info("applying data to "+player.getName());
            player.getInventory().setContents(inventory());
            player.setHealth(health());
            player.setSaturation(saturation());
            player.setFoodLevel(foodLevel());
            player.setGameMode(gameMode());
            player.setExp(xp());
            player.setLevel(xpLevel());
            player.teleport(location());
        }

        public void save(YamlConfiguration config, File file) {
            try {
                ConfigurationSection section = config.createSection(world.getName());

                section.set("health", health);
                section.set("xp", xp());
                section.set("xplevel", xpLevel());
                section.set("saturation", saturation());
                section.set("foodLevel", foodLevel());
                section.set("location", location);
                section.set("gamemode", gameMode().getValue());

                ConfigurationSection inventorySection = section.createSection("inventory");
                for(int i = 0; i < inventory.length; i++) {
                    ItemStack stack = inventory[i];
                    if(stack != null) {
                        inventorySection.set(String.valueOf(i), stack);
                    }
                }

                config.save(file);
            } catch (IOException e) {
                PlatformCompat.LOGGER.severe("Can't save "+world().getName()+" Player Data");
            }
        }

        @Nullable
        public static PlayerData load(World loadWorld, YamlConfiguration config, File file) {
            try {
                config.load(file);

                ConfigurationSection worldSection = config.getConfigurationSection(loadWorld.getName());
                if(worldSection != null) {
                    final List<ItemStack> inventory = new ArrayList<>();

                    // get inventory
                    ConfigurationSection inventorySection = worldSection.getConfigurationSection("inventory");
                    if(inventorySection != null) {
                        for(String item : inventorySection.getKeys(false)) {
                            ItemStack stack = inventorySection.getItemStack(item);
                            if(stack != null) {
                                inventory.add(stack);
                            }
                        }
                    }

                    // get health
                    int health = worldSection.getInt("health", 20);
                    int saturation = worldSection.getInt("saturation", 1);
                    int foodLevel = worldSection.getInt("foodLevel", 20);
                    float xp = (float)worldSection.getDouble("xp", 0);
                    int xpLevel = worldSection.getInt("xplevel", 0);
                    GameMode gameMode = GameMode.getByValue(worldSection.getInt("gamemode", 0));

                    Location location = worldSection.getLocation("location", loadWorld.getSpawnLocation());

                    return new PlayerData(inventory.toArray(new ItemStack[0]), health, xp, xpLevel, saturation, foodLevel, location, gameMode, loadWorld);
                }

                return null;

            } catch (IOException | InvalidConfigurationException | IllegalArgumentException e) {
                PlatformCompat.LOGGER.severe("Can't load "+loadWorld.getName()+" Player Data");
                return null;
            }
        }
    }
}
