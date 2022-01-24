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
import me.lukeben.utils.MenuFiller;
import me.lukeben.utils.Methods;
import me.lukeben.utils.SoundBuilder;
import me.lukeben.utils.versionsupport.IMaterial;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Getter
public class SettingsMenu extends Menu {

    private final List<Integer> itemSlots = Lists.newArrayList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34);
    public final MenuFiller menuFiller = new MenuFiller(Lists.newArrayList("0-9", "17-18", "26-27", "35-44"), IMaterial.WHITE_STAINED_GLASS_PANE.getBuilder().displayName(" ").build());

    private Map<String, Object> settings;
    private final SettingsMenuType type;
    private final SettingsMenu parent;
    private final SimpleConfig config;
    private final Class configClass;
    private final String identifier;
    private int page;

    public SettingsMenu(Player player, SimpleConfig config, Class configClass, SettingsMenu parent, Map<String, Object> settings, SettingsMenuType type, int page, String identifier) {
        super(player, "&e&lSettings", 5);
        this.settings = settings;
        this.type = type;
        this.parent = parent;
        this.config = config;
        this.configClass = configClass;
        this.page = page;
        this.identifier = identifier;
        buildMenu();
        displayMenu();
    }

    public HashMap<Object, Object> getPagedItems(Map<String, Object> map) {
        HashMap<Object, Object> returned = Maps.newHashMap();
        int amountPerPage = itemSlots.size(); //18
        int maxNumber = amountPerPage * page; //18 x 1 = 18
        List<String> keySet = Lists.newArrayList(map.keySet());

        // current => 18 - 18 = 0; current
        for (int current = maxNumber - amountPerPage; current< maxNumber; current++) {
            //checking if there is enough tags to fully fill that page.
            if (map.size() <= current) break;

            String identifier = keySet.get(current);
            returned.put(identifier, map.get(identifier));
        }
        return returned;
    }

    //for an array list
    public List<Object> getPagedItems(List<Object> collection) {
        List<Object> returnedObjects = Lists.newArrayList();
        int amountPerPage = itemSlots.size(); //18
        int maxNumber = amountPerPage * page; //18 x 1 = 18

        // current => 18 - 18 = 0; current
        for (int current = maxNumber - amountPerPage; current< maxNumber; current++) {
            //checking if there is enough tags to fully fill that page.
            if (collection.size() <= current) break;

            returnedObjects.add(collection.get(current));
        }
        return returnedObjects;
    }

    public void getPlayerInput(String question, String value, Consumer<ConvPrompt> onAnswer, Consumer<ConversationAbandonedEvent> onFinish) {
        getPlayer().closeInventory();
        ConvPrompt prompt = ConvPrompt.builder().promptText(question.replace("{0}", value)).answer(onAnswer).build();
        ConversationAPI.build(getPlayer(), prompt, 10, "QUIT", onFinish);
    }

    public void buildMenu() {
        menuFiller.getSlots().forEach(slot -> setItem(slot, menuFiller.getFillerItem().toItemStack()));
        if(parent == null) setItem(40, ItemBuilder.builder().item(Material.BARRIER).displayName("&c&lClick to close").lore("&7Click to display").toItemStack(), e -> getPlayer().closeInventory());
        if(parent != null) setBackButton();
        setSaveIcon();

        switch (type) {
            case NORMAL:
                setPageButtons(settings.size());
                HashMap<Object, Object> pagedSettings = getPagedItems(settings);
                for (Object key : pagedSettings.keySet()) {
                    Object value = pagedSettings.get(key);
                    //is a primitive = a variable
                    if (Methods.isPrimOrWrapper(value) || value instanceof String) {
                        setItem(getFirstSlot(), ItemBuilder.builder().item(Material.PAPER).displayName("&6" + key).lore("&eCurrent: &7" + value).toItemStack(), e -> {
                            getPlayerInput("&7Please enter a new value for &e{0} &7or type 'QUIT' to cancel!", String.valueOf(key), answer -> {
                                if (value instanceof Integer) settings.put(String.valueOf(key), Integer.parseInt(answer.getReceivedInput()));
                                else if (value instanceof Double) settings.put(String.valueOf(key), Double.parseDouble(answer.getReceivedInput()));
                                else if (value instanceof Boolean) settings.put(String.valueOf(key), Boolean.parseBoolean(answer.getReceivedInput()));
                                else settings.put(String.valueOf(key), answer.getReceivedInput());
                            }, finish -> new SettingsMenu(getPlayer(), config, configClass, parent, settings, SettingsMenuType.NORMAL, page, String.valueOf(key)));
                        });
                        //its a collection, list, or set
                    } else if (Collection.class.isAssignableFrom(value.getClass())) {
                        Collection<Object> values = (Collection<Object>) value; //collection
                        HashMap<String, Object> newValues = Maps.newHashMap(); //new hashmap
                        newValues.put(String.valueOf(key), values); //
                        setItem(getFirstSlot(), ItemBuilder.builder().item(Material.BOOK).displayName("&6" + key + " &7(List)").lore("&eSize: &7" + values.size()).toItemStack(), e -> {
                            new SettingsMenu(getPlayer(), config, configClass, this, newValues, SettingsMenuType.ARRAY, page, key + "*");
                        });
                    } else {
                        Map<Object, Object> values = (Map<Object, Object>) value;
                        ItemBuilder icon = ItemBuilder.builder().item(Material.BOOK).displayName("&6" + key).lore("&eSize: &7" + values.size()).build();
                        if (key.toString().toUpperCase().endsWith("LOCATION")) {
                            icon = ItemBuilder.builder().item(Material.ENDER_PEARL)
                                    .displayName("&6" + key)
                                    .lore("&7")
                                    .lore("&3&l* &bWorld: &7" + values.get("WORLD"))
                                    .lore("&3&l* &bX: &7" + values.get("CORD_X"))
                                    .lore("&3&l* &bY: &7" + values.get("CORD_Y"))
                                    .lore("&3&l* &bZ: &7" + values.get("CORD_Z"))
                                    .lore("&3&l* &bYaw: &7" + values.get("YAW"))
                                    .lore("&3&l* &bPitch: &7" + values.get("PITCH"))
                                    .lore("&7")
                                    .build();
                        } else if (key.toString().toUpperCase().endsWith("ITEM")) {
                            ItemBuilder item = (ItemBuilder) SettingsSerializer.getInstance().toObject(values, ItemBuilder.class);
                            icon = ItemBuilder.builder().item(Material.valueOf(values.get("TYPE").toString()))
                                    .displayName("&6" + key)
                                    .skull(item.getSkullIdentifier())
                                    .lore("&7")
                                    .lore("&3&l* &bType: &7" + item.getCurrent().getType())
                                    .lore("&3&l* &bAmount: &7" + item.getCurrent().getAmount())
                                    .lore("&3&l* &bData: &7" + item.getCurrent().getDurability())
                                    .lore("&7")
                                    .build();
                        } else if(key.toString().endsWith("SOUND")) {
                            SoundBuilder builder = (SoundBuilder) SettingsSerializer.getInstance().toObject(values, SoundBuilder.class);
                            icon = ItemBuilder.builder().item(Material.NOTE_BLOCK)
                                    .displayName("&6" + key)
                                    .lore("&7")
                                    .lore("&3&l* &bSound: &7" + builder.getSOUND().name())
                                    .lore("&3&l* &bPitch: &7" + builder.getPITCH())
                                    .lore("&3&l* &bVolume: &7" + builder.getVOLUME())
                                    .lore("&7")
                                    .build();
                        } else if(SettingsManager.getInstance().useCustomIcon(key.toString())) {
                            icon = SettingsManager.getInstance().getCustomIcon(key.toString());
                        }
                        setItem(getFirstSlot(), icon.toItemStack(), e -> new SettingsMenu(getPlayer(), config, configClass, this, (Map<String, Object>) value, SettingsMenuType.MAP, 1, String.valueOf(key)));
                    }
                }
                break;
            case ARRAY:
                List<Object> collection = (List<Object>) new ArrayList(settings.values()).get(0);
                setPageButtons(collection.size());
                if(SettingsManager.getInstance().arrayHasDefaultObject(identifier)) {
                    setItem(18, IMaterial.WRITABLE_BOOK.getBuilder().displayName("&aNEW &8[&a+&8]").lore("&7Click to add a new object to this list").toItemStack(), e -> {
                        collection.add(SettingsManager.getInstance().getDefaultObject(identifier));
                        settings.put(identifier, collection);
                        new SettingsMenu(getPlayer(), config, configClass,  parent, settings, SettingsMenuType.ARRAY, page, identifier);
                    });
                }

                final AtomicInteger current = new AtomicInteger((page - 1) * itemSlots.size());
                for (Object o : getPagedItems(collection)) {
                    if (Methods.isPrimOrWrapper(o) || o instanceof String) {
                        setItem(getFirstSlot(), ItemBuilder.builder().item(Material.PAPER).displayName("&7" + o.toString()).lore("&7").lore("&o&7(Middle Click) &cto delete!").toItemStack(), e -> {

                            if(e.getClick() == ClickType.MIDDLE) {
                                collection.remove(o);
                            } else {
                                getPlayerInput("&7Please enter a new value for &e{0} &7or type 'QUIT' to cancel!", String.valueOf(current.get()), answer -> {
                                    String promptResponse = answer.getReceivedInput();
                                    if (o instanceof Integer) collection.add(Integer.parseInt(promptResponse));
                                    else if (o instanceof Double) collection.add(Double.parseDouble(promptResponse));
                                    else if (o instanceof Boolean) collection.add(Boolean.parseBoolean(promptResponse));
                                    else collection.add(promptResponse);
                                }, finish -> {});
                                getPlayer().closeInventory();
                            }
                            settings.put(identifier, collection);
                            new SettingsMenu(getPlayer(), config, configClass,  parent, settings, SettingsMenuType.ARRAY, page, identifier);
                        });
                    } else {
                        setItem(getFirstSlot(), ItemBuilder.builder().item(Material.BOOK).displayName("&6" + current).lore("&7").lore("&o&7(Middle Click) &cto delete!").toItemStack(), e -> {
                            if(e.getClick() == ClickType.MIDDLE) {
                                collection.remove(o);
                                settings.put(identifier, collection);
                                new SettingsMenu(getPlayer(), config, configClass,  parent, settings, SettingsMenuType.ARRAY, page, identifier);
                            } else {
                                new SettingsMenu(getPlayer(), config, configClass,  this, (Map<String, Object>) o, SettingsMenuType.MAP, 1, identifier);
                            }
                        });
                        current.incrementAndGet();
                    }
                }
                break;
            case MAP:
                Map<String, Object> map = settings;
                setPageButtons(map.size());
                if(SettingsManager.getInstance().arrayHasDefaultObject(identifier + "**")) {
                    setItem(18, IMaterial.WRITABLE_BOOK.getBuilder().displayName("&aNEW &8[&a+&8]").lore("&7Click to add a new object to this list").toItemStack(), e -> { ;
                        Map<String, Object> addingMap = (Map<String, Object>) SettingsManager.getInstance().getDefaultObject(identifier + "**");
                        System.out.println(addingMap);
                        System.out.println(SettingsSerializer.getInstance().gson.toJson(map));
                        settings.putAll(addingMap);
                        settings = SettingsSerializer.getInstance().serialize(settings);
                        System.out.println(settings);
                        new SettingsMenu(getPlayer(), config, configClass,  parent, settings, SettingsMenuType.MAP, page, identifier);
                    });
                }
                List<Object> objects = Lists.newArrayList();
                objects.addAll(map.keySet());
                for (Object key : getPagedItems(objects)) {
                    Object value = map.get(key);

                    //is a primitive = a variable
                    if(SettingsManager.getInstance().arrayHasDefaultObject(identifier + "**")) {
                        if (Methods.isPrimOrWrapper(value) || value instanceof String) {
                            setItem(getFirstSlot(), ItemBuilder.builder().item(Material.PAPER).displayName("&6" + key).lore("&eCurrent: &7" + value).lore("&7").lore("&o&7(Middle Click) &cto delete!").lore("&o&7(Right Click) &cto change identifier").toItemStack(), e -> {
                                if (e.getClick() == ClickType.MIDDLE) {
                                    map.remove(key);
                                    new SettingsMenu(getPlayer(), config, configClass, parent, settings, SettingsMenuType.MAP, page, identifier);
                                } else if (e.getClick() == ClickType.RIGHT) {
                                    getPlayer().closeInventory();
                                    ConvPrompt prompt = ConvPrompt.builder().promptText("&7Please enter a new value for &eKEY &7or type 'QUIT' to cancel!").answer(ans -> {
                                        String newKey = ans.getReceivedInput();
                                        map.remove(key);
                                        map.put(newKey, value);
                                        new SettingsMenu(getPlayer(), config, configClass, parent, settings, SettingsMenuType.MAP, page, identifier);
                                    }).build();
                                    ConversationAPI.build(getPlayer(), prompt, 10, "QUIT");
                                } else {
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            getPlayer().closeInventory();
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
                                        //settings.put(id, map);
                                    }).build();
                                    ConversationAPI.build(getPlayer(), prompt, 10, "QUIT");
                                    new SettingsMenu(getPlayer(), config, configClass,  parent, settings, SettingsMenuType.MAP, page, identifier);
                                }
                            });
                        } else if (Collection.class.isAssignableFrom(value.getClass())) {
                            Collection<Object> values = (Collection<Object>) value;
                            HashMap<String, Object> newValues = Maps.newHashMap();
                            newValues.put(key.toString(), values);
                            setItem(getFirstSlot(), ItemBuilder.builder().item(Material.BOOK).displayName("&6" + key + " &7(List)").lore("&eSize: &7" + values.size()).toItemStack(), e -> {
                                new SettingsMenu(getPlayer(), config, configClass, this, newValues, SettingsMenuType.ARRAY, 1, String.valueOf(key) + "*");
                            });
                        } else {
                            Map<Object, Object> values = (Map<Object, Object>) value;
                            ItemBuilder icon = ItemBuilder.builder().item(Material.BOOK).displayName("&6" + key).lore("&eSize: &7" + values.size()).build();
                            if (key.toString().toUpperCase().endsWith("LOCATION")) {
                                icon = ItemBuilder.builder().item(Material.ENDER_PEARL)
                                        .displayName("&6" + key)
                                        .lore("&7")
                                        .lore("&3&l* &bWorld: &7" + values.get("WORLD"))
                                        .lore("&3&l* &bX: &7" + values.get("CORD_X"))
                                        .lore("&3&l* &bY: &7" + values.get("CORD_Y"))
                                        .lore("&3&l* &bZ: &7" + values.get("CORD_Z"))
                                        .lore("&3&l* &bYaw: &7" + values.get("YAW"))
                                        .lore("&3&l* &bPitch: &7" + values.get("PITCH"))
                                        .lore("&7")
                                        .build();
                            } else if (key.toString().toUpperCase().endsWith("ITEM")) {
                                ItemBuilder item = (ItemBuilder) SettingsSerializer.getInstance().toObject(values, ItemBuilder.class);
                                icon = ItemBuilder.builder().item(Material.valueOf(values.get("TYPE").toString()))
                                        .displayName("&6" + key)
                                        .skull(item.getSkullIdentifier())
                                        .lore("&7")
                                        .lore("&3&l* &bType: &7" + item.getCurrent().getType())
                                        .lore("&3&l* &bAmount: &7" + item.getCurrent().getAmount())
                                        .lore("&3&l* &bData: &7" + item.getCurrent().getDurability())
                                        .lore("&7")
                                        .build();
                            } else if(key.toString().endsWith("SOUND")) {
                                SoundBuilder builder = (SoundBuilder) SettingsSerializer.getInstance().toObject(values, SoundBuilder.class);
                                icon = ItemBuilder.builder().item(Material.NOTE_BLOCK)
                                        .displayName("&6" + key)
                                        .lore("&7")
                                        .lore("&3&l* &bSound: &7" + builder.getSOUND().name())
                                        .lore("&3&l* &bPitch: &7" + builder.getPITCH())
                                        .lore("&3&l* &bVolume: &7" + builder.getVOLUME())
                                        .lore("&7")
                                        .build();
                            } else if(SettingsManager.getInstance().useCustomIcon(key.toString())) {
                                icon = SettingsManager.getInstance().getCustomIcon(key.toString());
                            }
                            if(SettingsManager.getInstance().arrayHasDefaultObject(identifier + "**")) {
                                icon = ItemBuilder.builder().item(icon.toItemStack()).lore("&7").lore("&o&7(Middle Click) &cto delete!").lore("&o&7(Right Click) &cto change identifier").build();
                            }
                            setItem(getFirstSlot(), icon.toItemStack(), e -> {
                                if (e.getClick() == ClickType.MIDDLE) {
                                    map.remove(key);
                                    new SettingsMenu(getPlayer(), config, configClass, parent, settings, SettingsMenuType.MAP, page, identifier);
                                } else if (e.getClick() == ClickType.RIGHT) {
                                    getPlayer().closeInventory();
                                    ConvPrompt prompt = ConvPrompt.builder().promptText("&7Please enter a new value for &eKEY &7or type 'QUIT' to cancel!").answer(ans -> {
                                        String newKey = ans.getReceivedInput();
                                        map.remove(key);
                                        map.put(newKey, value);
                                        new SettingsMenu(getPlayer(), config, configClass, parent, settings, SettingsMenuType.MAP, page, identifier);
                                    }).build();
                                    ConversationAPI.build(getPlayer(), prompt, 10, "QUIT");
                                } else {
                                    new SettingsMenu(getPlayer(), config, configClass, this, (Map<String, Object>) value, SettingsMenuType.MAP, 1, String.valueOf(key));
                                }
                            });

                        }
                    } else {
                        if (Methods.isPrimOrWrapper(value) || value instanceof String) {
                            setItem(getFirstSlot(), ItemBuilder.builder().item(Material.PAPER).displayName("&6" + key).lore("&eCurrent: &7" + value).toItemStack(), e -> {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        getPlayer().closeInventory();
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
                                    //settings.put(id, map);
                                    new SettingsMenu(getPlayer(), config, configClass,  parent, settings, SettingsMenuType.MAP, page, identifier);
                                }).build();
                                ConversationAPI.build(getPlayer(), prompt, 10, "QUIT");
                            });
                        } else if (Collection.class.isAssignableFrom(value.getClass())) {
                            Collection<Object> values = (Collection<Object>) value;
                            HashMap<String, Object> newValues = Maps.newHashMap();
                            newValues.put(key.toString(), values);
                            setItem(getFirstSlot(), ItemBuilder.builder().item(Material.BOOK).displayName("&6" + key + " &7(List)").lore("&eSize: &7" + values.size()).toItemStack(), e -> {
                                new SettingsMenu(getPlayer(), config, configClass, this, newValues, SettingsMenuType.ARRAY, 1, String.valueOf(key) + "*");
                            });
                        } else {
                            Map<Object, Object> values = (Map<Object, Object>) value;
                            ItemBuilder icon = ItemBuilder.builder().item(Material.BOOK).displayName("&6" + key).lore("&eSize: &7" + values.size()).build();
                            if (key.toString().toUpperCase().endsWith("LOCATION")) {
                                icon = ItemBuilder.builder().item(Material.ENDER_PEARL)
                                        .displayName("&6" + key)
                                        .lore("&7")
                                        .lore("&3&l* &bWorld: &7" + values.get("WORLD"))
                                        .lore("&3&l* &bX: &7" + values.get("CORD_X"))
                                        .lore("&3&l* &bY: &7" + values.get("CORD_Y"))
                                        .lore("&3&l* &bZ: &7" + values.get("CORD_Z"))
                                        .lore("&3&l* &bYaw: &7" + values.get("YAW"))
                                        .lore("&3&l* &bPitch: &7" + values.get("PITCH"))
                                        .lore("&7")
                                        .build();
                            } else if (key.toString().toUpperCase().endsWith("ITEM")) {
                                ItemBuilder item = (ItemBuilder) SettingsSerializer.getInstance().toObject(values, ItemBuilder.class);
                                icon = ItemBuilder.builder().item(Material.valueOf(values.get("TYPE").toString()))
                                        .displayName("&6" + key)
                                        .skull(item.getSkullIdentifier())
                                        .lore("&7")
                                        .lore("&3&l* &bType: &7" + item.getCurrent().getType())
                                        .lore("&3&l* &bAmount: &7" + item.getCurrent().getAmount())
                                        .lore("&3&l* &bData: &7" + item.getCurrent().getDurability())
                                        .lore("&7")
                                        .build();
                            } else if(key.toString().endsWith("SOUND")) {
                                SoundBuilder builder = (SoundBuilder) SettingsSerializer.getInstance().toObject(values, SoundBuilder.class);
                                icon = ItemBuilder.builder().item(Material.NOTE_BLOCK)
                                        .displayName("&6" + key)
                                        .lore("&7")
                                        .lore("&3&l* &bSound: &7" + builder.getSOUND().name())
                                        .lore("&3&l* &bPitch: &7" + builder.getPITCH())
                                        .lore("&3&l* &bVolume: &7" + builder.getVOLUME())
                                        .lore("&7")
                                        .build();
                            } else if(SettingsManager.getInstance().useCustomIcon(key.toString())) {
                                icon = SettingsManager.getInstance().getCustomIcon(key.toString());
                            }
                            setItem(getFirstSlot(), icon.toItemStack(), e -> {
                                new SettingsMenu(getPlayer(), config, configClass, this, (Map<String, Object>) value, SettingsMenuType.MAP, 1, String.valueOf(key));
                            });

                        }
                    }
                }
                break;
        }
    }

    public void setSaveIcon() {
        setItem(26, ItemBuilder.builder().item(Material.COMPASS).displayName("&e&lClick to Save!").lore("&7Click to save file!").toItemStack(), e -> {
            Map<String, Object> hierarchy = getHierarchy(this);
            DiskUtil.write(config.getFile(), SettingsSerializer.getInstance().deserialize(hierarchy));
            Accessor.get(configClass).copy(SettingsSerializer.getInstance().toObject(hierarchy, configClass), configClass);
        });
    }

    public void setBackButton() {
        setItem(40, ItemBuilder.builder().item(Material.BARRIER).displayName("&c&l<- Go Back").lore("&7Click to go up a category").toItemStack(), e -> {
            getPlayer().closeInventory();
            parent.displayMenu();
        });
    }

    public void setPageButtons(int amount) {
        setItem(42, ItemBuilder.builder()
                .item(Material.ARROW)
                .displayName("&a&lNext Page >>")
                .lore("&7Click to advance to the next page.").toItemStack(), event -> {

            int maxNumber = itemSlots.size() * page;
            //basically checking if there is enough tags to warrant another page...
            if(maxNumber < amount) {
                page++;
                for(int slot : itemSlots) {
                    if(getItems().containsKey(slot)) getItems().remove(slot);
                    if(getExecutors().containsKey(slot)) getExecutors().remove(slot);
                }
                buildMenu();
                refresh();
            }
        });
        setItem(38, ItemBuilder.builder()
                .item(Material.ARROW)
                .displayName("&c&l<< Previous Page")
                .lore("&7Click to return to the previous page.")
                .toItemStack(), event -> {
            //So, theoretically they should always be able to go back, unless they are on the first page;
            if(page > 1) {
                page--;
                for(int slot : itemSlots) {
                    if(getItems().containsKey(slot)) getItems().remove(slot);
                    if(getExecutors().containsKey(slot)) getExecutors().remove(slot);
                }
                buildMenu();
                refresh();
            }
        });
    }

    public Map<String, Object> getHierarchy(SettingsMenu menu) {
        if (menu == null) return Maps.newHashMap();

        SettingsMenu parent = menu;
        while (parent.getParent() != null) {
            if (parent.getIdentifier().endsWith("*")) {
                String id = parent.getIdentifier().substring(0, parent.getIdentifier().toCharArray().length - 1);
                Collection<Object> collection = (Collection<Object>) parent.getSettings().get(id);
                parent.getParent().getSettings().put(id, collection);
            } else if (parent.getIdentifier().endsWith("**")) {
                String id = parent.getIdentifier().substring(0, parent.getIdentifier().toCharArray().length - 2);
                HashMap<String, Object> map = (HashMap<String, Object>) parent.getSettings();
                parent.getParent().getSettings().put(id, map);
            } else {
                parent.getParent().getSettings().put(parent.getIdentifier(), parent.getSettings());
            }
            parent = parent.getParent();
        }

        return parent.getSettings();
    }

    public int getFirstSlot() {
        for (int slot : itemSlots) {
            if (getItems().containsKey(slot)) continue;
            return slot;
        }
        return -1;
    }

}
