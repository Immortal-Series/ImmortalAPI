package net.immortal.commandbuilder;

import com.google.common.collect.Lists;
import net.immortal.utils.Methods;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class ImmortalHelp {

    public static final int COMMANDS_PER_PAGE = 9;

    private final CommandSender sender;
    private final ImmortalCommand command;
    private final List<ImmortalSubCommand> subCommands;
    private final int page;

    public ImmortalHelp(CommandSender sender, ImmortalCommand command, List<ImmortalSubCommand> subCommands, int page) {
        this.sender = sender;
        this.command = command;
        this.subCommands = subCommands;

        this.page = page;
    }

    public void buildAndSend() {

        if (sender instanceof Player) {

            Player player = (Player) sender;

            List<ImmortalSubCommand> subCommandPage = getPage(page);


            List<EasyComponent> subCommands = Lists.newArrayList();
            subCommandPage.forEach(cmd -> subCommands.add(
                    EasyComponent
                            .builder(Methods.placeholders("&6{0} &e{1}&7- &f{2}", "/" + command.label + " " + cmd.getLabel(), (cmd.usage == null ? "" : cmd.usage + " "),
                                    (cmd.description != null ? cmd.description : "")))
                            .onHover(Methods.placeholders("&eSuggest: &f{0}", "/" + command.label + " " + cmd.getLabel()))
                            .onClickSuggestCmd("/" + command.label + " " + cmd.getLabel())));

            subCommands.add(EasyComponent.builder(Methods.placeholders("&6{0} &e{1}&7- &f{2}", "/" + command.label, "",
                    (command.getDescription() != null ? command.getDescription()  : "")))
                    .onHover(Methods.placeholders("&eSuggest: &f{0}", "/" + command.label))
                    .onClickSuggestCmd("/" + command.label));

            EasyComponent.builder(Methods.placeholders("&8&l&m------------------&r &e&lHelp &7(&e{0}&7/&e{1}&7) &8&l&m------------------", page, size())).send(player);
            subCommands.forEach(easyComponent -> easyComponent.send(player));

        }

    }

    /**
     * Generate the page for the command.
     *
     * @param number
     * @return
     */
    public List<ImmortalSubCommand> getPage(int number) {
        if (!this.isValid(number)) return null;

        List<ImmortalSubCommand> items = subCommands;

        int index = number - 1;

        int from = index * COMMANDS_PER_PAGE;
        int to = from + COMMANDS_PER_PAGE;

        if (to > items.size()) {
            to = items.size();
        }

        return items.subList(from, to);
    }

    public int size() {
        return (int) Math.ceil((double) subCommands.size() / COMMANDS_PER_PAGE);
    }

    public boolean isValid(int number) {
        if (isEmpty()) return false;
        if (number < 1) return false;
        return number <= this.size();
    }

    public boolean isPageValid() {
        return page <= getMaxPage();
    }

    public int getMaxPage() {
        return size() + (subCommands.size() % size() > 0 ? 1 : 0);
    }

    public boolean isEmpty() {
        return subCommands.isEmpty();
    }

}

