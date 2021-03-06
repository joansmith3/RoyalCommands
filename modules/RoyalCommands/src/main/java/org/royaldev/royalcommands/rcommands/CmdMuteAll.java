/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.royaldev.royalcommands.rcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalcommands.AuthorizationHandler.PermType;
import org.royaldev.royalcommands.MessageColor;
import org.royaldev.royalcommands.RoyalCommands;
import org.royaldev.royalcommands.configuration.PlayerConfiguration;
import org.royaldev.royalcommands.configuration.PlayerConfigurationManager;

@ReflectCommand
public class CmdMuteAll extends BaseCommand {

    private boolean allMuted = false;

    public CmdMuteAll(final RoyalCommands instance, final String name) {
        super(instance, name, true);
    }

    @Override
    public boolean runCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
        for (final Player p : this.plugin.getServer().getOnlinePlayers()) {
            if (this.plugin.isVanished(p, cs) || this.ah.isAuthorized(p, cmd, PermType.EXEMPT)) continue;
            if (cs instanceof Player) {
                if (p == cs) continue;
            }
            PlayerConfiguration pcm = PlayerConfigurationManager.getConfiguration(p);
            if (!this.allMuted) {
                pcm.set("muted", true);
                p.sendMessage(MessageColor.NEGATIVE + "You have been muted!");
            } else {
                pcm.set("muted", false);
                p.sendMessage(MessageColor.POSITIVE + "You have been unmuted!");
            }
        }
        if (!this.allMuted) {
            cs.sendMessage(MessageColor.POSITIVE + "You have muted all players.");
            this.allMuted = true;
        } else {
            cs.sendMessage(MessageColor.POSITIVE + "You have unmuted all players.");
            this.allMuted = false;
        }
        return true;
    }
}
