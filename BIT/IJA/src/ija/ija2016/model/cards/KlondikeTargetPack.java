/**
 * @file KlondikeTargetPack.java
 * @brief Implementation of target pack
 * @author Martin Marusiak
 */
package ija.ija2016.model.cards;

/**
 * Class representing klondike target pack
 * @author mmarusiak
 */
public class KlondikeTargetPack extends KlondikeCardDeck {
	
    protected KlondikeCard.Color color;
    /**
     * Set color of target pack
     * @param color color of target pack
     */
    public void setColor(Card.Color color) {
        this.color = color;
    }
    /**
     * Create an empty target pack
     * @param size maximum number of cards on newly created target pack
     */
    public KlondikeTargetPack(int size){
        super(size);
    }
    /**
     * Create an empty target pack with specified color
     * @param size maximum number of cards on newly created target pack
     * @param c color of target pack
     */
    public KlondikeTargetPack(int size, KlondikeCard.Color c){
        super(size);
        this.color = c;
    }
    /**
     * Create target pack with specified color and maximum size 13
     * @param c color of target pack
     * @return Target pack with given color and maximum size equals to 13
     */
    public static KlondikeTargetPack createTargetPack(Card.Color c){
        return new KlondikeTargetPack(13, c);
    }
    /**
     * Put card on target pack and check if card can be put
     * @param card card that will be put on target pack
     * @return true if card was put, false if card can not be put to target pack
     */
    @Override
    public boolean put(Card card){    
        
        if(this.cardPackage.empty()){
            this.color = card.color();
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
     * Pop card from target pack
     * @return popped card
     */   
    @Override
    public Card pop(){
        
        if(this.cardPackage.empty()){
            return null;
        } else {
            Card c =  this.cardPackage.pop();
            if(!this.cardPackage.empty()){
                this.cardPackage.peek().turnFaceUp();
            }
            return c;
        }
    }   
    
        /**
     * Check if card can be put on stack
     * @param card card that will be checked
     * @return true if card can be put to stack 
     */
    @Override
    public boolean putCheck(Card card){
        
        
        if(!this.cardPackage.empty() && this.color != card.color()){
            return false;
        }
        if(this.size() + 1 > maxSize){
            return false;
        }
        
        if(!card.isTurnedFaceUp()){
            return false;
        }
        
        if(this.size() + 1 < card.value()){
            return false;
        }
        
        return true;
    }
}