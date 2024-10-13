package net.txsla.itemarchive.commands;
import net.txsla.itemarchive.ArchiveManager;
import net.txsla.itemarchive.ItemArchive;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
public class printarchive implements CommandExecutor , TabExecutor {
    private final ItemArchive plugin;
    public printarchive(ItemArchive plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        // Use the command correctly ffs
        if (args.length < 1) return false;

        // make sure the archive exists
        if (ArchiveManager.getArchiveList().stream().noneMatch(args[0]::contains)) {
            sender.sendMessage("§cArchive " + args[0] + " does not exist"); return true;
        }

        // Print archive data from cache to player chat
        sender.sendMessage( "Archive Data: ");
        String[][] archive = ArchiveManager.ArchiveCache[ArchiveManager.CacheLookupTable.indexOf(args[0])];
        for (int i = 0; i < archive.length; i++  ) {
            sender.sendMessage( "\nITEM NUMBER " + i);
            for (int x = 0; x < 6; x++) {
                sender.sendMessage( "[" + x + "] : " + archive[i][x]);
            }
        }
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return ArchiveManager.getArchiveList();
    }
}
