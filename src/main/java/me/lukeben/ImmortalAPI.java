package me.lukeben;

import lombok.Getter;
import me.lukeben.menubuilder.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ImmortalAPI {

    @Getter
    private static transient final ImmortalAPI instance = new ImmortalAPI();

    public void onEnable(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new MenuListener(), plugin);
    }

}
