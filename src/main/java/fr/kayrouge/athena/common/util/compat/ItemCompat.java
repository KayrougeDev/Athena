package fr.kayrouge.athena.common.util.compat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemCompat {

    public static void setDisplayName(ItemMeta meta, Component c) {
        if(PlatformCompat.isPaper) {
            meta.displayName(c);
        }
        else {
            meta.setDisplayName(LegacyComponentSerializer.legacySection().serialize(c));
        }
    }

    public static void setLore(ItemMeta meta, List<Component> c) {
        if(PlatformCompat.isPaper) {
            meta.lore(c);
        }
        else {
            meta.setLore(c.stream().map(LegacyComponentSerializer.legacySection()::serialize).toList());
        }
    }

    public static String getTranslationKey(Material material) {
        if(PlatformCompat.isPaper) {
            return material.translationKey();
        }
        else {
            if(material.isBlock()) {
                return material.getBlockTranslationKey();
            }
            else {
                return material.getItemTranslationKey();
            }
        }
    }

}
