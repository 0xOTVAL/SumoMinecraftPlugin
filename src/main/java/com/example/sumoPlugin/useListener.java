package com.example.sumoPlugin;

import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

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
    public Team findTeambyColor(Arena arena, Color color){
        for(Team i: arena.teams){
            if(i.GetTeamColor()==color)return i;
        }
        return null;
    }
    @EventHandler
    public void PlayerRightClick(PlayerInteractEvent event){
        if(!arenaManager.players.containsValue(event.getPlayer()))return;
        Arena arena=getKeyByValue(arenaManager.players,event.getPlayer());
        if(arenaManager.isArenaGameStarted.get(arena))return;
        arenaManager.arenasTeams.get(arena).remove(getKeyByValue(arenaManager.arenasTeams.get(arena),event.getPlayer()),event.getPlayer());
        switch (event.getItem().getType()){
            case RED_BANNER -> arenaManager.arenasTeams.get(arena).put(findTeambyColor(arena,Color.RED),event.getPlayer());
            case BLUE_BANNER -> arenaManager.arenasTeams.get(arena).put(findTeambyColor(arena,Color.BLUE),event.getPlayer());
            case GREEN_BANNER -> arenaManager.arenasTeams.get(arena).put(findTeambyColor(arena,Color.GREEN),event.getPlayer());
            case BLACK_BANNER -> arenaManager.arenasTeams.get(arena).put(findTeambyColor(arena,Color.BLACK),event.getPlayer());
            case WHITE_BANNER -> arenaManager.arenasTeams.get(arena).put(findTeambyColor(arena,Color.WHITE),event.getPlayer());
            case LIME_BANNER -> arenaManager.arenasTeams.get(arena).put(findTeambyColor(arena,Color.LIME),event.getPlayer());
            case ORANGE_BANNER -> arenaManager.arenasTeams.get(arena).put(findTeambyColor(arena,Color.ORANGE),event.getPlayer());
            case YELLOW_BANNER -> arenaManager.arenasTeams.get(arena).put(findTeambyColor(arena,Color.YELLOW),event.getPlayer());
            default -> {
                return;
            }
        }
        ItemStack helmet=new ItemStack(Material.LEATHER_HELMET);
        ItemStack boots=new ItemStack(Material.LEATHER_BOOTS);
        ItemStack chestplate=new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggins=new ItemStack(Material.LEATHER_LEGGINGS);

        LeatherArmorMeta helmetMeta= (LeatherArmorMeta) helmet.getItemMeta();
        helmetMeta.setColor(getKeyByValue(arenaManager.arenasTeams.get(arena),event.getPlayer()).GetTeamColor());
        helmet.addEnchantment(Enchantment.BINDING_CURSE,1);
        helmet.setItemMeta(helmetMeta);

        LeatherArmorMeta chestplateMeta =(LeatherArmorMeta) chestplate.getItemMeta();
        chestplateMeta.setColor(getKeyByValue(arenaManager.arenasTeams.get(arena),event.getPlayer()).GetTeamColor());
        chestplate.addEnchantment(Enchantment.BINDING_CURSE,1);
        chestplate.setItemMeta(chestplateMeta);

        LeatherArmorMeta legginsMeta=(LeatherArmorMeta) leggins.getItemMeta();
        legginsMeta.setColor(getKeyByValue(arenaManager.arenasTeams.get(arena),event.getPlayer()).GetTeamColor());
        leggins.addEnchantment(Enchantment.BINDING_CURSE,1);
        leggins.setItemMeta(legginsMeta);

        LeatherArmorMeta bootsMeata=(LeatherArmorMeta) boots.getItemMeta();
        bootsMeata.setColor(getKeyByValue(arenaManager.arenasTeams.get(arena),event.getPlayer()).GetTeamColor());
        boots.addEnchantment(Enchantment.BINDING_CURSE,1);
        boots.setItemMeta(bootsMeata);

        event.getPlayer().getInventory().setBoots(boots);
        event.getPlayer().getInventory().setHelmet(helmet);
        event.getPlayer().getInventory().setChestplate(chestplate);
        event.getPlayer().getInventory().setLeggings(leggins);
        event.getPlayer().sendMessage(arenaManager.arenasTeams.get(arena).toString());
    }
}
