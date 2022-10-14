package net.immortal.menubuilder;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.immortal.ImmortalAPI;
import net.immortal.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Getter
@Setter
public abstract class Menu {

    protected final static ConcurrentHashMap<UUID, Menu> menus = new ConcurrentHashMap<>();

    public Map<Integer, ClickExecutor> executors = Maps.newHashMap();
    public Map<Integer, ItemStack> items = Maps.newHashMap();

    private Inventory inventory;

    private final UUID playerUUID;
    private String title;
    private final int rows;

    private OpenExecutor openExecutor;
    private CloseExecutor closeExecutor;

    private BukkitTask refreshTask;

    private boolean destroyable = true;

    /**
     * @param player Player's menu.
     * @param title  Title of the menu.
     * @param rows   Amount of rows in this menu.
     */
    public Menu(final Player player, final String title, final int rows) {
        this.playerUUID = player.getUniqueId();
        this.title = title;
        this.rows = rows;
        this.inventory = Bukkit.createInventory(null, rows * 9, Methods.color(title));
    }

    protected void fillEmpty(final ItemStack item) {
        IntStream.range(0, getInventory().getSize())
                .filter(slot -> getInventory().getItem(slot) == null)
                .forEach(slot -> setItem(slot, item));
    }

    protected void setItem(final int slot, final Material material, final ClickExecutor executor) {
        setItem(slot, material);
        registerExecutors(slot, executor);
    }

    protected void setItem(final int slot, final Material material) {
        setItem(slot, new ItemStack(material));
    }

    protected void setItem(final int slot, final ItemStack item, final ClickExecutor executor) {
        setItem(slot, item);
        registerExecutors(slot, executor);
    }

    protected void setItem(final int slot, final ItemStack item) {
        items.put(slot, item);
        CompletableFuture.runAsync(() -> inventory.setItem(slot, item));
    }

    protected void setFiller(ItemStack item, List<Integer> slots) {
        for(int slot : slots) {
            setItem(slot, item);
        }
    }

    // ------------------------------------------------------------------
    // Managing menu
    // ------------------------------------------------------------------

    protected void registerExecutors(final int slot, final ClickExecutor executor) {
        executors.put(slot, executor);
    }

    protected void register() {
        menus.put(playerUUID, this);
    }

    protected void destroy() {
        if (refreshTask != null)
            refreshTask.cancel();
        menus.remove(playerUUID);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    // ------------------------------------------------------------------
    // Displaying
    // ------------------------------------------------------------------

    public void displayMenu() {
        getPlayer().openInventory(getInventory());
        register();
    }

    public void refresh() {
        inventory.clear();
        for (final Map.Entry<Integer, ItemStack> entry : this.items.entrySet())
            getInventory().setItem(entry.getKey(), entry.getValue());
        register();
        update();
    }

    public void update() {
        this.getInventory().getViewers().forEach(humanEntity -> ((Player) humanEntity).updateInventory());
    }

    public void refreshTask() {
        this.refreshTask = Bukkit.getScheduler().runTaskTimerAsynchronously(ImmortalAPI.getInstance().getPlugin(), this::refresh, 0, 20);
    }

    public void updateTitle(String title) {
        setDestroyable(false);
        this.title = title;
        Inventory menu = Bukkit.createInventory(null, rows * 9, Methods.color(title));
        inventory = menu;
        displayMenu();
        refresh();
        setDestroyable(true);
    }

    // ------------------------------------------------------------------
    // Static method
    // ------------------------------------------------------------------

    public static Menu getMenu(final Player player) {
        return getMenu(player.getUniqueId());
    }

    public static Menu getMenu(final UUID uuid) {
        return menus.get(uuid);
    }

}
