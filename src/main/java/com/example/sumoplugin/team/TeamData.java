package com.example.sumoplugin.team;

import org.bukkit.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TeamData {
    public TeamData(String name, String color){
        this.name=name;
        this.color=color;
    }public TeamData(String name, String color,String[] players){
        this.name=name;
        this.color=color;
        this.players= Arrays.stream(players).collect(Collectors.toCollection(ArrayList::new));
    }
    public TeamData(){

    }
    public String name="";
    public String color="";
    public ArrayList<String> players=new ArrayList<>();

    public Color GetTeamColor(){
        return switch (color) {
            case "RED" -> Color.RED;
            case "GREEN" -> Color.GREEN;
            case "BLUE" -> Color.BLUE;
            case "WHITE" -> Color.WHITE;
            case "YELLOW" -> Color.YELLOW;
            case "BLACK" -> Color.BLACK;
            case "ORANGE" -> Color.ORANGE;
            case "LIME" -> Color.LIME;
            default -> Color.FUCHSIA;
        };
    }
}
