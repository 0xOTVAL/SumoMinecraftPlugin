package com.example.sumoPlugin.command.setupcommands;

import com.example.sumoPlugin.*;
import com.example.sumoPlugin.command.SubCommand;
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
            for(TeamData td: d.teams){
                sender.sendMessage(td.name+" "+td.color+" "+td.spawnpos);
            }
        }
        for(Arena a:plugin.arenaManager.arenas){
            for(Team t:a.teams){
                sender.sendMessage(t.banner.displayName());
            }
        }
    }
    @Override
    public boolean canExecute(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if(subCommandArgs.length!=1)return false;
        return (subCommandLabel.equals("arena") && subCommandArgs[0].equals("list"));
    }
}
