/**
 * @file MoveWasteToWorking.java
 * @brief Implementation of moving card from waste deck to working stack
 * @author Martin Marusiak
 */
package ija.ija2016.commands;

import ija.ija2016.model.board.Score;
import ija.ija2016.model.cards.Card;
import ija.ija2016.model.cards.CardDeck;
import ija.ija2016.model.cards.CardStack;
/**
 * Class that implements moving card from waste deck to working stack
 */
public class MoveWasteToWorking implements Command {
    CardDeck waste;
    CardStack stack;
    Score score;
    /**
     * Constructor of MoveWateToWorking command
     * @param waste waste deck
     * @param stack working stack
     * @param score score
     */
    public MoveWasteToWorking(CardDeck waste, CardStack stack, Score score){
        this.waste = waste;
        this.stack = stack;
        this.score = score;    
    }
    
    /**
     * Execution of moving card from waste deck to working stack command
     * @return true if command was completed, false otherwise
     */
    @Override
    public boolean execute(){
        Card card = this.waste.get();
        if(card != null){
            if(this.stack.putCheck(card)){
                this.stack.put(card);
                this.score.addScore(5);
                this.waste.pop();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }   
    /**
     * Undo of moving card from waste deck to working stack command
     * @return true if undo was successful, false otherwise
     */
    @Override
    public boolean undo(){
        Card card = this.stack.get();
        if(card != null){
            if(this.waste.put(card)){
                this.score.addScore(-5);
                this.stack.pop();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}