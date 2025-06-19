package fr.kayrouge.athena.common.event;

import fr.kayrouge.athena.common.util.compat.PlatformCompat;
import fr.kayrouge.athena.common.util.CTextUtil;
import fr.kayrouge.athena.common.util.compat.ItemCompat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class CFurnaceEvent implements Listener {

    final List<String> ironFurnace;
    final List<String> goldFurnace;
    final List<String> diamondFurnace;
    final List<String> emeraldFurnace;
    final List<String> netheriteFurnace;

    public static final File CONFIG_FILE = new File(PlatformCompat.INSTANCE.getDataFolder(), "furnaces.yml");
    public static final YamlConfiguration CONFIG;

    static {
        CONFIG = YamlConfiguration.loadConfiguration(CONFIG_FILE);
        for(Type type : Type.values()) {
            CONFIG.addDefault(type.getName()+"-speed", type.getDefaultSpeed());
        }
        CONFIG.options().copyDefaults(true);
    }

    private final Set<Location> burningFurnaces = ConcurrentHashMap.newKeySet();

    public static final NamespacedKey KEY = new NamespacedKey(PlatformCompat.INSTANCE, "furnaceLevel");

    public CFurnaceEvent() {
        this.ironFurnace = CONFIG.getStringList(Type.IRON.getName());
        this.goldFurnace = CONFIG.getStringList(Type.GOLD.getName());
        this.diamondFurnace = CONFIG.getStringList(Type.DIAMOND.getName());
        this.emeraldFurnace = CONFIG.getStringList(Type.EMERALD.getName());
        this.netheriteFurnace = CONFIG.getStringList(Type.NETHERITE.getName());

        startGlobalTask();
    }

    @EventHandler
    public void onBurn(FurnaceBurnEvent e) {
        burningFurnaces.add(e.getBlock().getLocation());
    }

    @EventHandler
    public void onSmelt(FurnaceSmeltEvent e) {
        burningFurnaces.add(e.getBlock().getLocation());
    }

    public void startGlobalTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                burningFurnaces.removeIf(loc -> {
                    Block b = loc.getBlock();
                    if (!(b.getState() instanceof Furnace f)) return true;
                    if (f.getBurnTime() <= 0 || f.getInventory().getSmelting() == null) return true;
                    f.setCookTimeTotal(getType(loc).getSpeed());
                    f.update(false, false);
                    return false;
                });
            }
        }.runTaskTimer(PlatformCompat.INSTANCE, 1L, 1L);
    }

    @EventHandler
    public void addFurnace(BlockPlaceEvent e) {
        if(e.getBlock().getType() == Material.FURNACE) {
            ItemStack stack = e.getPlayer().getEquipment().getItem(e.getHand());
            if(!(stack.hasItemMeta()) || stack.getItemMeta() == null) return;
            PersistentDataContainer pdc = e.getItemInHand().getItemMeta().getPersistentDataContainer();
            if(!pdc.has(KEY)) return;
            for(Type type : Type.values()) {
                if(type == Type.NORMAL) continue;
                if(Objects.equals(pdc.get(KEY, PersistentDataType.STRING), type.getName())) {
                    List<String> list = getList(type);
                    if(list == null) {
                        PlatformCompat.LOGGER.log(Level.SEVERE, "Cannot find list for type '" + type.getName()+"'");
                        break;
                    }
                    list.add(e.getBlock().getLocation().toString());
                    CONFIG.set(type.getName(), list);

                    break;
                }
            }

            saveConfig();
        }
    }

    @EventHandler
    public void breakFurnace(BlockBreakEvent e) {
        if(e.getBlock().getType() == Material.FURNACE) {
            Type type = getType(e.getBlock());
            if(type != Type.NORMAL) {
                e.setDropItems(false);

                e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), createFurnaceStack(type));
                burningFurnaces.remove(e.getBlock().getLocation());
                List<String> list = getList(type);
                if(list != null) {
                    list.remove(e.getBlock().getLocation().toString());
                    CONFIG.set(type.getName(), list);
                    saveConfig();
                }
            }
        }
    }

    public static ItemStack createFurnaceStack(Type type) {
        ItemStack stack = new ItemStack(Material.FURNACE);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            ItemCompat.setDisplayName(meta, type.getDisplayName());
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, type.getName());
            final int speed =type.getSpeed();
            String speedString = "Temps pour cuire: {sec} secondes";
            Map<String, String> pattern = Map.of("sec", String.valueOf((float)speed/20));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(CTextUtil.replaceTokens(speedString, pattern)));
            lore.add(Component.text("Prérequis pour améliorer:"));
            if(type != Type.NETHERITE) {
                lore.add(Component.text("  - "+type.getLevelToUpgrade()+" niveau d'xp"));
                lore.add(Component.text("  - ")
                        .append(Component.translatable(
                                ItemCompat.getTranslationKey(type.getItemToUpgrade())
                        ))
                );
            }
            else {
                lore.add(Component.text("  - Niveau max"));
            }
            ItemCompat.setLore(meta, lore);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if(e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.FURNACE) {
                Block furnace = e.getClickedBlock();
                Type type = getType(furnace);
                EquipmentSlot hand = e.getHand();
                ItemStack itemInHand;
                if(hand == null) {
                    itemInHand = e.getPlayer().getEquipment().getItemInMainHand();
                }
                else {
                    itemInHand = e.getPlayer().getEquipment().getItem(hand);
                }
                if(itemInHand.getType() == type.getItemToUpgrade() && type != Type.NETHERITE) {
                    e.setCancelled(true);
                    if(e.getPlayer().getLevel() < type.getLevelToUpgrade() && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        e.getPlayer().sendMessage(Component.text("Il vous manque "+(type.getLevelToUpgrade()-e.getPlayer().getLevel())+" pour améliorer"));
                        return;
                    }
                    Type nextType = getNextType(type);
                    if(nextType != Type.NORMAL) {
                        itemInHand.setAmount(itemInHand.getAmount()-1);
                        if(e.getPlayer().getGameMode() != GameMode.CREATIVE) e.getPlayer().setLevel(e.getPlayer().getLevel()-type.getLevelToUpgrade());

                        List<String> oldList = getList(type);
                        if(oldList != null) {
                            oldList.remove(e.getClickedBlock().getLocation().toString());
                            CONFIG.set(type.getName(), oldList);
                        }

                        List<String> newList = getList(nextType);
                        if(newList == null) return;
                        newList.add(e.getClickedBlock().getLocation().toString());
                        CONFIG.set(nextType.getName(), newList);

                        saveConfig();

                        e.getPlayer().sendMessage(Component.text("Vous avez bien changer ").append(type.getDisplayName()).append(Component.text(" en ")).append(nextType.getDisplayName()));
                    }
                }
            }
        }
    }

    @NotNull
    private static Type getNextType(Type type) {
        Type nextType = Type.NORMAL;
        if(type == Type.NORMAL) {
            nextType = Type.IRON;
        } else if(type == Type.IRON) {
            nextType = Type.GOLD;
        } else if(type == Type.GOLD) {
            nextType = Type.DIAMOND;
        } else if(type == Type.DIAMOND) {
            nextType = Type.EMERALD;
        } else if(type == Type.EMERALD) {
            nextType = Type.NETHERITE;
        }
        return nextType;
    }

    public static void saveConfig() {
        try {
            CONFIG.save(CONFIG_FILE);
        } catch (IOException ex) {
            PlatformCompat.LOGGER.log(Level.SEVERE, "Could not save config to " + CONFIG_FILE);
        }
    }

    public Type getType(Block block) {
        return getType(block.getLocation());
    }

    @Nullable
    private List<String> getList(Type type) {
        switch (type) {
            case IRON -> {
                return this.ironFurnace;
            }
            case GOLD -> {
                return this.goldFurnace;
            }
            case DIAMOND -> {
                return this.diamondFurnace;
            }
            case EMERALD -> {
                return this.emeraldFurnace;
            }
            case NETHERITE -> {
                return this.netheriteFurnace;
            }
            default -> {
                return null;
            }
        }
    }

    public Type getType(Location loc) {
        if(netheriteFurnace.contains(loc.toString())) {
            return Type.NETHERITE;
        }
        if(emeraldFurnace.contains(loc.toString())) {
            return Type.EMERALD;
        }
        if(diamondFurnace.contains(loc.toString())) {
            return Type.DIAMOND;
        }
        if(goldFurnace.contains(loc.toString())) {
            return Type.GOLD;
        }
        if(ironFurnace.contains(loc.toString())) {
            return Type.IRON;
        }
        return Type.NORMAL;
    }

    public enum Type {
        NORMAL(Component.translatable(ItemCompat.getTranslationKey(Material.FURNACE)), 200, Material.IRON_INGOT, 4),
        IRON(Component.text("Iron Furnace"), 180, Material.GOLD_INGOT, 1),
        GOLD(Component.text("Gold Furnace"), 175, Material.DIAMOND, 10),
        DIAMOND(Component.text("Diamond Furnace"), 130, Material.EMERALD, 8),
        EMERALD(Component.text("Emerald Furnace"), 100, Material.NETHERITE_INGOT, 10),
        NETHERITE(Component.text("Netherite Furnace"), 80, Material.AIR, 0)
        ;

        final String name;
        final Component displayName;
        final int defaultSpeed;
        final Material itemToUpgrade;
        final int levelToUpgrade;

        Type(Component displayName, int defaultSpeed, Material itemToUpgrade, int levelToUpgrade) {
            this.name = this.toString().toLowerCase(Locale.ROOT);
            this.displayName = displayName;
            this.defaultSpeed = defaultSpeed;
            this.itemToUpgrade = itemToUpgrade;
            this.levelToUpgrade = levelToUpgrade;
        }

        public String getName() {
            return name;
        }

        public Component getDisplayName() {
            return displayName;
        }

        public int getDefaultSpeed() {
            return defaultSpeed;
        }

        public int getSpeed() {
            return CONFIG.getInt(this.name+"-speed", this.defaultSpeed);
        }

        public Material getItemToUpgrade() {
            return itemToUpgrade;
        }

        public int getLevelToUpgrade() {
            return levelToUpgrade;
        }
    }
}
