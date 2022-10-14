package net.immortal.menubuilder;

import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface ClickExecutor {

    void execute(InventoryClickEvent event);

}
