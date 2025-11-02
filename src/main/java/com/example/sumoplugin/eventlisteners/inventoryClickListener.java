package com.example.sumoplugin.eventlisteners;

import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.menu.SumoAdminMenu;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import java.io.File;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class inventoryClickListener implements Listener {
    public Sumo plugin;
    public inventoryClickListener(Sumo plugin){
        this.plugin=plugin;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getCurrentItem()==null || event.getCurrentItem().getItemMeta()==null)return;
        if(event.getView().getTitle().equals("Sumo spectator menu")){
            event.setCancelled(true);
            String itemName=PlainTextComponentSerializer.plainText().serialize(event.getCurrentItem().getItemMeta().itemName());
            Material itemType=event.getCurrentItem().getType();
            if(itemType==Material.GREEN_WOOL){
                if(plugin.arenaManager.getArenaByPlayer((Player) event.getWhoClicked())!=null)plugin.arenaManager.getArenaByPlayer((Player) event.getWhoClicked()).delSpec((Player) event.getWhoClicked());
                plugin.arenaManager.getArenaByName(itemName).addSpec((Player) event.getWhoClicked());
            }
            if(itemType==Material.END_ROD){
                if(plugin.arenaManager.getArenaByPlayer((Player) event.getWhoClicked())!=null)plugin.arenaManager.getArenaByPlayer((Player) event.getWhoClicked()).delSpec((Player) event.getWhoClicked());
            }
        }
        if(event.getInventory().getSize()!=54 || !event.getView().getTitle().contains("Sumo admin menu"))return;
        event.setCancelled(true);
        SumoAdminMenu m=new SumoAdminMenu(plugin);
        String itemName=PlainTextComponentSerializer.plainText().serialize(event.getCurrentItem().getItemMeta().itemName());
        Material itemType=event.getCurrentItem().getType();
        String arenaName="";
        if(event.getView().getTitle().contains(":") && event.getView().getTitle().contains("arena"))arenaName=event.getView().getTitle().split(":")[2];
        String teamName="";
        if(event.getView().getTitle().contains(":") && event.getView().getTitle().contains("team"))teamName=event.getView().getTitle().split(":")[2];
       // Bukkit.getServer().getConsoleSender().sendMessage(event.getView().getTitle()+"\\"+arenaName);
        event.getInventory().clear();
        switch (itemName){
            case "Арены":
                m.setupArenaPage();
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().openInventory(m.menuInventory);
                return;
            case "Остановить арену":
                plugin.arenaManager.getArenaByName(arenaName).stop();
                m.setupArenaActionsPage(arenaName);
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().openInventory(m.menuInventory);
                return;
            case "Запустить арену":
                plugin.arenaManager.getArenaByName(arenaName).start();
                m.setupArenaActionsPage(arenaName);
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().openInventory(m.menuInventory);
                return;
            case "Начать игру":
                event.getWhoClicked().closeInventory();
                plugin.arenaManager.getArenaByName(arenaName).startGame();
                return;
            case "Команды":
                m.setupTeamsPage();
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().openInventory(m.menuInventory);
                return;
            case "Импортировать":
                event.getWhoClicked().closeInventory();
                plugin.teamManager.fromFile(new File(plugin.getDataFolder(),"teams.json"));
                return;
        }
        if(event.getView().getTitle().contains("teams")){
            if(itemType==Material.PLAYER_HEAD){
                m.setupTeamActionsPage(itemName);
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().openInventory(m.menuInventory);
            }
        }
        switch (itemType) {
            case GREEN_WOOL, BLUE_WOOL, RED_WOOL:
                m.setupArenaActionsPage(itemName);
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().openInventory(m.menuInventory);
                break;
        }
        if(!teamName.isEmpty()){
            switch (itemType){
                case PLAYER_HEAD:
                    plugin.teamManager.getTeamByName(teamName).addPlayer(Bukkit.getPlayerExact(itemName));
                    m.setupTeamActionsPage(teamName);
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().openInventory(m.menuInventory);
                    return;
                case ZOMBIE_HEAD:
                    plugin.teamManager.getTeamByName(teamName).removePlayer(Bukkit.getPlayerExact(itemName));
                    m.setupTeamActionsPage(teamName);
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().openInventory(m.menuInventory);
                    return;
                case SKELETON_SKULL:
                    m.setupTeamActionsPage(teamName);
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().openInventory(m.menuInventory);
                    return;
            }
        }
        if(!arenaName.isEmpty()) {
            switch (itemType) {
                case PLAYER_HEAD:
                    plugin.arenaManager.getArenaByName(arenaName).addTeam(plugin.teamManager.getTeamByName(itemName));
                    m.setupArenaActionsPage(arenaName);
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().openInventory(m.menuInventory);
                    break;
                case ZOMBIE_HEAD:
                    plugin.arenaManager.getArenaByName(arenaName).delTeam(plugin.teamManager.getTeamByName(itemName));
                    m.setupArenaActionsPage(arenaName);
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().openInventory(m.menuInventory);
                    break;
                case SKELETON_SKULL:
                    m.setupArenaActionsPage(arenaName);
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().openInventory(m.menuInventory);
                    break;
            }
        }
    }
}
