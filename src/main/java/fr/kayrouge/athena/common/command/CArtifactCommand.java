package fr.kayrouge.athena.common.command;

import fr.kayrouge.athena.common.artifact.Artifacts;
import fr.kayrouge.athena.common.util.CFastAccess;
import fr.kayrouge.athena.common.util.compat.ItemCompat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CArtifactCommand {

    public void giveArtifact(Player player, String name) {
        if(!Artifacts.getArtifacts().containsKey(name)) {
            CFastAccess.sendMessage(player, Component.text("Invalid artifact: ").append(
                    Component.text(name).color(NamedTextColor.GRAY)
            ));
            return;
        }

        ItemStack stack = new ItemStack(Material.CARROT_ON_A_STICK);
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(Artifacts.ARTIFACT_KEY, PersistentDataType.STRING, name);
        ItemCompat.setDisplayName(meta, Artifacts.getArtifact(name).getDisplayName());
        stack.setItemMeta(meta);

        player.getInventory().addItem(stack);

    }

}
