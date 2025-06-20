package fr.kayrouge.athena.common.event;

import fr.kayrouge.athena.common.util.compat.PlatformCompat;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.persistence.PersistentDataType;

public class CMiscEvents implements Listener {

    public static final NamespacedKey USED_STAIR = new NamespacedKey(PlatformCompat.INSTANCE, "used_stair");

    @EventHandler
    public void clickOnStair(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {
            Block block = e.getClickedBlock();
            if(block.getBlockData() instanceof Stairs) {
                Chicken sitEntity = (Chicken) e.getPlayer().getWorld().spawnEntity(block.getLocation(), EntityType.CHICKEN);
                sitEntity.setPersistent(true);
                sitEntity.getPersistentDataContainer().set(USED_STAIR, PersistentDataType.BOOLEAN, true);
                sitEntity.setAI(false);
                sitEntity.teleport(sitEntity.getLocation().add(0.5d,-0.1d,0.5d).add(block.getFace(block).getDirection()));
                sitEntity.setSilent(true);
                sitEntity.setInvulnerable(true);
                sitEntity.setInvisible(true);
                sitEntity.addPassenger(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void dismount(EntityDismountEvent e) {
        if(e.getEntity() instanceof Player player) {
            if(e.getDismounted() instanceof Chicken sitEntity) {
                if(sitEntity.getPersistentDataContainer().has(USED_STAIR)) {
                    sitEntity.remove();
                    return;
                }
            }
            if(e.getDismounted() instanceof AbstractArrow arrow) {
                if(arrow.getPersistentDataContainer().has(USED_STAIR)) {
                    arrow.remove();
                    return;
                }
            }
        }
    }

    @EventHandler
    public void shootArrow(EntityShootBowEvent e) {
        if(e.getProjectile() instanceof AbstractArrow arrow) {
            arrow.addPassenger(e.getEntity());
            arrow.setGlowing(true);
            arrow.getPersistentDataContainer().set(USED_STAIR, PersistentDataType.BOOLEAN, true);
            arrow.setDamage(arrow.getDamage()/2);
        }
    }

    @EventHandler
    public void pickUp(PlayerPickupArrowEvent e) {
        if(e.getArrow().getPersistentDataContainer().has(USED_STAIR)) {
            e.getArrow().remove();
            e.setCancelled(true);
        }
    }

}
