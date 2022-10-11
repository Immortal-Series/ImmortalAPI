package me.lukeben.commandbuilder;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import me.lukeben.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;

public abstract class ImmortalCommand extends Command implements ImmortalCommandExecutor {

    /*
     *
     * @Author Aethonx - Thank you for this amazing class.
     *
     */

    // --------------------------------------------------
    // Constructor
    // --------------------------------------------------
    @Getter
    protected List<ImmortalSubCommand> subCommands;

    @Setter
    protected boolean handleHelpWithNoArgs = true;

    @Getter
    @Setter
    protected boolean handleArgsInMain = false;

    @Getter
    private final List<String> aliases = Lists.newArrayList();

    /**
     * The command sender is either the console or the player.
     * <p>
     * You will need to check if it's the player executing the command.
     */
    protected CommandSender sender;
    /**
     * The command labels from the command.
     * <p>
     * Example [/example, /label]
     */
    protected String label;

    // --------------------------------------------------
    // Temporary fields
    // --------------------------------------------------
    /**
     * The arguments from the command.
     * <p>
     * Will update every time a command is executed.
     */
    protected String[] args;

    /**
     * @param name The main command label "/<name>"
     */
    protected ImmortalCommand(final String name) {
        super(name);
        subCommands = Lists.newArrayList();
        this.setAliases(aliases);

    }

    // --------------------------------------------------
    // Default command execution.
    // --------------------------------------------------


    @Override
    public boolean execute(final CommandSender sender, final String commandLabel, final String[] args) {

        this.sender = sender;
        this.label = commandLabel;
        this.args = args;

        try {

            /*
             * Manages the execution of the sub commands.
             */
            if (!handleArgsInMain && args.length != 0 && getSubCommands() != null) {

                final List<String> arguments = Lists.newArrayList(args);
                arguments.removeIf(s -> s.equalsIgnoreCase(args[0]));

                getSubCommands().stream()
                        .filter(cmd -> cmd.getLabel().equalsIgnoreCase(args[0]))
                        .findFirst()
                        .ifPresent(cmd -> cmd.onCommand(sender, arguments.toArray(new String[0])));

                return false;
            }

            /*
             * Execute our command.
             */

            execute(sender, args);

        } catch (final CommandException ex) {
            tell(ex.getMessage());
        }

        return false;
    }

    // --------------------------------------------------
    // Checks
    // --------------------------------------------------

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
     * Check a boolean.
     *
     * @param toCheck
     * @param falseMessage
     */
    protected void checkBoolean(final boolean toCheck, final String falseMessage) {
        if (!toCheck)
            returnTell(falseMessage);
    }

    /**
     * Check to see if an object is null or not.
     *
     * @param toCheck
     * @param falseMessage
     */
    protected void checkNotNull(final Object toCheck, final String falseMessage) {
        if (toCheck == null)
            returnTell(falseMessage);
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

    protected Player findPlayer(final int arg, final String falseMessage) {
        final Player player = Bukkit.getPlayer(args[arg]);
        checkBoolean(player != null && player.isOnline(), falseMessage);
        return player;
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
     * Will send a multi lined message to the sender supports colour codes.
     *
     * @param messages
     */
    protected void tell(final String... messages) {
        for (final String message : messages)
            tell(Methods.color(message));
    }

    /**
     * Singled lined message.
     *
     * @param message
     */
    protected void tell(final String message) {
        sender.sendMessage(Methods.color(message));
    }

    /**
     * Throws our exception.
     *
     * @param message
     */
    protected void returnTell(final String message) {
        throw new CommandException(message);
    }

    // --------------------------------------------------
    // Tab complete
    // --------------------------------------------------

    @Override
    public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) throws IllegalArgumentException {

        final List<String> tabComplete = Lists.newArrayList();

        /**
         * Provids the subcommand labels for the args on
         * /example <will be added>
         */
        if (getSubCommands() != null) {
            if (!getSubCommands().isEmpty() && args.length == 1) {
                getSubCommands().forEach(cmd -> tabComplete.add(cmd.label.toLowerCase()));
            }
        }

        if (tabComplete(args) != null)
            tabComplete.addAll(tabComplete(args));

        /**
         * Handles the sub command and there custom tab complete.
         */
        if (getSubCommands() != null) {
            if (!getSubCommands().isEmpty() && args.length > 1) {
                for (final ImmortalSubCommand cmd : getSubCommands()) {
                    if (!cmd.label.equalsIgnoreCase(args[0]))
                        continue;

                    final List<String> cleanedArgs = Lists.newArrayList(args);
                    cleanedArgs.removeIf(s -> s.equalsIgnoreCase(args[0]));
                    return cmd.tabComplete(sender, cleanedArgs.toArray(new String[0]));
                }
            }
        }
        /**
         * If the tabcomplete isn'
         * t null for the main command we will just send a blank list.
         */
        return tabComplete;
    }

    /**
     * You will need to override this for local tab completion in your main command.
     *
     * @return The
     */
    protected List<String> tabComplete(final String[] args) {
        return null;
    }


    /**
     * Easily register the command directly through the command map.
     */
    public void registerCommand() {
        try {
            final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            Command instanceCommand = this;
            List<String> currentAliases = instanceCommand.getAliases();

            for(String alias : aliases) {
                currentAliases.add(alias);
            }

            instanceCommand.setAliases(currentAliases);

            final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(getLabel(), instanceCommand);

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    protected CommandSender getSender() {
        return sender;
    }

    protected String[] getArgs() {
        return args;
    }

    protected void registerSubCommand(ImmortalSubCommand subCommand) {
        subCommands.add(subCommand);
    }

}

