package me.lukeben;

import lombok.Getter;
import lombok.Setter;
import me.lukeben.hooks.HookManager;
import me.lukeben.menubuilder.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ImmortalAPI {


    @Getter @Setter
    private JavaPlugin plugin;

    //this is a test method.

    @Getter
    private static transient final ImmortalAPI instance = new ImmortalAPI();

    public void onEnable(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(new MenuListener(), plugin);
        HookManager.getInstance().getHooks().forEach(hook -> {
            if(hook.isAvailable()) hook.onEnable();
        });
    }

}
