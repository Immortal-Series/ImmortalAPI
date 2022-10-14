package net.immortalapi.hooks;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@AllArgsConstructor
public abstract class Hook {

    public String name = "";
    public boolean enabled = false;

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

    public Plugin getSource() {
        return Bukkit.getPluginManager().getPlugin(name);
    }

}
