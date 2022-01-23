package me.lukeben;

import lombok.Getter;
import lombok.Setter;
import me.lukeben.hooks.HookManager;
import me.lukeben.menubuilder.MenuListener;
import me.lukeben.settings.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class ImmortalAPI {

    @Getter @Setter
    private JavaPlugin plugin;

    @Getter
    private static transient final ImmortalAPI instance = new ImmortalAPI();

    public void onEnable(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(new MenuListener(), plugin);
        HookManager.getInstance().getHooks().forEach(hook -> {
            if(hook.isAvailable() && hook.isAvailable()) hook.onEnable();
        });
        SettingsManager.getInstance().addEditableArray("LORE*", "This is an example lore line");
        SettingsManager.getInstance().addEditableArray("FLAGS*", "HIDE_ENCHANTS");
        SettingsManager.getInstance().addEditableArray("ENCHANTMENTS**", new HashMap<String, Integer>() {{put("DAMAGE_ALL", 1);}});
    }

}
