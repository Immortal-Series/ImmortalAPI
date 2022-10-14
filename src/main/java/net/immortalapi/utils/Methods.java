package net.immortalapi.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class Methods {

    public static String color(String message) {
        final Pattern hexPattern = Pattern.compile("#<([A-Fa-f0-9]){6}>");
        if (getMinorVersion() >= 16) {
            Matcher matcher = hexPattern.matcher(message);
            while (matcher.find()) {
                String hexString = matcher.group();
                hexString = "#" + hexString.substring(2, hexString.length() - 1);
                final net.md_5.bungee.api.ChatColor hex = net.md_5.bungee.api.ChatColor.of(hexString);
                final String before = message.substring(0, matcher.start());
                final String after = message.substring(matcher.end());
                message = before + hex + after;
                matcher = hexPattern.matcher(message);
            }
        }

        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean isLegacy() {
        String[] split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        String majorVer = split[0]; //For 1.10 will be "1"
        String minorVer = split[1]; //For 1.10 will be "10"
        return !(Integer.parseInt(majorVer) > 1) && (Integer.parseInt(minorVer) <= 8);
    }

    public int getMinorVersion() {
        String[] split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        String minorVer = split[1];
        return Integer.parseInt(minorVer);
    }

    public List<String> colorAndConvert(List<String> oldLore) {
        List<String> lore = new ArrayList<>();
        for (String line : oldLore) {
            lore.add(color(line));
        }
        return lore;
    }

    public boolean isInt(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void tell(CommandSender sender, String message, Object... objects) {
        if (objects != null) for (int i = 0; i < objects.length; i++) message = message.replace("{" + i + "}", String.valueOf(objects[i]));
        sender.sendMessage(color(message));
    }

    public String placeholders(String message, Object... objects) {
        if (objects != null) for (int i = 0; i < objects.length; i++) message = message.replace("{" + i + "}", String.valueOf(objects[i]));
        return color(message);
    }

    public static void log(String text) {
        Bukkit.getConsoleSender().sendMessage(color(text));
    }

    public boolean canHoldItem(PlayerInventory inventory, ItemStack item) {
        if(inventory.firstEmpty() != -1) return true;
        for(ItemStack invItem : inventory.getContents()) {
            if(invItem.isSimilar(item) && invItem.getMaxStackSize() > (invItem.getAmount() + item.getAmount())) {
                return true;
            }
            continue;
        }
        return false;
    }

    public static Object getPrivateField(String fieldName, Class clazz, Object object) {
        Field field;
        Object o = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        } catch(NoSuchFieldException e) {
            e.printStackTrace();
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    public void launchFirework(Player player, FireworkEffect effect) {
        Firework fw = (Firework) player.getWorld().spawn(player.getEyeLocation(), Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(effect);
        fw.setFireworkMeta(meta);
    }

    public int getRandomNumber(int min, int max) {
        int value = (int) ((Math.random() * (max - min)) + min);
        return value;
    }

    public boolean isPrimOrWrapper(Object type) {
        type = type.getClass();
        if (type == Double.class || type == Float.class || type == Long.class ||
                type == Integer.class || type == Short.class || type == Character.class ||
                type == Byte.class || type == Boolean.class || type.getClass().isPrimitive()) {
            return true;
        }
        return false;
    }

}
