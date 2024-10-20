package com.example.sumoPlugin;

import org.joml.Vector3f;

import java.util.ArrayList;

public class ArenaData {
    public String name="";
    public String world="";
    public String pos1="";
    public String pos2="";
    public String lobbypos="";
    public Integer gameTime;
    public ArrayList<TeamData> teams=new ArrayList<TeamData>();
    public ArenaData(String name, String world, String pos1, String pos2, String lobbypos, TeamData[] teams){

    }
    public ArenaData(){

    }
    public Vector3f getPos1(){
        String[] split =pos1.split(",");
        return new Vector3f(Float.parseFloat(split[0]),Float.parseFloat(split[1]),Float.parseFloat(split[2]));
    }
    public Vector3f getPos2(){
        String[] split =pos2.split(",");
        return new Vector3f(Float.parseFloat(split[0]),Float.parseFloat(split[1]),Float.parseFloat(split[2]));
    }
    public Vector3f getLobbypos(){
        String[] split =lobbypos.split(",");
        return new Vector3f(Float.parseFloat(split[0]),Float.parseFloat(split[1]),Float.parseFloat(split[2]));
    }
    public void setPos1(float x,float y,float z){
        pos1=Float.toString(x)+","+Float.toString(y)+","+Float.toString(z);
    }
    public void setPos2(float x,float y,float z){
        pos2=Float.toString(x)+","+Float.toString(y)+","+Float.toString(z);
    }
}
