package com.example.sumoPlugin.command.setupcommands;

import com.example.sumoPlugin.ArenaData;
import com.example.sumoPlugin.Sumo;
import com.example.sumoPlugin.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Objects;

public class setPos1 extends SubCommand {
    Sumo plugin;
    public setPos1(Sumo plugin){
        this.plugin=plugin;
    }
    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        ArenaData arenaData=plugin.arenaManager.getArenaDataByName(subCommandArgs[0]);
        Player player= Objects.requireNonNull(Bukkit.getPlayer(sender.getName()));
        Vector playerPos=player.getLocation().toVector();
        if(arenaData.world.isEmpty()){
            arenaData.world= player.getWorld().getName();
        }
        if(!arenaData.world.equals(player.getWorld().getName())){
            sender.sendMessage("Pos1 and pos2 must be in same world");
            return;
        }
        arenaData.pos1= playerPos.toString();
    }
}
