package me.lukeben.menubuilder.pagedmenu;

import lombok.Setter;
import me.lukeben.menubuilder.Menu;
import me.lukeben.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PagedMenu extends Menu {

    private int page;
    private List<Integer> itemSlots;
    @Setter private ItemStack BACK = ItemBuilder.builder().item(Material.ARROW).displayName("&c<- Back").lore("&7Click me to go to the previous page").toItemStack();
    @Setter private ItemStack NEXT = ItemBuilder.builder().item(Material.ARROW).displayName("&aNext ->").lore("&7Click me to go to the next page").toItemStack();

    public PagedMenu(Player player, String title, int rows, int page, int backSlot, int nextSlot, List<PagedItem> list, List<Integer> itemSlots) {
        super(player, title, rows);
        this.page = page;
        this.itemSlots = itemSlots;
        setButtons(page, list, backSlot, nextSlot, BACK, NEXT);
        setItems(list, page, itemSlots);
    }

    private void setItems(List<PagedItem> items, int page, List<Integer> slots) {
        int amountPerPage = slots.size();
        int maxNumber = slots.size() * page;

        for (int current = maxNumber - amountPerPage; current < maxNumber; current++) {
            //checking if there is enough tags to fully fill that page.
            if (items.size() <= current) break;
            //finding a slot in the menu for that tag.
            for (int slot : slots) {
                if (!getItems().containsKey(slot)) {
                    PagedItem item = items.get(current);
                    setItem(slot, item.getItem(), item.getExecutor());
                }
                break;
            }
        }
    }

    private void setButtons(int page, List<PagedItem> list, int backSlot, int nextSlot, ItemStack back, ItemStack next) {
        setItem(nextSlot, next, event -> {
            int maxNumber = list.size() * page;
            //basically checking if there is enough tags to warrant another page...
            if(maxNumber < list.size()) {
                new PagedMenu(getPlayer(), getTitle(), getRows(), page + 1, backSlot, nextSlot, list, itemSlots);
            }
        });
        setItem(backSlot, back, event -> {
            //So, theoretically they should always be able to go back, unless they are on the first page;
            if(page > 1) {
                new PagedMenu(getPlayer(), getTitle(), getRows(), page - 1, backSlot, nextSlot, list, itemSlots);
            }
        });
    }

}
