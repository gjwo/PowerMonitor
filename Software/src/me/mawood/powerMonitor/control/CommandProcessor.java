package me.mawood.powerMonitor.control;

import me.mawood.powerMonitor.Main;

import java.util.Arrays;
import java.util.Queue;

public class CommandProcessor extends Thread
{
    String command;
    Queue<String> commandQ;
    Queue<String> loggingQ;

    public CommandProcessor()
    {
        this.commandQ = Main.getCommandQ();
        this.loggingQ =  Main.getLoggingQ();
    }

    /**
     * run  The main Command Processor loop
     */
    public void processSetCommand(String[] params)
    {
        loggingQ.add("Set Command Received: " + params);
    }

    public void processGetCommand(String[] params)
    {
        loggingQ.add("Get Command Received: " + params);
    }
    //
    // Runnable implementation
    //

    @Override
    public void run()
    {

        String[] commandElements;
        boolean exit = false;
        while (!(interrupted() || exit)) {
            command = commandQ.poll().toLowerCase();
            commandElements = command.split(" ");
            switch (commandElements[0]) {
                case "set": {
                    if (commandElements.length > 1) {
                        processSetCommand(Arrays.copyOfRange(commandElements, 1, commandElements.length));
                    } else { // not enough arguments}
                    }
                    break;
                }
                case "get": {
                    if (commandElements.length > 1) {
                        processGetCommand(Arrays.copyOfRange(commandElements, 1, commandElements.length));
                    } else { // not enough arguments}
                    }
                    break;
                }
                case "exit": {
                        exit = true;
                        break;
                    }
                    default: {//ignore for now
                    }
                }
            }
        }
    }