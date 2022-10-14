package net.immortal.commandbuilder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.immortal.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;


@Setter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ImmortalSubCommand implements ImmortalCommandExecutor {

    /**
     * Contains the label for our sub command must be set in the super.
     */
    @Getter
    protected final String label;
    /**
     * Optional
     */
    protected String description;
    protected String usage;

    // --------------------------------------------------
    // Temporary command fields.
    // --------------------------------------------------
    /*
     * Used for utilities
     */
    @Getter
    protected CommandSender sender;
    protected String[] args;

    /**
     * Handles commands.
     *
     * @param sender
     * @param args
     */
    protected void onCommand(final CommandSender sender, final String[] args) {
        this.sender = sender;
        this.args = args;

        try {
            execute(sender, args);
        } catch (final CommandException ex) {
            tell(ex.getMessage());
        }

    }


    // --------------------------------------------------
    // Command fields.
    // --------------------------------------------------

    /**
     * @param sender The command sender.
     * @param args   Command arguments not including the sub command label.
     * @return
     */
    protected List<String> tabComplete(final CommandSender sender, final String[] args) {
        return null;
    }

    protected Player findPlayer(final String name, final String falseMessage) {
        final Player player = Bukkit.getPlayer(name);
        checkBoolean(player != null && !player.isOnline(), falseMessage);
        return player;
    }

    protected void checkBoolean(final boolean toCheck, final String falseMessage) {
        if (!toCheck)
            returnTell(falseMessage);
    }

    protected void checkNotNull(final Object toCheck, final String falseMessage) {
        if (toCheck == null)
            returnTell(falseMessage);
    }


    protected int findNumber(final int arg, final String falseMessage) {
        checkBoolean(arg < args.length, falseMessage);

        Integer parsedNumber = null;

        try {
            parsedNumber = Integer.parseInt(args[arg]);
        } catch (final NumberFormatException ex) {
        }

        checkNotNull(parsedNumber, falseMessage);
        return parsedNumber;
    }

    /**
     * Stops the command from executing further.
     *
     * @param message Message
     */
    protected void returnTell(final String message) {
        throw new CommandException(message);
    }

    /**
     * Send multi lined message.
     *
     * @param messages Multi lined message to the player.
     */
    protected void tell(final String... messages) {
        for (final String message : messages)
            tell(message);
    }

    /**
     * Send a sing lined message to the command sender.
     *
     * @param message Message.
     */
    protected void tell(final String message) {
        sender.sendMessage(Methods.color(message));
    }

    /**
     * @return Get's the player who sent the command if it's console then return null.
     */
    protected Player getPlayer() {
        return isPlayer() ? (Player) getSender() : null;
    }

    /**
     * @return True of the player is a sender false if it isn't
     */
    protected boolean isPlayer() {
        return sender instanceof Player;
    }

    /**
     * Checks to see if the CommandSender has the permission.
     *
     * @param permission   Permission to check
     * @param noPermission No permission error.
     * @throws CommandException
     */
    protected void checkPermission(final String permission, final String noPermission) throws CommandException {
        if (isPlayer() && !getSender().hasPermission(permission))
            throw new CommandException(noPermission);
    }

    /**
     * Check
     *
     * @param minimumArguments
     * @param message
     */
    protected void checkArgs(final int minimumArguments, final String message) {
        if (args.length < minimumArguments)
            returnTell(message);
    }

}

