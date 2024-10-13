package net.txsla.itemarchive.events;

import net.txsla.itemarchive.ArchiveManager;
import net.txsla.itemarchive.GuiManager;
import net.txsla.itemarchive.ItemConverter;
import net.txsla.itemarchive.ItemArchive;
import net.txsla.itemarchive.commands.submit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;

import static java.lang.Integer.parseInt;
import static java.lang.System.currentTimeMillis;
import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class GuiListener implements Listener {
    private final ItemArchive plugin;
    private static YamlConfiguration config;
    private static File file;
    private final String[] placeholders = new String[]{"fillerItem", "labelItem", "submitItem", "nextItem", "prevItem"};
    public GuiListener(ItemArchive plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void OnClick(InventoryClickEvent event) throws InvalidConfigurationException {
        // I should really rewrite this entire class, the code is a hot mess

        // some bs vars, dw about this
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem(), air = new ItemStack( Material.AIR , 1);
        ItemStack[] submittedItems = new ItemStack[10];
        String item = clicked + "", gui, place = "air";

        // ignore inventory
        //if (player.getOpenInventory().getTitle().equals("Crafting")) return;

        // ignore if error ( I promise this is best practice :) )
        try { gui = GuiManager.getPlayerArchive(player); } catch (Exception e) { return; }

        // get player's current gui
        int page;
        assert gui != null;
        // regex magic to get page number (if applicable)
        try { page = Integer.parseInt( player.getOpenInventory().getTitle().replaceAll(".*[.]", "") ); }
        catch (Exception e) { page = 0; }

        // get data of item clicked (if configurable item)
        for (String placeholder : placeholders) {
            if (ArchiveManager.hash(getPlaceholder(gui, placeholder) + "").equals(ArchiveManager.hash(item))) {
                place = placeholder;
                break;
            }
        }

        // check hashes for submit menu
        switch (ArchiveManager.hash(item)) {
            case "d3ccd0c1ab74c6dbc89bd4c590caa45e5d857119a3b271cd6805dda4b6cd0d69":
            case "bd1ea7999c8de15f2106041d0b15240eda17dd59563768fb6328498b7a8152e1":
                place = "blank";
                break;
            case "2c45f30c1e53a555f0b15f14dca870c68f0125fada9f1eee90d52c1b434cadc9":
            case "423205e24f1f179efc030dccb4c11c0155afb7cd5dd823b6e101d2ecb7d65846":
            case "423b0e7d26d689f796145e161dab5f554020156d4383e0c13a0490305fcd141c":
            case "776d1e800d54a00fa3c3d9b3008a6cb3c31ead0e2c591b1edca39969c4d931d7":
                place = "backToArchiveButton";
                break;
            case "bf668f649cbf80e548d434485b8c7de79521bf4620026a2673e506f4e13ae7d7":
            case "0bc32217aad2ee23b03c07703c9e968ca9955e8d22d0b82e43d0703d8c400a86":
            case "7bf9448428a78cc98b1245a28cb7384e50ea84299b0d7a5516affe31faabf139":
            case "3401eb161ff2d7fdc12fa77d4d5f431c7ef9bfe50b936c20f26472c7e1b73749":
                place = "submitItemsButton";
                break;
        }

        switch (place) {
            case "fillerItem":
            case "labelItem":
            case "blank":
                event.setCancelled(true);
                break;
            case "submitItem":
                event.setCancelled(true);
                player.closeInventory();
                player.chat("/submit " + gui );
                break;
            case "nextItem":
                event.setCancelled(true);
                player.chat("/open " + gui + " " + ((int)page+1) );
                break;
            case "prevItem":
                event.setCancelled(true);
                if (page <= 0)  break;
                player.chat("/open " + gui + " " + ((int)page-1) );
                break;
            case "backToArchiveButton":
                event.setCancelled(true);
                player.chat("/open " + gui);
                break;
            case "submitItemsButton":
                event.setCancelled(true);

                // check submission delay
                if (submit.timeIndex == null) submit.timeIndex = new ArrayList<>();
                int time = (int) currentTimeMillis(), delay = plugin.getConfig().getInt("submission.delay");
                if (!submit.timeIndex.contains(player.toString())) { submit.timeIndex.addLast( player.toString()); submit.timeIndex.addLast( "" + ((int) (time - 10000)) );}
                int lastRequest = parseInt(submit.timeIndex.get(submit.timeIndex.indexOf(player.toString())+1));
                if ((time-lastRequest)<delay) { player.sendMessage("§aPlease wait " + ((float)(delay-(time-lastRequest))/1000) + " seconds before submitting more items" ); break;}
                submit.timeIndex.set(submit.timeIndex.indexOf(player.toString())+1, "" + time );

                // get items from gui
                int i = 10;
                for (int x = 24; x >= 11; x--) {
                    if (x == 19) x = 15;
                    i--;
                    // submit air if item is null
                    if (player.getOpenInventory().getItem(x) == null) {
                        submittedItems[i] = air;
                    }else { submittedItems[i] = player.getOpenInventory().getItem(x); }
                }

                // submit items
                ArchiveManager.submitItems(gui, player.toString().replaceAll("[}{]", "").replaceAll("CraftPlayername=",""), plugin.getConfig().getString("item-version"), submittedItems);

                // close submission menu
                player.getOpenInventory().close();
                break;
            default:
                return;
                }
    }
    @EventHandler
    public void OnClose(InventoryCloseEvent event) {
        /*

        WARNING: known bug/vuln in GuiTracker

         players who contain the same chars as another players name will interfere

        ex: player 'Steve', will interfere with the player 'Steve2770'
        probability of this happening in the wild should be low enough for me to ignore this for now
        might fix later idk

        also applies to archive caching, but probability of this occurring ion the wild is very low
        ( I will probably be the only person ever using this plugin )

        If this plugin becomes remotely popular I will fix these issues

        */
    }
    public static ItemStack getPlaceholder(String archive, String placeholder) throws InvalidConfigurationException {
        ItemStack item = new ItemStack(Material.AIR,1);

        //load config in case somehow miraculously a quantum particle caused a bit flip in RAM exactly before this method was called
        file = new File(getPlugin(ItemArchive.class).getDataFolder(), "theme.yml");
        if (!file.exists()) getPlugin(ItemArchive.class).saveResource("theme.yml", false);
        config = new YamlConfiguration(); config.options().parseComments(true);
        try { config.load(file); } catch (Exception e) {e.printStackTrace();}

        // get item from config
        item = ItemConverter.toItemStack( new String( Base64.getDecoder().decode( config.getString("archive." + archive + "." + placeholder))));

        // this code is genuinely horrible, remind me to rewrite this entire class as well as GuiManager
        return item;
    }
}
