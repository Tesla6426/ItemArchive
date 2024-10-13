package net.txsla.itemarchive.commands;
import net.txsla.itemarchive.ArchiveManager;
import net.txsla.itemarchive.GuiManager;
import net.txsla.itemarchive.ItemArchive;
import org.bukkit.command.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.System.currentTimeMillis;

public class open implements CommandExecutor , TabExecutor {
    private final ItemArchive plugin;
    private static List<String> timeIndex;
    public open(ItemArchive plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        int page = 0;

        // Use the command correctly ffs
        if (args.length < 1) return false;

        // make sure the archive exists
        if (ArchiveManager.getArchiveList().stream().noneMatch(args[0]::contains)) {
            sender.sendMessage("§cArchive " + args[0] + " does not exist"); return true;
        }

        // attempt to open archive
        int index = ArchiveManager.CacheLookupTable.indexOf(args[0]);
        Player p = (Player) sender;

            // check if page is specified
            if (args.length == 2) {
                try { page = Integer.parseInt(args[1]);
                }catch (Exception e) { // if string cannot be parsed as integer, then open a random page
                        page = (int) Math.floor( Math.random() * ( (double) (ArchiveManager.ArchiveCache[index].length-1) / 36 )  );}
            }else { page = 0; }
         try {
             GuiManager.open( p, args[0], page); } catch (Exception e) { plugin.getLogger().warning("[GuiManager.failToRun] "+e);}
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String str, @NotNull String[] args) {
                return ArchiveManager.getArchiveList();
    }
}
