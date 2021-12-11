/**
 * @file Command.java
 * @brief Iterface representing command
 * @author Martin Marusiak
 */
package ija.ija2016.commands;

/**
 * Interface commnad
 */
public interface Command {
    /**
     * Execution of command
     * @return true if execution was succesful, false otherwise
     */
	public boolean execute();
    /**
     * Undo of commnad
     * @return true if undo was successful, false otherwise
     */
	public boolean undo();
}


