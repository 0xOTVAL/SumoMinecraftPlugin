package com.example.sumoplugin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandExecutorBase  implements CommandExecutor {
    private final List<SubCommand> subCommands=new ArrayList<>();
    public void addSubCommand(SubCommand command){
        subCommands.add(command);
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        for(SubCommand c: subCommands){
            if(c.canExecute(sender,command,label,args[0], Arrays.copyOfRange(args, 1,args.length))){
                c.runCommand(sender,command,label,args[0],Arrays.copyOfRange(args, 1,args.length));
                return true;
            }
        }
        sender.sendMessage("Invalid command");
        return false;
    }

}
