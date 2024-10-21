package com.example.sumoPlugin.command;

import com.example.sumoPlugin.Sumo;
import com.example.sumoPlugin.command.setupcommands.*;

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
