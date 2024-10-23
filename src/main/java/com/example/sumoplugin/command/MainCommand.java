package com.example.sumoplugin.command;

import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.command.playercommands.ArenaList;
import com.example.sumoplugin.command.playercommands.JoinArena;
import com.example.sumoplugin.command.playercommands.LeaveArena;
import com.example.sumoplugin.command.playercommands.StartArenaGame;
import com.example.sumoplugin.command.setupcommands.*;

public class MainCommand {
    private final Sumo plugin;
    public CommandExecutorBase base;
    public MainCommand(Sumo plugin){
        this.plugin=plugin;
        this.base=new CommandExecutorBase();
        this.initCommands();
    }
    private void initCommands(){
        base.addSubCommand(new SetPos1(plugin));
        base.addSubCommand(new SetPos2(plugin));
        base.addSubCommand(new CreateArena(plugin));
        base.addSubCommand(new SaveArena(plugin));
        base.addSubCommand(new AddTeam(plugin));
        base.addSubCommand(new SetTeamSpawn(plugin));
        base.addSubCommand(new JoinArena(plugin));
        base.addSubCommand(new StartArena(plugin));
        base.addSubCommand(new StartArenaGame(plugin));
        base.addSubCommand(new StopArena(plugin));
        base.addSubCommand(new LeaveArena(plugin));
        base.addSubCommand(new ArenaList(plugin));
    }
}
