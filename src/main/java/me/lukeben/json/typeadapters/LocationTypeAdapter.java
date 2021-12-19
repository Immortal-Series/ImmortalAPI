package me.lukeben.json.typeadapters;

import com.google.gson.*;
import me.lukeben.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.logging.Level;

public class LocationTypeAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        try {
            World world = Bukkit.getWorld(object.get("WORLD").getAsString());
            double xCord = object.get("CORD_X").getAsDouble();
            double yCord = object.get("CORD_Y").getAsDouble();
            double zCord = object.get("CORD_Z").getAsDouble();
            float yaw = object.get("YAW").getAsFloat();
            float pitch = object.get("PITCH").getAsFloat();
            return new Location(world, xCord, yCord, zCord, yaw, pitch);
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, Methods.color("&c[!] There was an issue while deserializing a location, please contact the developer."));
            return null;
        }
    }

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        try {
            object.add("WORLD", new JsonPrimitive(location.getWorld().getName()));
            object.add("CORD_X", new JsonPrimitive(location.getX()));
            object.add("CORD_Y", new JsonPrimitive(location.getY()));
            object.add("CORD_Z", new JsonPrimitive(location.getZ()));
            object.add("YAW", new JsonPrimitive(location.getYaw()));
            object.add("PITCH", new JsonPrimitive(location.getPitch()));
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, Methods.color("&c[!] There was an issue while serializing a location, please contact the developer."));
            return object;
        }
        return object;
    }

}
