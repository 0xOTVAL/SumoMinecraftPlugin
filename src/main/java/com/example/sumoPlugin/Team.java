package com.example.sumoPlugin;

import org.joml.Vector3f;

public class Team {
    public Team(String name, String spawnpos){
        this.name=name;
        this.spawnpos=spawnpos;
    }
    public Team(){

    }
    public String name="";
    public String spawnpos="";

    public Vector3f GetSpawnpos(){
        String[] split =spawnpos.split(",");
        return new Vector3f(Float.parseFloat(split[0]),Float.parseFloat(split[1]),Float.parseFloat(split[2]));
    }
}
