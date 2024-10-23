package com.example.sumoplugin.command.playercommands;

import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class StartArenaGame extends SubCommand {
    Sumo plugin;
    public StartArenaGame(Sumo plugin){
        this.plugin=plugin;
    }
    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        Arena arena=plugin.arenaManager.getArenaByName(subCommandArgs[0]);
        if(arena==null){
            sender.sendMessage("Arena "+subCommandArgs[0]+" does not exist");
            return;
        }
        sender.sendMessage(arena.startGame());
    }
    @Override
    public boolean canExecute(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if(subCommandArgs.length!=2)return false;
        return (subCommandLabel.equals("arena") && subCommandArgs[1].equals("startgame"));
    }
}
