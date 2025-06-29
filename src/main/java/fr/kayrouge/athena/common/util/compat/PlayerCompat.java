package fr.kayrouge.athena.common.util.compat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class PlayerCompat {

    public static Component displayName(Player player) {
        if(PlatformCompat.isPaper) {
            return player.displayName();
        }
        else {
            return LegacyComponentSerializer.legacySection().deserialize(player.getDisplayName());
        }
    }

}
