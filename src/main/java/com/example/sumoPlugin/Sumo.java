package com.example.sumoPlugin;


import com.example.sumoPlugin.command.MainCommand;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
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
            configFile.createNewFile();
            getConfig().load(configFile);
        }catch (Exception e){
            e.printStackTrace();
        }

        //load arenas from file
        File arenas = new File(getDataFolder(), "arena_list.json");
        try {
            String jsonstring = FileUtils.readFileToString(arenas);
            Gson g = new Gson();
            arenas_list = new ArrayList<>(Arrays.asList(g.fromJson(jsonstring, ArenaData[].class)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //create arena manager
        arenaManager=new ArenaManager(arenas_list,new Location(Bukkit.getWorld("world"),0,105,0));

        //register command
        MainCommand mainCommand=new MainCommand(this);
        Objects.requireNonNull(this.getCommand("sumo")).setExecutor(mainCommand.base);
        //register events
        Bukkit.getPluginManager().registerEvents(new attackListener(), this);
        Bukkit.getPluginManager().registerEvents(new interactListener(this),this);
        Bukkit.getPluginManager().registerEvents(new moveListener(arenaManager),this);
        Bukkit.getPluginManager().registerEvents(new useListener(arenaManager),this);
        Bukkit.getPluginManager().registerEvents(new deathListener(arenaManager),this);
        Bukkit.getPluginManager().registerEvents(new blockPlaceListener(this),this);
    }
}