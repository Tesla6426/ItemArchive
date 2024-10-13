package net.txsla.itemarchive.commands;
import net.txsla.itemarchive.ArchiveManager;
import net.txsla.itemarchive.ItemArchive;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
public class removeitem implements CommandExecutor , TabExecutor {
    private final ItemArchive plugin;
    public removeitem(ItemArchive plugin) {
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

        Player p = (Player) sender;

        int removeType = 0;
        String data;
        switch (args[1]) {
            case "item_name":
                data = args[2];
                break;
            case "item_in_hand":
                data = p.getInventory().getItemInMainHand().toString();
                removeType = 1;
                break;
            case "player":
                removeType = 2;
                break;
            case "game_version":
                removeType = 3;
                 break;
            case "hash":
                removeType = 4;
                break;
            default:
                return false;
        }

        // Format data for in-hand
        ItemStack item = p.getInventory().getItemInMainHand();
        item.setAmount(1);
        if (removeType == 1) data = item.toString();
            else data = args[2];

        // Remove items
        ArchiveManager.removeItem(args[0], removeType, data, p);

        return true;
    }
    public static void tellPlayerHowManyItemsHaveBeenRemoved(int removed, String archive, Player player) {
        player.sendMessage("§aRemoved " + removed + " items from " + archive);
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 1:
                return ArchiveManager.getArchiveList();
            case 2:
                list.add("item_name");
                list.add("item_in_hand");
                list.add("player");
                list.add("hash");
                return list;
        }
        return null;
    }
}
