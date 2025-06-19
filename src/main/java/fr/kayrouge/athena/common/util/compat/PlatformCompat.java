package fr.kayrouge.athena.common.util.compat;

import fr.kayrouge.athena.bukkit.BukkitAthena;
import fr.kayrouge.athena.common.CAthena;
import fr.kayrouge.athena.common.util.CPlatform;
import fr.kayrouge.athena.paper.PaperAthena;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

public class PlatformCompat {

    public static final CAthena INSTANCE = getMain();
    public static final Logger LOGGER = INSTANCE.getLogger();
    public static final boolean isPaper = INSTANCE.getPlatform() == CPlatform.PAPER;

    private static CAthena getMain() {
        if(BukkitAthena.INSTANCE != null) {
            return BukkitAthena.INSTANCE;
        }
        return PaperAthena.INSTANCE;
    }

    public static void sendMessage(CommandSender target, Component message) {
        if(PlatformCompat.isPaper) {
            target.sendMessage(message);
        }
        else {
            target.sendMessage(LegacyComponentSerializer.legacySection().serialize(message));
        }
    }

}
