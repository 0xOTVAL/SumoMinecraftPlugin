package com.example.sumoplugin;


import com.example.sumoplugin.arena.ArenaManager;
import com.example.sumoplugin.command.MainCommand;
import com.example.sumoplugin.arena.ArenaData;
import com.example.sumoplugin.eventlisteners.*;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Sumo extends JavaPlugin implements Listener {
    public List<ArenaData> arenas_list;
    public ArenaManager arenaManager;

    @Override
    public void onEnable() {
        //create data folder if not exists
        getDataFolder().mkdir();
        try {
            //load config
            File configFile = new File(getDataFolder(), "config.yml");
            if(!configFile.exists())configFile.createNewFile();
            getConfig().load(configFile);
        }catch (Exception e){
            e.printStackTrace();
        }
        String respawnloc= getConfig().get("respawn_location").toString();
        //load arenas from file
        File arenas = new File(getDataFolder(), "arena_list.json");
        try {
            String jsonstring = FileUtils.readFileToString(arenas, Charset.defaultCharset());
            Gson g = new Gson();
            arenas_list = new ArrayList<>(Arrays.asList(g.fromJson(jsonstring, ArenaData[].class)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //create arena manager
        arenaManager=new ArenaManager(arenas_list,new Location(Bukkit.getWorld(respawnloc.split(",")[0]),Float.parseFloat(respawnloc.split(",")[1]),Float.parseFloat(respawnloc.split(",")[2]),Float.parseFloat(respawnloc.split(",")[3])));

        //register command
        MainCommand mainCommand=new MainCommand(this);
        Objects.requireNonNull(this.getCommand("sumo")).setExecutor(mainCommand.base);
        //register events
        Bukkit.getPluginManager().registerEvents(new attackListener(this), this);
        Bukkit.getPluginManager().registerEvents(new interactListener(this),this);
        Bukkit.getPluginManager().registerEvents(new moveListener(this),this);
        Bukkit.getPluginManager().registerEvents(new useListener(this),this);
        Bukkit.getPluginManager().registerEvents(new deathListener(this),this);
        Bukkit.getPluginManager().registerEvents(new blockPlaceListener(this),this);
        Bukkit.getPluginManager().registerEvents(new logoutListener(this),this);
        Bukkit.getPluginManager().registerEvents(new blockBreakListener(this),this);
    }
}