package fr.kayrouge.athena.bukkit.command;

import fr.kayrouge.athena.common.command.CDevWorldCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitDevWorldCommand extends CDevWorldCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(sender instanceof Player player) {
            join(player);
        }

        return true;
    }
}
