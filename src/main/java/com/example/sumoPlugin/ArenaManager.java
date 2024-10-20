package com.example.sumoPlugin;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ArenaManager {
    List<ArenaData> arenas_list;
    public HashMap<ArenaData,ArrayList<Player>> players=new HashMap<>();
    public HashMap <ArenaData, Boolean> isArenaStarted=new HashMap<ArenaData,Boolean>();
    public HashMap <ArenaData, Boolean> isArenaGameStarted=new HashMap<ArenaData,Boolean>();
    public HashMap <Player, ItemStack[]> playerInventory=new HashMap<Player,ItemStack[]>();
    public Location respawn_loc;
    public HashMap <ArenaData, BossBar> bars=new HashMap<>();
    public HashMap <ArenaData, Integer> times=new HashMap<>();
    public HashMap <ArenaData,HashMap<TeamData,ArrayList<Player>>> arenasTeams=new HashMap<>();
   // public HashMap <Player, Timer> playerDeathTimers=new HashMap<>();

    ArenaManager(List<ArenaData> arenas_list, Location respawn_loc){
        this.arenas_list=arenas_list;
        for(ArenaData i:this.arenas_list){
            isArenaGameStarted.put(i,false);
            isArenaStarted.put(i,false);
        }
        this.respawn_loc=respawn_loc;
    }
    public Arena getArenaByName(String name){
        return new Arena(arenas_list.get(1));
    }
    public ArenaData getArenaDataByName(String name){
        return  arenas_list.get(1);
    }
    public Arena getArenaByPlayer(Player player){
        return new Arena(arenas_list.get(1));
    }
    public void addArenaData(ArenaData arenaData){

    }
    public void startArena(ArenaData arena){

        if(isArenaStarted.containsKey(arena))isArenaStarted.put(arena,true);
        HashMap<TeamData,ArrayList<Player>> teams=new HashMap<>();
        arenasTeams.put(arena,teams);
        players.put(arena,new ArrayList<Player>());
        for(TeamData t: arena.teams){
            arenasTeams.get(arena).put(t,new ArrayList<Player>());
        }
    }
    public void stopArena(ArenaData arena){
        if(isArenaStarted.containsKey(arena))isArenaStarted.put(arena,false);
    }
    public void startGame(ArenaData arena){
        isArenaGameStarted.put(arena,true);
        bars.put(arena,Bukkit.getServer().createBossBar("test", BarColor.BLUE, BarStyle.SOLID));
        for(Player p: players.get(arena)) {
            p.getInventory().clear();
            p.getInventory().addItem(new ItemStack(Material.STICK));
            ItemStack wool_stack=new ItemStack(Material.GREEN_WOOL);
            wool_stack.setAmount(64);
            p.getInventory().addItem(wool_stack);
            bars.get(arena).addPlayer(p);
        }
        Timer timer=new Timer();
        float speedx=Math.abs(arena.getPos1().x-arena.getPos2().x)/122;
        float speedz=Math.abs(arena.getPos1().z-arena.getPos2().z)/122;
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
                Bukkit.getWorld(arena.world).spawnParticle(Particle.DRAGON_BREATH, arena.getPos1().x, (arena.getPos1().y+arena.getPos2().y)/2, (arena.getPos1().z+arena.getPos2().z)/2, 3000, 0.2, Math.abs(arena.getPos1().y-arena.getPos2().y)/2, Math.abs(arena.getPos1().z-arena.getPos2().z)/2, 0);
                Bukkit.getWorld(arena.world).spawnParticle(Particle.DRAGON_BREATH, arena.getPos2().x, (arena.getPos1().y+arena.getPos2().y)/2, (arena.getPos1().z+arena.getPos2().z)/2, 3000, 0.2, Math.abs(arena.getPos1().y-arena.getPos2().y)/2, Math.abs(arena.getPos1().z-arena.getPos2().z)/2, 0);
                Bukkit.getWorld(arena.world).spawnParticle(Particle.DRAGON_BREATH, (arena.getPos1().x+arena.getPos2().x)/2, (arena.getPos1().y+arena.getPos2().y)/2, arena.getPos1().z, 3000, Math.abs(arena.getPos1().x-arena.getPos2().x)/2, Math.abs(arena.getPos1().y-arena.getPos2().y)/2,0.2 , 0);
                Bukkit.getWorld(arena.world).spawnParticle(Particle.DRAGON_BREATH, (arena.getPos1().x+arena.getPos2().x)/2, (arena.getPos1().y+arena.getPos2().y)/2, arena.getPos2().z, 3000, Math.abs(arena.getPos1().x-arena.getPos2().x)/2, Math.abs(arena.getPos1().y-arena.getPos2().y)/2,0.2 , 0);
                updateBar(arena);

                if(arena.getPos1().x<arena.getPos2().x){
                    arena.setPos1(arena.getPos1().x+speedx,arena.getPos1().y,arena.getPos1().z);
                    arena.setPos2(arena.getPos2().x-speedx,arena.getPos2().y,arena.getPos2().z);
                }
                else{
                    arena.setPos2(arena.getPos2().x+speedx,arena.getPos2().y,arena.getPos2().z);
                    arena.setPos1(arena.getPos1().x-speedx,arena.getPos1().y,arena.getPos1().z);
                }
                if(arena.getPos1().z<arena.getPos2().z){
                    arena.setPos1(arena.getPos1().x,arena.getPos1().y,arena.getPos1().z+speedz);
                    arena.setPos2(arena.getPos2().x,arena.getPos2().y,arena.getPos2().z-speedz);
                }
                else{
                    arena.setPos2(arena.getPos2().x,arena.getPos2().y,arena.getPos2().z+speedz);
                    arena.setPos1(arena.getPos1().x,arena.getPos1().y,arena.getPos1().z-speedz);
                }
            }

        },0,1000);
    }
    private void updateBar(ArenaData arena){
        times.put(arena,times.get(arena)-1000);
        bars.get(arena).setTitle((Integer.toString(times.get(arena)/1000)));
    }
    public void stopGame(ArenaData arena, boolean isTimerDown){
        isArenaGameStarted.put(arena,false);
        for(Player p: players.get(arena)){
           // p.sendMessage("All commands sucked dick");
            returnPlayer(arena, p);
        }
    }
    public void joinPlayer(ArenaData arena, Player player){
        if(!isArenaStarted.get(arena)) return;
        players.get(arena).add(player);
        Location loc=new Location(Bukkit.getWorld(arena.world),arena.getLobbypos().x,arena.getLobbypos().y,arena.getLobbypos().z);
        player.teleport(loc);
        playerInventory.put(player,player.getInventory().getContents());
        player.getInventory().clear();
        ItemStack item;
        for(TeamData i:arena.teams){
            switch (i.color) {
                case "RED" -> item = new ItemStack(Material.RED_BANNER);
                case "GREEN" -> item = new ItemStack(Material.GREEN_BANNER);
                case "BLUE" -> item = new ItemStack(Material.BLUE_BANNER);
                case "BLACK" -> item = new ItemStack(Material.BLACK_BANNER);
                case "WHITE" -> item = new ItemStack(Material.WHITE_BANNER);
                case "YELLOW" -> item = new ItemStack(Material.YELLOW_BANNER);
                case "LIME" -> item = new ItemStack(Material.LIME_BANNER);
                default -> item = new ItemStack(Material.ORANGE_BANNER);
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
    public void returnPlayer(ArenaData arena, Player player){
        players.get(arena).remove(player);
        player.getInventory().setContents(playerInventory.get(player));
        player.teleportAsync(respawn_loc);
        bars.get(arena).removePlayer(player);
        HashMap<TeamData,ArrayList<Player>> active_teams=new HashMap<>();
        for(Map.Entry e:arenasTeams.get(arena).entrySet()){
            if(!((ArrayList)e.getValue()).isEmpty()){
                active_teams.put((TeamData)e.getKey(),(ArrayList<Player>) e.getValue());
            }
        }
        if(active_teams.size()==1){
            for(Map.Entry e:active_teams.entrySet()){
                for(Object p: ((ArrayList)e.getValue())){
                    ((Player)p).sendMessage("Team"+((TeamData)e.getKey()).name+"won!");
                    stopGame(arena,false);
                }
            }
        }
    }
}
