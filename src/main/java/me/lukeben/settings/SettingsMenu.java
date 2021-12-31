package me.lukeben.settings;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import me.lukeben.ImmortalAPI;
import me.lukeben.coversationbuilder.ConvPrompt;
import me.lukeben.coversationbuilder.ConversationAPI;
import me.lukeben.menubuilder.Menu;
import me.lukeben.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SettingsMenu extends Menu {

    private final List<Integer> itemSlots = Lists.newArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);

    @Getter
    private final Map<String, Object> settings;


    @Getter
    private final SettingsMenuType type;

    @Getter
    private final SettingsMenu parent;

    public SettingsMenu(Player player, SettingsMenu parent, Map<String, Object> settings, SettingsMenuType type) {
        super(player, "&e&lSettings", 4);
        this.settings = settings;
        this.type = type;
        this.parent = parent;
        buildMenu();
        displayMenu();
    }

    public void buildMenu() {
        System.out.println(getHierarchy(this));
        switch (type) {
            case NORMAL:
                for (String key : settings.keySet()) {
                    Object value = settings.get(key);
                    //is a primitive = a variable
                    if (value.getClass().isPrimitive() || value instanceof String) {
                        setItem(getFirstSlot(), ItemBuilder.builder().item(Material.PAPER).displayName("&6" + key).lore("&eCurrent: &7" + value).toItemStack(), e -> {
                            getPlayer().closeInventory();
                            System.out.println(getPlayer().getOpenInventory().getTopInventory() == null);
                            ConvPrompt prompt = ConvPrompt.builder().promptText("&7Please enter a new value for &e" + key + " &7or type 'QUIT' to cancel!").answer(answer -> {
                                if (value instanceof Integer) {
                                    settings.put(key, Integer.parseInt(answer.getReceivedInput()));
                                } else if (value instanceof Double) {
                                    settings.put(key, Double.parseDouble(answer.getReceivedInput()));
                                } else if (value instanceof Boolean) {
                                    settings.put(key, Boolean.parseBoolean(answer.getReceivedInput()));
                                } else {
                                    settings.put(key, answer.getReceivedInput());
                                }
                                new SettingsMenu(getPlayer(), parent, settings, SettingsMenuType.MAP);
                            }).build();
                            ConversationAPI.build(getPlayer(), prompt, 10, "QUIT");
                        });
                        //its a collection, list, or set
                    } else if (Collection.class.isAssignableFrom(value.getClass())) {
                        Collection<Object> values = (Collection<Object>) value;
                        HashMap<String, Object> newValues = Maps.newHashMap();
                        newValues.put(key, values);
                        setItem(getFirstSlot(), ItemBuilder.builder().item(Material.BOOK).displayName("&6" + key + " &7(List)").lore("&eSize: &7" + values.size()).toItemStack(), e -> {
                            SettingsMenu settingsMenu = new SettingsMenu(getPlayer(), this, newValues, SettingsMenuType.ARRAY);
                        });
                    } else {
                        Material type = Material.BOOK;
                        Map<Object, Object> values = (Map<Object, Object>) value;
                        if (key.toUpperCase().endsWith("_LOCATION")) {
                            type = Material.ENDER_PEARL;
                        } else if (key.toUpperCase().endsWith("_MENU")) {
                            type = Material.CHEST;
                        } else if (key.toUpperCase().endsWith("_ITEM")) {
                            ;
                            type = Material.valueOf(values.get("TYPE").toString());
                        }
                        Map<String, Object> newValues = Maps.newHashMap();
                        newValues.put(key, value);
                        setItem(getFirstSlot(), ItemBuilder.builder().item(type).displayName("&6" + key).lore("&eSize: &7" + values.size()).toItemStack(), e -> {
                            SettingsMenu settingsMenu = new SettingsMenu(getPlayer(), this, newValues, SettingsMenuType.MAP);
                        });
                    }
                }
                break;
            case ARRAY:
                String arrayId = (String) settings.keySet().stream().toArray()[0];
                List<Object> collection = (List<Object>) new ArrayList(settings.values()).get(0);
                if (arrayId.equalsIgnoreCase("LORE") || arrayId.equalsIgnoreCase("FLAGS")) {
                    setItem(30, ItemBuilder.builder().item(Material.EMERALD).displayName("&aNEW &8[&a+&8]").lore("&7Click to add a new object to this list").toItemStack(), e -> {
                        if (arrayId.equalsIgnoreCase("LORE")) {
                            collection.add("&7This is an example lore line! &c:D");
                            setItem(getFirstSlot(), ItemBuilder.builder().item(Material.PAPER).displayName("&7This is an example lore line! &c:D").toItemStack());
                            displayMenu();
                            System.out.println(getHierarchy(this));
                        } else if (arrayId.equalsIgnoreCase("FLAGS")) {
                            collection.add("HIDE_ENCHANTS");
                            setItem(getFirstSlot(), ItemBuilder.builder().item(Material.PAPER).displayName("&7HIDE_ENCHANTS").toItemStack());
                            displayMenu();
                        }
                    });
                }
                for (Object o : collection) {
                    if (o.getClass().isPrimitive() || o instanceof String) {
                        setItem(getFirstSlot(), ItemBuilder.builder().item(Material.PAPER).displayName("&7" + o.toString()).toItemStack(), e -> {
                            getPlayer().closeInventory();
                            ConvPrompt prompt = ConvPrompt.builder().promptText("&7Please enter a new value for &e" + arrayId + " &7or type 'QUIT' to cancel!").build();
                            ConversationAPI.build(getPlayer(), prompt, 10, "QUIT");
                            String promptResponse = prompt.getReceivedInput();
                            if (o instanceof Integer) {
                                collection.add(Integer.parseInt(promptResponse));
                            } else if (o instanceof Double) {
                                collection.add(Double.parseDouble(promptResponse));
                            } else if (o instanceof Boolean) {
                                collection.add(Boolean.parseBoolean(promptResponse));
                            } else {
                                collection.add(promptResponse);
                            }
                            settings.put(arrayId, collection);
                            new SettingsMenu(getPlayer(), parent, settings, SettingsMenuType.ARRAY);
                        });
                    } else {
                        Map<Object, Object> values = (Map<Object, Object>) o;
                        Map<String, Object> newValues = Maps.newHashMap();
                        newValues.put(o.toString(), values);
                        setItem(getFirstSlot(), ItemBuilder.builder().item(Material.BOOK).displayName("&6" + o.toString()).lore("&eSize: &7" + values.size()).toItemStack(), e -> {
                            SettingsMenu settingsMenu = new SettingsMenu(getPlayer(), this, newValues, SettingsMenuType.MAP);
                        });
                    }
                }
                break;
            case MAP:
                String id = (String) settings.keySet().stream().toArray()[0];
                List<Object> list = new ArrayList<>(settings.values());
                Map<Object, Object> map = (Map<Object, Object>) list.get(0);
                if (id.equalsIgnoreCase("ENCHANTMENTS")) {
                    setItem(31, ItemBuilder.builder().item(Material.EMERALD).displayName("&aNEW &8[&a+&8]").lore("&7Click to add a new object to this list").toItemStack(), e -> {
                        if (id.equalsIgnoreCase("ENCHANTMENTS")) {
                            map.put("sharpness", 1);
                            setItem(getFirstSlot(), ItemBuilder.builder().item(Material.PAPER).displayName("&6sharpness").lore("&eCurrent: &71").toItemStack(), ev -> {
                                getPlayer().closeInventory();
                                System.out.println(getPlayer().getOpenInventory().getTopInventory() == null);
                                ConvPrompt prompt = ConvPrompt.builder().promptText("&7Please enter a new value for &elevel &7or type 'QUIT' to cancel!").answer(answer -> {
                                    settings.put("sharpness", Integer.parseInt(answer.getReceivedInput()));
                                    new SettingsMenu(getPlayer(), parent, settings, SettingsMenuType.MAP);
                                }).build();
                                ConversationAPI.build(getPlayer(), prompt, 10, "QUIT");
                            });
                            displayMenu();
                        }
                    });
                }
                for (Object key : map.keySet()) {
                    Object value = map.get(key);
                    //is a primitive = a variable
                    if (value.getClass().isPrimitive() || value instanceof String) {
                        setItem(getFirstSlot(), ItemBuilder.builder().item(Material.PAPER).displayName("&6" + key).lore("&eCurrent: &7" + value).toItemStack(), e -> {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    getPlayer().closeInventory();
                                    System.out.println(getPlayer().getOpenInventory().getTopInventory() == null);
                                }
                            }.runTaskLater(ImmortalAPI.getInstance().getPlugin(), 1);
                            ConvPrompt prompt = ConvPrompt.builder().promptText("&7Please enter a new value for &e" + key + " &7or type 'QUIT' to cancel!").answer(answer -> {
                                if (value instanceof Integer) {
                                    map.put(key.toString(), Integer.parseInt(answer.getReceivedInput()));
                                } else if (value instanceof Double) {
                                    map.put(key.toString(), Double.parseDouble(answer.getReceivedInput()));
                                } else if (value instanceof Boolean) {
                                    map.put(key.toString(), Boolean.parseBoolean(answer.getReceivedInput()));
                                } else {
                                    map.put(key.toString(), answer.getReceivedInput());
                                }
                                settings.put(id, map);
                                new SettingsMenu(getPlayer(), parent, settings, SettingsMenuType.MAP);
                            }).build();
                            ConversationAPI.build(getPlayer(), prompt, 10, "QUIT");
                        });
                    } else if (Collection.class.isAssignableFrom(value.getClass())) {
                        Collection<Object> values = (Collection<Object>) value;
                        HashMap<String, Object> newValues = Maps.newHashMap();
                        newValues.put(key.toString(), values);
                        setItem(getFirstSlot(), ItemBuilder.builder().item(Material.BOOK).displayName("&6" + key + " &7(List)").lore("&eSize: &7" + values.size()).toItemStack(), e -> {
                            new SettingsMenu(getPlayer(), this, newValues, SettingsMenuType.ARRAY);
                        });
                    } else {
                        Material type = Material.BOOK;
                        Map<Object, Object> values = (Map<Object, Object>) value;
                        if (key.toString().toUpperCase().endsWith("_LOCATION")) {
                            type = Material.ENDER_PEARL;
                        } else if (key.toString().toUpperCase().endsWith("_MENU")) {
                            type = Material.CHEST;
                        } else if (key.toString().toUpperCase().endsWith("_ITEM")) {
                            ;
                            type = Material.valueOf(values.get("TYPE").toString());
                        }
                        Map<String, Object> newValues = Maps.newHashMap();
                        newValues.put(key.toString(), value);
                        setItem(getFirstSlot(), ItemBuilder.builder().item(type).displayName("&6" + key).lore("&eSize: &7" + values.size()).toItemStack(), e -> {
                            new SettingsMenu(getPlayer(), this, newValues, SettingsMenuType.MAP);
                        });

                    }
                }
                break;
        }
    }

    public Map<String, Object> getHierarchy(SettingsMenu menu) {
        if (menu == null) return null;
        Map<String, Object> hierarchy = menu.getSettings();
        if (menu.getParent() == null) return hierarchy;
        hierarchy = getHierarchy(menu.parent);
        hierarchy.putAll(menu.getSettings());
        return hierarchy;
    }

    public int getFirstSlot() {
        for (int slot : itemSlots) {
            if (getItems().containsKey(slot)) continue;
            return slot;
        }
        return -1;
    }

}
