package me.lukeben.utils.versionsupport;

import me.lukeben.utils.Methods;
import me.lukeben.utils.SoundBuilder;
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
        return Methods.getMinorVersion() <= 8;
    }

    public SoundBuilder.Builder getBuilder() {
        Optional<SoundBuilder.Builder> sound = PARSED_CACHE.containsKey(this) ? PARSED_CACHE.get(this) : null;
        if(sound != null) return sound.orElse(null);
        SoundBuilder.Builder builder = SoundBuilder.builder();
        if(useLegacy()) {
            builder.sound(Sound.valueOf(legacySound));
        } else {
            builder.sound(Sound.valueOf(this.name()));
        }
        PARSED_CACHE.put(this, Optional.ofNullable(builder));
        return builder;
    }

    public static final HashMap<ISound, Optional<SoundBuilder.Builder>> PARSED_CACHE = new HashMap<>();

}