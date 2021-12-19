package me.lukeben.json.typeadapters;

import com.google.gson.*;
import me.lukeben.utils.Methods;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;
import java.util.UUID;
import java.util.logging.Level;

public class UUIDTypeAdapter implements JsonSerializer<UUID>, JsonDeserializer<UUID> {

    @Override
    public UUID deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        try {
            UUID uuid = UUID.fromString(object.get("UUID").getAsString());
            return uuid;
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, Methods.color("&c[!] There was an issue while deserializing a uuid, please contact the developer."));
            return null;
        }
    }

    @Override
    public JsonElement serialize(UUID uuid, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        try {
            object.add("UUID", new JsonPrimitive(uuid.toString()));
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, Methods.color("&c[!] There was an issue while serializing a uuid, please contact the developer."));
            return object;
        }
        return object;
    }

}
