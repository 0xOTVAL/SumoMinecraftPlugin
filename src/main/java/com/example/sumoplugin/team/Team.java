package com.example.sumoplugin.team;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Team {
    public String name;
    public Vector3f spawnpos;
    public Color color;
    public NamedTextColor chatColor;
    public ItemStack banner;
    public ArrayList<Player> players;
    public ArrayList<Player> deadPlayers;
    public ItemStack[] armor;
    public boolean isUsed=false;

    public Team(TeamData data) {
        name=data.name;
        color = switch (data.color) {
            case "RED" -> Color.RED;
            case "GREEN" -> Color.GREEN;
            case "BLUE" -> Color.BLUE;
            case "WHITE" -> Color.WHITE;
            case "YELLOW" -> Color.YELLOW;
            case "BLACK" -> Color.BLACK;
            case "ORANGE" -> Color.ORANGE;
            case "LIME" -> Color.LIME;
            default -> Color.FUCHSIA;
        };
        chatColor = switch (data.color) {
            case "RED" -> NamedTextColor.DARK_RED;
            case "GREEN" -> NamedTextColor.DARK_GREEN;
            case "BLUE" -> NamedTextColor.BLUE;
            case "WHITE" ->NamedTextColor.WHITE;
            case "YELLOW" -> NamedTextColor.YELLOW;
            case "BLACK" -> NamedTextColor.BLACK;
            case "ORANGE" -> NamedTextColor.GREEN;
            case "LIME" -> NamedTextColor.RED;
            default -> NamedTextColor.LIGHT_PURPLE;
        };
        banner = switch (data.color) {
            case "RED" -> new ItemStack(Material.RED_BANNER);
            case "GREEN" -> new ItemStack(Material.GREEN_BANNER);
            case "BLUE" -> new ItemStack(Material.BLUE_BANNER);
            case "BLACK" -> new ItemStack(Material.BLACK_BANNER);
            case "WHITE" -> new ItemStack(Material.WHITE_BANNER);
            case "YELLOW" -> new ItemStack(Material.YELLOW_BANNER);
            case "LIME" -> new ItemStack(Material.LIME_BANNER);
            default -> new ItemStack(Material.ORANGE_BANNER);

        };
        ItemMeta bannermeta=banner.getItemMeta();
        bannermeta.setDisplayName("Join team "+name);
        banner.setItemMeta(bannermeta);
        players=new ArrayList<Player>();
        deadPlayers=new ArrayList<Player>();

        ItemStack helmet=new ItemStack(Material.LEATHER_HELMET);
        ItemStack boots=new ItemStack(Material.LEATHER_BOOTS);
        ItemStack chestplate=new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggins=new ItemStack(Material.LEATHER_LEGGINGS);

        LeatherArmorMeta helmetMeta= (LeatherArmorMeta) helmet.getItemMeta();
        helmetMeta.setColor(color);
        helmet.addEnchantment(Enchantment.BINDING_CURSE,1);
        helmet.setItemMeta(helmetMeta);

        LeatherArmorMeta chestplateMeta =(LeatherArmorMeta) chestplate.getItemMeta();
        chestplateMeta.setColor(color);
        chestplate.addEnchantment(Enchantment.BINDING_CURSE,1);
        chestplate.setItemMeta(chestplateMeta);

        LeatherArmorMeta legginsMeta=(LeatherArmorMeta) leggins.getItemMeta();
        legginsMeta.setColor(color);
        leggins.addEnchantment(Enchantment.BINDING_CURSE,1);
        leggins.setItemMeta(legginsMeta);

        LeatherArmorMeta bootsMeata=(LeatherArmorMeta) boots.getItemMeta();
        bootsMeata.setColor(color);
        boots.addEnchantment(Enchantment.BINDING_CURSE,1);
        boots.setItemMeta(bootsMeata);

        armor=new ItemStack[]{boots,leggins,chestplate,helmet};

    }

    public void killPlayer(Player p){
        deadPlayers.add(p);
    }
    public void reset(){
        deadPlayers.clear();
    }
    public void addPlayer(Player player){
        if(!players.contains(player))players.add(player);
    }
    public void removePlayer(Player player){
        if(players.contains(player))players.remove(player);
    }
}
