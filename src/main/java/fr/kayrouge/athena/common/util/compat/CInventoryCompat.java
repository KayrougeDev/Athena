package fr.kayrouge.athena.common.util.compat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CInventoryCompat {

    public static Inventory createInventory(InventoryHolder holder, int size, Component name) {
        if(PlatformCompat.isPaper) {
            return Bukkit.createInventory(holder, size, name);
        }
        else {
            return Bukkit.createInventory(holder, size, LegacyComponentSerializer.legacySection().serialize(name));
        }
    }

}
