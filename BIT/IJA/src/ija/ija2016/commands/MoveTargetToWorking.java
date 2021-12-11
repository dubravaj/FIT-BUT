/**
 * @file MoveTargetToWorking.java
 * @brief Implementation of moving card from target deck to working stack
 * @author Martin Marusiak
 */
package ija.ija2016.commands;

import ija.ija2016.model.board.Score;
import ija.ija2016.model.cards.Card;
import ija.ija2016.model.cards.CardDeck;
import ija.ija2016.model.cards.CardStack;

/**
 * Class implementing moving card from target deck to working stack
 */
public class MoveTargetToWorking implements Command {
    CardDeck target;
    CardStack stack;
    Score score;
    /**
     * Constructor of MoveTargetToWorking command
     * @param target
     * @param stack
     * @param score 
     */
    public MoveTargetToWorking(CardDeck target, CardStack stack, Score score){
        this.target = target;
        this.stack = stack;
        this.score = score;      
    }
    /**
     * Execution of moving card from target deck to working stack
     * @return true if command was completed, false otherwise
     */
    @Override
    public boolean execute(){
        Card card = this.target.get();
        if(card != null){
            if(this.stack.putCheck(card)){
                this.stack.put(card);
                this.score.addScore(-15);
                this.target.pop();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    /**
     * Undo of moving card from target deck to working pack
     * @return true if undo was successful, false otherwise
     */
    @Override
    public boolean undo(){
        Card card = this.stack.get();
        if(card != null){
            if(this.target.put(card)){
                this.score.addScore(15);
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