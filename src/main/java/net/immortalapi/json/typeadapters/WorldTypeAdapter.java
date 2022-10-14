package net.immortalapi.json.typeadapters;

import com.google.gson.*;
import net.immortalapi.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.logging.Level;

public class WorldTypeAdapter implements JsonSerializer<World>, JsonDeserializer<World> {

    @Override
    public World deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        try {
            return Bukkit.getWorld(object.get("WORLD").getAsString());
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, Methods.color("&c[!] There was an issue while deserializing a world, please contact the developer."));
            return null;
        }
    }

    @Override
    public JsonElement serialize(World world, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        try {
            object.add("WORLD", new JsonPrimitive(world.getName()));
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, Methods.color("&c[!] There was an issue while serializing a world, please contact the developer."));
            return object;
        }
        return object;
    }

}
