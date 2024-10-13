package net.txsla.itemarchive.commands;
import net.txsla.itemarchive.ArchiveManager;
import net.txsla.itemarchive.ItemArchive;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
public class itemsize implements CommandExecutor , TabExecutor {
    private final ItemArchive plugin;
    public itemsize(ItemArchive plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        // Gets item data size, NOT NBT SIZE
        Player p = (Player) sender;
        ItemStack item = p.getInventory().getItemInMainHand();
        float size = (float) ArchiveManager.getNBTSize(item) / 1000;
        String itemName = item.getItemMeta().getDisplayName();
        if (itemName.matches("")) itemName = item.getType().toString();
        sender.sendMessage("§dItem " + itemName + "§d is " + size + "kb");
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String str, @NotNull String[] args) {
        return null;
    }
}