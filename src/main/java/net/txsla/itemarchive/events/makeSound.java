package net.txsla.itemarchive.events;
import net.txsla.itemarchive.ItemArchive;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class makeSound {
    private final ItemArchive plugin;
    public makeSound(ItemArchive plugin) {
        this.plugin = plugin;
    }
    public static void makeSound(String user, String sound, float volume, float pitch) {
        Player player = Bukkit.getPlayer(user);
        if (player != null) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }
}