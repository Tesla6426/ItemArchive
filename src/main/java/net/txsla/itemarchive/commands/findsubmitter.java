package net.txsla.itemarchive.commands;
import net.txsla.itemarchive.ArchiveManager;
import net.txsla.itemarchive.ItemArchive;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class findsubmitter implements CommandExecutor , TabExecutor {
    private final ItemArchive plugin;
    public findsubmitter(ItemArchive plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        Player p = (Player) sender;
        // use the damn command correctly
        if (args.length < 1) return false;

        // make sure the archive exists
        if (ArchiveManager.getArchiveList().stream().noneMatch(args[0]::contains)) {
            sender.sendMessage("§cArchive " + args[0] + " does not exist"); return true;
        }

        ItemStack item = p.getInventory().getItemInMainHand();
        String submitter = ArchiveManager.findSubmitter(args[0], ArchiveManager.hash(item.toString()) );

        if (submitter == null) sender.sendMessage("§cOops! Item not found in archive " + args[0]);
            else sender.sendMessage(  "§d" + submitter + " submitted the item " + item.getItemMeta().getDisplayName());

        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String str, @NotNull String[] args) {
        return ArchiveManager.getArchiveList();
    }
}