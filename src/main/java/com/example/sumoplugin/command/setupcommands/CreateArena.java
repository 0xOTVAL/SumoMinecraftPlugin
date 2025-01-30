package com.example.sumoplugin.command.setupcommands;

import com.example.sumoplugin.arena.ArenaData;
import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.command.SubCommand;
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
        sender.sendMessage("Arena "+arenaData.name+" created");
    }
    @Override
    public boolean canExecute(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if(subCommandArgs.length!=2)return false;
        return (subCommandLabel.equals("arena") && subCommandArgs[1].equals("create"));
    }
}
