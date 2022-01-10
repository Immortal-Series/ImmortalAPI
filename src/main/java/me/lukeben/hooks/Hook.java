package me.lukeben.hooks;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@AllArgsConstructor
@Data
public abstract class Hook {

    public String name;
    public boolean enabled;

    public void registerHook() {
        HookManager.getInstance().getHooks().add(this);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        HookManager.getInstance().getHooks().add(this);
    }

    public void onEnable() {}

    public boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled(name);
    }

    public Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin(name);
    }

}
