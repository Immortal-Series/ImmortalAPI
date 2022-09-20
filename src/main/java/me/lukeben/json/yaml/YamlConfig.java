package me.lukeben.json.yaml;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import me.lukeben.ImmortalAPI;
import me.lukeben.json.Accessor;
import me.lukeben.json.DiskUtil;
import me.lukeben.json.typeadapters.ItemTypeAdapter;
import me.lukeben.json.typeadapters.LocationTypeAdapter;
import me.lukeben.json.typeadapters.UUIDTypeAdapter;
import me.lukeben.json.typeadapters.WorldTypeAdapter;
import me.lukeben.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

@Getter
public abstract class YamlConfig {

    private transient Gson gson = getGson().create();

    protected transient File file;
    protected transient FileConfiguration fileConfiguration;

    public YamlConfig(final File file) {
        this.file = file;
    }

    public YamlConfig(final String configName) {
        this.file = (getFile(configName));
    }

    private GsonBuilder getGson() {
        GsonBuilder builder = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
                .registerTypeAdapter(ItemBuilder.class, new ItemTypeAdapter())
                .registerTypeAdapter(World.class, new WorldTypeAdapter())
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .serializeNulls()
                .enableComplexMapKeySerialization()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
        return builder;
    }

    private static File getFile(String fileName) {
        return new File(ImmortalAPI.getInstance().getPlugin().getDataFolder(), fileName + ".yml");
    }

    public void load() {

        // detect if the current file exists, if not then create it and register the defaults.
        if(!(file.exists())) {
            try {
                // file is created
                file.createNewFile();
                // defaults must be loaded to the file


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
        loadDefaults();

    }

    protected static boolean contentRequestsDefaults(final String content) {
        if (content == null) return false;
        if (content.length() == 0) return false;
        final char c = content.charAt(0);
        return c == 'd' || c == 'D';
    }

    public void loadDefaults() {
        if (this.getFile().isFile()) {
            String content = convertToJson(DiskUtil.read(this.getFile()));
            content = content.trim();
            Object toShallowLoad = null;

            if (contentRequestsDefaults(content)) {
                try {
                    toShallowLoad = this.getClass().newInstance();
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    return;
                }
            } else {
                toShallowLoad = this.gson.fromJson(content, this.getClass());
            }

            Accessor.get(this.getClass()).copy(toShallowLoad, this);

        }
        save();
    }

    public void save() {
        String content = convertToJson(DiskUtil.read(this.getFile()));
        if (contentRequestsDefaults(content)) return;

        content = this.gson.toJson(this);
        Type serializedDataType = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> serializedData = gson.fromJson(content, serializedDataType);
        setSavedData(serializedData);

        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSavedData(Map<String, Object> data) {

        for(String key : data.keySet()) {
            Object value = data.get(key);

            fileConfiguration.set(key, value);

        }


    }

    private String convertToJson(String yamlString) {
        Yaml yaml= new Yaml();
        Map<String,Object> map= (Map<String, Object>) yaml.load(yamlString);

        //convert to gson
        Type serializedDataType = new TypeToken<Map<String, Object>>() {}.getType();
        String gsonString = gson.toJson(map, serializedDataType);
        return gsonString;
    }

}
