package com.example.sumoPlugin;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Arena {
    Boolean isStarted=false;
    Boolean isGameStarted=false;

    Vector3f pos1,pos2,lobbypos,barrierPos1,barrierPos2;
    Location respawn_loc;
    String name;
    World world;
    Integer gameTime,activeGameTime;
    Float barrierXSpeed;
    Float barrierZSpeed;

    ArrayList<Player> players=new ArrayList<>();
    ArrayList<Team> teams=new ArrayList<>();
    HashMap<Player, ItemStack[]> playerInventory=new HashMap<>();

    Timer timer;
    BossBar bar;
    Particle barrierParticle;

    public Arena(@NotNull ArenaData data){
        name = data.name;
        world = Bukkit.getWorld(data.world);

        pos1=new Vector3f(Float.parseFloat(data.pos1.split(",")[0]),Float.parseFloat(data.pos1.split(",")[1]),Float.parseFloat(data.pos1.split(",")[2]));
        pos2=new Vector3f(Float.parseFloat(data.pos2.split(",")[0]),Float.parseFloat(data.pos2.split(",")[1]),Float.parseFloat(data.pos2.split(",")[2]));
        lobbypos=new Vector3f(Float.parseFloat(data.lobbypos.split(",")[0]),Float.parseFloat(data.lobbypos.split(",")[1]),Float.parseFloat(data.lobbypos.split(",")[2]));

        gameTime= data.gameTime;
    }
    public Team getTeamByPlayer(Player player){
        for(Team t: teams){
            if(t.players.contains(player))return t;
        }
        return null;
    }
    public Team getTeamByItem(ItemStack item){
        for(Team t: teams){
            if(t.banner==item)return t;
        }
        return null;
    }
    public int addPlayer(Player player){
        //If arena is not started or game on arena has started we cannot add player
        if(!isStarted || isGameStarted)return -1;
        //Add player to array
        players.add(player);
        //Teleport player to lobby
        player.teleport(new Location(world,lobbypos.x,lobbypos.y,lobbypos.z));
        //Backup player inventory
        playerInventory.put(player,player.getInventory().getContents());
        player.getInventory().clear();
        //Add team banners to players inventory
        for(Team i:teams){
            player.getInventory().addItem(i.banner);
        }
        //Set player gamemode to survival
        player.setGameMode(GameMode.SURVIVAL);
        return 0;
    }
    public int startGame(){
        activeGameTime=gameTime;

        barrierPos1.x=min(pos1.x,pos2.x);
        barrierPos1.y=min(pos1.y,pos2.y);
        barrierPos1.z=min(pos1.z,pos2.z);

        barrierPos2.x=max(pos1.x,pos2.x);
        barrierPos2.y=max(pos1.y,pos2.y);
        barrierPos2.z=max(pos1.z,pos2.z);

        barrierXSpeed=Math.abs(barrierPos2.x-barrierPos1.x-2)/(2*gameTime);
        barrierZSpeed=Math.abs(barrierPos2.z-barrierPos1.z-2)/(2*gameTime);

        if(players.isEmpty())return -1;
        if(!isStarted)return -2;
        //create bar that will show remaining time
        bar=Bukkit.getServer().createBossBar(gameTime / 60 +":"+ gameTime % 60, BarColor.BLUE, BarStyle.SOLID);

        for(Player p: players) {
            //Cleat players inventory, but save armor
            ItemStack[]armor= p.getInventory().getArmorContents();
            p.getInventory().clear();
            p.getInventory().setArmorContents(armor);
            //give stick and wool to player
            p.getInventory().addItem(new ItemStack(Material.STICK));
            ItemStack wool_stack=new ItemStack(Material.GREEN_WOOL);
            wool_stack.setAmount(64);
            p.getInventory().addItem(wool_stack);
            //add bar to player
            bar.addPlayer(p);
            //teleport player to its spawn
            Vector3f spawn=getTeamByPlayer(p).spawnpos;
            p.teleportAsync(new Location(world,spawn.x,spawn.y,spawn.z));
        }
        //after this time game will finish
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopGame();
            }
        },gameTime*1000);
        //schedule bar and barrier update every second
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                activeGameTime-=1;
                shrinkBarrier();
                drawBarrier();
                bar.setTitle(activeGameTime / 60 +":"+ activeGameTime % 60);
                bar.setProgress((double) activeGameTime /gameTime);
            }
        },0,1000);
        return 0;
    }
    private void shrinkBarrier(){
        barrierPos1.x+=barrierXSpeed;
        barrierPos2.x-=barrierXSpeed;

        barrierPos1.z+=barrierZSpeed;
        barrierPos2.z-=barrierZSpeed;
    }
    private void drawBarrier(){
        float barrierXWidth=Math.abs(barrierPos2.x-barrierPos1.x);
        float barrierYWidth =Math.abs(barrierPos2.y-barrierPos1.y);
        float barrierZWidth =Math.abs(barrierPos2.z-barrierPos1.z);

        world.spawnParticle(barrierParticle, barrierPos1.x, (barrierPos1.y+barrierPos2.y)/2, (barrierPos1.z+barrierPos2.z)/2, 3000, 0.2, barrierYWidth, barrierZWidth, Optional.of(0));
        world.spawnParticle(barrierParticle, barrierPos2.x, (barrierPos1.y+barrierPos2.y)/2, (barrierPos1.z+barrierPos2.z)/2, 3000, 0.2, barrierYWidth, barrierZWidth, Optional.of(0));

        world.spawnParticle(barrierParticle, (barrierPos1.x+barrierPos2.x)/2, (barrierPos1.y+barrierPos2.y)/2, barrierPos1.z, 3000, barrierXWidth, barrierYWidth, 0.2, Optional.of(0));
        world.spawnParticle(barrierParticle, (barrierPos1.x+barrierPos2.x)/2, (barrierPos1.y+barrierPos2.y)/2, barrierPos1.z, 3000, barrierXWidth, barrierYWidth, 0.2, Optional.of(0));
    }
    public int stopGame(){
        timer.cancel();
        for(Player p : players){
            returnPlayer(p,"gameFinish");
        }
        return 0;
    }
    public int returnPlayer(Player player,String reason){
        if(!isStarted)return -1;
        if(!players.contains(player))return -2;
        //restore player inventory
        player.getInventory().setContents(playerInventory.get(player));
        players.remove(player);
        playerInventory.remove(player);
        getTeamByPlayer(player).removePlayer(player);
        bar.removePlayer(player);
        //teleport player to respawn location
        if(reason.equals("exit") || reason.equals("gameFinish"))player.teleportAsync(respawn_loc);
        return 0;
    }
    public int start(){
        isStarted=true;
        return 0;
    }
    public int stop(){
        if(!players.isEmpty())return -1;
        isStarted=false;
        return 0;
    }
    public boolean isInsideBarrier(Vector3f pos){
        return (barrierPos1.x<pos.x() && pos.x()<barrierPos2.x &&
                barrierPos1.y<pos.y() && pos.y()<barrierPos2.y &&
                barrierPos1.z<pos.z() && pos.z()<barrierPos2.z);
    }
}
