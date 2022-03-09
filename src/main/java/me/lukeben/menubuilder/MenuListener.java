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

        if (event.getClickedInventory() == null || event.getCurrentItem() == null) return;
        if (menu == null) return;

        event.setCancelled(true);

        final int rawSlot = event.getRawSlot();

        if (menu.executors.get(rawSlot) != null)
            menu.executors.get(rawSlot).execute(event);
    }


    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        System.out.println("fired!1");
        final Player player = (Player) event.getPlayer();

        final Menu menu = Menu.getMenu(player);

        if (menu == null) return;
        System.out.println("fired!2");

        if (menu.getCloseExecutor() != null) {
            System.out.println("fired!3");
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

