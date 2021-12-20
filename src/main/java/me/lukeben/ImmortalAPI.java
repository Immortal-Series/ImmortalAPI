package me.lukeben;

import lombok.Getter;
import me.lukeben.menubuilder.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ImmortalAPI {

    @Getter
    private static ImmortalAPI instance;

    public ImmortalAPI() {
        instance = this;
    }

    public void register(JavaPlugin inst) {
        Bukkit.getPluginManager().registerEvents(new MenuListener(), inst);
    }


}
