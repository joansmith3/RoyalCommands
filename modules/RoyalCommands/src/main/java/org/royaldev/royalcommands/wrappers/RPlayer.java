package org.royaldev.royalcommands.wrappers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.royaldev.royalcommands.RoyalCommands;
import org.royaldev.royalcommands.WorldManager;
import org.royaldev.royalcommands.configuration.PConfManager;
import org.royaldev.royalcommands.listeners.BackpackListener;
import org.royaldev.royalcommands.rcommands.home.Home;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Wrapper class for {@link org.bukkit.entity.Player}. Stores instances in memory by the player's UUID key.
 */
public class RPlayer {

    private final static Map<UUID, RPlayer> players = Collections.synchronizedMap(new HashMap<UUID, RPlayer>());
    private final UUID uuid;
    private final PConfManager pcm;

    private RPlayer(final OfflinePlayer op) {
        this.uuid = op.getUniqueId();
        this.pcm = PConfManager.getPConfManager(this.uuid);
    }

    private RPlayer(final String name) {
        this.uuid = RoyalCommands.getInstance().getServer().getOfflinePlayer(name).getUniqueId();
        this.pcm = PConfManager.getPConfManager(this.uuid);
    }

    private RPlayer(final UUID uuid) {
        this.uuid = uuid;
        this.pcm = PConfManager.getPConfManager(this.uuid);
    }

    public static RPlayer getRPlayer(final OfflinePlayer op) {
        return RPlayer.getRPlayer(op.getUniqueId());
    }

    public static RPlayer getRPlayer(final String name) {
        return RPlayer.getRPlayer(RoyalCommands.getInstance().getServer().getOfflinePlayer(name).getUniqueId());
    }

    public static RPlayer getRPlayer(final UUID uuid) {
        synchronized (RPlayer.players) {
            if (RPlayer.players.containsKey(uuid)) return RPlayer.players.get(uuid);
            final RPlayer rp = new RPlayer(uuid);
            RPlayer.players.put(uuid, rp);
            return rp;
        }
    }

    /**
     * Gets this player's backpack.
     *
     * @param w World to get backpack in
     * @return Backpack - never null
     */
    public Inventory getBackpack(final World w) {
        String worldGroup = WorldManager.il.getWorldGroup(w);
        if (worldGroup == null) worldGroup = "w-" + w.getName();
        int invSize = this.pcm.getInt("backpack." + worldGroup + ".size", -1);
        if (invSize < 9) invSize = 36;
        if (invSize % 9 != 0) invSize = 36;
        final Inventory i = Bukkit.createInventory(new BackpackListener.BackpackHolder(this.getUUID(), w), invSize, "Backpack");
        if (!this.pcm.isSet("backpack." + worldGroup + ".item")) return i;
        for (int slot = 0; slot < invSize; slot++) {
            final ItemStack is = this.pcm.getItemStack("backpack." + worldGroup + ".item." + slot);
            if (is == null) continue;
            i.setItem(slot, is);
        }
        return i;
    }

    /**
     * Gets the maximum amount of homes this player is allowed to have. If there is no limit, -1 will be returned.
     *
     * @return Home limit
     */
    public int getHomeLimit() {
        final String name = this.getOfflinePlayer().getName();
        String group;
        if (RoyalCommands.getInstance().vh.usingVault() && this.getPlayer() != null) {
            try {
                group = RoyalCommands.getInstance().vh.getPermission().getPrimaryGroup(this.getPlayer());
            } catch (final Exception e) {
                group = "";
            }
        } else group = "";
        if (group == null) group = "";
        int limit;
        final FileConfiguration c = RoyalCommands.getInstance().getConfig();
        if (c.isSet("homes.limits.players." + name)) limit = c.getInt("homes.limits.players." + name, -1);
        else limit = c.getInt("homes.limits.groups." + group, -1);
        return limit;
    }

    public List<String> getHomeNames() {
        final List<String> names = new ArrayList<>();
        for (final Home h : this.getHomes()) {
            names.add(h.getName());
        }
        return names;
    }

    /**
     * Gets all homes for this player.
     *
     * @return A list
     */
    public List<Home> getHomes() {
        final List<Home> homes = new ArrayList<>();
        final PConfManager pcm = this.getPConfManager();
        if (pcm == null || !pcm.isSet("home")) return homes;
        for (final String name : pcm.getConfigurationSection("home").getKeys(false)) {
            homes.add(Home.fromPConfManager(this.getPConfManager(), name));
        }
        return homes;
    }

    public String getName() {
        return this.getOfflinePlayer().getName();
    }

    public OfflinePlayer getOfflinePlayer() {
        return RoyalCommands.getInstance().getServer().getOfflinePlayer(this.getUUID());
    }

    public PConfManager getPConfManager() {
        return this.pcm;
    }

    public Player getPlayer() {
        return RoyalCommands.getInstance().getServer().getPlayer(this.getUUID());
    }

    public UUID getUUID() {
        return this.uuid;
    }
}