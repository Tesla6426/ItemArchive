package net.txsla.itemarchive.commands;
import net.txsla.itemarchive.GuiManager;
import net.txsla.itemarchive.ItemArchive;
import net.txsla.itemarchive.ArchiveManager;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;

import static java.lang.Integer.parseInt;
import static java.lang.System.currentTimeMillis;

public class submit implements CommandExecutor , TabExecutor {
    private final ItemArchive plugin;
    public submit(ItemArchive plugin) {
        this.plugin = plugin;
    }
    public static List<String> timeIndex;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        //check if submissions are enabled
        if (!ArchiveManager.submissionsEnabled) {sender.sendMessage("§cOops! Submissions failed"); return true;}

        Player p = (Player) sender;

        // use the damn command correctly
        if (args.length < 1) return false;

        // make sure the archive exists
        if (ArchiveManager.getArchiveList().stream().noneMatch(args[0]::contains)) {
            sender.sendMessage("§cArchive " + args[0] + " does not exist"); return true;
        }

        // check if player is submit-banned
        if (plugin.getConfig().getStringList("submission.banned-players").stream().anyMatch( sender.toString().replaceAll("[}{]", "").replaceAll("CraftPlayername=","")::contains)) {
            // ghost banned regard
            sender.sendMessage("§aItems Submitted");
            return true;
        }

        //double check so no exploits :)
        if (!ArchiveManager.submissionsEnabled) {sender.sendMessage("§cOops! Submissions failed"); return true;}


        // submit items :)
        // ArchiveManager.submitItems( args[0], p.toString().replaceAll("[}{]", "").replaceAll("CraftPlayername=",""), plugin.getConfig().getString("item-version"), items );
        GuiManager.submitMenu(p, args[0]);
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return ArchiveManager.getArchiveList();
    }
}
