package me.lukeben.utils.versionsupport;

import me.lukeben.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Optional;

public enum IMaterial {

    WRITABLE_BOOK(0, "BOOK_AND_QUILL"),
    PLAYER_HEAD(3, "SKULL_ITEM"),
    RED_WOOL(14, "WOOL"),
    ORANGE_WOOL(1, "WOOL"),
    GREEN_WOOL(13, "WOOL"),
    BLUE_WOOL(11, "WOOL"),
    PURPLE_WOOL(10, "WOOL"),
    CYAN_WOOL(9, "WOOL"),
    BLACK_WOOL(15, "WOOL"),
    YELLOW_WOOL(4, "WOOL"),
    LIME_WOOL(5, "WOOL"),
    LIGHT_BLUE_WOOL(3, "WOOL"),
    PINK_WOOL(6, "WOOL"),
    WHITE_WOOL(0, "WOOL"),
    GRAY_WOOL(7, "WOOL"),
    LIGHT_GRAY_WOOL(8, "WOOL"),
    GRAY_DYE(8, "INK_SACK"),
    LIME_DYE(10, "INK_SACK"),
    WHITE_STAINED_GLASS_PANE(0, "STAINED_GLASS_PANE"),
    OAK_SIGN(0, "SIGN");

    private int data;
    private String legacyMaterial;

    IMaterial(int data, String legacyMaterial) {
        this.data = data;
        this.legacyMaterial = legacyMaterial;
    }

    public boolean useLegacy() {
        String[] split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        String majorVer = split[0]; //For 1.10 will be "1"
        String minorVer = split[1]; //For 1.10 will be "10"
        return !(Integer.parseInt(majorVer) > 1) && (Integer.parseInt(minorVer) <= 8);
    }


    public ItemBuilder.Builder getBuilder() {
        Optional<ItemBuilder.Builder> material= PARSED_CACHE.containsKey(this) ? PARSED_CACHE.get(this) : null;
        if(material != null) return material.orElse(null);
        ItemBuilder.Builder builder = ItemBuilder.builder();
        if(useLegacy()) builder.item(Material.valueOf(legacyMaterial), 1, data); else builder.item(Material.valueOf(this.name()));
        PARSED_CACHE.put(this, Optional.ofNullable(builder));
        return builder;
    }

    public static final HashMap<IMaterial, Optional<ItemBuilder.Builder>> PARSED_CACHE = new HashMap<>();

}
