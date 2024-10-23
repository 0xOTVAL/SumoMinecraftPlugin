package com.example.sumoplugin.command.playercommands;

import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class LeaveArena extends SubCommand {
    Sumo plugin;
    public LeaveArena(Sumo plugin){
        this.plugin=plugin;
    }
    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        Player player= Objects.requireNonNull(Bukkit.getPlayer(sender.getName()));
        Arena arena=plugin.arenaManager.getArenaByPlayer(player);
        if(arena==null){
            sender.sendMessage("You are not in game");
            return;
        }
        if(arena.isGameStarted)arena.logoutPlayer(player);
        else arena.returnPlayer(player);
    }
    @Override
    public boolean canExecute(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        return ((sender instanceof Player) && subCommandLabel.equals("leave"));
    }
}
