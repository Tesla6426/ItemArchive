package net.txsla.itemarchive;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.txsla.itemarchive.ArchiveManager.CacheLookupTable;
import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class GuiManager {
    /*

       READ THIS BEFORE VIEWING CODE

    *   WARNING:
    *   Every bit of code in this file is complete garbage,
    *   do not copy any code from here as it was written at 3am
    *   and is incredibly inefficient and messy.

       This code will be completely rewritten in the future to
       iron out the multitude of issues and inefficiencies.

    */
    private final static GuiManager instance = new GuiManager();
    public static GuiManager getInstance() {return instance;}
    public static List<String> archiveOpened;
    private static YamlConfiguration config;
    private static File file;

    public void load() {
        // Loads archive theme file in archives folder
        file = new File(getPlugin(ItemArchive.class).getDataFolder(), "theme.yml");
        if (!file.exists()) getPlugin(ItemArchive.class).saveResource("theme.yml", false);
        config = new YamlConfiguration(); config.options().parseComments(true);
        try { config.load(file); } catch (Exception e) {e.printStackTrace();}
    }
    public static void save() { try { config.save(file); } catch (Exception e) {e.printStackTrace();} }
    public static void set(String path, String data) { try { config.set(path, data); save(); } catch (Exception e) {e.printStackTrace();}}
    public static void createDefaultArchiveTheme(String archive) {
        // Set config to default options
        setArchiveTheme(archive,"name", "wqc2QXJjaGl2ZcKnNw==");
        if (getPlugin(ItemArchive.class).getConfig().getString("item-version").matches("1")) {
            // Set default items for 1.21+
            setArchiveTheme(archive, "fillerItem", "aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzk1NQogIHR5cGU6IEdSQVlfU1RBSU5FRF9HTEFTU19QQU5FCiAgbWV0YToKICAgID09OiBJdGVtTWV0YQogICAgbWV0YS10eXBlOiBVTlNQRUNJRklDCiAgICBkaXNwbGF5LW5hbWU6ICciICInCg==");
            setArchiveTheme(archive, "labelItem", "aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzk1NQogIHR5cGU6IE9BS19TSUdOCiAgbWV0YToKICAgID09OiBJdGVtTWV0YQogICAgbWV0YS10eXBlOiBUSUxFX0VOVElUWQogICAgZGlzcGxheS1uYW1lOiAneyJ0ZXh0IjoiTGFiZWwgSXRlbSIsIml0YWxpYyI6ZmFsc2UsImNvbG9yIjoiZ29sZCJ9JwogICAgYmxvY2tNYXRlcmlhbDogT0FLX1NJR04K");
            setArchiveTheme(archive, "submitItem", "aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzk1NQogIHR5cGU6IEhPUFBFUl9NSU5FQ0FSVAogIG1ldGE6CiAgICA9PTogSXRlbU1ldGEKICAgIG1ldGEtdHlwZTogVU5TUEVDSUZJQwogICAgZGlzcGxheS1uYW1lOiAneyJ0ZXh0IjoiU3VibWl0IEl0ZW1zIiwiaXRhbGljIjpmYWxzZSwiY29sb3IiOiJhcXVhIn0nCg==");
            setArchiveTheme(archive, "nextItem", "aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzk1NQogIHR5cGU6IEFSUk9XCiAgbWV0YToKICAgID09OiBJdGVtTWV0YQogICAgbWV0YS10eXBlOiBVTlNQRUNJRklDCiAgICBkaXNwbGF5LW5hbWU6ICd7InRleHQiOiJOZXh0IFBhZ2UiLCJpdGFsaWMiOmZhbHNlLCJjb2xvciI6ImdyZWVuIn0nCg==");
            setArchiveTheme(archive, "prevItem", "aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzk1NQogIHR5cGU6IEFSUk9XCiAgbWV0YToKICAgID09OiBJdGVtTWV0YQogICAgbWV0YS10eXBlOiBVTlNQRUNJRklDCiAgICBkaXNwbGF5LW5hbWU6ICd7InRleHQiOiJQcmV2aW91cyBQYWdlIiwiaXRhbGljIjpmYWxzZSwiY29sb3IiOiJncmVlbiJ9Jwo=");
        }else if (getPlugin(ItemArchive.class).getConfig().getString("item-version").matches("0")) {
            // Set defaults for 1.20.4-
            setArchiveTheme(archive, "fillerItem", "aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzcwMAogIHR5cGU6IEdSQVlfU1RBSU5FRF9HTEFTU19QQU5FCiAgbWV0YToKICAgID09OiBJdGVtTWV0YQogICAgbWV0YS10eXBlOiBVTlNQRUNJRklDCiAgICBkaXNwbGF5LW5hbWU6ICd7InRleHQiOiIgIn0nCg==");
            setArchiveTheme(archive, "labelItem",  "aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzcwMAogIHR5cGU6IE9BS19TSUdOCiAgbWV0YToKICAgID09OiBJdGVtTWV0YQogICAgbWV0YS10eXBlOiBUSUxFX0VOVElUWQogICAgZGlzcGxheS1uYW1lOiAneyJ0ZXh0IjoiTGFiZWwgSXRlbSIsImNvbG9yIjoiZ29sZCIsIml0YWxpYyI6ZmFsc2V9JwogICAgYmxvY2tNYXRlcmlhbDogT0FLX1NJR04K");
            setArchiveTheme(archive, "submitItem", "aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzcwMAogIHR5cGU6IEhPUFBFUl9NSU5FQ0FSVAogIG1ldGE6CiAgICA9PTogSXRlbU1ldGEKICAgIG1ldGEtdHlwZTogVU5TUEVDSUZJQwogICAgZGlzcGxheS1uYW1lOiAneyJ0ZXh0IjoiU3VibWl0IEl0ZW1zIiwiY29sb3IiOiJhcXVhIiwiaXRhbGljIjpmYWxzZX0nCg==");
            setArchiveTheme(archive, "nextItem", "aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzcwMAogIHR5cGU6IEFSUk9XCiAgbWV0YToKICAgID09OiBJdGVtTWV0YQogICAgbWV0YS10eXBlOiBVTlNQRUNJRklDCiAgICBkaXNwbGF5LW5hbWU6ICd7InRleHQiOiJOZXh0IFBhZ2UiLCJjb2xvciI6ImdyZWVuIiwiaXRhbGljIjpmYWxzZX0nCg==");
            setArchiveTheme(archive, "prevItem", "aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzcwMAogIHR5cGU6IEFSUk9XCiAgbWV0YToKICAgID09OiBJdGVtTWV0YQogICAgbWV0YS10eXBlOiBVTlNQRUNJRklDCiAgICBkaXNwbGF5LW5hbWU6ICd7InRleHQiOiJQcmV2aW91cyBQYWdlIiwiY29sb3IiOiJncmVlbiIsIml0YWxpYyI6ZmFsc2V9Jwo=");
        }
        setArchiveTheme(archive, "isCustomGui", "false");
        setArchiveTheme(archive, "Layout", "default");
        save();
    }
    public static void setArchiveTheme(String archive, String themeType, String data) {
        // save archive theme to file
        set("archive"+"."+archive+"."+themeType, data);
        save();
    }
    public static String decode(String b64) {
        return new String(Base64.getDecoder().decode(b64));
    }
    public static void open(final Player p, final String archive, final int page) throws InvalidConfigurationException {

        Thread openPage = new Thread(()->{
            String[] Layout;
            // Load archive theme
            int itemIndex = 0; ItemStack filler, label, submit, next, prev;
            String name = decode(config.getString("archive." + archive + ".name"));
            try {
                filler = ItemConverter.toItemStack(decode(config.getString("archive." + archive + ".fillerItem")));
                label = ItemConverter.toItemStack(decode(config.getString("archive." + archive + ".labelItem")));
                submit = ItemConverter.toItemStack(decode(config.getString("archive." + archive + ".submitItem")));
                next = ItemConverter.toItemStack(decode(config.getString("archive." + archive + ".nextItem")));
                prev = ItemConverter.toItemStack(decode(config.getString("archive." + archive + ".prevItem")));
            } catch (Exception e) { throw new RuntimeException(e); } // not sure why try/catch is neccessary
            String isCustom = config.getString("archive." + archive + ".isCustomGui");
            String LayoutAsString = decode(config.getString("archive." + archive + ".Layout"));
            Inventory archivePage = Bukkit.createInventory(p, 54, name + " page." + page);

            // Load default layout
            // I promise this is the most efficient way to do this :)
            Layout = ("36 fil fil fil fil lab fil fil fil fil" +
                    " itm itm itm itm itm itm itm itm itm" +
                    " itm itm itm itm itm itm itm itm itm" +
                    " itm itm itm itm itm itm itm itm itm" +
                    " itm itm itm itm itm itm itm itm itm" +
                    " prv sub fil fil fil fil fil fil nex").split(" ");

            // load custom layout if custom
            if (isCustom.matches("true")) Layout = LayoutAsString.split(" ");

            // I promise this is the most optimal way to build an inv
            ItemStack item = new ItemStack(Material.AIR, 1);

            // First item in array is number of archive items to retrieve
            itemIndex = (Integer.parseInt(Layout[0]) * page);
            for (int s = 1; 55 > s; s++) {
                // I pinky promise this is the best way
                switch (Layout[s]) {
                    case "fil":
                        item = filler;
                        break;
                    case "lab":
                        item = label;
                        break;
                    case "nex":
                        item = next;
                        break;
                    case "prv":
                        item = prev;
                        break;
                    case "sub":
                        item = submit;
                        break;
                    case "itm":
                        try {
                            item = ItemConverter.toItemStack(ArchiveManager.getItem(archive, itemIndex));
                        } catch (InvalidConfigurationException e) {
                            item = new ItemStack(Material.AIR, 1);
                        }
                        itemIndex++;
                        break;
                }
                // Add item to archive
                archivePage.setItem(s - 1, item);
            }
            playerGuiTracker(p, archive);

            //end thread

            // this line is really fucked - built-in console spammer :)     [   remember to fix later    ]
                Bukkit.getScheduler().runTask(getPlugin(ItemArchive.class), () -> Bukkit.getPluginManager().callEvent(syncPlayerInv(p, archivePage)));
        });
        openPage.start();
    }
public static Event syncPlayerInv(Player p, Inventory inv) {
    p.openInventory(inv);
    return null;
}

    public static void playerGuiTracker(Player p, String archiveMenu) {
        // ik bukkit has some built-in features that offer similar functionality, however I do not particularly enjoy bukkit's api
        // if archive == submit, then it is the submit page, DO NOT NAME AN ARCHIVE submit.*

        // Check if player index is null
        if (archiveOpened == null) archiveOpened = new ArrayList<>();

        // check if player is already cached
        int index = archiveOpened.indexOf(p.toString());

        // if player is not found in tracker, add data to the end of the list
        if (index == -1) {
            archiveOpened.addLast(p.toString());
            archiveOpened.addLast(archiveMenu);
            return;
        }
        // if player is already in tracker, then update tracked gui
        archiveOpened.set(index+1, archiveMenu);
        // player+1 = gui
    }
    public static String getPlayerArchive(Player p) {
        // returns player;s current opened archive

        // get index of player in tracker
        int index = archiveOpened.indexOf(p.toString());

        // returns player's current gui info if found in list
        if (index > -1) {
            return archiveOpened.get(index+1);
        }

        // if no gui is found, return null
        return null;
    }
    public static void searchItem(Player p, String archive, String query) {
        playerGuiTracker(p, archive);
        Thread searchArchive = new Thread(()->{
            int index = CacheLookupTable.indexOf(archive);
            // get placeholders
            ItemStack filler, label;
            try {
                filler = ItemConverter.toItemStack(decode(config.getString("archive." + archive + ".fillerItem")));
                label = ItemConverter.toItemStack(decode(config.getString("archive." + archive + ".labelItem")));
            } catch (Exception e) { throw new RuntimeException(e); }

            // create inventory
            String title = decode(config.getString("archive." + archive + ".name")) + "§7 search: §8" + query;
            Inventory searchResults = Bukkit.createInventory(p, 54, title);
            // I promise this is efficient :)
            searchResults.setItem(0, filler);searchResults.setItem(1, filler);
            searchResults.setItem(2, filler);searchResults.setItem(3, filler);
            searchResults.setItem(4, label);
            searchResults.setItem(5, filler);searchResults.setItem(6, filler);
            searchResults.setItem(7, filler);searchResults.setItem(8, filler);

            int numFound = 0;
            String item;
            // search archive for matching items
            for (int x = ArchiveManager.ArchiveCache[index].length; x > 0; x--) {
                if (ArchiveManager.ArchiveCache[index][x-1][0].matches(query)) {
                    // add found item to inventory
                    try { searchResults.setItem(numFound+9 ,  ItemConverter.toItemStack(decode(ArchiveManager.ArchiveCache[index][x-1][5])) ); } catch (InvalidConfigurationException e) {throw new RuntimeException(e);}
                    numFound++;
                }
                if (numFound > 44) break;
            }

            // display inventory
            Bukkit.getScheduler().runTask(getPlugin(ItemArchive.class), () -> Bukkit.getPluginManager().callEvent(syncPlayerInv(p, searchResults)));
        });
        searchArchive.start();
    }
    public static void submitMenu(Player p, String archive) {
        playerGuiTracker(p, archive);
        Thread submitMenu = new Thread(()-> {
            int index = CacheLookupTable.indexOf(archive);

            ItemStack filler, label, blank, submitItems, backToArchive, submitItemsGlass, backToArchivesGlass;
            try {
                filler = ItemConverter.toItemStack(decode(config.getString("archive." + archive + ".fillerItem")));
                label = ItemConverter.toItemStack(decode(config.getString("archive." + archive + ".labelItem")));

                // add 1.20.4 compatibility later
                    submitItems = ItemConverter.toItemStack(decode("aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzk1NQogIHR5cGU6IEVNRVJBTEQKICBtZXRhOgogICAgPT06IEl0ZW1NZXRhCiAgICBtZXRhLXR5cGU6IFVOU1BFQ0lGSUMKICAgIGRpc3BsYXktbmFtZTogJ3sidGV4dCI6IlsgU3VibWl0IEl0ZW1zIF0iLCJpdGFsaWMiOmZhbHNlLCJjb2xvciI6ImdyZWVuIn0nCg=="));
                    backToArchive = ItemConverter.toItemStack(decode("aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzk1NQogIHR5cGU6IENIRVNUCiAgbWV0YToKICAgID09OiBJdGVtTWV0YQogICAgbWV0YS10eXBlOiBUSUxFX0VOVElUWQogICAgZGlzcGxheS1uYW1lOiAneyJ0ZXh0IjoiWyBCYWNrIHRvIEFyY2hpdmUgXSIsIml0YWxpYyI6ZmFsc2UsImNvbG9yIjoicmVkIn0nCiAgICBibG9ja01hdGVyaWFsOiBDSEVTVAo="));
                    blank = ItemConverter.toItemStack(decode("aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzk1NQogIHR5cGU6IExJR0hUX0dSQVlfU1RBSU5FRF9HTEFTU19QQU5FCiAgbWV0YToKICAgID09OiBJdGVtTWV0YQogICAgbWV0YS10eXBlOiBVTlNQRUNJRklDCiAgICBkaXNwbGF5LW5hbWU6ICciICInCg=="));
                    submitItemsGlass = ItemConverter.toItemStack(decode("aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzk1NQogIHR5cGU6IExJTUVfU1RBSU5FRF9HTEFTU19QQU5FCiAgbWV0YToKICAgID09OiBJdGVtTWV0YQogICAgbWV0YS10eXBlOiBVTlNQRUNJRklDCiAgICBkaXNwbGF5LW5hbWU6ICd7InRleHQiOiJbIFN1Ym1pdCBJdGVtcyBdIiwiaXRhbGljIjpmYWxzZSwiY29sb3IiOiJncmVlbiJ9Jwo="));
                    backToArchivesGlass = ItemConverter.toItemStack(decode("aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzk1NQogIHR5cGU6IFJFRF9TVEFJTkVEX0dMQVNTX1BBTkUKICBtZXRhOgogICAgPT06IEl0ZW1NZXRhCiAgICBtZXRhLXR5cGU6IFVOU1BFQ0lGSUMKICAgIGRpc3BsYXktbmFtZTogJ3sidGV4dCI6IlsgQmFjayB0byBBcmNoaXZlIF0iLCJpdGFsaWMiOmZhbHNlLCJjb2xvciI6InJlZCJ9Jwo="));

                    //if 1,20,4, then revert items
                if (getPlugin(ItemArchive.class).getConfig().getString("item-version").matches("0")) {
                    submitItems = ItemConverter.toItemStack(decode("aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzcwMAogIHR5cGU6IEVNRVJBTEQKICBtZXRhOgogICAgPT06IEl0ZW1NZXRhCiAgICBtZXRhLXR5cGU6IFVOU1BFQ0lGSUMKICAgIGRpc3BsYXktbmFtZTogJ3sidGV4dCI6IlsgU3VibWl0IEl0ZW1zIF0iLCJjb2xvciI6ImdyZWVuIiwiaXRhbGljIjpmYWxzZX0nCg=="));
                    backToArchive = ItemConverter.toItemStack(decode("aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzcwMAogIHR5cGU6IENIRVNUCiAgbWV0YToKICAgID09OiBJdGVtTWV0YQogICAgbWV0YS10eXBlOiBUSUxFX0VOVElUWQogICAgZGlzcGxheS1uYW1lOiAneyJ0ZXh0IjoiWyBCYWNrIHRvIEFyY2hpdmUgXSIsImNvbG9yIjoicmVkIiwiaXRhbGljIjpmYWxzZX0nCiAgICBibG9ja01hdGVyaWFsOiBDSEVTVAo="));
                    blank = ItemConverter.toItemStack(decode("aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzcwMAogIHR5cGU6IExJR0hUX0dSQVlfU1RBSU5FRF9HTEFTU19QQU5FCiAgbWV0YToKICAgID09OiBJdGVtTWV0YQogICAgbWV0YS10eXBlOiBVTlNQRUNJRklDCiAgICBkaXNwbGF5LW5hbWU6ICd7InRleHQiOiIgIn0nCg=="));
                    submitItemsGlass = ItemConverter.toItemStack(decode("aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzcwMAogIHR5cGU6IEdSRUVOX1NUQUlORURfR0xBU1NfUEFORQogIG1ldGE6CiAgICA9PTogSXRlbU1ldGEKICAgIG1ldGEtdHlwZTogVU5TUEVDSUZJQwogICAgZGlzcGxheS1uYW1lOiAneyJ0ZXh0IjoiWyBTdWJtaXQgSXRlbXMgXSIsImNvbG9yIjoiZ3JlZW4iLCJpdGFsaWMiOmZhbHNlfScK"));
                    backToArchivesGlass = ItemConverter.toItemStack(decode("aXRlbToKICA9PTogb3JnLmJ1a2tpdC5pbnZlbnRvcnkuSXRlbVN0YWNrCiAgdjogMzcwMAogIHR5cGU6IFJFRF9TVEFJTkVEX0dMQVNTX1BBTkUKICBtZXRhOgogICAgPT06IEl0ZW1NZXRhCiAgICBtZXRhLXR5cGU6IFVOU1BFQ0lGSUMKICAgIGRpc3BsYXktbmFtZTogJ3sidGV4dCI6IlsgQmFjayB0byBBcmNoaXZlIF0iLCJjb2xvciI6InJlZCIsIml0YWxpYyI6ZmFsc2V9Jwo="));
                }
            } catch (Exception e) { throw new RuntimeException(e); }

            Inventory submitInv = Bukkit.createInventory(p, 36, decode(config.getString("archive." + archive + ".name")) + "§7 submit: ");
            // I promise this is efficient :)

            // top row
            submitInv.setItem(0, filler); submitInv.setItem(1, filler); submitInv.setItem(2, filler); submitInv.setItem(3, filler);
            submitInv.setItem(4, label);
            submitInv.setItem(5, filler); submitInv.setItem(6, filler); submitInv.setItem(7, filler); submitInv.setItem(8, filler);

            // item submit grid margins
            submitInv.setItem(9, blank); submitInv.setItem(10, blank);
            submitInv.setItem(16, blank); submitInv.setItem(17, blank);
            submitInv.setItem(18, blank); submitInv.setItem(19, blank);
            submitInv.setItem(25, blank); submitInv.setItem(26, blank);

            // back button
            submitInv.setItem(27, backToArchivesGlass);
            submitInv.setItem(28, backToArchive);
            submitInv.setItem(29, backToArchivesGlass);

            //bottom row fill
            submitInv.setItem(30, filler); submitInv.setItem(31, filler); submitInv.setItem(32, filler);

            // submit button
            submitInv.setItem(33, submitItemsGlass);
            submitInv.setItem(34, submitItems);
            submitInv.setItem(35, submitItemsGlass);

            Bukkit.getScheduler().runTask(getPlugin(ItemArchive.class), () -> Bukkit.getPluginManager().callEvent(syncPlayerInv(p, submitInv)));
        });
        submitMenu.start();
    }

}
