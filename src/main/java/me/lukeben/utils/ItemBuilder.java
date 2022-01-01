package me.lukeben.utils;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTGameProfile;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTListCompound;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter

@Builder(builderClassName = "Builder")
public class ItemBuilder {

    private ItemStack current;
    private String displayName;
    private String skullIdentifier;
    @lombok.Builder.Default
    private Boolean glowing = false;
    @Singular("flag") private List<ItemFlag> flags;
    @Singular("lore") private List<String> lore;

    public static class Builder {

        public Builder clone() {
            return ItemBuilder.builder().item(this.toItemStack());
        }

        public Builder item(ItemStack item) {
            current(item);
            return this;
        }

        public Builder item(Material material) {
            current(new ItemStack(material));
            return this;
        }

        public Builder item(Material material, Integer amount) {
            current(new ItemStack(material, amount));
            return this;
        }

        public Builder item(Material material, Integer amount, int data) {
            current(new ItemStack(material, amount, (short) data));
            return this;
        }

        public Builder skullPlayer(Player player) {
            SkullMeta meta = (SkullMeta) current.getItemMeta();
            meta.setOwner(player.getName());
            current.setItemMeta(meta);
            return this;
        }

        public Builder skull(String base64) {
            NBTItem nbtItem = new NBTItem(current);
            NBTCompound skull = nbtItem.addCompound("SkullOwner");
            int[] version = Arrays.stream(Bukkit.getBukkitVersion().substring(0, Bukkit.getBukkitVersion().indexOf("-")).split("\\.")).mapToInt(Integer::parseInt).toArray();
            UUID randomUUID = UUID.randomUUID();
            if (version[1] > 16) {
                StringBuilder result = new StringBuilder();
                long msb = randomUUID.getMostSignificantBits();
                long lsb = randomUUID.getLeastSignificantBits();
                String uuidString = result.append("[I;")
                        .append(msb >> 32)
                        .append(',')
                        .append(msb & Integer.MAX_VALUE)
                        .append(',')
                        .append(lsb >> 32)
                        .append(',')
                        .append(lsb & Integer.MAX_VALUE)
                        .append(']')
                        .toString();
                skull.setString("Id", uuidString);
            } else {
                skull.setString("Id", randomUUID.toString());
            }
            NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
            texture.setString("Value", base64);
            skullIdentifier(base64);
            return this;
        }

        public Builder addEnchantment(Enchantment enchantment, int level) {
            current.addUnsafeEnchantment(enchantment, level);
            return this;
        }

        public Builder setGlowing() {
            addEnchantment(Enchantment.LUCK, 1);
            flag(ItemFlag.HIDE_ENCHANTS);
            glowing(true);
            return this;
        }

        public Builder registerPlaceholder(String placeholder, String value) {
            if(!lore.isEmpty()) {
                for(String line : lore)  {
                    line = line.replace(placeholder, value);
                }
            }
            if(displayName != null) displayName = displayName.replace(placeholder, value);
            return this;
        }

        public ItemStack toItemStack() {
            ItemMeta meta = current.getItemMeta();
            if(lore != null && !lore.isEmpty()) meta.setLore(Methods.colorAndConvert(lore));
            if(displayName != null) meta.setDisplayName(Methods.color(displayName));
            if(flags != null && !flags.isEmpty()) flags.forEach(flag -> meta.addItemFlags(flag));
            current.setItemMeta(meta);
            return current;
        }

    }

    public ItemStack toItemStack() {
        ItemMeta meta = current.getItemMeta();
        if(!lore.isEmpty()) meta.setLore(Methods.colorAndConvert(lore));
        if(displayName != null) meta.setDisplayName(Methods.color(displayName));
        if(!flags.isEmpty()) flags.forEach(flag -> meta.addItemFlags(flag));
        current.setItemMeta(meta);
        return current;
    }

}
