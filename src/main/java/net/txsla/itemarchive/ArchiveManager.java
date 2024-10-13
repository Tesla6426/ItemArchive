package net.txsla.itemarchive;

import net.txsla.itemarchive.commands.removeitem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.security.MessageDigest;

public class ArchiveManager {
    public static String[][][] ArchiveCache; // Used to cache archive data
    public static List<String> CacheLookupTable; // Used to find index of archive
    public static boolean submissionsEnabled; // Self-explanatory
    private static ItemArchive plugin; // :)
    public static String archiveDirectory = System.getProperty("user.dir") + File.separator + "archives"; // directory for all archive files
    public static String sep = "¦", beg = " "; // formatting for '.ar' files
    public ArchiveManager(ItemArchive plugin) { this.plugin = plugin; }
    public static String createArchive(String archive) {
        // Creates an archive file
        File file = new File(archiveDirectory + File.separator + archive + ".ar");
        // Sets Archive theme to default
        GuiManager.createDefaultArchiveTheme(archive);

        // Cache archive
        cacheArchive(archive);

        // Provide user feedback
        try { if (file.createNewFile()) {
            return "§aArchive created!";
        } else { return "§cArchive already exists.";}} catch (IOException e) {
            e.printStackTrace(); return "§4An error occurred!"; }

    }
    public static List<String> getArchiveList() {
        List<String> archives = new ArrayList<>();
        File folder = new File(archiveDirectory);
        File[] files = folder.listFiles();

        // some linux/windows compatibility bs
        String fileSeparatorForRegexMatch = File.separator;
        if (fileSeparatorForRegexMatch.equals("\\")) {fileSeparatorForRegexMatch = "\\\\";}

        // Searches archive folder for all archives
        if (files == null) {return archives;}
        for (int i = files.length-1; i >= 0; i--) {
            if ( files[i].toString().matches(".*[.]ar$") ) archives.add(files[i].toString().replaceAll( (".*" + fileSeparatorForRegexMatch) , "").replaceAll("[.]ar$", ""));
        }
        return archives;
    }
    public static void submitItems(String archive, String player,String version, ItemStack[] items) {
        // open thread for async writing
        Player p = Bukkit.getPlayer(player);
        p.sendMessage("§aSubmitting items to" + archive + "§a!");
        Thread submitThread = new Thread(()->{

            //declare some bullshit
            Base64.Encoder encoder = Base64.getEncoder();
            String entry, hashed, item_name, item_type, serial = "", allEntries = "";
            boolean isDuplicate;
            int archiveIndex = CacheLookupTable.indexOf(archive), submitted = 0,
                    minNBTSize = plugin.getConfig().getInt("submission.min-nbt-size"),
                    maxNBTSize = plugin.getConfig().getInt("submission.max-nbt-size");
            //recursively check through every item submitted
            for (int i = items.length-1; i >= 0; i--) {
                //Process item and set amount to zero
                ItemStack item = items[i];
                item.setAmount(1);

                //get hash
                hashed = hash(item.toString());

                //
                // ADD ITEM DUPLICATE CHECK BETWEEN SUBMITTED ITEMS
                //

                // Decide if you want to skip an item
                isDuplicate = false;
                for (int n = ArchiveCache[archiveIndex].length; n > 0; n--) {
                    // Check if item is a duplicate
                    if (ArchiveCache[archiveIndex][n-1][4].matches(hashed)) {
                        plugin.getLogger().info("Rejecting Item for isDuplicate: " + hashed);
                        isDuplicate = true; break;
                    }
                }

                // Check item size
                if (!((getNBTSize(item)>minNBTSize)&&(getNBTSize(item)<maxNBTSize))) {
                    isDuplicate = true;
                    if (!(getNBTSize(item)>minNBTSize)) {
                        plugin.getLogger().info("Rejecting Item for Too Small: " + getNBTSize(item));
                    }else {
                        plugin.getLogger().info("Rejecting Item for Too Large: " + getNBTSize(item));
                    }
                }

                // if no metadata then terminate submission (usually air)
                if (item.getItemMeta() == null) {
                    plugin.getLogger().info("Rejecting Item for No Metadata");
                    isDuplicate = true;}

                // skips item if duplicate is found
                // lil try/catch for weird bug
                try {
                if (!isDuplicate) {
                    //get item type and name + sanitise
                    item_type = item.getType().toString();

                    if (item.getItemMeta().displayName() != null)
                        item_name = item.getItemMeta().getDisplayName().replaceAll("§.", "").replaceAll("[¦ \n]*", "");
                    else item_name = "unnamed-item";

                    // Limit item name size (for file/search, not item data)
                    if (item_name.length() > 82) item_name = item_name.substring(0, 80);

                    // serialise item data
                    try {
                        serial = ItemConverter.toString(item);
                    } catch (InvalidConfigurationException e) {
                        throw new RuntimeException(e);
                    }

                    // format item for upload
                    entry = item_name + sep + item_type + sep + player + sep + version + sep + hashed + sep + encoder.encodeToString(serial.getBytes()) + sep; // add data compression later?

                    // add items
                    allEntries += beg + entry + "\n";

                    // log item
                    plugin.getLogger().info(player + " submitted an item!");

                    // counts items
                    submitted++;
                    } }catch (Exception e) { plugin.getLogger().warning("ERROR SUBMITTING ITEM : " + e); }
                }

            // Attempt to save file 3 times (in case 2 people submit concurrently)
            for (int x = 3; x > 0; x--) {
                try {
                    // write items to file
                    plugin.getLogger().info("writing " + player + "'s items to archive: ");

                    BufferedWriter out = new BufferedWriter(new FileWriter(archiveDirectory + File.separator + archive + ".ar", true));
                    out.write(allEntries);
                    out.close();
                    break;
                } catch (IOException e) {
                    plugin.getLogger().warning(e.toString());
                    plugin.getLogger().info("submission tries: " + x );
                }
            }

            // load new archive
            plugin.getLogger().info("caching archive");
            cacheArchive(archive);


            p.sendMessage("§a" + submitted + " Items Submitted to " + archive + "§a!");
            plugin.getLogger().info(player + " submitted " + submitted + " items!" );
        });
        submitThread.start();
    }
    public static String getItem(String archive, int itemIndex) throws InvalidConfigurationException {
        //get archive index
        int index = CacheLookupTable.indexOf(archive);

        // return air if item is outside of index
        int cacheLength = ArchiveCache[index].length;
        if (itemIndex > cacheLength-1) return new ItemConverter().toString( new ItemStack(Material.AIR,1) );

        // return item
        try {
            return new String(Base64.getDecoder().decode(ArchiveCache[index][itemIndex][5]));
        }catch (Exception e) {
            return new ItemStack(Material.AIR, 1).toString();
        }
    }
    static boolean isStopped = false;
    public static void load(String archive) {
        // returns a matrix of data in from archive

        Thread loadArchive = new Thread(()-> {
        int n, index = CacheLookupTable.indexOf(archive); String data = "", item, hold;

        // get all data in archive as a string
            try {
                data = new String(Files.readAllBytes(Paths.get(archiveDirectory + File.separator + archive + ".ar")), StandardCharsets.UTF_8);
            }catch (IOException e) { e.printStackTrace(); }
        n = StringUtils.countMatches(data, "\n");

            // Make sure CacheTable isn't null
            plugin.getLogger().info("[ItemArchive] loading archive : " + archive);
            ArchiveCache[index] = new String[n-1][6];

        //parse each item into the array
        // ADD MULTITHREADING SUPPORT LATER
        plugin.getLogger().info("[ItemArchive] scanning items : " + archive);
        long startTime = System.currentTimeMillis(), score;

            for (int i = 0; i < n - 1; i++) {
                // read items from last to first
                // single out the current parsed item
                item = data.substring(data.lastIndexOf(beg) + 1, data.length());

                // removes current item from list
                data = data.substring(0, data.lastIndexOf(beg) - 1);
                //loads item data into matrix
                for (int y = 0; y < 6; y++) {
                    hold = item.substring(0, item.indexOf("¦"));
                    //matrix[i][y] = hold.replaceAll("¦", "");
                    // Procedural writing?
                    ArchiveCache[index][i][y] = hold.replaceAll("¦", "");
                    item = item.substring(item.indexOf("¦") + 1, item.length());
                }
                if (isStopped) {
                    data = null;
                    plugin.getLogger().info("[ItemArchive] Terminating Load Thread Early (Interrupted?) : " + archive);
                    return;
                }
            }
            data = null;

            //Save data directly to array
            //ArchiveCache[CacheLookupTable.indexOf(archive)] = matrix;

            score = System.currentTimeMillis() - startTime;
            plugin.getLogger().info("[ItemArchive] Load Speed for " + archive + ": " + score);
            System.gc();
        });
        // Restart loading to avoid loading the archive multiple times at the same time
        if (loadArchive.isAlive()) {
            plugin.getLogger().info("[ItemArchive] Reloading Archive - Killing Current Running Thread : " + archive);
            loadArchive.interrupt();
            isStopped = true;
        }
        plugin.getLogger().info("Starting Archive Load Thread : " + archive);
        loadArchive.start();

        // Garbage Collect items set to null
        System.gc();
    }
public static void cacheArchive(String archive) {
    int index = -1;
    // check to make sure archive exists
    if (ArchiveManager.getArchiveList().stream().anyMatch(archive::contains)) {

        // check/fix if table is null and read archive cache table for index of current archive
        if (CacheLookupTable != null) index = CacheLookupTable.indexOf(archive);
        else CacheLookupTable = new ArrayList<>();

        // check if archive is already cached, then reload it
        if (index != -1) {
            load(archive);
            return;
        }

        // if archive is not cached, then append it to the lookup table and add it to the cache
        CacheLookupTable.addLast(archive);
        load(archive);
    }
}
    public static void removeItem(String archive, int removeTypeIn, String dataIn, Player p) {
        final File tempFile = new File(archiveDirectory + File.separator + archive + ".ar.temp");
        final File oldFile = new File(archiveDirectory + File.separator + archive + ".ar");
    // 0 = item_name, 1 = item_in_hand, 2 = player, 3 = game_version, 4 = hash
    // after hashing, 1 == 3

        // Temporarily disable submissions
        plugin.getLogger().info("(TEMP) submissions disabled");
        submissionsEnabled = false;

        // reload archive (should already be cached)
        //plugin.getLogger().info("caching archive");
        //cacheArchive(archive);

        // get current cache info
        final int index = CacheLookupTable.indexOf(archive);
        final int cacheLength = ArchiveCache[index].length;

        // make item_in_hand equivalent to hash
        plugin.getLogger().info("finding item");
        if (removeTypeIn == 1) {
            dataIn = hash(dataIn);
            removeTypeIn = 4;
        }
        final String data = dataIn;
        final int removeType = removeTypeIn;

        // add all items the DO NOT match the remove descriptor to the temp archive
                Thread rebuildArchive = new Thread(()->{
                    StringBuilder tempArchive = new StringBuilder("");

                    plugin.getLogger().info("scanning items into string from cache");
                    for (int x = cacheLength-1; x > 0; x--) {
                        // Check if item does NOT match the remove descriptor
                        if (!ArchiveCache[index][x][removeType].matches(data))
                        {
                            // add item to tempArchive
                            tempArchive.append(beg);
                            for (int i = 0; i < 6; i++) tempArchive.append(ArchiveCache[index][x][i] + sep);
                            tempArchive.append("\n");
                        }
                    }
                    plugin.getLogger().info("writing new stored archive to file");

                    try {
                        BufferedWriter out = new BufferedWriter(new FileWriter(tempFile, true)); // add multi archive compatibility later
                        out.write(tempArchive.toString() );
                        out.close();
                    } catch (IOException e) { plugin.getLogger().warning(e.toString()); }
                    plugin.getLogger().info("Temp archive file created" );

                    // Delete old archive file and rename temp archive file
                    if (oldFile.delete()) tempFile.renameTo(oldFile);
                    else plugin.getLogger().severe("FAILED TO DELETE OLD ARCHIVE FILE : ITEM REMOVAL FAILED?");

                    // reload archive
                    cacheArchive(archive);

                    // Reset submissions
                    submissionsEnabled = plugin.getConfig().getBoolean("submission.allowSubmissions");
                    // clear from mem
                    tempArchive.delete(0,tempArchive.length());
                    // tell player how many items have been removed
                    removeitem.tellPlayerHowManyItemsHaveBeenRemoved(cacheLength - ArchiveCache[index].length, archive, p);
                });
        rebuildArchive.start();
        return;
    }
    public static String findSubmitter(String archive, String hashed) {
        int index = CacheLookupTable.indexOf(archive);
        int num = ArchiveCache[index].length;
        for (int i = 0; i < num-1; i++ ) {
             if (ArchiveCache[index][i][4].matches(hashed)) return ArchiveCache[index][i][2];
        }
        return null;
    }
    public static int getNBTSize(ItemStack item) {
        return item.toString().getBytes().length;
    }
    public static String hash(final String input) {
        //hopefully this one is self-explanatory
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
