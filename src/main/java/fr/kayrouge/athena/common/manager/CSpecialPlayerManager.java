package fr.kayrouge.athena.common.manager;

import fr.kayrouge.athena.common.util.compat.PlatformCompat;
import fr.kayrouge.athena.common.util.compat.TaskCompat;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;

public class CSpecialPlayerManager {

    private static final Map<UUID, CSpecialPlayerManager> PLAYERS = new HashMap<>();

    public final File PLAYERDATA_FILE;
    public final File HOMES_FILE;

    private final Player player;

    private PlayerData overworldData = null;
    private PlayerData devworldData = null;

    private final HashMap<String,Location> HOMES = new HashMap<>();

    private CSpecialPlayerManager(Player player) {
        this.player = player;
        File dataFolder = PlatformCompat.INSTANCE.getDataFolder();
        this.PLAYERDATA_FILE = new File(dataFolder, "specialplayers/"+player.getUniqueId()+".yml");
        this.HOMES_FILE = new File(dataFolder, "homes/"+player.getUniqueId()+".yml");
        PLAYERS.put(player.getUniqueId(), this);
        init();
    }

    @Nullable
    public static CSpecialPlayerManager get(Player player) {
        return PLAYERS.get(player.getUniqueId());
    }

    public static CSpecialPlayerManager getOrCreate(Player player) {
        CSpecialPlayerManager spm = get(player);
        if(spm == null) {
            return create(player);
        }
        return spm;
    }

    public static CSpecialPlayerManager create(Player player) {
        return new CSpecialPlayerManager(player);
    }

    public void init() {
        loadHomes();
    }

    public void deInit() {
        saveHomes();
        if(isInDevWorld()) {
            moveToOverworld();
        }
        saveWorldsData();
        PLAYERS.remove(this.player.getUniqueId());
    }

    public void onJoin() {
    }


    public void onQuit() {
        deInit();
    }

    public static List<Player> getPlayers() {
        return PLAYERS.keySet().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).toList();
    }

    public static List<CSpecialPlayerManager> getManagers() {
        return PLAYERS.values().stream().filter(Objects::nonNull).toList();
    }

    // HOME LOGIC

    public void loadHomes() {
        String playerName = this.player.getName();
        TaskCompat.runAsyncTask(asyncTask -> {
            String content = "";
            try {
                content = Files.readString(this.HOMES_FILE.toPath(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                PlatformCompat.LOGGER.severe("Can't load "+playerName+"'s homes");
            }

            final String yaml = content;
            TaskCompat.runTask(task -> {
                this.HOMES.clear();
                YamlConfiguration config = new YamlConfiguration();
                try {
                    config.loadFromString(yaml);

                    for(String name : config.getKeys(false)) {
                        Location location = config.getLocation(name);
                        if(location == null) continue;
                        this.HOMES.put(name, location);
                    }

                } catch (InvalidConfigurationException e) {
                    PlatformCompat.LOGGER.severe("Invalid YML in "+playerName+" homes files");
                }
            });

        });
    }

    public void saveHomes() {
        YamlConfiguration config = new YamlConfiguration();
        this.HOMES.forEach(config::set);
        String dump = config.saveToString();
        String playerName = player.getName();
        TaskCompat.runAsyncTask(simpleTask -> {
            try {
                Files.createDirectories(this.HOMES_FILE.toPath().getParent());
                BufferedWriter writer = Files.newBufferedWriter(this.HOMES_FILE.toPath());
                writer.write(dump);
                writer.close();
            } catch (IOException e) {
                PlatformCompat.LOGGER.severe("Can't save "+playerName+"'s Homes");
            }
        });
    }

    public HashMap<String, Location> getHomes() {
        return HOMES;
    }
    // END HOME LOGIC

    // START DEV WORLD LOGIC

    public void moveToOverworld() {
        this.devworldData = PlayerData.of(player, getDevWorld());
        World overworld = getOverworld();
        PlayerData.loadAsync(overworld.getName(), PLAYERDATA_FILE, yamlConfiguration -> {
            if(overworldData == null) {
                PlatformCompat.LOGGER.warning("Loading from file overworld data for "+player.getName());
                overworldData = PlayerData.getFromConfig(overworld, yamlConfiguration);
            }
            if(overworldData == null) {
                PlatformCompat.LOGGER.warning("Can't load overworld data for "+player.getName());
            }
            else {
                overworldData.applyOn(player);
            }
        });
    }

    public void moveToDevWorld() {
        this.overworldData = PlayerData.of(player, getOverworld());
        World devWorld = getDevWorld();
        PlayerData.loadAsync(devWorld.getName(), this.PLAYERDATA_FILE, yamlConfiguration -> {
            if(devworldData == null) devworldData = PlayerData.getFromConfig(getDevWorld(), yamlConfiguration);
            if(devworldData == null) {
                PlatformCompat.LOGGER.info("No devworld data, using default...");
                this.player.getInventory().clear();
                this.player.setHealth(20);
                this.player.teleport(getDevWorld().getSpawnLocation());
                this.player.setGameMode(GameMode.CREATIVE);
                this.player.clearActivePotionEffects();
            }
            else {
                devworldData.applyOn(this.player);
            }
        });
    }



    public boolean isInDevWorld() {
        return this.player.getWorld().getName().equals("devworld");
    }
    public World getDevWorld() { return Bukkit.getWorld("devworld"); }
    public World getOverworld() { return Bukkit.getWorld("world"); }

    public void saveWorldsData() {
        if(overworldData == null) return;
        overworldData.saveAsync(this.PLAYERDATA_FILE, () -> {
            if(devworldData != null) devworldData.saveAsync(this.PLAYERDATA_FILE, () -> {});
        });
    }

    // END DEV WORLD LOGIC

    public record PlayerData(ItemStack[] inventory, double health, float xp, int xpLevel, float saturation, int foodLevel, Location location, GameMode gameMode, Collection<PotionEffect> potionEffects, World world) {

        public static PlayerData of(Player player, World world) {
            return new PlayerData(player.getInventory().getContents(),
                    player.getHealth(), player.getExp(), player.getLevel(), player.getSaturation(), player.getFoodLevel(),
                    player.getLocation(), player.getGameMode(), player.getActivePotionEffects(), world);
        }

        public void applyOn(Player player) {
            player.getInventory().setContents(inventory());
            player.setHealth(health());
            player.setSaturation(saturation());
            player.setFoodLevel(foodLevel());
            player.setGameMode(gameMode());
            player.setExp(xp());
            player.setLevel(xpLevel());
            player.teleport(location());
            player.addPotionEffects(potionEffects());
        }

        public void saveAsync(File file, Runnable callback) {
            loadAsync(world().getName(), file,config -> {
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

                ConfigurationSection potionSection = section.createSection("potions");
                int i = 0;
                for(PotionEffect effect : potionEffects()) {
                    potionSection.set(String.valueOf(i++), effect);
                }

                String stringConfig = config.saveToString();

                TaskCompat.runAsyncTask(simpleTask -> {
                    try {
                        Files.createDirectories(file.toPath().getParent());
                        BufferedWriter writer = Files.newBufferedWriter(file.toPath());
                        writer.write(stringConfig);
                        writer.close();
                        TaskCompat.runTask(task -> callback.run());
                    } catch (IOException e) {
                        PlatformCompat.LOGGER.severe("Can't save "+world().getName()+" Player Data");
                    }
                });
            });
        }

        public static void loadAsync(String worldName, File file, Consumer<YamlConfiguration> callback) {
            TaskCompat.runAsyncTask(asyncTask -> {
                String content = "";
                try {
                    content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    PlatformCompat.LOGGER.severe("Can't load "+worldName+" Player Data");
                }

                final String yaml = content;
                TaskCompat.runTask(task -> {

                    YamlConfiguration config = new YamlConfiguration();
                    try {
                        config.loadFromString(yaml);
                        callback.accept(config);
                    } catch (InvalidConfigurationException e) {
                        PlatformCompat.LOGGER.severe("Invalid YML in "+worldName);
                    }
                });

            });
        }

        @Nullable
        public static PlayerData getFromConfig(World loadWorld, YamlConfiguration config) {
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

                ConfigurationSection potionSection = worldSection.getConfigurationSection("potions");
                Collection<PotionEffect> effects = new ArrayList<>();


                for(String name : potionSection.getKeys(false)) {
                    Object i = potionSection.get(name);
                    if(i instanceof PotionEffect effect) {
                        effects.add(effect);
                    }
                }

                return new PlayerData(inventory.toArray(new ItemStack[0]), health, xp, xpLevel, saturation, foodLevel, location, gameMode,effects, loadWorld);
            }

            return null;
        }
    }
}
