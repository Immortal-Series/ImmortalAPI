package me.lukeben.menubuilder.configmenu;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.lukeben.menubuilder.pagedmenu.PagedItem;
import me.lukeben.menubuilder.pagedmenu.PagedMenu;
import me.lukeben.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ConfigArrayMenu extends PagedMenu {

    private List<PagedItem> items;
    private JsonArray array;
    private String fileName;
    private int backSlot;
    private int nextSlot;
    private int page;

    private boolean inDeleteMode = false;

    public ConfigArrayMenu(Player player, String fileName, int page, int backSlot, int nextSlot, List<PagedItem> items, List<Integer> itemSlots, JsonArray array) {
        super(player, "&e&lSettings -> &7" + fileName + ".json", 4, page, backSlot, nextSlot, items, itemSlots);
        this.items = items;
        this.array = array;
        this.fileName = fileName;
        this.backSlot = backSlot;
        this.nextSlot = nextSlot;
    }

    public void setAddButton() {
        List<Integer> slots = Lists.newArrayList();
        for(int i = 0; i < 27; i++) slots.add(i);
        setItem(21, ItemBuilder.builder().item(Material.EMERALD).displayName("&a&lNEW &8[&&a+&8]").lore("&7Click me to add a new object to this array!").toItemStack(), e -> {
            items.add(items.get(0));
            array.add(array.get(0));
            new ConfigArrayMenu(getPlayer(), fileName, page, backSlot, nextSlot, items, slots, array);
        });
    }

    public void setRemoveButton() {
        List<Integer> slots = Lists.newArrayList();
        for(int i = 0; i < 27; i++) slots.add(i);
        setItem(23, ItemBuilder.builder().item(Material.REDSTONE).displayName("&c&lREMOVE &8[&&c-&8]").lore("&7Click me to remove this new object from this array!").toItemStack(), e -> {
            inDeleteMode = true;
            //items.remove(items.get(0));
            //array.add(array.get(0));
            //new ConfigArrayMenu(getPlayer(), fileName, page, backSlot, nextSlot, items, slots, array);
        });
    }


}
