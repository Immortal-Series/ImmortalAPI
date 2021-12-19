package me.lukeben;

import lombok.Getter;
import me.lukeben.menubuilder.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ImmortalAPI extends JavaPlugin {

    @Getter
    private static ImmortalAPI instance;

    @Override
    public void onEnable() {
        instance = this;
        getDataFolder().mkdirs();
        Bukkit.getPluginManager().registerEvents(new MenuListener(), this);
    }

}
