package com.example.sumoPlugin.command;

import com.example.sumoPlugin.Sumo;
import com.example.sumoPlugin.command.setupcommands.*;

public class MainCommand {
    private final Sumo plugin;
    private final CommandExecutorBase base;
    MainCommand(Sumo plugin){
        this.plugin=plugin;
        this.base=new CommandExecutorBase();
        this.initCommands();
    }
    private void initCommands(){
        base.addSubCommand(new setPos1(plugin));
        base.addSubCommand(new setPos2(plugin));
        base.addSubCommand(new createArena(plugin));
        base.addSubCommand(new saveArena(plugin));
        base.addSubCommand(new addTeam(plugin));
        base.addSubCommand(new setTeamSpawn(plugin));
    }
}
