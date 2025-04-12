package net.txsla.itemarchive;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.Map;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class ItemConverter {
    public static String toString(ItemStack savedItem) throws InvalidConfigurationException {
            YamlConfiguration itemConfig = new YamlConfiguration();
            itemConfig.set("item", savedItem);
            String serialized = itemConfig.saveToString();
            return serialized;
    }
    public static ItemStack toItemStack(String serialized) throws InvalidConfigurationException {
        ItemStack restoredItem;
        YamlConfiguration restoreConfig = new YamlConfiguration();
            try {
                restoreConfig.loadFromString(serialized);
                restoredItem = restoreConfig.getItemStack("item");
        } catch (Exception e) {
                ItemStack nullItem = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
                ItemMeta nullItemMeta = nullItem.getItemMeta();
                nullItemMeta.setMaxStackSize(1);
                nullItemMeta.setDisplayName(ChatColor.RED + "Loading Item...");
                nullItem.setItemMeta(nullItemMeta);
                return nullItem;
                //return ItemConverter.toItemStack("aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzcwMAogIHR5cGU6IFJFRF9TVEFJTkVEX0dMQVNTX1BBTkUKICBtZXRhOgogICAgPT06IEl0ZW1NZXRhCiAgICBtZXRhLXR5cGU6IFVOU1BFQ0lGSUMKICAgIGRpc3BsYXktbmFtZTogJ3sidGV4dCI6IkxvYWRpbmcgSXRlbS4uLiIsImNvbG9yIjoicmVkIiwiaXRhbGljIjpmYWxzZX0nCiAgICBsb3JlOgogICAgLSAneyJ0ZXh0IjoicmUtb3BlbiBwYWdlIHRvIHJlZnJlc2ggaXRlbXMiLCJjb2xvciI6ImdyYXkiLCJpdGFsaWMiOmZhbHNlfScK");
            }
        return restoredItem;
    }
}
