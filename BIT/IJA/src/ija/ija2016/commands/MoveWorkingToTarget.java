/**
 * @file MoveWorkingToTarget.java
 * @brief Implementation of moving card from working stack to target pack
 * @author Martin Marusiak
 */
package ija.ija2016.commands;

import ija.ija2016.model.board.Score;
import ija.ija2016.model.cards.Card;
import ija.ija2016.model.cards.CardDeck;
import ija.ija2016.model.cards.CardStack;

/**
 * Class that implements moving card from working stack to target pack
 */
public class MoveWorkingToTarget implements Command {
    CardDeck target;
    CardStack stack;
    Score score;
    boolean turned;
    
    /**
     * Construtor of MoveWorkingToTarget command
     * @param stack 
     * @param target
     * @param score 
     */
    public MoveWorkingToTarget(CardStack stack, CardDeck target, Score score){
        this.stack = stack;
        this.target = target;
        this.score = score;
        this.turned = true;
    }
    /**
     * Execution of moving card from working stack to target pack command
     * @return true if command was completed, false otherwise
     */
    @Override
    public boolean execute(){
        Card card = this.stack.get();
        if(card != null){
            if(this.target.putCheck(card)){
                this.target.put(card);
                this.score.addScore(10);
                card = this.stack.get(-1);
                if(card != null){
                    this.turned = card.isTurnedFaceUp();
                    if(!this.turned){
                        card.turnFaceUp();
                        this.score.addScore(5);
                    }
                }    
                this.stack.pop();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }   
    /**
     * Undo moving card from working stack to target pack
     * @return true if undo was successful, false otherwise
     */
    @Override
    public boolean undo(){
        Card card = this.target.get();
        if(card != null){
            if(this.stack.put(card)){
                if(!turned){
                    card = this.stack.get(-1);
                    if(card != null){
                        this.score.addScore(-5);
                        card.turnFaceDown();
                    }    
                }
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
