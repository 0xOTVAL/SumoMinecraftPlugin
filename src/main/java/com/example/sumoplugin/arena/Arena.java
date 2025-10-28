package com.example.sumoplugin.arena;

import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.team.Team;
import com.example.sumoplugin.team.TeamData;
import joptsimple.internal.Reflection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.random.*;
import java.io.*;
import java.util.*;

import static java.lang.Math.*;

public class Arena {
    public Boolean isStarted=false;
    public Boolean isGameStarted=false;

    public Vector3f pos1,pos2,lobbypos,barrierPos1,barrierPos2;
    public Location respawn_loc=new Location(Bukkit.getWorld("world"),0,200,0);
    public String name,worldname;
    public World worldcopy;
    public Integer gameTime,activeGameTime;
    public Float barrierXSpeed;
    public Float barrierZSpeed;

    public ArrayList<Player> players=new ArrayList<>();
    public ArrayList<Team> teams=new ArrayList<>();
    public ArrayList<Team> activeTeams=new ArrayList<>();
    public ArrayList<Player> spectators=new ArrayList<>();
    public HashMap<Player, ItemStack[]> playerInventory=new HashMap<>();

    public Timer timer;
    public BossBar bar;
    public WorldBorder border;
    public Particle barrierParticle=Particle.DRAGON_BREATH;
    Sumo plugin;
    //Термоядерный костыль для нормальной перезагрузки мира
    int worldcopyindex=0;

    public Arena(@NotNull ArenaData data,Sumo plugin){
        this.plugin=plugin;
        name = data.name;
        worldname=data.world;

        pos1=new Vector3f(Float.parseFloat(data.pos1.split(",")[0]),Float.parseFloat(data.pos1.split(",")[1]),Float.parseFloat(data.pos1.split(",")[2]));
        pos2=new Vector3f(Float.parseFloat(data.pos2.split(",")[0]),Float.parseFloat(data.pos2.split(",")[1]),Float.parseFloat(data.pos2.split(",")[2]));
        lobbypos=new Vector3f(Float.parseFloat(data.lobbypos.split(",")[0]),Float.parseFloat(data.lobbypos.split(",")[1]),Float.parseFloat(data.lobbypos.split(",")[2]));

        gameTime= data.gameTime;
        for(TeamData td: data.teams){
            teams.add(new Team(td));
        }
        barrierPos1=new Vector3f();
        barrierPos2=new Vector3f();

        //Чистим директории прошлых арен чтобы не возникало ошибок
        clearOldWorldsDirectorys();
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
        worldcopy.setTime(1000);
        //If arena is not started or game on arena has started we cannot add player
        if(!isStarted)return "Arena "+name+" has not started";
        if(isGameStarted)return "Can not join to started game";
        if(players.contains(player))return "WTF?";
        //Add player to array
        players.add(player);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20);
        //Teleport player to lobby
        player.teleport(new Location(worldcopy,lobbypos.x,lobbypos.y,lobbypos.z));
        //Backup player inventory
        playerInventory.put(player,player.getInventory().getContents());
        player.getInventory().clear();
        //Add team banners to players inventory
        for(Team i:teams){
            player.getInventory().addItem(i.banner);
        }
        ItemStack startgameitem=new ItemStack(Material.DIAMOND);
        ItemMeta startgameitemmeta=startgameitem.getItemMeta();
        startgameitemmeta.setDisplayName(ChatColor.BLUE+"Start game");
        startgameitem.setItemMeta(startgameitemmeta);
        ItemStack exititem=new ItemStack(Material.SLIME_BALL);
        ItemMeta exititemmeta=exititem.getItemMeta();
        exititemmeta.setDisplayName(ChatColor.RED+"Exit");
        exititem.setItemMeta(exititemmeta);
        player.getInventory().setItem(7,startgameitem);
        player.getInventory().setItem(8,exititem);
        //Set player gamemode to survival
        player.setGameMode(GameMode.ADVENTURE);
        return "You joined "+name;
    }
    public String startGame(){
        if(players.isEmpty())return "Can not start arena with no players";
        if(!isStarted)return "Arena is not started";

        for(Player p : players){
            p.setSaturation(5);
            Team t=getTeamByPlayer(p);
            p.setGameMode(GameMode.SURVIVAL);
            if(t==null){
                return "There are players with no team";
            }
            else{
                if(!activeTeams.contains(t))activeTeams.add(t);
            }
        }

        if(activeTeams.size()<2){
            activeTeams.clear();
            return "There must be at least 2 teams with players in them";
        }

        timer=new Timer();
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
            Vector3f spawn=getTeamByPlayer(p).spawnpos;
            p.sendMessage(spawn.toString());
            p.setWorldBorder(border);
            p.teleportAsync(new Location(worldcopy,spawn.x,spawn.y,spawn.z));
        }
        //after this time game will finish
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                cancel();
                stopGame("timerEnd");

            }
        },gameTime*1000);
        //schedule bar and barrier update every second
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                activeGameTime-=1;
                shrinkBarrier();
                // drawBarrier();

                bar.setTitle(activeGameTime / 60 +" : "+ activeGameTime % 60);
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

        worldcopy.spawnParticle(barrierParticle, barrierPos1.x, (barrierPos1.y+barrierPos2.y)/2, (barrierPos1.z+barrierPos2.z)/2, 3000, 0.2, barrierYWidth, barrierZWidth, 0);
        worldcopy.spawnParticle(barrierParticle, barrierPos2.x, (barrierPos1.y+barrierPos2.y)/2, (barrierPos1.z+barrierPos2.z)/2, 3000, 0.2, barrierYWidth, barrierZWidth, 0);

        worldcopy.spawnParticle(barrierParticle, (barrierPos1.x+barrierPos2.x)/2, (barrierPos1.y+barrierPos2.y)/2, barrierPos1.z, 3000, barrierXWidth, barrierYWidth, 0.2,0);
        worldcopy.spawnParticle(barrierParticle, (barrierPos1.x+barrierPos2.x)/2, (barrierPos1.y+barrierPos2.y)/2, barrierPos2.z, 3000, barrierXWidth, barrierYWidth, 0.2,0);
    }
    private void stopGame(String reason){
        Bukkit.getServer().getConsoleSender().sendMessage("Stop game was called");
        timer.cancel();
        timer.purge();

        while(!players.isEmpty()) {
            Player p = players.getFirst();
         //   p.sendMessage(reason);
            if (reason.equals("teamWin"))
                p.showTitle(Title.title(Component.text("Team " + activeTeams.getFirst().name + " won", TextColor.color(activeTeams.getFirst().color.asRGB())), Component.text("")));
            if (reason.equals("timerEnd"))
                p.showTitle(Title.title(Component.text("There is no winner"), Component.text("")));
            returnPlayer(p);
        }
        bar.removeAll();
        if(!spectators.isEmpty())spectators.clear();
        activeTeams.clear();
        isGameStarted=false;

        if(worldcopy!=null){
            unloadWorld(worldcopy);
        }
        worldcopyindex++;
        File oldworld= new File( Bukkit.getWorld(worldname).getWorldFolder().getPath()+"_sumotmp");
        deleteDirectory(oldworld);
        copyWorld(Bukkit.getWorld(worldname),worldname+"_sumotmp_"+worldcopyindex);
        worldcopy=Bukkit.getWorld(worldname+"_sumotmp_"+worldcopyindex);

        worldcopy.setTime(1000);
        worldcopy.setStorm(false);
        worldcopy.setThundering(false);
        worldcopy.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);
        worldcopy.setGameRule(GameRule.DO_MOB_SPAWNING,false);
        worldcopy.setGameRule(GameRule.DO_WEATHER_CYCLE,false);
        worldcopy.setGameRule(GameRule.MOB_GRIEFING,false);

        barrierPos1.x=min(pos1.x,pos2.x);
        barrierPos1.y=min(pos1.y,pos2.y);
        barrierPos1.z=min(pos1.z,pos2.z);

        barrierPos2.x=max(pos1.x,pos2.x);
        barrierPos2.y=max(pos1.y,pos2.y);
        barrierPos2.z=max(pos1.z,pos2.z);
  //
    }
    public void spawnBonus(){
        Vector3f pos=teams.get((int)(Math.random()*10)%teams.size()).spawnpos;
        FallingBlock bonus= worldcopy.spawnFallingBlock(new Location(worldcopy,pos.x,pos.y+10,pos.z),Material.DIAMOND_BLOCK.createBlockData());
        bonus.setGravity(false);
        bonus.setVelocity(Vector.fromJOML(new Vector3f(0,(float)-0.5,0)));
    }
    public void fillBonusInventory(Inventory inv){
        inv.addItem(new ItemStack(Material.TNT));
        inv.addItem(new ItemStack(Material.FLINT_AND_STEEL));
    }
    public void returnPlayer(Player player){
        if(!players.contains(player))return;
        player.sendMessage("returnPlayer");
        //restore player inventory
        player.getInventory().setContents(playerInventory.get(player));
        playerInventory.remove(player);
        if(getTeamByPlayer(player)!=null)getTeamByPlayer(player).removePlayer(player);
        if(bar!=null)if(bar.getPlayers().contains(player))bar.removePlayer(player);
        //teleport player to respawn location
        player.setGameMode(GameMode.SURVIVAL);
        player.teleportAsync(respawn_loc);
        player.setGameMode(GameMode.SURVIVAL);
        player.setWorldBorder(null);

        players.remove(player);
    }
    public String start(){
        if(isStarted)return "Arena is already started";
        //create copy of arena world, if it already exists overwrite it
        if(worldcopy!=null){
            unloadWorld(worldcopy);
        }
        File oldworld= new File( Bukkit.getWorld(worldname).getWorldFolder().getPath()+"_sumotmp");
        deleteDirectory(oldworld);
        copyWorld(Bukkit.getWorld(worldname),worldname+"_sumotmp_"+worldcopyindex);
        worldcopy=Bukkit.getWorld(worldname+"_sumotmp_"+worldcopyindex);
        isStarted=true;
        worldcopy.setTime(1000);
        worldcopy.setStorm(false);
        worldcopy.setThundering(false);
        worldcopy.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);
        worldcopy.setGameRule(GameRule.DO_MOB_SPAWNING,false);
        worldcopy.setGameRule(GameRule.DO_WEATHER_CYCLE,false);
        worldcopy.setGameRule(GameRule.MOB_GRIEFING,false);

        barrierPos1.x=min(pos1.x,pos2.x);
        barrierPos1.y=min(pos1.y,pos2.y);
        barrierPos1.z=min(pos1.z,pos2.z);

        barrierPos2.x=max(pos1.x,pos2.x);
        barrierPos2.y=max(pos1.y,pos2.y);
        barrierPos2.z=max(pos1.z,pos2.z);

        border = Bukkit.createWorldBorder();
        border.setCenter(new Location(worldcopy,(barrierPos2.x+barrierPos1.x)/2,(barrierPos2.y-barrierPos1.y)/2,(barrierPos2.z+barrierPos1.z)/2));
        border.setSize(max(barrierPos2.x-barrierPos1.x,barrierPos2.z-barrierPos1.z));
        border.setDamageAmount(Integer.parseInt(plugin.getConfig().get("barrier_damage").toString()));
        border.setWarningDistance(0);
        border.setDamageBuffer(0);

        return "Arena "+name+" has started";
    }
    public String stop(){
        if(!players.isEmpty())return "There are players on arena";
        if(worldcopy!=null){
            unloadWorld(worldcopy);
            worldcopy.getWorldFolder().delete();
        }
        isStarted=false;
        return "Arena "+name+" stopped";
    }
    public boolean isInsideBarrier(Vector3f pos){
        return (barrierPos1.x<pos.x() && pos.x()<barrierPos2.x &&
                barrierPos1.y<pos.y() && pos.y()<barrierPos2.y &&
                barrierPos1.z<pos.z() && pos.z()<barrierPos2.z);
    }
    public void killPlayer(Player player){
        getTeamByPlayer(player).removePlayer(player);
        player.teleport(new Location(worldcopy,lobbypos.x,lobbypos.y,lobbypos.z));
        player.showTitle(Title.title(Component.text("You died", TextColor.color(255,0,0)),Component.text("")));

        player.setGameMode(GameMode.SPECTATOR);
        spectators.add(player);

        activeTeams.removeIf(t -> t.players.isEmpty());
        if(activeTeams.size() == 1) {
            stopGame("teamWin");
            return;
        }
        if (activeTeams.isEmpty()) {
            stopGame("timerEnd");
        }
        //spawnBonus();
    }
    public void logoutPlayer(Player player){
        if( getTeamByPlayer(player)!=null)getTeamByPlayer(player).removePlayer(player);
        activeTeams.removeIf(t -> t.players.isEmpty());
        returnPlayer(player);
        if(!isGameStarted)return;
        if(activeTeams.size() == 1) {
            stopGame("teamWin");
            return;
        }
        if (activeTeams.isEmpty()) {
            stopGame("timerEnd");
        }
    }
    private static void copyFileStructure(File source, File target){
        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
            if(!ignore.contains(source.getName())) {
                if(source.isDirectory()) {
                    if(!target.exists())
                        if (!target.mkdirs())
                            throw new IOException("Couldn't create world directory!");
                    String files[] = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyFileStructure(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean unloadWorld(World world) {
        Bukkit.getServer().unloadWorld(world, false);
        return world!=null;
    }
    public static void copyWorld(World originalWorld, String newWorldName) {
        copyFileStructure(originalWorld.getWorldFolder(), new File(Bukkit.getWorldContainer(), newWorldName));
        new WorldCreator(newWorldName).createWorld();
    }
    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
    private void clearOldWorldsDirectorys(){
        File dir = Bukkit.getWorldContainer();
        File [] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(worldname+"_sumotmp");
            }
        });
        Bukkit.getConsoleSender().sendMessage(files.toString());
        for(File f:files){
            deleteDirectory(f);
        }
    }
}
