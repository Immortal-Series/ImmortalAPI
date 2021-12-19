package me.lukeben.utils;

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

@UtilityClass
public class Methods {

    public String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
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

    public void sendMessage(Player player, String text) {
        player.sendMessage(Methods.color(text));
    }

}
