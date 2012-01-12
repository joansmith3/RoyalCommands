package tk.royalcraf.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.royalcraf.royalcommands.RoyalCommands;

public class Feed implements CommandExecutor {

    RoyalCommands plugin;

    public Feed(RoyalCommands plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label,
                             String[] args) {
        if (cmd.getName().equalsIgnoreCase("feed")) {
            if (!plugin.isAuthorized(cs, "rcmds.feed")) {
                cs.sendMessage(ChatColor.RED
                        + "You don't have permission for that!");
                plugin.log.warning("[RoyalCommands] " + cs.getName()
                        + " was denied access to the command!");
                return true;
            }
            if (args.length < 1) {
                if (!(cs instanceof Player)) {
                    cs.sendMessage(ChatColor.RED
                            + "You can't feed the console!");
                    return true;
                }
                Player t = (Player) cs;
                t.sendMessage(ChatColor.BLUE + "You have fed yourself!");
                t.setFoodLevel(20);
                return true;
            }
            Player t = plugin.getServer().getPlayer(args[0].trim());
            if (t == null) {
                cs.sendMessage(ChatColor.RED + "That player does not exist!");
                return true;
            }
            if (plugin.isVanished(t)) {
                cs.sendMessage(ChatColor.RED + "That player does not exist!");
                return true;
            }
            cs.sendMessage(ChatColor.BLUE + "You have fed " + ChatColor.GRAY
                    + t.getName() + ChatColor.BLUE + ".");
            t.sendMessage(ChatColor.BLUE + "You have been fed by "
                    + ChatColor.GRAY + cs.getName() + ChatColor.BLUE + "!");
            t.setFoodLevel(20);
            return true;
        }
        return false;
    }

}
