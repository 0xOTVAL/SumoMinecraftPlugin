package com.example.sumoplugin.arena;

import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.team.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.minecraft.network.chat.ChatDecorator;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
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
    public ArrayList<Team> teams =new ArrayList<>();
    public ArrayList<Player> spectators=new ArrayList<>();
    public ArrayList<Vector3f> spawns=new ArrayList<>();
    public HashMap<Team,Vector3f> teamspawns=new HashMap<>();
    public HashMap<Player, ItemStack[]> playerInventory=new HashMap<>();
   // public ArrayList<org.bukkit.scoreboard.Team> scoreboardTeams=new ArrayList<>();

    public Timer timer;
    public BossBar bar;
    public Scoreboard board;
    public Objective boardObj;
    public Particle barrierParticle=Particle.FIREWORK;
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
        barrierPos1=new Vector3f();
        barrierPos2=new Vector3f();

        for(String s:data.spawnpos){
            spawns.add(new Vector3f(Float.parseFloat(s.split(",")[0]),Float.parseFloat(s.split(",")[1]),Float.parseFloat(s.split(",")[2])));
        }
        //Чистим директории прошлых арен чтобы не возникало ошибок
        clearOldWorldsDirectorys();
    }
    public String addPlayer(Player player){
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
        plugin.getServer().getConsoleSender().sendMessage(lobbypos.toString());
        player.teleport(new Location(worldcopy,lobbypos.x,lobbypos.y,lobbypos.z));
        //Backup player inventory
        playerInventory.put(player,player.getInventory().getContents());
        player.getInventory().clear();
        //Add team banners to players inventory
        /*for(Team i:teams){
            player.getInventory().addItem(i.banner);
        }
        ItemStack startgameitem=new ItemStack(Material.DIAMOND);
        //ItemMeta startgameitemmeta=startgameitem.getItemMeta();
        startgameitemmeta.setDisplayName(ChatColor.BLUE+"Start game");
        startgameitem.setItemMeta(startgameitemmeta);
        ItemStack exititem=new ItemStack(Material.SLIME_BALL);
        ItemMeta exititemmeta=exititem.getItemMeta();
        exititemmeta.setDisplayName(ChatColor.RED+"Exit");
        exititem.setItemMeta(exititemmeta);
        player.getInventory().setItem(7,startgameitem);
        player.getInventory().setItem(8,exititem);*/
        //Set player gamemode to adventure
        player.setGameMode(GameMode.ADVENTURE);
        return "You joined "+name;
    }
    public void addTeam(Team t){
        if(teams.contains(t))return;
        t.reset();
        teams.add(t);
        org.bukkit.scoreboard.Team st= board.registerNewTeam(t.name);
        st.setDisplayName(t.name);
        teamspawns.put(t,spawns.get(teams.size()-1));
        for(Player p: t.players){
            addPlayer(p);
            st.addPlayer(p);
            p.setScoreboard(board);
            p.getInventory().setArmorContents(t.armor);
        }
        t.isUsed=true;

    }
    public void delTeam(Team t){
        if(isGameStarted)return;
        for(Player p: t.players){
            board.getTeam(t.name).removePlayer(p);
            returnPlayer(p);
        }
        teams.remove(t);
        teamspawns.clear();
        for(int i=0;i<teams.size();i++){
            teamspawns.put(teams.get(i),spawns.get(i));
        }
        t.isUsed=false;
        Objects.requireNonNull(board.getTeam(t.name)).unregister();
    }
    public String startGame(){
        if(players.isEmpty())return "Can not start arena with no players";
        if(!isStarted)return "Arena is not started";

        for(Player p : players){
            p.setSaturation(5);
            p.setGameMode(GameMode.SURVIVAL);
        }

        if(teams.size()<2){
            teams.clear();
            return "There must be at least 2 teams with players in them";
        }

        timer=new Timer();
        activeGameTime=gameTime;

        barrierXSpeed=Math.abs(barrierPos2.x-barrierPos1.x-10)/(2*gameTime);
        barrierZSpeed=Math.abs(barrierPos2.z-barrierPos1.z-10)/(2*gameTime);

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
            Vector3f spawn=teamspawns.get(plugin.teamManager.getTeamByPlayer(p));
            p.sendMessage(spawn.toString());
            p.teleportAsync(new Location(worldcopy,spawn.x,spawn.y,spawn.z));
        }
        //after this time game will finish
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                cancel();
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        stopGame("timerEnd");
                    }
                });
            }
        },gameTime*1000);
        //Задаём параметры барьера
        //schedule bar and barrier update every second
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                activeGameTime-=1;
                shrinkBarrier();
                drawBarrier();
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
        float barrierXWidth=Math.abs(barrierPos2.x-barrierPos1.x)/4;
        float barrierYWidth =Math.abs(barrierPos2.y-barrierPos1.y)/2;
        float barrierZWidth =Math.abs(barrierPos2.z-barrierPos1.z)/4;

        worldcopy.spawnParticle(barrierParticle,barrierPos1.x,barrierPos1.y,(barrierPos1.z+barrierPos2.z)/2,300,0.2,0.2,barrierZWidth,0);
        worldcopy.spawnParticle(barrierParticle,barrierPos1.x,barrierPos2.y,(barrierPos1.z+barrierPos2.z)/2,300,0.2,0.2,barrierZWidth,0);

        worldcopy.spawnParticle(barrierParticle,barrierPos2.x,barrierPos1.y,(barrierPos1.z+barrierPos2.z)/2,300,0.2,0.2,barrierZWidth,0);
        worldcopy.spawnParticle(barrierParticle,barrierPos2.x,barrierPos2.y,(barrierPos1.z+barrierPos2.z)/2,300,0.2,0.2,barrierZWidth,0);

        worldcopy.spawnParticle(barrierParticle,(barrierPos1.x+barrierPos2.x)/2,barrierPos1.y,barrierPos1.z,300,barrierXWidth,0.2,0.2,0);
        worldcopy.spawnParticle(barrierParticle,(barrierPos1.x+barrierPos2.x)/2,barrierPos2.y,barrierPos1.z,300,barrierXWidth,0.2,0.2,0);

        worldcopy.spawnParticle(barrierParticle,(barrierPos1.x+barrierPos2.x)/2,barrierPos1.y,barrierPos2.z,300,barrierXWidth,0.2,0.2,0);
        worldcopy.spawnParticle(barrierParticle,(barrierPos1.x+barrierPos2.x)/2,barrierPos2.y,barrierPos2.z,300,barrierXWidth,0.2,0.2,0);

        worldcopy.spawnParticle(barrierParticle,barrierPos1.x,(barrierPos1.y+barrierPos2.y)/2,barrierPos1.z,300,0.2,barrierYWidth,0.2,0);
        worldcopy.spawnParticle(barrierParticle,barrierPos1.x,(barrierPos1.y+barrierPos2.y)/2,barrierPos2.z,300,0.2,barrierYWidth,0.2,0);

        worldcopy.spawnParticle(barrierParticle,barrierPos2.x,(barrierPos1.y+barrierPos2.y)/2,barrierPos1.z,300,0.2,barrierYWidth,0.2,0);
        worldcopy.spawnParticle(barrierParticle,barrierPos2.x,(barrierPos1.y+barrierPos2.y)/2,barrierPos2.z,300,0.2,barrierYWidth,0.2,0);
        /*worldcopy.spawnParticle(barrierParticle, barrierPos1.x, (barrierPos1.y+barrierPos2.y)/2, (barrierPos1.z+barrierPos2.z)/2, 3000, 0.2, barrierYWidth, barrierZWidth, 0);
        worldcopy.spawnParticle(barrierParticle, barrierPos2.x, (barrierPos1.y+barrierPos2.y)/2, (barrierPos1.z+barrierPos2.z)/2, 3000, 0.2, barrierYWidth, barrierZWidth, 0);

        worldcopy.spawnParticle(barrierParticle, (barrierPos1.x+barrierPos2.x)/2, (barrierPos1.y+barrierPos2.y)/2, barrierPos1.z, 3000, barrierXWidth, barrierYWidth, 0.2,0);
        worldcopy.spawnParticle(barrierParticle, (barrierPos1.x+barrierPos2.x)/2, (barrierPos1.y+barrierPos2.y)/2, barrierPos2.z, 3000, barrierXWidth, barrierYWidth, 0.2,0);*/
    }
    private void stopGame(String reason){
        Bukkit.getServer().getConsoleSender().sendMessage("Stop game was called");
        timer.cancel();
        timer.purge();

        while(!players.isEmpty()) {
            Player p = players.getFirst();
         //   p.sendMessage(reason);
            if (reason.equals("teamWin"))
                p.showTitle(Title.title(Component.text("Team " + teams.getFirst().name + " won", TextColor.color(teams.getFirst().color.asRGB())), Component.text("")));
            if (reason.equals("timerEnd"))
                p.showTitle(Title.title(Component.text("There is no winner"), Component.text("")));
            returnPlayer(p);
        }
        bar.removeAll();
        if(!spectators.isEmpty())spectators.clear();

        for(Team t:teams){
            t.isUsed=false;
        }
        teams.clear();

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

        board=Bukkit.getScoreboardManager().getNewScoreboard();
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
        if(bar!=null)if(bar.getPlayers().contains(player))bar.removePlayer(player);
        //teleport player to respawn location
        player.setGameMode(GameMode.SURVIVAL);
        player.teleportAsync(respawn_loc);
        player.setGameMode(GameMode.SURVIVAL);
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
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

        board=Bukkit.getScoreboardManager().getNewScoreboard();

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
        player.teleport(new Location(worldcopy,lobbypos.x,lobbypos.y,lobbypos.z));
        player.showTitle(Title.title(Component.text("You died", TextColor.color(255,0,0)),Component.text("")));

        player.setGameMode(GameMode.SPECTATOR);
        spectators.add(player);

        Team t=plugin.teamManager.getTeamByPlayer(player);
        t.killPlayer(player);
        if(t.players.size()==t.deadPlayers.size() || t.players.isEmpty()){
            t.isUsed=false;
            teams.remove(t);
        }
        if(teams.size() == 1) {
            stopGame("teamWin");
            return;
        }
        if (teams.isEmpty()) {
            stopGame("timerEnd");
        }
    }
    public void logoutPlayer(Player player){
        Team t=plugin.teamManager.getTeamByPlayer(player);
        t.removePlayer(player);
        if(t.players.size()==t.deadPlayers.size() || t.players.isEmpty()){
            t.isUsed=false;
            teams.remove(t);
        }
        returnPlayer(player);
        if(!isGameStarted)return;
        if(teams.size() == 1) {
            stopGame("teamWin");
            return;
        }
        if (teams.isEmpty()) {
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
        for(File f:files){
            deleteDirectory(f);
        }
    }
}
