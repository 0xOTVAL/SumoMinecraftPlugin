package com.example.sumoPlugin.command.setupcommands;

import com.example.sumoPlugin.ArenaData;
import com.example.sumoPlugin.Sumo;
import com.example.sumoPlugin.TeamData;
import com.example.sumoPlugin.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class addTeam extends SubCommand {
    Sumo plugin;
    public addTeam(Sumo plugin){
        this.plugin=plugin;
    }
    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        ArenaData arenaData=plugin.arenaManager.getArenaDataByName(subCommandArgs[0]);
        TeamData teamData=new TeamData(subCommandArgs[1],subCommandArgs[2],"");
        for(TeamData t:arenaData.teams){
            if(t.name.equals(teamData.name)){
                sender.sendMessage("Team "+t.name+" already exists");
                return;
            }
        }
        arenaData.teams.add(teamData);
    }
}
