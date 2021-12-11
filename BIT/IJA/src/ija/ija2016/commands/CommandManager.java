/**
 * @file CommandManager.java
 * @brief Implementation of command manager
 * @author Martin Marusiak
 */
package ija.ija2016.commands;

import java.util.Stack;

/**
 * Class that manages commands
 */
public class CommandManager {
   private final Stack<Command> commands;
   /**
    * Constructor of CommandManager
    */
   public CommandManager(){
       this.commands = new Stack<>();
   }
   
   /**
    * Adds command to command manager and executes it
    * @param comm command to be added to command manager
    * @return true if command was completed, false otherwise
    */
   public boolean execute(Command comm){
      if(comm.execute()){
        this.commands.push(comm);
        return true;
      } else {
          return false;
      }
   }
   /**
    * Undo command
    * @return true if undo was successful, false otherwise
    */
   public boolean undo(){
       if(!commands.empty()){
          boolean ok = commands.peek().undo();
          commands.pop();
          return ok;
       }
       return false;
   }
 
}
