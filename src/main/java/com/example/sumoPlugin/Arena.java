package com.example.sumoPlugin;

import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.ints.IntLists;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Arena {
    public String name="";
    public String world="";
    public String pos1="";
    public String pos2="";
    public String lobbypos="";
    public ArrayList<Team> teams=new ArrayList<Team>();
    public Arena(String name,String world,String pos1,String pos2,String lobbypos,Team[] teams){

    }
    public Arena(){

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

}
