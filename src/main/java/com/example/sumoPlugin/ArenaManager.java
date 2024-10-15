package com.example.sumoPlugin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class ArenaManager {
    List<Arena> arenas_list;
    public Map<Arena,Player> players=new HashMap<Arena,Player>();
    public HashMap <Arena, Boolean> isArenaStarted=new HashMap<Arena,Boolean>();
    public HashMap <Arena, Boolean> isArenaGameStarted=new HashMap<Arena,Boolean>();
    public HashMap <Player, ItemStack[]> playerInventory=new HashMap<Player,ItemStack[]>();
    public Location respawn_loc;
    public HashMap <Arena, BossBar> bars=new HashMap<>();
    public HashMap <Arena, Integer> times=new HashMap<>();
    public HashMap <Arena,Map<Team,Player>> arenasTeams=new HashMap<>();

    ArenaManager(List<Arena> arenas_list, Location respawn_loc){
        this.arenas_list=arenas_list;
        for(Arena i:this.arenas_list){
            isArenaGameStarted.put(i,false);
            isArenaStarted.put(i,false);
        }
        this.respawn_loc=respawn_loc;
    }
    public void startArena(Arena arena){
        if(isArenaStarted.containsKey(arena))isArenaStarted.put(arena,true);
        HashMap<Team,Player> teams=new HashMap<>();                                                                      
        arenasTeams.put(arena,teams);
    }
    public void stopArena(Arena arena){
        if(isArenaStarted.containsKey(arena))isArenaStarted.put(arena,false);
    }
    public void startGame(Arena arena){
        isArenaGameStarted.put(arena,true);
        bars.put(arena,Bukkit.getServer().createBossBar("test", BarColor.BLUE, BarStyle.SOLID));
        for(Map.Entry e: players.entrySet()) {
            bars.get(arena).addPlayer((Player) e.getValue());
        }
        Timer timer=new Timer();
        times.put(arena,10*1000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopGame(arena);
            }
        },10*1000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateBar(arena);
            }
        },0,1000);

    }
    private void updateBar(Arena arena){
        times.put(arena,times.get(arena)-1000);
        bars.get(arena).setTitle((Integer.toString(times.get(arena)/1000)));
    }
    public void stopGame(Arena arena){
        isArenaGameStarted.put(arena,false);
        for(Map.Entry e: players.entrySet()){
            if(e.getKey()==arena)returnPlayer(arena, (Player) e.getValue());
        }
    }
    public void joinPlayer(Arena arena,Player player){
        if(!isArenaStarted.get(arena)) return;
        players.put(arena,player);
        Location loc=new Location(Bukkit.getWorld(arena.world),arena.getLobbypos().x,arena.getLobbypos().y,arena.getLobbypos().z);
        player.teleport(loc);
        playerInventory.put(player,player.getInventory().getContents());
        player.getInventory().clear();
        ItemStack item;
        for(Team i:arena.teams){
            switch (i.color){
                case "RED"->item=new ItemStack(Material.RED_BANNER);
                case "GREEN"->item=new ItemStack(Material.GREEN_BANNER);
                case "BLUE"->item=new ItemStack(Material.BLUE_BANNER);
                case "BLACK"->item=new ItemStack(Material.BLACK_BANNER);
                case "WHITE"->item=new ItemStack(Material.WHITE_BANNER);
                case "YELLOW"->item=new ItemStack(Material.YELLOW_BANNER);
                case "LIME"->item=new ItemStack(Material.LIME_BANNER);
                default -> item=new ItemStack(Material.ORANGE_BANNER);
            }
            player.getInventory().addItem(item);
        }
        player.setGameMode(GameMode.SURVIVAL);
    }
    public void returnPlayer(Arena arena,Player player){
        bars.get(arena).removePlayer(player);
        player.getInventory().setContents(playerInventory.get(player));
        player.teleportAsync(respawn_loc);
        players.remove(arena,player);
    }
}
