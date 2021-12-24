package me.lukeben.menubuilder.configmenu;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.lukeben.menubuilder.pagedmenu.PagedItem;
import me.lukeben.menubuilder.pagedmenu.PagedMenu;
import org.bukkit.entity.Player;

import java.util.List;

public class ConfigRemoveMenu extends PagedMenu {

    private final String fileName;
    private final int page;
    private final int backSlot;
    private final int nextSlot;
    private final List<PagedItem> items;
    private final List<Integer> itemSlots;
    private final JsonArray array;


    public ConfigRemoveMenu(Player player, String filename, String category, int page, int backSlot, int nextSlot, List<PagedItem> items, List<Integer> itemSlots, JsonArray array) {
        super(player, "&cRemoving from: &7" + category, 4, page, backSlot, nextSlot, items, itemSlots);
        setItems(items,page, itemSlots);
        this.fileName = filename;
        this.page = page;
        this.backSlot = backSlot;
        this.nextSlot = nextSlot;
        this.items = items;
        this.itemSlots = itemSlots;
        this.array = array;
    }

    private void setItems(List<PagedItem> items, int page, List<Integer> slots) {
        int amountPerPage = slots.size();
        int maxNumber = slots.size() * page;

        int tick = 0;
        for (int current = maxNumber - amountPerPage; current < maxNumber; current++) {
            tick = current;
            //checking if there is enough tags to fully fill that page.
            if (items.size() <= current) break;
            //finding a slot in the menu for that tag.
            for (int slot : slots) {
                if (!getItems().containsKey(slot)) {
                    PagedItem item = items.get(current);
                    int finalTick = tick;
                    setItem(slot, item.getItem(), e -> {

                        //remove from array
                        //remove from items
                        //display items
                        array.remove(finalTick);
                        new ConfigArrayMenu(getPlayer(), fileName, page, backSlot, nextSlot, items, itemSlots, array);

                    });
                }
                break;
            }
        }
    }

}
