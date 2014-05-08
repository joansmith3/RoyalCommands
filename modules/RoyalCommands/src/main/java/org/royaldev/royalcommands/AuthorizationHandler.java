package org.royaldev.royalcommands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

public class AuthorizationHandler {

    private final RoyalCommands plugin;

    public AuthorizationHandler(RoyalCommands instance) {
        this.plugin = instance;
    }

    private boolean permissionsLoaded() {
        return this.plugin.vh.usingVault() && this.plugin.vh.getPermission() != null;
    }

    private boolean chatLoaded() {
        return this.plugin.vh.usingVault() && this.plugin.vh.getChat() != null;
    }

    private boolean economyLoaded() {
        return this.plugin.vh.usingVault() && this.plugin.vh.getEconomy() != null;
    }

    /**
     * Returns if something has the specified permission node. That something can be one of the following:
     * <ul>
     * <li>Player</li>
     * <li>OfflinePlayer</li>
     * <li>CommandSender</li>
     * <li>RemoteConsoleCommandSender</li>
     * <li>BlockCommandSender</li>
     * </ul>
     * Anything else will throw an IllegalArgumentException.
     *
     * @param o    Thing to check for permissions
     * @param node Node to check for
     * @return true or false
     * @throws IllegalArgumentException If invalid type if passed for <code>o</code>.
     */
    public boolean isAuthorized(Object o, String node) {
        if (o instanceof RemoteConsoleCommandSender)
            return this.iARemoteConsoleCommandSender((RemoteConsoleCommandSender) o, node);
        else if (o instanceof Player) return this.iAPlayer((Player) o, node);
        else if (o instanceof OfflinePlayer) return this.iAOfflinePlayer((OfflinePlayer) o, node);
        else if (o instanceof BlockCommandSender) return this.iABlockCommandSender((BlockCommandSender) o, node);
        else if (o instanceof CommandSender) return this.iACommandSender((CommandSender) o, node);
        else throw new IllegalArgumentException("Object was not a valid authorizable!");
    }

    private boolean iAPlayer(Player p, String node) {
        if (this.plugin.vh.usingVault() && this.permissionsLoaded()) return this.plugin.vh.getPermission().has(p, node);
        return p.hasPermission(node);
    }

    private boolean iAOfflinePlayer(OfflinePlayer op, String node) {
        if (op.isOnline()) return this.iAPlayer(op.getPlayer(), node);
        if (this.plugin.vh.usingVault() && this.permissionsLoaded()) {
            final String world = this.plugin.getServer().getWorlds().get(0).getName();
            return this.plugin.vh.getPermission().has(world, op.getName(), node);
        }
        return false;
    }

    private boolean iARemoteConsoleCommandSender(RemoteConsoleCommandSender rccs, String node) {
        return true;
    }

    private boolean iABlockCommandSender(BlockCommandSender cb, String node) {
        return true;
    }

    private boolean iACommandSender(CommandSender cs, String node) {
        if (this.plugin.vh.usingVault() && permissionsLoaded()) return this.plugin.vh.getPermission().has(cs, node);
        return cs.hasPermission(node);
    }

}