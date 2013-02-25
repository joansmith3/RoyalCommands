package org.royaldev.royalcommands.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.royaldev.royalcommands.RoyalCommands;
import org.royaldev.royalcommands.rcommands.CmdBack;

@SuppressWarnings("unused")
public class RoyalCommandsEntityListener implements Listener {

    public static RoyalCommands plugin;

    public RoyalCommandsEntityListener(RoyalCommands instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent ent) {
        if (!(ent instanceof PlayerDeathEvent)) return;
        if (!plugin.backDeath) return;
        PlayerDeathEvent e = (PlayerDeathEvent) ent;
        if (e.getEntity() == null) return;
        Player p = e.getEntity();
        Location pLoc = p.getLocation();
        CmdBack.addBackLocation(p, pLoc);
        if (plugin.isAuthorized(p, "rcmds.back"))
            p.sendMessage(ChatColor.BLUE + "Type " + ChatColor.GRAY + "/back" + ChatColor.BLUE + " to go back to where you died.");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void oneHitKill(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) event;
        Entity e = ev.getDamager();
        Entity ed = ev.getEntity();
        if (!(e instanceof Player)) return;
        Player p = (Player) e;
        if (!plugin.getUserdata(p).getBoolean("ohk")) return;
        if (ed instanceof LivingEntity) {
            LivingEntity le = (LivingEntity) ed;
            le.damage(le.getHealth() * 1000);
            le.setLastDamageCause(new EntityDamageByEntityEvent(p, le, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1));
        }
        if (ed instanceof EnderDragonPart) {
            EnderDragonPart ldp = (EnderDragonPart) ed;
            LivingEntity le = ldp.getParent();
            le.damage(le.getHealth() * 1000);
            le.setLastDamageCause(new EntityDamageByEntityEvent(p, le, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void buddhaMode(EntityDamageEvent e) {
        Entity ent = e.getEntity();
        if (!(ent instanceof Player)) return;
        Player p = (Player) ent;
        if (!plugin.getUserdata(p).getBoolean("buddha")) return;
        if (e.getDamage() >= p.getHealth()) e.setDamage(p.getHealth() - 1);
        if (p.getHealth() == 1) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void godMode(EntityDamageEvent e) {
        Entity ent = e.getEntity();
        if (!(ent instanceof Player)) return;
        Player p = (Player) ent;
        if (plugin.getUserdata(p).getBoolean("godmode")) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityTarget(EntityTargetEvent event) {
        if (!(event.getTarget() instanceof Player)) return;
        Player p = (Player) event.getTarget();
        if (plugin.getUserdata(p).getBoolean("mobignored")) event.setTarget(null);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player p = (Player) event.getEntity();
        if (plugin.getUserdata(p).getBoolean("godmode")) {
            event.setFoodLevel(20);
            p.setSaturation(20F);
        }
    }

}