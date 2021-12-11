/**
 * @file ReloadDeck.java
 * @brief Implementation of game reload
 * @author Martin Marusiak
 */
package ija.ija2016.commands;

import ija.ija2016.model.board.Score;
import ija.ija2016.model.cards.CardDeck;
/**
 * Class representing game reload 
 */
public class ReloadDeck implements Command {

    protected CardDeck deck;
    protected CardDeck waste;
    protected Score score;
    protected int scoreVal;
    
    /**
     * Constructor of reload class
     * @param deck deck
     * @param waste waste deck
     * @param score score
     */
    public ReloadDeck(CardDeck deck, CardDeck waste, Score score){
        this.deck = deck;
        this.waste = waste;
        this.score = score;
        this.scoreVal = this.score.getScore();
    }
    /**
     * Excutes reload
     * @return true if command was completed, false otherwise
     */
    @Override
    public boolean execute() {
       if(this.deck.isEmpty() && !this.waste.isEmpty()){
            this.score.addScore(-100);
            while(!this.waste.isEmpty()){
                this.deck.put(this.waste.pop());
            }
            return true;
       }
       
       return false;
    }
    /**
     * Undo reload     
     * @return true if undo was successful, false otherwise
     */
    @Override
    public boolean undo() {
        if(this.waste.isEmpty() && !this.deck.isEmpty()){
            this.score.setScore(this.scoreVal);
            while(!this.deck.isEmpty()){
                this.waste.put(this.deck.pop());
            }
            return true;
        }
        return true;
    }
    
}
