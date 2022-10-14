package net.immortal.utils;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Getter
@Builder(builderClassName = "Builder")
public class SoundBuilder {

    private final Sound SOUND;
    private final float VOLUME;
    private final float PITCH;

    public static class Builder {

        public Builder sound(Sound sound) {
            SOUND(sound);
            return this;
        }

        public Builder volume(float volume) {
            VOLUME(volume);
            return this;
        }

        public Builder pitch(float pitch) {
            PITCH(pitch);
            return this;
        }

    }

    public void playSound(Player player) {
        player.playSound(player.getLocation(), SOUND, VOLUME, PITCH);
    }


}
