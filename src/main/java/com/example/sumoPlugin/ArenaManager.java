package com.example.sumoPlugin;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.sql.Time;
import java.util.*;

public class ArenaManager {
    List<Arena> arenas_list;
    public HashMap<Arena,ArrayList<Player>> players=new HashMap<>();
    public HashMap <Arena, Boolean> isArenaStarted=new HashMap<Arena,Boolean>();
    public HashMap <Arena, Boolean> isArenaGameStarted=new HashMap<Arena,Boolean>();
    public HashMap <Player, ItemStack[]> playerInventory=new HashMap<Player,ItemStack[]>();
    public Location respawn_loc;
    public HashMap <Arena, BossBar> bars=new HashMap<>();
    public HashMap <Arena, Integer> times=new HashMap<>();
    public HashMap <Arena,HashMap<Team,ArrayList<Player>>> arenasTeams=new HashMap<>();
   // public HashMap <Player, Timer> playerDeathTimers=new HashMap<>();

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
        HashMap<Team,ArrayList<Player>> teams=new HashMap<>();
        arenasTeams.put(arena,teams);
        players.put(arena,new ArrayList<Player>());
        for(Team t: arena.teams){
            arenasTeams.get(arena).put(t,new ArrayList<Player>());
        }
    }
    public void stopArena(Arena arena){
        if(isArenaStarted.containsKey(arena))isArenaStarted.put(arena,false);
    }
    public void startGame(Arena arena){
        isArenaGameStarted.put(arena,true);
        bars.put(arena,Bukkit.getServer().createBossBar("test", BarColor.BLUE, BarStyle.SOLID));
        for(Player p: players.get(arena)) {
            p.getInventory().clear();
            p.getInventory().addItem(new ItemStack(Material.STICK));
            bars.get(arena).addPlayer(p);
        }
        Timer timer=new Timer();
        times.put(arena,60*1000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopGame(arena,true);
            }
        },60*1000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Bukkit.getWorld(arena.world).spawnParticle(Particle.DRAGON_BREATH,arena.getLobbypos().x,arena.getLobbypos().y,arena.getLobbypos().z,100,0.2,0.2,0.2,0);
                updateBar(arena);
            }
        },0,1000);
    }
    private void updateBar(Arena arena){
        times.put(arena,times.get(arena)-1000);
        bars.get(arena).setTitle((Integer.toString(times.get(arena)/1000)));
    }
    public void stopGame(Arena arena,boolean isTimerDown){
        isArenaGameStarted.put(arena,false);
        for(Player p: players.get(arena)){
           // p.sendMessage("All commands sucked dick");
            returnPlayer(arena, p);
        }
    }
    public void joinPlayer(Arena arena,Player player){
        if(!isArenaStarted.get(arena)) return;
        players.get(arena).add(player);
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
        for(int i=Math.round(arena.getPos1().x);i<Math.round(arena.getPos2().x);i++){
            for(int j=Math.round(arena.getPos1().y);j<Math.round(arena.getPos2().y);j++){
                Bukkit.getServer().getWorld(arena.world).getBlockAt(i,j,Math.round(arena.getPos1().z)).setType(Material.BLUE_WOOL);
            }
        }
        player.setGameMode(GameMode.SURVIVAL);
    }
    public void returnPlayer(Arena arena,Player player){
        players.get(arena).remove(player);
        player.getInventory().setContents(playerInventory.get(player));
        player.teleportAsync(respawn_loc);
        bars.get(arena).removePlayer(player);
        HashMap<Team,ArrayList<Player>> active_teams=new HashMap<>();
        for(Map.Entry e:arenasTeams.get(arena).entrySet()){
            if(!((ArrayList)e.getValue()).isEmpty()){
                active_teams.put((Team)e.getKey(),(ArrayList<Player>) e.getValue());
            }
        }
        if(active_teams.size()==1){
            for(Map.Entry e:active_teams.entrySet()){
                for(Object p: ((ArrayList)e.getValue())){
                    ((Player)p).sendMessage("Team"+((Team)e.getKey()).name+"won!");
                    stopGame(arena,false);
                }
            }
        }
    }
}
