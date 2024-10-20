package com.example.sumoPlugin;

import org.bukkit.Color;
import org.joml.Vector3f;

public class TeamData {
    public TeamData(String name, String color, String spawnpos){
        this.name=name;
        this.color=color;
        this.spawnpos=spawnpos;
    }
    public TeamData(){

    }
    public String name="";
    public String spawnpos="";
    public String color="";

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
