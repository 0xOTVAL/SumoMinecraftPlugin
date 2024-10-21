package com.example.sumoPlugin.command.setupcommands;

import com.example.sumoPlugin.ArenaData;
import com.example.sumoPlugin.Sumo;
import com.example.sumoPlugin.TeamData;
import com.example.sumoPlugin.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Objects;

public class SetTeamSpawn extends SubCommand {
    Sumo plugin;
    public SetTeamSpawn(Sumo plugin){
        this.plugin=plugin;
    }
    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        ArenaData arenaData=plugin.arenaManager.getArenaDataByName(subCommandArgs[0]);
        if(arenaData==null){
            sender.sendMessage("Arena "+subCommandArgs[0]+" does not exist");
            return;
        }
        TeamData teamData=arenaData.getTeamDataByName(subCommandArgs[2]);
        Player player= Objects.requireNonNull(Bukkit.getPlayer(sender.getName()));
        Vector playerPos=player.getLocation().toVector();
        if(arenaData.world.isEmpty()){
            arenaData.world= player.getWorld().getName();
        }
        if(!arenaData.world.equals(player.getWorld().getName())){
            sender.sendMessage("Team spawn must be in same world");
            return;
        }
        teamData.spawnpos= playerPos.toString();
    }
    @Override
    public boolean canExecute(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if(subCommandArgs.length!=2)return false;
        return ((sender instanceof Player) && subCommandLabel.equals("arena") && subCommandArgs[1].equals("teamspawn"));
    }
}
