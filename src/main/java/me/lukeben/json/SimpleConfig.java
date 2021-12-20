package me.lukeben.json;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
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
import java.util.UUID;

@Getter
public class SimpleConfig {

    private transient Gson gson = getGson().create();

    protected transient File file;

    public SimpleConfig(final File file) {
        this.file = file;
    }

    public SimpleConfig(final String configName) {
        this(getFile(configName));
    }

    private GsonBuilder getGson() {
        return new GsonBuilder()
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
    }

    protected static boolean contentRequestsDefaults(final String content) {
        if (content == null) return false;
        if (content.length() == 0) return false;
        final char c = content.charAt(0);
        return c == 'd' || c == 'D';
    }

    public void load() {
        if (this.getFile().isFile()) {
            String content = DiskUtil.read(this.getFile());
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
        String content = DiskUtil.read(this.getFile());
        if (contentRequestsDefaults(content)) return;
        content = this.gson.toJson(this);
        DiskUtil.write(this.getFile(), content);
    }

    public String getCopy() {
        return this.gson.toJson(this);
    }

    private static File getFile(String fileName) {
        return new File(ImmortalAPI.getInstance().getPlugin().getDataFolder(), fileName + ".json");
    }

}

