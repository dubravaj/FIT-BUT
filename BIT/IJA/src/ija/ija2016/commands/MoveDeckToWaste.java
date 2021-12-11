/**
 * @file MoveDeckToWaste.java
 * @brief Implementation of moving card from deck to waste deck
 * @author Martin Marusiak
 */
package ija.ija2016.commands;

import ija.ija2016.model.cards.Card;
import ija.ija2016.model.cards.CardDeck;

/**
 * Class that implements moving card from deck to waste deck
 */
public class MoveDeckToWaste implements Command{
    
    CardDeck deck;
    CardDeck waste;
    
    /**
     * Constructor of MoveDeckToWaste command
     * @param deck deck
     * @param waste waste deck
     */
    public MoveDeckToWaste(CardDeck deck, CardDeck waste){
        this.deck = deck;
        this.waste = waste;
    }
    
    /**
     * Execution of moving card from deck to waste deck command
     * @return true if command was completed, false otherwise
     */
    @Override
    public boolean execute(){
        Card card = this.deck.pop();
        if(card != null){
            return this.waste.put(card);
        } else {
            return false;
        }
        
    }   
    /**
     * Unfo of moving card from deck to waste deck command
     * @return true if undo was successful, false otherwise
     */
    @Override
    public boolean undo(){
        Card card = this.waste.pop();
        if(card != null){
            return this.deck.put(card);
        } else {
            return false;
        }
    }
}
