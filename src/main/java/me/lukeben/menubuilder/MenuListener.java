package me.lukeben.menubuilder;

import me.lukeben.ImmortalAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Menu menu = Menu.getMenu(player);

        System.out.println("DEBUGGING");

        if (event.getClickedInventory() == null || event.getCurrentItem() == null) return;
        if (menu == null) return;

        System.out.println("DEBUGGING 2");

        event.setCancelled(true);

        System.out.println("DEBUGGING");

        final int rawSlot = event.getRawSlot();

        System.out.println("DEBUGGING");

        if (menu.executors.get(rawSlot) != null)
            System.out.println("DEBUGGING 3");
            menu.executors.get(rawSlot).execute(event);
    }


    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();

        final Menu menu = Menu.getMenu(player);

        if (menu == null) return;

        if (menu.getCloseExecutor() != null) {
            menu.getCloseExecutor().execute(event);
            return;
        }

        if(menu.isDestroyable()) {
            menu.destroy();
        }

    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent event) {
        final Player player = (Player) event.getPlayer();
        Bukkit.getScheduler().runTaskLater(ImmortalAPI.getInstance().getPlugin(), () -> {
            final Menu menu = Menu.getMenu(player);

            if (menu == null) return;

            if (menu.getOpenExecutor() != null)
                menu.getOpenExecutor().execute();
        }, 1);
    }


}

