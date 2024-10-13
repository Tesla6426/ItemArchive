package net.txsla.itemarchive.commands;
import net.txsla.itemarchive.ArchiveManager;
import net.txsla.itemarchive.ItemArchive;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
public class countitems implements CommandExecutor , TabExecutor {
    private final ItemArchive plugin;
    public countitems(ItemArchive plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        // use the damn command correctly
        if (args.length < 1) return false;

        // make sure the archive exists
        if (ArchiveManager.getArchiveList().stream().noneMatch(args[0]::contains)) {
             sender.sendMessage("§cArchive" + args[0] + " does not exist"); return true;
        }

        sender.sendMessage("§aThere are " + ArchiveManager.ArchiveCache[ArchiveManager.CacheLookupTable.indexOf(args[0])].length + " items in " + args[0]);
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String str, @NotNull String[] args) {
        return ArchiveManager.getArchiveList();
    }
}
