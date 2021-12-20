package me.lukeben.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.lukeben.ImmortalAPI;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;

public class Persist {

    @Getter
    private final Gson gson = buildGson().create();

    @Getter
    private final static Persist instance = new Persist();

    // ------------------------------------------------------------ //
    // GET NAME - What should we call this type of object?
    // ------------------------------------------------------------ //


    public static String getName(final Object o) {
        return getName(o.getClass());
    }

    public static String getName(final Class<?> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }

    private GsonBuilder buildGson() {
        return new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .serializeNulls()
                .enableComplexMapKeySerialization();
    }

    // ------------------------------------------------------------ //
    // GET FILE - In which file would we like to store this object?
    // ------------------------------------------------------------ //

    public static File getFile(final String name) {
        return new File(ImmortalAPI.getInstance().getPlugin().getDataFolder(), name + ".json");
    }

    public File getFile(final Class<?> clazz) {
        return getFile(getName(clazz));
    }

    public static File getFile(final Object obj) {
        return getFile(getName(obj));
    }

    public File getFile(final boolean data, final String name) {
        File dataFolder = ImmortalAPI.getInstance().getPlugin().getDataFolder();

        if (data) {
            dataFolder = new File(dataFolder, "/data");
            dataFolder.mkdirs();
        }
        return new File(dataFolder, name + ".json");
    }

    // SAVE

    public void save(final Object instance) {
        save(instance, getFile(instance));
    }

    public void save(final Object instance, final File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        DiskUtil.write(file, gson.toJson(instance));
    }

    // LOAD BY CLASS

    public <T> T load(final Class<T> clazz) {
        return load(clazz, getFile(clazz));
    }

    public <T> T load(final Class<T> clazz, final File file) {
        final String content = DiskUtil.read(file);
        if (content == null) {
            return null;
        }

        try {
            return gson.fromJson(content, clazz);
        } catch (final Exception ex) {
            ImmortalAPI.getInstance().getPlugin().getLogger().severe("Failed to parse " + file.toString() + ": " + ex.getMessage());
            Bukkit.getPluginManager().disablePlugin(ImmortalAPI.getInstance().getPlugin());
        }

        return null;
    }

    public <T> T loadOrSaveDefault(final T def, final Class<T> clazz, final File file) {
        if (!file.exists()) {
            this.save(def, file);
            return def;
        }

        final T loaded = this.load(clazz, file);

        if (loaded == null) {
            // backup bad file, so user can attempt to recover their changes from it
            final File backup = new File(file.getPath() + "_bad");
            if (backup.exists()) {
                backup.delete();
            }
            file.renameTo(backup);

            return def;
        }

        return loaded;
    }

}
