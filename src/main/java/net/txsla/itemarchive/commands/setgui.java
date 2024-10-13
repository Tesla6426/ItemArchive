package net.txsla.itemarchive.commands;
import net.txsla.itemarchive.ArchiveManager;
import net.txsla.itemarchive.GuiManager;
import net.txsla.itemarchive.ItemArchive;
import net.txsla.itemarchive.ItemConverter;
import org.bukkit.command.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
public class setgui implements CommandExecutor , TabExecutor {
    private final ItemArchive plugin;
    public setgui(ItemArchive plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        Player p = (Player) sender;
        // make sure the archive exists
        if (ArchiveManager.getArchiveList().stream().noneMatch(args[0]::contains)) {
            sender.sendMessage("§cArchive " + args[0] + " does not exist"); return true;
        }

        // get encoder ready
        Base64.Encoder encoder = Base64.getEncoder();
        String serial,place = "air";

        // get item from player
        ItemStack item = p.getInventory().getItemInMainHand();
        // serialise item data
        try { serial = ItemConverter.toString(item); } catch (InvalidConfigurationException e) { throw new RuntimeException(e); }

        plugin.getLogger().info("Item = " + item);

        switch (args[1]) {
            case "item":
                if (item.getItemMeta() == null) return false;
                GuiManager.setArchiveTheme(args[0], args[2], encoder.encodeToString(serial.getBytes()));
                sender.sendMessage("§a " + args[0] + " " + args[2] + " set!");
                return true;
            case "layout":
                GuiManager.setArchiveTheme(args[0], "Layout", args[2]);
                sender.sendMessage("§a " + args[0] + " Layout set!");
                return true;
            case "enableCustomLayout":
                if (args[2].matches("true") || args[2].matches("false")) {
                    GuiManager.setArchiveTheme(args[0], "isCustomGui", args[2]);
                    sender.sendMessage("§a " + args[0] + " isCustomGui set to" + args[2] + "!");
                    return true;
                }
                return false;
            case "name":
                GuiManager.setArchiveTheme(args[0],"name", encoder.encodeToString(args[2].replaceAll("&", "§").getBytes()));
                sender.sendMessage("§a " + args[0] + " name set to " + args[2].replaceAll("&", "§") + "!");
                return true;
        }


        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String str, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 1:
                return ArchiveManager.getArchiveList();
            case 2:
                list.add("name");
                list.add("item");
                list.add("layout");
                list.add("enableCustomLayout");

            return list;
            case 3:
                switch (args[1]) {
                    case "item":
                        list.add("labelItem");
                        list.add("prevItem");
                        list.add("nextItem");
                        list.add("submitItem");
                        list.add("fillerItem");
                        return list;
                    case "layout":
                        list.add("default");
                        return list;
                    case "enableCustomLayout":
                        list.add("true");
                        list.add("false");
                        return list;
                    case "name":
                        list.add("&6Archive&7");
                        return list;
                }

        }
        return null;
    }
}
