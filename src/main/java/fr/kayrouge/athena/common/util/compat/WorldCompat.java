package fr.kayrouge.athena.common.util.compat;

import org.bukkit.NamespacedKey;
import org.bukkit.WorldCreator;

public class WorldCompat {

    public static WorldCreator newWorldCreator(NamespacedKey key) {
        if(PlatformCompat.isPaper) {
            return new WorldCreator(key);
        }
        else {
            return new WorldCreator(key.getKey());
        }
    }

}
