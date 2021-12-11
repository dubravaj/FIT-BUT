/**
 * @file KlondikeCard.java
 * @brief Implementation of card
 * @author Martin Marusiak
 */

package ija.ija2016.model.cards;

/**
 * Class representing klondike card
 */
public class KlondikeCard implements Card {

    protected Color c;
    protected int value;
    protected boolean turned;
    
    /**
     * Constructor of klondike card
     * @param c color of card
     * @param value value of card
     */
    public KlondikeCard(Color c, int value){

        if(value < 1 || value > 13){
            throw new IllegalArgumentException("Invalid card value");
        }

        this.c = c;
        this.value = value;
        this.turned = false;

    }
    /**
     * Get color of card
     * @return color of card
     */
    @Override
    public Color color(){
        return c;
    }
    /**
     * Compares vallue of card to other card
     * @param c card
     * @return difference in value of cards
     */
    @Override
    public int compareValue(Card c) {
        return this.value - c.value();
    }
    /**
     * Checks if card is turned face up
     * @return true if card is face up
     */
    @Override
    public boolean isTurnedFaceUp() {
        return turned;
    }
    /**
     * Compare color of cards
     * @param c card
     * @return true if card color is similar
     */
    @Override
    public boolean similarColorTo(Card c) {
        return this.c.similarColorTo(c.color());
    }
    /**
     * Turn card face up
     * @return true if card was turned, false if card was already turned
     */
    @Override
    public boolean turnFaceUp() {
        if(turned) {
            return false;
        } else {
            turned = true;
            return true;
        }
    }
     /**
     * Turn card face down.
     * @return true if card was turned, false if card was already face down
     */    
    @Override
    public boolean turnFaceDown(){
        if(turned == false){
            return false;
        } else {
            turned = false;
            return true;
        }
    }
    /**
     * Get value of card
     * @return value of card
     */
    @Override
    public int value(){
        return value;
    }
    /**
     * String representation of card
     * @return string representing card
     */
    @Override
    public String toString() {
        switch (value) {
            case 1:
                return "A(" + c + ")";
            case 11:
                return "J(" + c + ")";
            case 12:
                return "Q(" + c + ")";
            case 13:
                return "K(" + c + ")";
            default:
                return value + "("+ c + ")";
        }
    }

    @Override
    public int hashCode(){
        return (this.toString() + Boolean.toString(this.turned)).hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof KlondikeCard){
            KlondikeCard klondikeCard = (KlondikeCard) obj;
            return value == klondikeCard.value() && c == klondikeCard.color() && this.turned == klondikeCard.isTurnedFaceUp();
        } else {
            return super.equals(obj);
        }
    }

}



