package fr.kayrouge.athena.common.command;

import fr.kayrouge.athena.common.event.CFurnaceEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CFurnacesCommand {

    public boolean execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof Player)) return true;
        Player player = (Player)commandSender;

        CFurnaceEvent.Type type;

        if(args.length == 0) {
            type = CFurnaceEvent.Type.IRON;
        }
        else {
            try {
                type = CFurnaceEvent.Type.valueOf(args[0].toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                player.sendMessage("Type de four inconnu");
                return true;
            }
        }

        player.getInventory().addItem(CFurnaceEvent.createFurnaceStack(type));
        return true;
    }

    public List<String> getFurnacesList() {
        return Arrays.stream(CFurnaceEvent.Type.values()).filter(type -> type != CFurnaceEvent.Type.NORMAL).map(Enum::name).toList();
    }

}
