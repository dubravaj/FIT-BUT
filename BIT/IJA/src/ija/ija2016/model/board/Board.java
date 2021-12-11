/**
 * @file Board.java
 * @brief Implementation of game board
 * @author Martin Marusiak
 */
package ija.ija2016.model.board;

import java.io.File;
import java.io.IOException;
/**
 * Class representing board
 */
public class Board {
    /**
     * Get number of current games
     * @return number of games
     */
    public int getCurrentGames() {
        return currentGames;
    }
    
    protected Game[] games;
    protected int currentGames;
    public static final int MAX_GAMES = 4;
    /**
     * Create a board
     */
    public Board(){
        this.games = new Game[MAX_GAMES];
        this.currentGames = 0;
    }
    /**
     * Get game on given index
     * @param i index
     * @return game on given index
     */
    public Game getGame(int i){
        if(i >= this.currentGames || i < 0)
            return null;
        return this.games[i];
    }
    /**
     * Get last created game
     * @return last created game
     */
    public Game getGame(){
        if(this.currentGames < 1)
            return null;
        return this.games[this.currentGames-1];
    }
    /**
     * Create game
     * @return true if game was created, false otherwise
     */    
    public boolean createGame(){
        if(this.currentGames >= MAX_GAMES)
            return false;
        this.games[this.currentGames] = new Game();
        this.currentGames++;
        return true;
    }
    /**
     * Load game from file
     * @param file file where game is stored
     * @return true if game was loaded successfully, false in case of failure
     */
    public boolean createGame(File file){
        try {
            if(this.currentGames >= MAX_GAMES)
                return false;
            this.games[this.currentGames] = new Game(file);
            this.currentGames++;
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    /**
     * Removes last created game from board
     * @return true game was canceled, false if board does not containt any game
     */
    public boolean cancelGame(){
        
        if(this.currentGames > 0){
            this.games[this.currentGames -1] = null;
            this.currentGames -= 1;
            return true;
        }
              
        return false;
    }
    /**
     * Removes game from board on given index
     * if removed game was not created as last, 
     * than all games created after game we want to remove will be shifted to left by one index
     * @param i index of game that will be removed
     * @return true if game was removed, false if index is invalid or borad does not contain any game
     */
    public boolean cancelGame(int i){
        
        if(this.currentGames > 0 && i < this.currentGames && i >= 0){
            
            for(int j=i; j < this.currentGames - 1; j++){
                this.games[j] = this.games[j+1];
            }
            
            this.games[this.currentGames-1] = null;
            
            this.currentGames -= 1;
            
            return true;
        }
              
        return false;
    }
    
}
