package fr.kayrouge.athena.common.artifact;

import fr.kayrouge.athena.common.util.CFastAccess;
import fr.kayrouge.athena.common.util.compat.PlatformCompat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Artifacts {
    private static final Map<String, Artifact> artifacts = new HashMap<>();

    public static final Artifact TEST_ARTIFACT = register(new TestArtifact(Component.text("Test").color(NamedTextColor.RED), "test"));


    public static final NamespacedKey ARTIFACT_KEY = new NamespacedKey(PlatformCompat.INSTANCE, "artifact");

    private static Artifact register(Artifact artifact) {
        CFastAccess.PLUGIN_MANAGER.registerEvents(artifact, PlatformCompat.INSTANCE);
        artifacts.put(artifact.getName(), artifact);
        return artifact;
    }

    public static List<Artifact> getPlayerArtifacts(Player player) {
        List<Artifact> list = new ArrayList<>();
        for (ItemStack item : player.getInventory().getContents()) {
            if(item == null || item.getItemMeta() == null) continue;
            if(item.getItemMeta().getPersistentDataContainer().has(ARTIFACT_KEY)) {
                String name = item.getItemMeta().getPersistentDataContainer().get(ARTIFACT_KEY, PersistentDataType.STRING);
                if(name != null && artifacts.containsKey(name)) {
                    list.add(artifacts.get(name));
                }
            }
        }
        return list;
    }

    public static Map<String, Artifact> getArtifacts() {
        return artifacts;
    }

    @Nullable
    public static Artifact getArtifact(String name) {
        return getArtifacts().get(name);
    }

    public static void init() {}
}
