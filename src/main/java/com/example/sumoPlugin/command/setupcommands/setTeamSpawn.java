package com.example.sumoPlugin.command.setupcommands;

import com.example.sumoPlugin.Sumo;
import com.example.sumoPlugin.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class setTeamSpawn extends SubCommand {
    Sumo plugin;
    public setTeamSpawn(Sumo plugin){
        this.plugin=plugin;
    }
    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {

    }
}
