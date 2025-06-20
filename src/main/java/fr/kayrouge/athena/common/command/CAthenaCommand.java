package fr.kayrouge.athena.common.command;

import fr.kayrouge.athena.common.util.CFastAccess;
import fr.kayrouge.athena.common.util.compat.PlatformCompat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class CAthenaCommand {

    public boolean sendMessage(CommandSender commandSender) {
        Component c =Component.text("Athena v"+ PlatformCompat.INSTANCE.getDescription().getVersion()+":").appendNewline()
                .append(Component.text("    Platform: ")
                        .append(Component.text()
                                .content(PlatformCompat.INSTANCE.getPlatform().toString())
                                .color(NamedTextColor.GRAY)
                        )
                );

        CFastAccess.sendMessage(commandSender, c);

        return true;
    }

}
