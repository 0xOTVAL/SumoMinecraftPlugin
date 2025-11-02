package com.example.sumoplugin.team;

import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.arena.ArenaData;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeamManager {
    public ArrayList<Team> teams;
    Sumo plugin;
    public TeamManager(Sumo plugin){
        this.plugin=plugin;
        teams=new ArrayList<>();
    }
    public void addTeam(String name,String color){
        Team t=new Team(new TeamData(name,color));
        teams.add(t);
    }
    public void fromFile(File file){
        for(Arena a:plugin.arenaManager.arenas){
            if(a.isGameStarted || !a.teams.isEmpty())return;
        }
        teams.clear();
        try {
            String jsonstring = FileUtils.readFileToString(file, Charset.defaultCharset());
            Gson g = new Gson();
            ArrayList<TeamData> teamList = new ArrayList<>(Arrays.asList(g.fromJson(jsonstring, TeamData[].class)));
            for(TeamData td:teamList){
                Team t=new Team(td);
                for(String name: td.players){
                    Player p=plugin.getServer().getPlayerExact(name);
                    if(p==null)continue;
                    plugin.getServer().getConsoleSender().sendMessage(p.name());
                    t.addPlayer(p);
                }
                teams.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Team getTeamByPlayer(Player p){
        for(Team t: teams){
            if(t.players.contains(p))return t;
        }
        return null;
    }
    public Team getTeamByName(String name){
        for(Team t:teams){
            if(t.name.equals(name))return t;
        }
        return null;
    }
}
