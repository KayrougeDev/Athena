package fr.kayrouge.athena.common.util.compat;

import fr.kayrouge.athena.common.util.CFastAccess;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EntityCompat {

    public static <T extends Entity> T spawnEntity(final @NotNull Location location, final @NotNull Class<T> clazz, final @Nullable Consumer<? super T> function) {
        if(PlatformCompat.isPaper) {
            return location.getWorld().spawn(location, clazz, function);
        }
        else {
            EntityType type = getTypeWithClass(clazz);
            if(type == null) throw new IllegalArgumentException("Given class is not in the EntityType enum");
            T entity = (T)location.getWorld().spawnEntity(location, type);
            function.accept(entity);
            return entity;
        }
    }

    private static EntityType getTypeWithClass(final Class<? extends Entity> clazz) {
        for(EntityType type : EntityType.values()) {
            if(type.getEntityClass() == clazz) {
                return type;
            }
        }
        return null;
    }

}
