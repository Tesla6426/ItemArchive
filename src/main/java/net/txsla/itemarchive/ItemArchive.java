package net.txsla.itemarchive;

import org.bukkit.plugin.java.JavaPlugin;
import net.txsla.itemarchive.commands.*;
import net.txsla.itemarchive.events.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class ItemArchive extends JavaPlugin {
    @Override
    public void onEnable() {
        // Plugin startup logic

        // Load Configs
        saveDefaultConfig();
        int var, maxNewArchives;

        /*
         *  untested but possible issue:
         *
         *   When string hit 2GB, the string.length could run into some overflow issues
         *  this can be switched to use StringBuilder to mitigate the issue, however I doubt any archive will
         *  grow to over 1gb
         *
         *   Once any public archive hits 1.1GB, I will fix this issue
         *
         *  pom Compile versions:
         * 1.21-R0.1-SNAPSHOT
         * 1.20.4-R0.1-SNAPSHOT
         *
         * */

        // Find/Create archives folder
        File directory = new File(System.getProperty("user.dir") + File.separator + "archives");
        if (directory.mkdir()) {
            getLogger().warning("Failed to find archives: generating new folder ");
        } else {
            getLogger().info("Archive found");
        }

        // Initialise commands
        ArchiveManager AM = new ArchiveManager(this);
        ItemConverter IC = new ItemConverter();
        GuiManager GM = new GuiManager(); GM.load();
        getCommand("open").setExecutor(new open(this) );
        getCommand("remove-item").setExecutor(new removeitem(this) );
        getCommand("search").setExecutor(new search(this) );
        getCommand("submit").setExecutor(new submit(this) );
        getCommand("submit-ban").setExecutor(new submitban(this) );
        getCommand("list-archives").setExecutor(new listarchives(this) );
        getCommand("create-archive").setExecutor(new createarchive(this) );
        getCommand("print-archive").setExecutor(new printarchive(this) );
        getCommand("find-submitter").setExecutor(new findsubmitter(this) );
        getCommand("hash-item").setExecutor(new hashitem(this) );
        getCommand("count-items").setExecutor(new countitems(this) );
        getCommand("item-size").setExecutor(new itemsize(this) );
        getCommand("set-gui").setExecutor(new setgui(this) );
        getCommand("bulk-submit").setExecutor(new bulksubmit(this) );

        //Initialize Events
        getServer().getPluginManager().registerEvents(new clearInv(), this);
            // def rewrite the entirety of GuiManager and GuiListener rather soon
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);

        // Initiate Caches
        List<String> archives = ArchiveManager.getArchiveList();
        var = getConfig().getInt("max-new-archives");
        maxNewArchives = ( ( var * var ) / var ) + 1;
        ArchiveManager.ArchiveCache = new String[ maxNewArchives + archives.size() ][][];
        getLogger().info("archivebuffer = " + (maxNewArchives + archives.size() ));

        // Load archives
        for (int x = archives.size(); x > 0; x-- ) {
            getLogger().info("[loading] loading arcjoev");
            ArchiveManager.cacheArchive(archives.get(x-1));
        }

        // Enable/Disable Submissions
        ArchiveManager.submissionsEnabled = getConfig().getBoolean("submission.allowSubmissions");

        getLogger().info("[ItemArchive.onEnable] plugin loaded");
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("[ItemArchive.onDisable] plugin disabled");
    }
}
