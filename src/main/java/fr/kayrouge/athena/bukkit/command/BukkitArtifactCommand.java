package fr.kayrouge.athena.bukkit.command;

import fr.kayrouge.athena.common.artifact.Artifacts;
import fr.kayrouge.athena.common.command.CArtifactCommand;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BukkitArtifactCommand extends CArtifactCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(sender instanceof Player player) {
            if(args.length > 0) {
                giveArtifact(player, args[0]);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(args.length == 1) {
            return Artifacts.getArtifacts().keySet().stream().toList();
        }
        else {
            return List.of();
        }
    }
}
