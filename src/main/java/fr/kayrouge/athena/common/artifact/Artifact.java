package fr.kayrouge.athena.common.artifact;

import net.kyori.adventure.text.Component;
import org.bukkit.event.Listener;

public abstract class Artifact implements Listener {

    private final Component displayName;
    private final String name;

    public Artifact(String name, Component displayName) {
        this.displayName = displayName;
        this.name = name;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name;
    }
}
