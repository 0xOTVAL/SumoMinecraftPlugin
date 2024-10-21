package com.example.sumoPlugin;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaManager {
    public List<ArenaData> arenaDataList;
    public ArrayList<Arena> arenas=new ArrayList<>();
    public Location respawn_loc;

    ArenaManager(List<ArenaData> arenaDataList, Location respawn_loc){
        this.arenaDataList = arenaDataList;
        this.respawn_loc=respawn_loc;
        for(ArenaData d: arenaDataList){
            arenas.add(new Arena(d));
        }
    }
    public Arena getArenaByName(String name){
        for(Arena a: arenas){
            if(a.name.equals(name))return a;
        }
        return null;
    }
    public ArenaData getArenaDataByName(String name){
        for(ArenaData d:arenaDataList){
            if(d.name.equals(name))return d;
        }
        return null;
    }
    public Arena getArenaByPlayer(Player player){
        for(Arena a: arenas){
            if(a.players.contains(player))return a;
        }
        return null;
    }
    public void addArenaData(ArenaData arenaData){

    }
    public void updateArenasData(){

    }
}
