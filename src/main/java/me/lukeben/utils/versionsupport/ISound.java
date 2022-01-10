package me.lukeben.utils.versionsupport;

import me.lukeben.utils.SoundBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.HashMap;
import java.util.Optional;

public enum ISound {

    BLOCK_WOODEN_DOOR_OPEN("DOOR_OPEN"),
    UI_BUTTON_CLICK("CLICK"),
    ENTITY_GENERIC_EXPLODE("EXPLODE"),
    ENTITY_VILLAGER_NO("VILLAGER_NO");

    private final String legacySound;

    ISound(String legacySound) {
        this.legacySound = legacySound;
    }

    public boolean useLegacy() {
        String[] split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        String majorVer = split[0]; //For 1.10 will be "1"
        String minorVer = split[1]; //For 1.10 will be "10"
        return !(Integer.parseInt(majorVer) > 1) && (Integer.parseInt(minorVer) <= 8);
    }

    public SoundBuilder.Builder getBuilder() {
        Optional<SoundBuilder.Builder> sound = PARSED_CACHE.containsKey(this) ? PARSED_CACHE.get(this) : null;
        if(sound != null) return sound.orElse(null);
        SoundBuilder.Builder builder = SoundBuilder.builder();
        if(useLegacy()) builder.sound(Sound.valueOf(legacySound));
        PARSED_CACHE.put(this, Optional.ofNullable(builder));
        return builder;
    }

    public static final HashMap<ISound, Optional<SoundBuilder.Builder>> PARSED_CACHE = new HashMap<>();

}