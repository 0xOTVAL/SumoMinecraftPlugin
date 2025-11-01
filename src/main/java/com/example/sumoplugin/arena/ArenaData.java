package com.example.sumoplugin.arena;

import com.example.sumoplugin.team.TeamData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ArenaData {
    public String name="";
    public String world="";
    public String pos1="";
    public String pos2="";
    public String lobbypos="";
    public int gameTime=-1;
    public ArrayList<String> spawnpos=new ArrayList<>();

    public ArenaData(String name, String world, String pos1, String pos2, String lobbypos, String[] spawnpos,int gameTime){
        this.name=name;
        this.world=world;
        this.pos1=pos1;
        this.pos2=pos2;
        this.lobbypos=lobbypos;
        this.spawnpos=Arrays.stream(spawnpos).collect(Collectors.toCollection(ArrayList::new));
        this.gameTime=gameTime;
    }
    public ArenaData(){

    }
}
