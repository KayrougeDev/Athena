package fr.kayrouge.athena.common.event;

import fr.kayrouge.athena.common.manager.CSpecialPlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class CSpecialPlayerEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        CSpecialPlayerManager spm = CSpecialPlayerManager.getOrCreate(event.getPlayer());
        spm.onJoin();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        CSpecialPlayerManager spm = CSpecialPlayerManager.get(event.getPlayer());
        if(spm != null) {
            spm.onQuit();
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        CSpecialPlayerManager spm = CSpecialPlayerManager.get(event.getPlayer());
        if(spm != null) {
            if(spm.isInDevWorld()) {
                event.setRespawnLocation(spm.getDevWorld().getSpawnLocation());
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        CSpecialPlayerManager spm = CSpecialPlayerManager.get(event.getPlayer());
        if(spm != null) {
            if(spm.isInDevWorld()) {
                event.setKeepLevel(true);
                event.setKeepInventory(true);
            }
        }
    }
}
