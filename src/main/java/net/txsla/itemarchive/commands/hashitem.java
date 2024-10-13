package net.txsla.itemarchive.commands;
import net.txsla.itemarchive.ArchiveManager;
import net.txsla.itemarchive.ItemArchive;
import org.apache.commons.lang3.arch.Processor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
public class hashitem implements CommandExecutor , TabExecutor {
    private final ItemArchive plugin;
    public hashitem(ItemArchive plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
    {
        Player p = (Player) sender;
        ItemStack item = p.getInventory().getItemInMainHand();
        item.setAmount(1);
        if (item.getType().toString().toLowerCase().matches("air")) sender.sendMessage("§cYou must be holding an item");
            else sender.sendMessage("§dhash: " + ArchiveManager.hash(item.toString()));
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String str, @NotNull String[] args) {
        return null;
    }
}