package net.txsla.itemarchive.commands;
import net.txsla.itemarchive.ArchiveManager;
import net.txsla.itemarchive.ItemArchive;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class bulksubmit implements CommandExecutor , TabExecutor {
    private final ItemArchive plugin;
    public bulksubmit(ItemArchive plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        // THIS METHOD HAS NO SAFETY CHECKS, FOR ADMIN USE ONLY

        // use the damn command correctly
        if (args.length < 1) return false;

        // make sure the archive exists
        if (ArchiveManager.getArchiveList().stream().noneMatch(args[0]::contains)) {
            sender.sendMessage("§cArchive " + args[0] + " does not exist"); return true;
        }

        Player p = (Player) sender;
        ItemStack[] items = p.getInventory().getStorageContents();


        ArchiveManager.submitItems( args[0], p.toString().replaceAll("[}{]", "").replaceAll("CraftPlayername=",""), plugin.getConfig().getString("item-version"), items );

        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String str, @NotNull String[] args) {
        return ArchiveManager.getArchiveList();
    }
}