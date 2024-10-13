package net.txsla.itemarchive.commands;
import net.txsla.itemarchive.ArchiveManager;
import net.txsla.itemarchive.GuiManager;
import net.txsla.itemarchive.ItemArchive;
import net.txsla.itemarchive.ItemConverter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.System.currentTimeMillis;
import static net.txsla.itemarchive.GuiManager.decode;

public class search implements CommandExecutor , TabExecutor {
    private final ItemArchive plugin;
    private static List<String> timeIndex;
    public search(ItemArchive plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        // add small delay to patch possible lag exploit
        if (timeIndex == null) timeIndex = new ArrayList<>();
        int time = (int) currentTimeMillis(), delay = 100;
        if ( timeIndex.indexOf(sender.toString()) == -1) { timeIndex.addLast(sender.toString()); timeIndex.addLast( ""+ 100 ); }
        int lastRequest = parseInt(timeIndex.get(timeIndex.indexOf(sender.toString())+1));
        if ((time-lastRequest)<delay) return true;
        timeIndex.set(timeIndex.indexOf(sender.toString())+1, "" + time );


        // make sure the archive exists
        if (ArchiveManager.getArchiveList().stream().noneMatch(args[0]::contains)) {
            sender.sendMessage("§cArchive " + args[0] + " does not exist"); return true;
        }

        // Use the command correctly ffs
        if (args.length < 1) return false;

        // build search query from command
        Player p = (Player) sender;
        String query = "";
        // args[0] = archive, args[1+] = search query
        for (int x = args.length-1; x > 0; x--) {
            query += args[x];
        }

        // search for item
        GuiManager.searchItem(p, args[0], query);

        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return ArchiveManager.getArchiveList();
    }
}
