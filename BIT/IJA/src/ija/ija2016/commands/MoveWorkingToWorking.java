/**
 * @file MoveWorkingToWorking.java
 * @brief Implementation of moving card from working stack to other working stack
 * @author Martin Marusiak
 */
package ija.ija2016.commands;

import ija.ija2016.model.board.Score;
import ija.ija2016.model.cards.Card;
import ija.ija2016.model.cards.CardStack;

/**
 * Class that implements moving card/cards from working stack to other working stack
 */
public class MoveWorkingToWorking implements Command{
    
    CardStack stackFrom;
    CardStack stackTo;
    Score score;
    Card card;
    boolean turned;
    /**
     * Construtor of class MoveWorkingToWorking command
     * @param stackFrom stack from which cards will be taken
     * @param stackTo stack where cards will be put
     * @param card card from which cards will be taken from stack
     * @param score  score
     */
    public MoveWorkingToWorking(CardStack stackFrom, CardStack stackTo, Card card, Score score){      
        this.stackFrom = stackFrom;
        this.stackTo = stackTo;
        this.score = score;
        this.card = card;
        this.turned = true;
    }
    
    /**
     * Executes moving card from working stack to ther working stack
     * @return true if command was completed, false otherwise
     */
    @Override
    public boolean execute(){  
        CardStack move;
        if(this.card != null){
            if(this.stackTo.putCheck(this.card)){
                move = this.stackFrom.pop(this.card);
                Card c = this.stackFrom.get();
                if(move != null){  
                    if(this.stackTo.put(move)){                      
                        if(c != null){
                            this.turned = c.isTurnedFaceUp();
                            if(!this.turned){
                                c.turnFaceUp();
                                this.score.addScore(5);
                            }
                        }    
                        return true;
                    } else {
                        this.stackFrom.put(move);
                        return false;
                    }
                }
                return false;
            }
        }
       return false;
    }   
    /**
     * Undo moving card
     * @return true if undo was successful, false otherwise
     */
    @Override
    public boolean undo(){  
        CardStack move;
        if(this.card != null){
            move = this.stackTo.pop(this.card);
            Card c = this.stackFrom.get();
            if(move != null){
                if(this.stackFrom.put(move)){
                    if(c != null && !this.turned){
                        c.turnFaceDown();
                        this.score.addScore(-5);
                    }
                    return true;
                }
            }
            return false;
            
        }
       return false;
    }   
}
