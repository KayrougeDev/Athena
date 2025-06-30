package fr.kayrouge.athena.common.command;

import fr.kayrouge.athena.common.manager.CSpecialPlayerManager;
import fr.kayrouge.athena.common.util.CFastAccess;
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
                    player, getNoHomeNamedComponent(name)
            );
            return;
        }
        Location location = manager.getHomes().get(name);

        player.teleport(location);
    }

    public void setHome(Player player, String name) {
        CSpecialPlayerManager manager = CSpecialPlayerManager.getOrCreate(player);
        int maxHomeCount = CFastAccess.CONFIG.getInt("maxHomePerPlayer", 3);
        if(!manager.getHomes().containsKey(name) && manager.getHomes().size() >= maxHomeCount) {
            CFastAccess.sendMessage(player, Component.text("You already have the maximum number of homes: ").color(NamedTextColor.RED)
                    .append(Component.text(maxHomeCount).color(NamedTextColor.GOLD)));
            return;
        }

        player.sendMessage("Putting "+name+" in the homes");
        manager.getHomes().forEach((s, location) -> {
            player.sendMessage(s+" "+location.toString());
        });
        manager.getHomes().put(name, player.getLocation());
        player.sendMessage("AFTER put "+name+" in the homes");
        manager.getHomes().forEach((s, location) -> {
            player.sendMessage(s+" "+location.toString());
        });
        CFastAccess.sendMessage(player, Component.text("Home ")
                .append(Component.text(name).color(NamedTextColor.GOLD))
                .append(Component.text(" has been created !")).color(NamedTextColor.RED));
    }

    public void delHome(Player player, String name) {
        CSpecialPlayerManager manager = CSpecialPlayerManager.getOrCreate(player);
        if(!manager.getHomes().containsKey(name) ) {
            CFastAccess.sendMessage(player, getNoHomeNamedComponent(name));
            return;
        }

        manager.getHomes().remove(name);
        CFastAccess.sendMessage(player, Component.text("Home ")
                .append(Component.text(name).color(NamedTextColor.GOLD))
                .append(Component.text(" has been deleted !")).color(NamedTextColor.RED));
    }

    public void info(Player player, String name) {
        CSpecialPlayerManager manager = CSpecialPlayerManager.getOrCreate(player);
        if(!manager.getHomes().containsKey(name) ) {
            CFastAccess.sendMessage(player, getNoHomeNamedComponent(name));
            return;
        }

        Location location = manager.getHomes().get(name);

        Component component = Component
                .text("Home Info: ").appendNewline()
                        .append(
                                Component.text("    Name: ").append(Component.text(name).color(NamedTextColor.GOLD)).appendNewline(),
                                Component.text("    Location:")
                                        .appendNewline()
                                        .append(
                                                Component.text("        World: ")
                                                    .color(NamedTextColor.DARK_RED)
                                                    .append(Component.text(location.getWorld().getName()).color(NamedTextColor.GOLD))
                                                    .appendNewline(),
                                                Component.text("        X: ")
                                                        .color(NamedTextColor.DARK_RED)
                                                        .append(Component.text(location.getBlockX()).color(NamedTextColor.GOLD))
                                                        .appendNewline(),
                                                Component.text("        Y: ")
                                                        .color(NamedTextColor.DARK_RED)
                                                        .append(Component.text(location.getBlockY()).color(NamedTextColor.GOLD))
                                                        .appendNewline(),
                                                Component.text("        Z: ")
                                                        .color(NamedTextColor.DARK_RED)
                                                        .append(Component.text(location.getBlockZ()).color(NamedTextColor.GOLD))
                                                        .appendNewline()
                                        )
                        )
                .color(NamedTextColor.RED);

        CFastAccess.sendMessage(player, component);
    }

    public List<String> getHomeList(Player player) {
        CSpecialPlayerManager manager = CSpecialPlayerManager.get(player);
        if(manager != null) {
            return manager.getHomes().keySet().stream().toList();
        }
        return List.of();
    }

    public Component getNoHomeNamedComponent(String name) {
        return Component.text("You don't have a homes called ").color(NamedTextColor.RED)
                .append(
                        Component.text(name).color(NamedTextColor.GOLD)
                );
    }

}
