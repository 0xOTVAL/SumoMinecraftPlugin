package com.example.sumoplugin.command.playercommands;

import com.example.sumoplugin.*;
import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.command.SubCommand;
import com.example.sumoplugin.arena.ArenaData;
import com.example.sumoplugin.team.Team;
import com.example.sumoplugin.team.TeamData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ArenaList  extends SubCommand {
    Sumo plugin;
    public ArenaList(Sumo plugin){
        this.plugin=plugin;
    }
    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        for(ArenaData d: plugin.arenaManager.arenaDataList){
            sender.sendMessage(d.name+" "+d.world+" "+d.pos1+" "+d.pos2+" "+d.lobbypos+" "+d.gameTime);
        }
    }
    @Override
    public boolean canExecute(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if(subCommandArgs.length!=1)return false;
        return (subCommandLabel.equals("arena") && subCommandArgs[0].equals("list"));
    }
}
