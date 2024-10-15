package com.example.sumoPlugin;


import com.google.gson.Gson;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class sumoMain extends JavaPlugin implements Listener {
    public List<Arena> arenas_list;
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
            arenas_list = new ArrayList<>(Arrays.asList(g.fromJson(jsonstring, Arena[].class)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //create arena manager
        arenaManager=new ArenaManager(arenas_list,new Location(Bukkit.getWorld("world"),0,105,0));

        //register command
        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("sumo",new sumoCommandApi(getDataFolder(),arenas_list,arenaManager));
        });

        //register events
        Bukkit.getPluginManager().registerEvents(new attackListener(), this);
        Bukkit.getPluginManager().registerEvents(new interactListener(),this);
        Bukkit.getPluginManager().registerEvents(new moveListener(arenaManager),this);
        Bukkit.getPluginManager().registerEvents(new useListener(arenaManager),this);
    }
}