package me.lukeben.menubuilder.pagedmenu;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.lukeben.menubuilder.ClickExecutor;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class PagedItem {

    private ItemStack item;
    private ClickExecutor executor;

}
