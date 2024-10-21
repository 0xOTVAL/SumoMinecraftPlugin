package com.example.sumoPlugin.command.setupcommands;

import com.example.sumoPlugin.ArenaData;
import com.example.sumoPlugin.Sumo;
import com.example.sumoPlugin.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CreateArena extends SubCommand {
    Sumo plugin;
    public CreateArena(Sumo plugin){
        this.plugin=plugin;
    }
    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        ArenaData arenaData=new ArenaData();
        arenaData.name=subCommandArgs[0];
        if(plugin.arenaManager.getArenaDataByName(arenaData.name)!=null){
            sender.sendMessage("Arena "+arenaData.name+" already exists");
            return;
        }
        plugin.arenaManager.addArenaData(arenaData);
    }
    @Override
    public boolean canExecute(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if(subCommandArgs.length!=2)return false;
        return (subCommandLabel.equals("arena") && subCommandArgs[1].equals("create"));
    }
}
