package me.lukeben.menubuilder.configmenu;

import com.google.common.collect.Lists;
import com.google.gson.*;
import me.lukeben.coversationbuilder.ConvPrompt;
import me.lukeben.coversationbuilder.ConversationAPI;
import me.lukeben.json.SimpleConfig;
import me.lukeben.json.typeadapters.ItemTypeAdapter;
import me.lukeben.json.typeadapters.LocationTypeAdapter;
import me.lukeben.json.typeadapters.UUIDTypeAdapter;
import me.lukeben.json.typeadapters.WorldTypeAdapter;
import me.lukeben.menubuilder.ClickExecutor;
import me.lukeben.menubuilder.Menu;
import me.lukeben.menubuilder.pagedmenu.PagedItem;
import me.lukeben.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConfigMenu extends Menu {

    private transient Gson gson = getGson().create();

    private List<Integer> slots;

    public ConfigMenu(Player player, String fileName, SimpleConfig config) {
        super(player, "&e&lSettings -> &7" + fileName + ".json", 4);
        this.slots = Lists.newArrayList();
        for (int i = 0; i < 27; i++) slots.add(i);
        start(config, fileName);
    }

    private void start(SimpleConfig config, String fileName) {
        JsonObject element = gson.fromJson(gson.toJson(config), JsonObject.class).getAsJsonObject();

        List<PagedItem> items = getPageItems(element, fileName);

        for (Map.Entry<String, JsonElement> elementEntry : element.entrySet()) {
            JsonElement entry = elementEntry.getValue();

            if (entry.isJsonArray()) {
                setItem(getFirstFreeSlot(), ItemBuilder.builder().item(Material.BOOK, 1).displayName("&3&l" + elementEntry.getKey()).toItemStack(),
                        event -> new ConfigArrayMenu(getPlayer(), fileName, 1, 18, 26, items, slots, entry.getAsJsonArray()));
            } else if (entry.isJsonPrimitive()) {
                setItem(getFirstFreeSlot(), ItemBuilder.builder().item(Material.PAPER, 1).displayName("&b" + elementEntry.getKey()).lore("&7Value: " + elementEntry.getValue().toString()).toItemStack());
            } else if (entry.isJsonObject()) {
                setItem(getFirstFreeSlot(), ItemBuilder.builder().item(Material.BOOK, 1).displayName("&3&l" + elementEntry.getKey()).toItemStack(), event -> {
                    buildMenu(getPlayer(), element, fileName);
                });
            }

        }
        displayMenu();
    }

    private void buildMenu(Player player, JsonObject element, String fileName) {

        List<PagedItem> items = getPageItems(element, fileName);

        for (Map.Entry<String, JsonElement> elementEntry : element.entrySet()) {
            JsonElement entry = elementEntry.getValue();

            if (entry.isJsonArray()) {
                setItem(getFirstFreeSlot(), ItemBuilder.builder().item(Material.BOOK, 1).displayName("&3&l" + elementEntry.getKey()).toItemStack(),
                        event -> new ConfigArrayMenu(player, fileName, 1, 18, 26, items, slots, entry.getAsJsonArray()));
            } else if (entry.isJsonPrimitive()) {
                setItem(getFirstFreeSlot(), ItemBuilder.builder().item(Material.PAPER, 1).displayName("&b" + elementEntry.getKey()).lore("&7Value: " + elementEntry.getValue().toString()).toItemStack());
            } else if (entry.isJsonObject()) {
                setItem(getFirstFreeSlot(), ItemBuilder.builder().item(Material.BOOK, 1).displayName("&3&l" + elementEntry.getKey()).toItemStack(), event -> {
                    buildMenu(player, element, fileName);
                });
            }

        }
        displayMenu();
    }

    private List<PagedItem> getPageItems(JsonElement jsonElement, String fileName) {
        List<PagedItem> items = Lists.newArrayList();

        if(jsonElement.isJsonPrimitive()) {
            return items;
        } else if(jsonElement.isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            for(int i = 0; i < array.size(); i++) {
                JsonElement el = array.get(i);
                if(el.isJsonPrimitive()) {
                    int finalI = i;
                    ClickExecutor clickExecutor = event -> {
                        ConvPrompt prompt = ConvPrompt.builder().promptText("&7Please type new value for 'variable' or type QUIT to quit!").build();
                        ConversationAPI.build(getPlayer(), prompt, 10, "QUIT", e-> {});
                        String response = prompt.getReceivedInput();
                        JsonPrimitive prim = el.getAsJsonPrimitive();
                        if(prim.isBoolean()) {
                            array.set(finalI, new JsonPrimitive(Boolean.valueOf(response)));
                        } else if(prim.isNumber()) {
                            array.set(finalI, new JsonPrimitive(Double.parseDouble(response)));
                        } else if(prim.isString()) {
                            array.set(finalI, new JsonPrimitive(response));
                        }
                    };
                    items.add(new PagedItem(ItemBuilder.builder().item(Material.PAPER, 1).displayName("&b" + elementEntry.getKey()).lore("&7Value: " + elementEntry.getValue().toString()).toItemStack(), clickExecutor));
                } else if(el.isJsonObject()) {

                }
            }
            
        }

        for (Map.Entry<String, JsonElement> elementEntry : element.entrySet()) {

            if (elementEntry.getValue().isJsonArray()) {
                List<PagedItem> childItems = getPageItems(elementEntry.getValue(), fileName);
                ClickExecutor clickExecutor = event -> new ConfigArrayMenu(getPlayer(), fileName, 1, 18, 26, childItems, slots, elementEntry.getValue().getAsJsonArray());
                items.add(new PagedItem(ItemBuilder.builder().item(Material.BOOK, 1).displayName("&3&l" + elementEntry.getKey()).toItemStack(), clickExecutor));
            } else if (elementEntry.getValue().isJsonPrimitive()) {
                ClickExecutor clickExecutor = event -> {
                    ConvPrompt prompt = ConvPrompt.builder().promptText("&7Please type new value for '" + elementEntry.getKey() + "' or type QUIT to quit!").build();
                    ConversationAPI.build(getPlayer(), prompt, 10, "QUIT", e-> {});
                    String response = prompt.getReceivedInput();
                    JsonPrimitive prim = elementEntry.getValue().getAsJsonPrimitive();
                    if(prim.isBoolean()) {
                        element.add(elementEntry.getKey(), new JsonPrimitive(Boolean.valueOf(response)));
                    } else if(prim.isNumber()) {
                        element.add(elementEntry.getKey(), new JsonPrimitive(Double.parseDouble(response)));
                    } else if(prim.isString()) {
                        element.add(elementEntry.getKey(), new JsonPrimitive(response));
                    }
                };
                items.add(new PagedItem(ItemBuilder.builder().item(Material.PAPER, 1).displayName("&b" + elementEntry.getKey()).lore("&7Value: " + elementEntry.getValue().toString()).toItemStack(), clickExecutor));
            } else if (elementEntry.getValue().isJsonObject()) {
                ClickExecutor clickExecutor = event -> {
                    buildMenu(getPlayer(), element, fileName);
                };
                items.add(new PagedItem(ItemBuilder.builder().item(Material.BOOK, 1).displayName("&3&l" + elementEntry.getKey()).toItemStack(), clickExecutor));
            }

        }
        return items;
    }

    public int getFirstFreeSlot() {
        for(int slot : slots) {
            if(getItems().containsKey(slot)) continue;
            return slot;
        }
        return -1;
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

    public void serializeClass(Class clazz) {
        try {
            Field[] fields = clazz.getFields();
            for(int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String name = field.getName();
                if(field.getType().isPrimitive()) {
                    System.out.println(name + " : " + field.get(clazz));
                } else if(field.get(clazz) == null){
                    System.out.println(name + " : NULL");
                } else {
                    serializeClass(field.getClass());
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
