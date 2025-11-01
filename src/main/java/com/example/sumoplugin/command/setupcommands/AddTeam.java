package com.example.sumoplugin.command.setupcommands;

import com.example.sumoplugin.arena.ArenaData;
import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.team.Team;
import com.example.sumoplugin.team.TeamData;
import com.example.sumoplugin.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddTeam extends SubCommand {
    Sumo plugin;
    public AddTeam(Sumo plugin){
        this.plugin=plugin;
    }
    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        String name=subCommandArgs[2].split(",")[0];
        String color=subCommandArgs[2].split(",")[1];
        for(Team t:plugin.teamManager.teams){
            if(t.name.equals(name)){
                sender.sendMessage("Team "+t.name+" already exists");
                return;
            }
        }
        plugin.teamManager.addTeam(name,color);
    }

    @Override
    public boolean canExecute(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if(subCommandArgs.length!=3)return false;
        return ((sender instanceof Player) && subCommandLabel.equals("arena") && subCommandArgs[1].equals("addteam"));
    }
}
