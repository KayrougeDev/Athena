package fr.kayrouge.athena.common.artifact;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class TestArtifact extends Artifact {

    public TestArtifact(Component displayName, String name) {
        super(name, displayName);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(Artifacts.getPlayerArtifacts(event.getPlayer()).contains(this)) {
            event.getPlayer().getInventory().addItem(new ItemStack(Material.ROOTED_DIRT));
        }
    }
}
