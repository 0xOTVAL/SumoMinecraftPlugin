package com.example.sumoplugin.eventlisteners;

import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.arena.ArenaManager;
import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.menu.SpectatorMenu;
import com.example.sumoplugin.menu.SumoAdminMenu;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class useListener implements Listener {
    public ArenaManager arenaManager;
    public Sumo plugin;
    public useListener(Sumo plugin){
        this.plugin=plugin;
        arenaManager=plugin.arenaManager;
    }
    @EventHandler
    public void PlayerRightClick(PlayerInteractEvent event){
        if(event.getAction().isLeftClick())return;
        //get arena and player
        Player player=event.getPlayer();
        Arena arena=arenaManager.getArenaByPlayer(player);
        if(event.getItem()==null)return;
        String itemName= PlainTextComponentSerializer.plainText().serialize(event.getItem().getItemMeta().itemName());
        if(itemName.equals("Sumo admin menu")){
            SumoAdminMenu m=new SumoAdminMenu(plugin);
            event.getPlayer().openInventory(m.menuInventory);
            event.setCancelled(true);
            return;
        }
        if(itemName.equals("Sumo spectator menu")){
            SpectatorMenu m=new SpectatorMenu(plugin);
            event.getPlayer().openInventory(m.menuInventory);
            event.setCancelled(true);
            return;
        }
        //we don't want to do anything if: 1)player is not on arena 2)arena is not started 3)game is already started
      /*  if(arena==null)return;
        if(!arena.isStarted)return;
        if(arena.isGameStarted)return;
        if(event.getItem()==null)return;
        //open admin menu

        if(event.getItem().getType()== Material.DIAMOND){
            player.sendMessage(arena.startGame());
            return;
        }
        if(event.getItem().getType()== Material.SLIME_BALL ) {
            if(arena.isGameStarted)arena.logoutPlayer(player);
            else arena.returnPlayer(player);
            return;
        }
        if(arena.getTeamByItem(event.getItem())==null)return;
        //get previous team and target team
        Team prev_team=arena.getTeamByPlayer(player);
        Team team=arena.getTeamByItem(event.getItem());
        //do nothing if player is already in target team
        if(prev_team==team)return;
        //remove player from previous team
        if(prev_team!=null)prev_team.removePlayer(player);
        //add player to target team
        team.addPlayer(player);
        //set player armor to armor, colored by team color
        player.getInventory().setArmorContents(team.armor);
        //send message to player
        player.sendMessage("You joined "+team.name+" team");
        event.setCancelled(true);*/
    }
}
