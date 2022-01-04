package me.lukeben.settings;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import me.lukeben.json.SimpleConfig;
import me.lukeben.json.typeadapters.ItemTypeAdapter;
import me.lukeben.json.typeadapters.LocationTypeAdapter;
import me.lukeben.json.typeadapters.UUIDTypeAdapter;
import me.lukeben.json.typeadapters.WorldTypeAdapter;
import me.lukeben.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

public class SettingsSerializer {

    @Getter
    private static transient final SettingsSerializer instance = new SettingsSerializer();

    @Getter
    public final Gson gson = getGson().create();

    public HashMap<String, Object> serialize(Class<? extends SimpleConfig> config) {
        Type mapType = new TypeToken<HashMap<String, Object>>() {}.getType();
        return gson.fromJson(gson.toJson(config), mapType);
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

}
