package net.immortalapi.commandbuilder;

import org.bukkit.command.CommandSender;

public interface ImmortalCommandExecutor {

    void execute(final CommandSender sender, final String[] args);

}
