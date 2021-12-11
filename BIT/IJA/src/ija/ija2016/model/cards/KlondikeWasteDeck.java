/**
 * @file KlondikeWasteDeck.java
 * @brief Implementation of waste deck
 * @author Martin Marusiak
 */
package ija.ija2016.model.cards;
/**
 * Class representing waste deck
 */
public class KlondikeWasteDeck extends KlondikeCardDeck{
    /**
     * Create an empty waste deck
     * @param size maximum size of newly created waste deck
     */
    public KlondikeWasteDeck(int size) {
        super(size);
    }
    /**
     * Put card on waste deck and check if card can be put
     * @param card card that will be put on waste deck
     * @return true if card was put, false if card can not be put to target pack
     */
    @Override
    public boolean put(Card card){

        if(this.size() + 1 > maxSize){
            return false;
        }
        
        Card c = this.get();
        
        if(c != null){
            c.turnFaceDown();
        }
        
        card.turnFaceUp();
        
        this.cardPackage.push(card);
        
        return true;
    }
    /**
     * Pop card from waste deck
     * @return popped card
     */
    @Override
    public Card pop(){
        if(this.cardPackage.empty()){
            return null;
        } else {
            Card c;
            c =  this.cardPackage.pop();
            if(!this.cardPackage.empty()){
                this.cardPackage.peek().turnFaceUp();
            }
            return c;
        }
    }
}
