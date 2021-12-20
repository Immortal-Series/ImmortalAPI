package me.lukeben;

import lombok.Getter;
import me.lukeben.menubuilder.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ImmortalAPI extends JavaPlugin {

    @Getter
    private static transient final ImmortalAPI instance = new ImmortalAPI();

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();

        Bukkit.getPluginManager().registerEvents(new MenuListener(), this);
    }

}
