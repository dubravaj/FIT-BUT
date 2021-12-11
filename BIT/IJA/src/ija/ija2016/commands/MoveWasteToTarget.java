/**
 * @file MoveWasteToTarget.java
 * @brief Implementation of moving card from waste deck to target deck
 * @author Martin Marusiak
 */
package ija.ija2016.commands;

import ija.ija2016.model.board.Score;
import ija.ija2016.model.cards.Card;
import ija.ija2016.model.cards.CardDeck;

/**
 * Class implementing moving card from waste deck to target deck
 */
public class MoveWasteToTarget implements Command{
    CardDeck waste;
    CardDeck target;
    Score score;
    /**
     * Constructor of MoveWasteToTarget command
     * @param waste waste deck
     * @param target target deck
     * @param score score
     */
    public MoveWasteToTarget(CardDeck waste, CardDeck target, Score score){
        this.waste = waste;
        this.target = target;
        this.score = score;    
    }
    /**
     * Execution of moving card from waste deck to target deck command
     * @return true if command was completed, false otherwise
     */
    @Override
    public boolean execute(){
        Card card = this.waste.get();
        if(card != null){
            if(this.target.putCheck(card)){
                this.target.put(card);
                this.score.addScore(10);
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
     * Undo of moving card from waste deck to target deck command
     * @return true if undo was successful, false otherwise
     */
    @Override
    public boolean undo(){
        Card card = this.target.get();
        if(card != null){
            if(this.waste.put(card)){
                this.score.addScore(-10);
                this.target.pop();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
