package me.lukeben.settings;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import me.lukeben.ImmortalAPI;
import me.lukeben.coversationbuilder.ConvPrompt;
import me.lukeben.coversationbuilder.ConversationAPI;
import me.lukeben.json.Accessor;
import me.lukeben.json.DiskUtil;
import me.lukeben.json.SimpleConfig;
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

    @Getter
    private final SimpleConfig config;

    @Getter
    private int page;

    public SettingsMenu(Player player, SimpleConfig config, SettingsMenu parent, Map<String, Object> settings, SettingsMenuType type, int page) {
        super(player, "&e&lSettings", 4);
        this.settings = getPagedItems(settings);
        this.type = type;
        this.parent = parent;
        this.config = config;
        this.page = page;
        buildMenu();
        displayMenu();
    }

    public HashMap<String, Object> getPagedItems(Map<String, Object> map) {
        HashMap<String, Object> returned = Maps.newHashMap();
        int amountPerPage = itemSlots.size();
        int maxNumber = amountPerPage * page;

        for (int current = maxNumber - amountPerPage; current< maxNumber; current++) {
            //checking if there is enough tags to fully fill that page.
            if (settings.size() <= current) break;

            List<String> keySet = Lists.newArrayList(map.keySet());
            String identifier = keySet.get(current);
            returned.put(identifier, map.get(identifier));
        }
        return returned;
    }

    public void buildMenu() {
        setSaveIcon();
        setPageButtons();

        switch (type) {
            case NORMAL:
                for (String key : settings.keySet()) {
                    Object value = settings.get(key);
                    //is a primitive = a variable
                    if (value.getClass().isPrimitive() || value instanceof String) {
                        setItem(getFirstSlot(), ItemBuilder.builder().item(Material.PAPER).displayName("&6" + key).lore("&eCurrent: &7" + value).toItemStack(), e -> {
                            e.setCancelled(true);
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
                            }).build();
                            ConversationAPI.build(getPlayer(), prompt, 10, "QUIT", qs -> {
                                new SettingsMenu(getPlayer(), config, parent, settings, SettingsMenuType.NORMAL, 0);
                            });
                        });
                        //its a collection, list, or set
                    } else if (Collection.class.isAssignableFrom(value.getClass())) {
                        Collection<Object> values = (Collection<Object>) value;
                        HashMap<String, Object> newValues = Maps.newHashMap();
                        newValues.put(key, values);
                        setItem(getFirstSlot(), ItemBuilder.builder().item(Material.BOOK).displayName("&6" + key + " &7(List)").lore("&eSize: &7" + values.size()).toItemStack(), e -> {
                            SettingsMenu settingsMenu = new SettingsMenu(getPlayer(), config,this, newValues, SettingsMenuType.ARRAY,0);
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
                            SettingsMenu settingsMenu = new SettingsMenu(getPlayer(), config,this, newValues, SettingsMenuType.MAP, 0);
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
                            e.setCancelled(true);
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
                            new SettingsMenu(getPlayer(), config, parent, settings, SettingsMenuType.ARRAY, 0);
                        });
                    } else {
                        Map<Object, Object> values = (Map<Object, Object>) o;
                        Map<String, Object> newValues = Maps.newHashMap();
                        newValues.put(o.toString(), values);
                        setItem(getFirstSlot(), ItemBuilder.builder().item(Material.BOOK).displayName("&6" + o.toString()).lore("&eSize: &7" + values.size()).toItemStack(), e -> {
                            SettingsMenu settingsMenu = new SettingsMenu(getPlayer(), config, this, newValues, SettingsMenuType.MAP, 0);
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
                                    new SettingsMenu(getPlayer(), config, parent, settings, SettingsMenuType.MAP, 0);
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
                                new SettingsMenu(getPlayer(), config, parent, settings, SettingsMenuType.MAP, 0);
                            }).build();
                            ConversationAPI.build(getPlayer(), prompt, 10, "QUIT");
                        });
                    } else if (Collection.class.isAssignableFrom(value.getClass())) {
                        Collection<Object> values = (Collection<Object>) value;
                        HashMap<String, Object> newValues = Maps.newHashMap();
                        newValues.put(key.toString(), values);
                        setItem(getFirstSlot(), ItemBuilder.builder().item(Material.BOOK).displayName("&6" + key + " &7(List)").lore("&eSize: &7" + values.size()).toItemStack(), e -> {
                            new SettingsMenu(getPlayer(), config,this, newValues, SettingsMenuType.ARRAY, 0);
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
                            new SettingsMenu(getPlayer(), config,this, newValues, SettingsMenuType.MAP, 0);
                        });

                    }
                }
                break;
        }
    }

    public void setSaveIcon() {
        setItem(31, ItemBuilder.builder().item(Material.COMPASS).displayName("&e&lClick to Save!").lore("&7Click to save file!").toItemStack(), e -> {
            Map<String, Object> hierarchy = getHierarchy(this);
            DiskUtil.write(config.getFile(), SettingsSerializer.getInstance().deserialize(hierarchy));
            config.load(new HashMap<>());
        });
    }

    public void setPageButtons() {
        setItem(35, ItemBuilder.builder()
                .item(Material.PAPER)
                .displayName("&a&lNext Page >>")
                .lore("&7Click to advance to the next page.").toItemStack(), event -> {

            int maxNumber = itemSlots.size() * page;
            //basically checking if there is enough tags to warrant another page...
            if(maxNumber < settings.size()) {
                page++;
                buildMenu();
                displayMenu();
            }
        });
        setItem(27, ItemBuilder.builder()
                .item(Material.PAPER)
                .displayName("&c&l<< Previous Page")
                .lore("&7Click to return to the previous page.")
                .toItemStack(), event -> {
            //So, theoretically they should always be able to go back, unless they are on the first page;
            if(page > 1) {
                page--;
                buildMenu();
                displayMenu();
            }
        });
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
