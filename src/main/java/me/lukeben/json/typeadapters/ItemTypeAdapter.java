package me.lukeben.json.typeadapters;

import com.google.common.collect.Maps;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.lukeben.utils.ItemBuilder;
import me.lukeben.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ItemTypeAdapter implements JsonSerializer<ItemBuilder>, JsonDeserializer<ItemBuilder> {


    @Override
    public ItemBuilder deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        try {
            Material material = Material.valueOf(object.get("TYPE").getAsString());

            int amount = object.get("AMOUNT").getAsInt();
            int data = object.get("DATA").getAsInt();
            String skullIdentifier = object.get("SKULL_ID").equals("") ? null : object.get("SKULL_ID").getAsString();
            String displayName = object.get("DISPLAY_NAME").equals("") ? null : object.get("DISPLAY_NAME").getAsString();
            boolean glowing = object.get("GLOWING").getAsBoolean();
            Type enchantType = new TypeToken<Map<String, Integer>>() {}.getType();
            Type listType = new TypeToken<List<String>>() {}.getType();
            Type flagType = new TypeToken<List<ItemFlag>>() {}.getType();
            List<String> lore = new Gson().fromJson(object.get("LORE"), listType);
            Map<String, Integer> enchantments =  new Gson().fromJson(object.get("ENCHANTMENTS"), enchantType);
            List<ItemFlag> flags = new Gson().fromJson(object.get("FLAGS"), flagType);

            ItemBuilder.Builder builder = ItemBuilder.builder();

            builder.item(material, amount, data);

            if(!skullIdentifier.equals("")) {
                builder.skull(Bukkit.getPlayer(skullIdentifier));
            }

            if(!displayName.equals("")) {
                builder.displayName(displayName);
            }

            if(!lore.isEmpty()) {
                builder.lore(lore);
            }

            if(glowing) {
                builder.setGlowing();
            }

            flags.forEach(f -> builder.flag(f));
            enchantments.forEach((e, l) -> builder.addEnchantment(Enchantment.getByKey(NamespacedKey.fromString(e)), l));

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
            object.add("AMOUNT", new JsonPrimitive(itemBuilder.getCurrent().getAmount()));
            object.add("DATA", new JsonPrimitive(itemBuilder.getCurrent().getDurability()));
            object.add("SKULL_ID", itemBuilder.getSkullIdentifier() != null ? new JsonPrimitive(itemBuilder.getSkullIdentifier()) : new JsonPrimitive(""));
            object.add("DISPLAY_NAME", itemBuilder.getDisplayName() != null ? new JsonPrimitive(itemBuilder.getDisplayName()) : new JsonPrimitive(""));
            object.add("GLOWING", new JsonPrimitive(itemBuilder.getGlowing()));
            Type enchantType = new TypeToken<Map<String, Integer>>() {}.getType();
            Type listType = new TypeToken<List<String>>() {}.getType();
            Type flagType = new TypeToken<List<ItemFlag>>() {}.getType();
            Map<String, Integer> enchantments = Maps.newHashMap();
            itemBuilder.getCurrent().getEnchantments().forEach((e, l) -> enchantments.put(e.getKey().getNamespace(), l));
            object.add("ENCHANTMENTS", new Gson().toJsonTree(itemBuilder.getCurrent().getEnchantments(), enchantType));
            object.add("LORE", new Gson().toJsonTree(itemBuilder.getLore(), listType));
            object.add("FLAGS", new Gson().toJsonTree(itemBuilder.getFlags(), flagType));

            if(!itemBuilder.getLore().isEmpty()) object.add("LORE", new Gson().toJsonTree(itemBuilder.getLore(), listType));
            return object;
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, Methods.color("&c[!] There was an issue while serializing an itembuilder, please contact the developer."));
            return object;
        }

    }

}

