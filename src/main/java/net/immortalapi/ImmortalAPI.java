package net.immortalapi;

import lombok.Getter;
import lombok.Setter;
import net.immortalapi.hooks.HookManager;
import net.immortalapi.menubuilder.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class ImmortalAPI {


    @Getter
    @Setter
    private JavaPlugin plugin;

    @Getter
    private static final ImmortalAPI instance = new ImmortalAPI();

    public void onEnable(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(new MenuListener(), plugin);
        HookManager.getInstance().getHooks().forEach(hook -> {
            if (hook.isAvailable()) hook.onEnable();
        });
    }

}
