package fr.kayrouge.athena.common.command;

import fr.kayrouge.athena.common.manager.CSpecialPlayerManager;
import fr.kayrouge.athena.common.util.CFastAccess;
import fr.kayrouge.athena.common.util.compat.PlayerCompat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class CHomeCommand {

    public void teleport(Player player, String name) {
        CSpecialPlayerManager manager = CSpecialPlayerManager.getOrCreate(player);

        if(!manager.getHomes().containsKey(name)) {
            CFastAccess.sendMessage(
                    player, PlayerCompat.displayName(player).color(NamedTextColor.DARK_RED)
                    .append(
                            Component.text(" don't have a homes called ").color(NamedTextColor.RED),
                            Component.text(name).color(NamedTextColor.GOLD)
                    )
            );
            return;
        }
        Location location = manager.getHomes().get(name);

        player.teleport(location);
    }

    public List<String> getHomeList(Player player) {
        CSpecialPlayerManager manager = CSpecialPlayerManager.get(player);
        if(manager != null) {
            return manager.getHomes().keySet().stream().toList();
        }
        return List.of();
    }

}
