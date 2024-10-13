package net.txsla.itemarchive.events;

import net.txsla.itemarchive.ItemArchive;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.EventListener;
public class clearInv implements Listener {
    @EventHandler
    public static void OnPlayerJoin(PlayerJoinEvent event) {
        // Clears players inventory upon join to prevent NBT bans
        event.getPlayer().getInventory().clear();
    }
}
