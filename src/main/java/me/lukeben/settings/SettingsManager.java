package me.lukeben.settings;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import me.lukeben.utils.ItemBuilder;

import java.util.HashMap;
import java.util.List;

public class SettingsManager {

    @Getter
    private static transient final SettingsManager instance = new SettingsManager();

    @Getter
    private final HashMap<String, Object> defaultArrayObjects = Maps.newHashMap();

    @Getter
    private final HashMap<String, ItemBuilder> customIcons = Maps.newHashMap();

    public void addEditableArray(String identifier, Object object) {
        defaultArrayObjects.put(identifier, object);
    }

    public void addCustomIcon(String identifer, ItemBuilder customIcon) {
        customIcons.put(identifer, customIcon);
    }

    public ItemBuilder getCustomIcon(String identifier) {
        if(!useCustomIcon(identifier)) return null;

        ItemBuilder value;
        for(String id : customIcons.keySet()) {
            if(id.toUpperCase().endsWith("_" + identifier.toUpperCase())) {
                value = customIcons.get(id);
                return value;
            }
            continue;
        }
        return null;
    }

    public boolean useCustomIcon(String identifier) {
        boolean value = false;
        for(String id : customIcons.keySet()) {
            if(id.toUpperCase().endsWith("_" + identifier.toUpperCase())) {
                value = true;
                break;
            }
            continue;
        }
        return value;
    }

    public boolean arrayHasDefaultObject(String identifier) {
        return defaultArrayObjects.containsKey(identifier);
    }

    public Object getDefaultObject(String identifier) {
        return defaultArrayObjects.get(identifier);
    }

}
