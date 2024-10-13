package net.txsla.itemarchive.commands;
import net.txsla.itemarchive.ItemArchive;
import net.txsla.itemarchive.ArchiveManager;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
public class listarchives implements CommandExecutor , TabExecutor {
    private final ItemArchive plugin;
    public listarchives(ItemArchive plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        sender.sendMessage( "Existing archives: " + ArchiveManager.getArchiveList().toString() );
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
