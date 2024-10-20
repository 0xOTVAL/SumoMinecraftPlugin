package com.example.sumoPlugin;

import com.google.gson.Gson;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.apache.commons.io.FileUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class sumoCommandApi implements BasicCommand {
    private List<ArenaData> arenas_list;
    private File dataFolder;
    private ArenaManager arenaManager;
    sumoCommandApi(File dataFolder, List<ArenaData> arenas_list, ArenaManager arenaManager){
        this.dataFolder=dataFolder;
        this.arenas_list=arenas_list;
        this.arenaManager=arenaManager;
    }
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args){
       /* File arenas = new File(dataFolder, "arena_list.json");
        try {
            String jsonstring = FileUtils.readFileToString(arenas);
            Gson g = new Gson();
            arenas_list = new ArrayList(Arrays.asList(g.fromJson(jsonstring, Arena[].class)));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        if(args.length==0) {
            stack.getSender().sendMessage("kill yourself");
            return;
        }
        if(args[0].equals("arena")) {
            if(args.length<2) {
                stack.getSender().sendMessage(String.valueOf(arenas_list.size()));
                for (ArenaData i : arenas_list) {
                    stack.getSender().sendMessage(i.teams.get(0).name);
                }
                return;
            }
            switch (args[1]){
                case "create": {
                    if (args.length<3) {
                        stack.getSender().sendMessage("specify name or kill yourself");
                        return;
                    }
                    ArenaData arena = new ArenaData();
                    arena.name = args[2];
                    arenas_list.add(arena);
                    stack.getSender().sendMessage("Arena created(maybe)");
                    }
                    break;
                default:
                    if (args.length<3) {
                        stack.getSender().sendMessage("specify command or kill yourself");
                        return;
                    }
                    switch (args[2]){
                        case "start":{
                            for (int i=0;i<arenas_list.size();i++) {
                                if(arenas_list.get(i).name.equals(args[1])){
                                    arenaManager.startArena(arenas_list.get(i));
                                    break;
                                }
                            }
                        }
                        break;
                        case "join":{
                            for (int i=0;i<arenas_list.size();i++) {
                                if(arenas_list.get(i).name.equals(args[1])){
                                    //stack.getSender().sendMessage(stack.getSender().getServer().getPlayer(stack.getSender().getName()).getName());
                                    arenaManager.joinPlayer(arenas_list.get(i),stack.getSender().getServer().getPlayer(stack.getSender().getName()));
                                    break;
                                }
                            }
                        }
                        break;
                        case "gamestart":{
                            for (int i=0;i<arenas_list.size();i++) {
                                if(arenas_list.get(i).name.equals(args[1])){
                                    //stack.getSender().sendMessage(stack.getSender().getServer().getPlayer(stack.getSender().getName()).getName());
                                    arenaManager.startGame(arenas_list.get(i));
                                    break;
                                }
                            }
                        }
                        break;
                        case "pos1": {
                            for (int i=0;i<arenas_list.size();i++) {
                                if(arenas_list.get(i).name.equals(args[1])){
                                    arenas_list.get(i).pos1=stack.getLocation().toVector().toString();
                                    arenas_list.get(i).world=stack.getLocation().getWorld().getName();
                                    break;
                                }
                            }
                        }
                        break;
                        case "pos2":{
                            for (int i=0;i<arenas_list.size();i++) {
                                if(arenas_list.get(i).name.equals(args[1])){
                                    arenas_list.get(i).pos2=stack.getLocation().toVector().toString();
                                    break;
                                }
                            }
                        }
                        case "lobbypos":{
                            for (int i=0;i<arenas_list.size();i++) {
                                if(arenas_list.get(i).name.equals(args[1])){
                                    arenas_list.get(i).lobbypos=stack.getLocation().toVector().toString();
                                    break;
                                }
                            }
                        }
                        case "team":{
                            if(args.length<4){
                                stack.getSender().sendMessage("specify team name (and kill yourself)");
                                return;
                            }
                            TeamData team=new TeamData(args[3].split(",")[0],args[3].split(",")[1],stack.getLocation().toVector().toString());
                            for (int i=0;i<arenas_list.size();i++) {
                                if(arenas_list.get(i).name.equals(args[1])){
                                    arenas_list.get(i).teams.add(team);
                                    break;
                                }
                            }
                        }
                        case "save":
                            try {
                                Gson g = new Gson();
                                String out= g.toJson(arenas_list);
                                File arenas = new File(dataFolder, "arena_list.json");
                                FileUtils.writeStringToFile(arenas,out);
                                String jsonstring = FileUtils.readFileToString(arenas);
                                arenas_list = new ArrayList(Arrays.asList(g.fromJson(jsonstring, ArenaData[].class)));
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                            break;
                    }
                    break;
            }
        }
    }


}
