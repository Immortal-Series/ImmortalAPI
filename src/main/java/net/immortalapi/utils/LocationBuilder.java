package net.immortalapi.utils;


import lombok.Builder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
@Builder(builderClassName = "Builder")
public class LocationBuilder {

    private String world;
    private double xCord;
    private double yCord;
    private double zCord;
    private float yaw;
    private float pitch;

    public static class Builder {

        public Builder clone() {
            return LocationBuilder.builder().fromLocation(this.toLocation());
        }

        public Builder fromLocation(Location location) {
            return LocationBuilder.builder().world(location.getWorld().getName()).xCord(location.getX()).yCord(location.getY()).zCord(location.getZ()).yaw(location.getYaw()).pitch(location.getPitch());
        }

        public Builder fromLocation(Player player) {
            Location loc = player.getLocation();
            return fromLocation(loc);
        }

        public Location toLocation() {
            Location builderLoc = new Location(Bukkit.getWorld(world), xCord, yCord, zCord, yaw,  pitch);
            return builderLoc;
        }

    }

    public Location toLocation() {
        Location builderLoc = new Location(Bukkit.getWorld(world), xCord, yCord, zCord, yaw,  pitch);
        return builderLoc;
    }

}
