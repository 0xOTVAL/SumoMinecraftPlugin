package com.example.sumoPlugin;

import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class useListener implements Listener {
    private ArenaManager arenaManager;
    useListener(ArenaManager arenaManager){
        this.arenaManager=arenaManager;
    }
    public <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
    private Arena getArenaByPlayer( Player player) {
        for (Map.Entry e: arenaManager.players.entrySet()) {
            for(Object p: (ArrayList)e.getValue()){
                if(p==player)return (Arena) e.getKey();
            }
        }
        return null;
    }
    public Team findTeambyColor(Arena arena, Color color){
        for(Team i: arena.teams){
            if(i.GetTeamColor()==color)return i;
        }
        return null;
    }
    private Team findTeamByPlayer(Arena arena,Player player){
        for (Map.Entry e: arenaManager.arenasTeams.get(arena).entrySet()) {
            for(Object p: (ArrayList)e.getValue()){
                if(p==player)return (Team) e.getKey();
            }
        }
        return null;
    }
    @EventHandler
    public void PlayerRightClick(PlayerInteractEvent event){
        Arena arena=getArenaByPlayer(event.getPlayer());
        if(arena==null)return;
        if(arenaManager.isArenaGameStarted.get(arena))return;
        Team team=findTeamByPlayer(arena,event.getPlayer());
        if(team!=null)arenaManager.arenasTeams.get(arena).get(team).remove(event.getPlayer());
        switch (event.getItem().getType()){
            case RED_BANNER -> arenaManager.arenasTeams.get(arena).get(findTeambyColor(arena,Color.RED)).add(event.getPlayer());
            case BLUE_BANNER -> arenaManager.arenasTeams.get(arena).get(findTeambyColor(arena,Color.BLUE)).add(event.getPlayer());
            case GREEN_BANNER -> arenaManager.arenasTeams.get(arena).get(findTeambyColor(arena,Color.GREEN)).add(event.getPlayer());
            case BLACK_BANNER -> arenaManager.arenasTeams.get(arena).get(findTeambyColor(arena,Color.BLACK)).add(event.getPlayer());
            case WHITE_BANNER -> arenaManager.arenasTeams.get(arena).get(findTeambyColor(arena,Color.WHITE)).add(event.getPlayer());
            case LIME_BANNER -> arenaManager.arenasTeams.get(arena).get(findTeambyColor(arena,Color.LIME)).add(event.getPlayer());
            case ORANGE_BANNER -> arenaManager.arenasTeams.get(arena).get(findTeambyColor(arena,Color.ORANGE)).add(event.getPlayer());
            case YELLOW_BANNER -> arenaManager.arenasTeams.get(arena).get(findTeambyColor(arena,Color.YELLOW)).add(event.getPlayer());
            default -> {
                return;
            }
        }
        team=findTeamByPlayer(arena,event.getPlayer());
        ItemStack helmet=new ItemStack(Material.LEATHER_HELMET);
        ItemStack boots=new ItemStack(Material.LEATHER_BOOTS);
        ItemStack chestplate=new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggins=new ItemStack(Material.LEATHER_LEGGINGS);

        LeatherArmorMeta helmetMeta= (LeatherArmorMeta) helmet.getItemMeta();
        helmetMeta.setColor(team.GetTeamColor());
        helmet.addEnchantment(Enchantment.BINDING_CURSE,1);
        helmet.setItemMeta(helmetMeta);

        LeatherArmorMeta chestplateMeta =(LeatherArmorMeta) chestplate.getItemMeta();
        chestplateMeta.setColor(team.GetTeamColor());
        chestplate.addEnchantment(Enchantment.BINDING_CURSE,1);
        chestplate.setItemMeta(chestplateMeta);

        LeatherArmorMeta legginsMeta=(LeatherArmorMeta) leggins.getItemMeta();
        legginsMeta.setColor(team.GetTeamColor());
        leggins.addEnchantment(Enchantment.BINDING_CURSE,1);
        leggins.setItemMeta(legginsMeta);

        LeatherArmorMeta bootsMeata=(LeatherArmorMeta) boots.getItemMeta();
        bootsMeata.setColor(team.GetTeamColor());
        boots.addEnchantment(Enchantment.BINDING_CURSE,1);
        boots.setItemMeta(bootsMeata);

        event.getPlayer().getInventory().setBoots(boots);
        event.getPlayer().getInventory().setHelmet(helmet);
        event.getPlayer().getInventory().setChestplate(chestplate);
        event.getPlayer().getInventory().setLeggings(leggins);
        event.getPlayer().sendMessage(arenaManager.arenasTeams.get(arena).toString());

    }
}
