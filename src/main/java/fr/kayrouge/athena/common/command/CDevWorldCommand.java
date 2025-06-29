package fr.kayrouge.athena.common.command;

import fr.kayrouge.athena.common.manager.CSpecialPlayerManager;
import fr.kayrouge.athena.common.util.CFastAccess;
import fr.kayrouge.athena.common.util.compat.PlatformCompat;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class CDevWorldCommand {

    public void join(Player player) {
        if(CFastAccess.isExperimentalDisabledAndSendMessage(player)) return;
        CSpecialPlayerManager spm = CSpecialPlayerManager.getOrCreate(player);
        if(spm.isInDevWorld()) {
            player.sendMessage(Component.text("Going to overworld"));
            spm.moveToOverworld();
        }
        else {
            player.sendMessage(Component.text("Going to devworld"));
            spm.moveToDevWorld();
        }
    }

}
