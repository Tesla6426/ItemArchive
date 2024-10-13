package net.txsla.itemarchive.commands;
import net.txsla.itemarchive.ItemArchive;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class submitban implements CommandExecutor , TabExecutor {
    private final ItemArchive plugin;
    public submitban(ItemArchive plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        // Use the command correctly ffs
        if (args.length < 1) return false;

        String player = args[0];

        //get current list of banned players
        List<String> players = plugin.getConfig().getStringList("submission.banned-players");

        //check if player name is valid
        if ((player.length() > 16) || (!player.replaceAll("\\w*", "").matches(""))) {
            sender.sendMessage("§cInvalid payer name");
            return true;
        }

        //add player to list
        players.add(player);
        plugin.getConfig().set( "submission.banned-players", players );
        this.plugin.saveConfig();

        sender.sendMessage("§aPlayer " + args[0] + " has been banned from submitting items");
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
