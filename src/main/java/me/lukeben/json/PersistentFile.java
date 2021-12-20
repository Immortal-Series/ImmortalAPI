package me.lukeben.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.lukeben.ImmortalAPI;
import me.lukeben.json.typeadapters.ItemTypeAdapter;
import me.lukeben.json.typeadapters.LocationTypeAdapter;
import me.lukeben.json.typeadapters.UUIDTypeAdapter;
import me.lukeben.json.typeadapters.WorldTypeAdapter;
import me.lukeben.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class PersistentFile {

    private static List<PersistentFile> files = new ArrayList<>();

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
        return new GsonBuilder()
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