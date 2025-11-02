package com.example.sumoplugin.menu;

import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.arena.ArenaManager;
import com.example.sumoplugin.team.Team;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

public class SumoAdminMenu {
    public Inventory menuInventory;
    Sumo plugin;
    public SumoAdminMenu(Sumo plugin){
        this.plugin=plugin;
        menuInventory=Bukkit.createInventory(null,54, Component.text("Sumo admin menu"));
        menuInventory.clear();
        setupStartPage();
    }
    private void addMenuItem(int row,int column, Material type,String name){
        ItemStack item=new ItemStack(type,1);
        ItemMeta meta=item.getItemMeta();
        meta.setItemName(name);
        item.setItemMeta(meta);
        menuInventory.setItem(column+row*9,item);
    }
    private void addMenuItem(int row,int column, Material type,String name, List<String> lore){
        ItemStack item=new ItemStack(type,1);
        ItemMeta meta=item.getItemMeta();
        meta.setItemName(name);
        item.setItemMeta(meta);
        item.setLore(lore);
        menuInventory.setItem(column+row*9,item);
    }
    public void setupStartPage(){
        addMenuItem(0,0,Material.NETHERITE_SWORD,"Арены");
        addMenuItem(0,1,Material.RED_BANNER,"Команды");
    }
    public void setupTeamsPage(){
        menuInventory=Bukkit.createInventory(null,54, Component.text("Sumo admin menu teams"));
        addMenuItem(0,0,Material.PAPER,"Импортировать");
        for(int i=0;i<plugin.teamManager.teams.size();i++){
            ArrayList<String> players=new ArrayList<>();
            for(Player p: plugin.teamManager.teams.get(i).players){
                players.add(p.getName());
            }
            addMenuItem((i+1)/9,(i+1)%9,Material.PLAYER_HEAD,plugin.teamManager.teams.get(i).name,players);
        }
    }
    public void setupArenaPage(){
        ArenaManager manager=plugin.arenaManager;
        ArrayList<Arena> arenas=manager.arenas;
        IntStream.range(0, arenas.size()).forEach(i -> {
            Arena a = arenas.get(i);
            if (!a.isStarted) addMenuItem(i/9, i % 9, Material.RED_WOOL, a.name);
            if (a.isStarted && !a.isGameStarted) addMenuItem(i/9, i % 9, Material.GREEN_WOOL, a.name);
            if (a.isGameStarted) addMenuItem(i/9, i % 9, Material.BLUE_WOOL, a.name);
        });
    }
    public void setupTeamActionsPage(String teamName){
        Team t=plugin.teamManager.getTeamByName(teamName);
        menuInventory=Bukkit.createInventory(null,54, Component.text("Sumo admin menu:team:"+teamName));
        ArrayList<Player> players=new ArrayList<>(Bukkit.getServer().getOnlinePlayers());

        for(int i=0;i<Bukkit.getServer().getOnlinePlayers().size();i++){
            Player p= players.get(i);
            if(plugin.teamManager.getTeamByPlayer(p)!=null && plugin.teamManager.getTeamByPlayer(p)!=t)addMenuItem((i)/9,(i)%9,Material.SKELETON_SKULL,p.getName(),List.of("Игрок в команде "+plugin.teamManager.getTeamByPlayer(p)));
            else if(plugin.teamManager.getTeamByPlayer(p)==t)addMenuItem((i)/9,(i)%9,Material.ZOMBIE_HEAD,p.getName(),List.of("Нажми чтобы удалить из команды"));
            else addMenuItem((i)/9,(i)%9,Material.PLAYER_HEAD,p.getName(),List.of("Нажми чтобы добваить в команду"));
        }
    }
    public void setupArenaActionsPage(String arenaName){
        menuInventory=Bukkit.createInventory(null,54, Component.text("Sumo admin menu:arena:"+arenaName));
        Arena a=plugin.arenaManager.getArenaByName(arenaName);
        if(!a.isStarted){addMenuItem(0,0,Material.GREEN_WOOL,"Запустить арену");return;}
        if(a.isStarted)addMenuItem(0,0,Material.RED_WOOL,"Остановить арену");
        if(!a.isGameStarted)addMenuItem(0,1,Material.BLUE_WOOL,"Начать игру");
        for(int i=0;i<plugin.teamManager.teams.size();i++){
            if(plugin.teamManager.teams.get(i).players.isEmpty())addMenuItem((i+2)/9,(i+2)%9,Material.SKELETON_SKULL,plugin.teamManager.teams.get(i).name,List.of("В команде нет игроков"));
            else if(!plugin.teamManager.teams.get(i).isUsed)addMenuItem((i+2)/9,(i+2)%9,Material.PLAYER_HEAD,plugin.teamManager.teams.get(i).name,List.of("Нажми чтобы отправить эту команду на арену"));
            else if(plugin.arenaManager.getArenaByName(arenaName).teams.contains(plugin.teamManager.teams.get(i)))addMenuItem((i+2)/9,(i+2)%9,Material.ZOMBIE_HEAD,plugin.teamManager.teams.get(i).name,List.of("Нажми чтобы убрать команду с арены"));
            else addMenuItem((i+2)/9,(i+2)%9,Material.SKELETON_SKULL,plugin.teamManager.teams.get(i).name,List.of("Команда на другой арене"));
        }
    }


}
