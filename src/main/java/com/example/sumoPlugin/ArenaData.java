package com.example.sumoPlugin;

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
    public ArrayList<TeamData> teams= new ArrayList<>();

    public ArenaData(String name, String world, String pos1, String pos2, String lobbypos, TeamData[] teams,int gameTime){
        this.name=name;
        this.world=world;
        this.pos1=pos1;
        this.pos2=pos2;
        this.lobbypos=lobbypos;
        this.teams= Arrays.stream(teams).collect(Collectors.toCollection(ArrayList::new));
        this.gameTime=gameTime;
    }
    public ArenaData(){

    }
    public TeamData getTeamDataByName(String name){
        for(TeamData t: teams){
            if(t.name.equals(name))return t;
        }
        return null;
    }
}
