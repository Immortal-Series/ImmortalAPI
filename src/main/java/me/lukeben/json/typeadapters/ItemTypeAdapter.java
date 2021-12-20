package me.lukeben.json.typeadapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.lukeben.utils.ItemBuilder;
import me.lukeben.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;

public class ItemTypeAdapter implements JsonSerializer<ItemBuilder>, JsonDeserializer<ItemBuilder> {


    @Override
    public ItemBuilder deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        try {
            Material material = Material.valueOf(object.get("TYPE").getAsString());

            int amount = object.has("AMOUNT") ? object.get("AMOUNT").getAsInt() : 1;
            int data = object.has("DATA") ? object.get("DATA").getAsInt() : 0;
            String skullIdentifier = object.has("SKULL_ID") ? object.get("SKULL_ID").getAsString() : null;
            String displayName = object.has("DISPLAY_NAME") ? object.get("DISPLAY_NAME").getAsString() : null;
            boolean glowing = object.has("GLOWING") ? object.get("GLOWING").getAsBoolean() : false;
            Type listType = new TypeToken<List<String>>() {}.getType();
            List<String> lore = object.has("LORE") ? new Gson().fromJson(object.get("LORE"), listType) : null;

            ItemBuilder.Builder builder = ItemBuilder.builder();

            builder.item(material, amount, data);

            if(skullIdentifier != null) {
                builder.skull(Bukkit.getPlayer(skullIdentifier));
            }

            if(displayName != null) {
                builder.displayName(displayName);
            }

            if(lore != null) {
                builder.lore(lore);
            }

            if(glowing) {
                builder.setGlowing();
            }

            return builder.build();
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, Methods.color("&c[!] There was an issue while serializing a location, please contact the developer."));
            return null;
        }
    }

    @Override
    public JsonElement serialize(ItemBuilder itemBuilder, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject object = new JsonObject();
        try {
            object.add("TYPE", new JsonPrimitive(itemBuilder.getCurrent().getType().name()));
            if(itemBuilder.getCurrent().getAmount() > 1) object.add("AMOUNT", new JsonPrimitive(itemBuilder.getCurrent().getAmount()));
            if(itemBuilder.getCurrent().getDurability() > 0) object.add("DATA", new JsonPrimitive(itemBuilder.getCurrent().getDurability()));
            if(itemBuilder.getSkullIdentifier() != null) object.add("SKULL_ID", new JsonPrimitive(itemBuilder.getSkullIdentifier()));
            if(itemBuilder.getDisplayName() != null) object.add("DISPLAY_NAME", new JsonPrimitive(itemBuilder.getDisplayName()));
            if(itemBuilder.getGlowing()) object.add("GLOWING", new JsonPrimitive(true));
            Type listType = new TypeToken<List<String>>() {}.getType();
            if(!itemBuilder.getLore().isEmpty()) object.add("LORE", new Gson().toJsonTree(itemBuilder.getLore(), listType));
            return object;
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, Methods.color("&c[!] There was an issue while serializing an itembuilder, please contact the developer."));
            return object;
        }

    }

}

