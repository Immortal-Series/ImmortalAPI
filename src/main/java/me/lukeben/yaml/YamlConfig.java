package me.lukeben.yaml;

import me.lukeben.ImmortalAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;

public abstract class YamlConfig implements ConfigurationSerializable {

    protected transient File file;
    protected transient FileConfiguration fileConfiguration;

    public YamlConfig(final File file) {
        this.file = file;
        ConfigurationSerialization.registerClass(this.getClass());
    }

    public YamlConfig(final String configName) {
        this.file = (getFile(configName));
        ConfigurationSerialization.registerClass(this.getClass());
    }

    public void load() {
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        ImmortalAPI.getInstance().getPlugin().saveResource(file.getName() + ".yml", false);
    }

    private static File getFile(String fileName) {
        return new File(ImmortalAPI.getInstance().getPlugin().getDataFolder(), fileName + ".yml");
    }

}
