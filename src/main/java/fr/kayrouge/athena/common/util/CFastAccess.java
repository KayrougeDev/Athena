package fr.kayrouge.athena.common.util;

import fr.kayrouge.athena.common.util.compat.PlatformCompat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class CFastAccess {

    public static final PluginManager PLUGIN_MANAGER = PlatformCompat.INSTANCE.getServer().getPluginManager();

    public static void sendMessage(CommandSender target, Component message) {
        if(PlatformCompat.isPaper) {
            target.sendMessage(message);
        }
        else {
            target.sendMessage(LegacyComponentSerializer.legacySection().serialize(message));
        }
    }


    public static boolean isExperimentalDisabledAndSendMessage(CommandSender sender) {
        if(PlatformCompat.INSTANCE.isExperimentalFeaturesEnabled()) {
            PlatformCompat.INSTANCE.sendExperimentalFeatureDisabledMessage(sender);
            return false;
        }
        return true;
    }
}
