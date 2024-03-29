package net.immortalapi.json;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.immortalapi.ImmortalAPI;
import net.immortalapi.json.typeadapters.ItemTypeAdapter;
import net.immortalapi.json.typeadapters.LocationTypeAdapter;
import net.immortalapi.json.typeadapters.UUIDTypeAdapter;
import net.immortalapi.json.typeadapters.WorldTypeAdapter;
import net.immortalapi.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class PersistentFile {

    private static List<PersistentFile> files = new ArrayList<>();

    private HashMap<Class, Object> typeAdapters = Maps.newHashMap();
    protected PersistentFile(HashMap<Class, Object> adapters) {
        files.add(this);
        typeAdapters.putAll(adapters);
    }

    protected PersistentFile() {
        files.add(this);
    }

    // ----------------------------------------
    // GSON
    // ----------------------------------------

    protected final Gson gson = buildGson().create();


    // ----------------------------------------
    // File
    // ----------------------------------------

    private GsonBuilder buildGson() {
        GsonBuilder builder = new GsonBuilder()
                .serializeNulls()
                .enableComplexMapKeySerialization()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
                .registerTypeAdapter(ItemBuilder.class, new ItemTypeAdapter())
                .registerTypeAdapter(World.class, new WorldTypeAdapter())
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .setPrettyPrinting()
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation();
        for(Class cl : typeAdapters.keySet()) {
            builder.registerTypeAdapter(cl,typeAdapters.get(cl));
        }
        return builder;
    }

    protected File getFile(boolean data, String name) {

        if (data) {
            File dataFolder = new File(ImmortalAPI.getInstance().getPlugin().getDataFolder() + "/data");
            dataFolder.mkdirs();
            return new File(dataFolder , name + ".json");
        }

        return new File(name + ".json");
    }

    // ----------------------------------------
    // Saving
    // ----------------------------------------

    public void save(boolean data, Object toSave, String name) {
        save(data, toSave, getFile(data, name));
    }

    public void save(boolean data, Object toSave, File file) {
        DiskUtil.write(file, gson.toJson(toSave));
    }

    // ----------------------------------------
    // Abstract methods.
    // ----------------------------------------

    public abstract void load();

    public abstract void save();

    // ----------------------------------------
    // Mass saving.
    // ----------------------------------------

    public static void saveAll() {
        files.forEach(PersistentFile::save);
    }

}