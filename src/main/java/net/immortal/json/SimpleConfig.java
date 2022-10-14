package net.immortal.json;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.immortal.ImmortalAPI;
import net.immortal.json.typeadapters.ItemTypeAdapter;
import net.immortal.json.typeadapters.LocationTypeAdapter;
import net.immortal.json.typeadapters.UUIDTypeAdapter;
import net.immortal.json.typeadapters.WorldTypeAdapter;
import net.immortal.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.HashMap;
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

    protected static boolean contentRequestsDefaults(final String content) {
        if (content == null) return false;
        if (content.length() == 0) return false;
        final char c = content.charAt(0);
        return c == 'd' || c == 'D';
    }

    public void load(HashMap<Class, Object> typeAdapters) {
        GsonBuilder builder = getGson();
        for(Class cl : typeAdapters.keySet()) {
            builder.registerTypeAdapter(cl, typeAdapters.get(cl));
        }
        gson = builder.create();
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

    /*

    SubCategory -> Return back to main new SubCategoryValue?



    Hierarchy hi = ...

    hi.displayMenu()
    hi.getParent()
    hi.getChildren()

    save() {

        hi2 - > parent -> h1 -> 

    }

    @Data
    public abstract class CustomType {



        Config
          - String
          - SubCategory(1)
              - item
              - float
          - SubCategory(2)
              - double
              - SubCategory(A)

    }



     */

}

