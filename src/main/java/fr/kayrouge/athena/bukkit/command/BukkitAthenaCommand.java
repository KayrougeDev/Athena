package fr.kayrouge.athena.bukkit.command;

import fr.kayrouge.athena.common.command.CAthenaCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BukkitAthenaCommand extends CAthenaCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return sendMessage(commandSender);
    }

}
