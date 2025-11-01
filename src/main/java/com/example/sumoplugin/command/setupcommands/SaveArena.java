package com.example.sumoplugin.command.setupcommands;

import com.example.sumoplugin.arena.ArenaData;
import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.command.SubCommand;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.nio.charset.Charset;

public class SaveArena extends SubCommand {
    Sumo plugin;
    public SaveArena(Sumo plugin){
        this.plugin=plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        try {
            Gson g = new Gson();
            for(ArenaData d: plugin.arenaManager.arenaDataList){
                if(d.name.isEmpty() || d.spawnpos.isEmpty() || d.world.isEmpty() || d.pos1.isEmpty() || d.pos2.isEmpty() || d.lobbypos.isEmpty() || d.gameTime==-1){
                    sender.sendMessage("Arena "+d.name+" is incomplete, not saving");
                    return;
                }
            }
            String out= g.toJson(plugin.arenaManager.arenaDataList);
            File arenas = new File(plugin.getDataFolder(), "arena_list.json");
            FileUtils.writeStringToFile(arenas,out, Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
        }
        plugin.arenaManager.updateArenasData();
    }
    @Override
    public boolean canExecute(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if(subCommandArgs.length!=1)return false;
        return (subCommandLabel.equals("arena") && subCommandArgs[0].equals("save"));
    }
}
