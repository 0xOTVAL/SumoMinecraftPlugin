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
    public Boolean isStarted=false;
    public Boolean isGameStarted=false;

    public Vector3f pos1,pos2,lobbypos,barrierPos1,barrierPos2;
    public Location respawn_loc;
    public String name;
    public World world;
    public Integer gameTime,activeGameTime;
    public Float barrierXSpeed;
    public Float barrierZSpeed;

    public ArrayList<Player> players=new ArrayList<>();
    public ArrayList<Team> teams=new ArrayList<>();
    public HashMap<Player, ItemStack[]> playerInventory=new HashMap<>();

    public Timer timer;
    public BossBar bar;
    public Particle barrierParticle=Particle.DRAGON_BREATH;

    public Arena(@NotNull ArenaData data){
        name = data.name;
        world = Bukkit.getWorld(data.world);

        pos1=new Vector3f(Float.parseFloat(data.pos1.split(",")[0]),Float.parseFloat(data.pos1.split(",")[1]),Float.parseFloat(data.pos1.split(",")[2]));
        pos2=new Vector3f(Float.parseFloat(data.pos2.split(",")[0]),Float.parseFloat(data.pos2.split(",")[1]),Float.parseFloat(data.pos2.split(",")[2]));
        lobbypos=new Vector3f(Float.parseFloat(data.lobbypos.split(",")[0]),Float.parseFloat(data.lobbypos.split(",")[1]),Float.parseFloat(data.lobbypos.split(",")[2]));

        gameTime= data.gameTime;
        for(TeamData td: data.teams){
            teams.add(new Team(td));
        }
    }
    public Team getTeamByPlayer(Player player){
        for(Team t: teams){
            if(t.players.contains(player))return t;
        }
        return null;
    }
    public Team getTeamByItem(ItemStack item){
        for(Team t: teams){
            if(t.banner.getType()==item.getType())return t;
        }
        return null;
    }
    public String addPlayer(Player player){
        //If arena is not started or game on arena has started we cannot add player
        if(!isStarted)return "Arena "+name+" has not started";
        if(isGameStarted)return "Can not join to started game";
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
        return "You joined "+name;
    }
    public String startGame(){
        timer=new Timer();
        if(players.isEmpty())return "Can not start arena with no players";
        if(!isStarted)return "Arena is not started";

        activeGameTime=gameTime;

        barrierXSpeed=Math.abs(barrierPos2.x-barrierPos1.x-2)/(2*gameTime);
        barrierZSpeed=Math.abs(barrierPos2.z-barrierPos1.z-2)/(2*gameTime);

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
          //  Vector3f spawn=getTeamByPlayer(p).spawnpos;
            //p.sendMessage(spawn.toString());
            //p.teleportAsync(new Location(world,spawn.x,spawn.y,spawn.z));
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
        isGameStarted=true;
        return "Game on arena "+name+" started successful";
    }
    private void shrinkBarrier(){
        barrierPos1.x+=barrierXSpeed;
        barrierPos2.x-=barrierXSpeed;

        barrierPos1.z+=barrierZSpeed;
        barrierPos2.z-=barrierZSpeed;
    }
    private void drawBarrier(){
        float barrierXWidth=Math.abs(barrierPos2.x-barrierPos1.x)/2;
        float barrierYWidth =Math.abs(barrierPos2.y-barrierPos1.y)/2;
        float barrierZWidth =Math.abs(barrierPos2.z-barrierPos1.z)/2;

        world.spawnParticle(barrierParticle, barrierPos1.x, (barrierPos1.y+barrierPos2.y)/2, (barrierPos1.z+barrierPos2.z)/2, 3000, 0.2, barrierYWidth, barrierZWidth, 0);
        world.spawnParticle(barrierParticle, barrierPos2.x, (barrierPos1.y+barrierPos2.y)/2, (barrierPos1.z+barrierPos2.z)/2, 3000, 0.2, barrierYWidth, barrierZWidth, 0);

        world.spawnParticle(barrierParticle, (barrierPos1.x+barrierPos2.x)/2, (barrierPos1.y+barrierPos2.y)/2, barrierPos1.z, 3000, barrierXWidth, barrierYWidth, 0.2,0);
        world.spawnParticle(barrierParticle, (barrierPos1.x+barrierPos2.x)/2, (barrierPos1.y+barrierPos2.y)/2, barrierPos2.z, 3000, barrierXWidth, barrierYWidth, 0.2,0);
    }
    private void stopGame(){
        timer.cancel();
        for(Player p : players){
            returnPlayer(p,"gameFinish");
        }
    }
    public String returnPlayer(Player player,String reason){
        if(!players.contains(player))return "You are not on arena "+name;
        //restore player inventory
        player.getInventory().setContents(playerInventory.get(player));
        players.remove(player);
        playerInventory.remove(player);
        getTeamByPlayer(player).removePlayer(player);
        bar.removePlayer(player);
        //teleport player to respawn location
        if(reason.equals("exit") || reason.equals("gameFinish"))player.teleportAsync(respawn_loc);
        return "You left "+name;
    }
    public String start(){
        if(isStarted)return "Arena is already started";
        isStarted=true;

        barrierPos1=new Vector3f();
        barrierPos2=new Vector3f();

        barrierPos1.x=min(pos1.x,pos2.x);
        barrierPos1.y=min(pos1.y,pos2.y);
        barrierPos1.z=min(pos1.z,pos2.z);

        barrierPos2.x=max(pos1.x,pos2.x);
        barrierPos2.y=max(pos1.y,pos2.y);
        barrierPos2.z=max(pos1.z,pos2.z);
        return "Arena "+name+" has started";
    }
    public String stop(){
        if(!players.isEmpty())return "There are players on arena";
        isStarted=false;
        return "Arena "+name+" stopped";
    }
    public boolean isInsideBarrier(Vector3f pos){
        return (barrierPos1.x<pos.x() && pos.x()<barrierPos2.x &&
                barrierPos1.y<pos.y() && pos.y()<barrierPos2.y &&
                barrierPos1.z<pos.z() && pos.z()<barrierPos2.z);
    }
}
