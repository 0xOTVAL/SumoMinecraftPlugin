package com.example.sumoPlugin;

import com.google.gson.Gson;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class sumoCommandApi implements BasicCommand {
    public List<Arena> arenas_list;
    private File dataFolder;
    sumoCommandApi(File dataFolder,List<Arena> arenas_list){
        this.dataFolder=dataFolder;
        this.arenas_list=arenas_list;
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
                for (Arena i : arenas_list) {
                    stack.getSender().sendMessage(i.teams.get(0).name);
                }
                return;
            }
            switch (args[1]){
                case "create": {
                    if (args.length<3) {
                        stack.getSender().sendMessage("specify name(and kill yourself)");
                        return;
                    }
                    Arena arena = new Arena();
                    arena.name = args[2];
                    arenas_list.add(arena);
                    stack.getSender().sendMessage("Arena created(maybe)");
                    }
                    break;
                default:
                    if (args.length<3) {
                        stack.getSender().sendMessage("specify command(and kill yourself)");
                        return;
                    }
                    switch (args[2]){
                        case "pos1": {
                            for (int i=0;i<arenas_list.size();i++) {
                                if(arenas_list.get(i).name.equals(args[1])){
                                    arenas_list.get(i).pos1=stack.getLocation().toVector().toString();
                                    arenas_list.get(i).world=stack.getLocation().getWorld().getName();
                                }
                            }
                        }
                        break;
                        case "pos2":{
                            for (int i=0;i<arenas_list.size();i++) {
                                if(arenas_list.get(i).name.equals(args[1])){
                                    arenas_list.get(i).pos2=stack.getLocation().toVector().toString();
                                }
                            }
                        }
                        case "lobbypos":{
                            for (int i=0;i<arenas_list.size();i++) {
                                if(arenas_list.get(i).name.equals(args[1])){
                                    arenas_list.get(i).lobbypos=stack.getLocation().toVector().toString();
                                }
                            }
                        }
                        case "team":{
                            if(args.length<4){
                                stack.getSender().sendMessage("specify team name (and kill yourself)");
                                return;
                            }
                            Team team=new Team(args[3],stack.getLocation().toVector().toString());
                            for (int i=0;i<arenas_list.size();i++) {
                                if(arenas_list.get(i).name.equals(args[1])){
                                    arenas_list.get(i).teams.add(team);
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
                                arenas_list = new ArrayList(Arrays.asList(g.fromJson(jsonstring, Arena[].class)));
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
