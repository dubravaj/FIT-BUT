package ija.ija2016.model.cards;

/**
 * @file Card.java
 * @brief Card interface
 * @author Martin Marusiak
 */

/**
 * Interface representing card 
 */

public interface Card {

    /**
     * Enum representing color
     */
    enum Color {
        SPADES("S"),
        DIAMONDS("D"),
        HEARTS("H"),
        CLUBS("C");

        private final String color;

        Color(final String color){
            this.color = color;
        }
        /**
         * @return String representation of card
         */
        @Override
        public String toString()
        {
            return color;
        }
        /**
         * 
         * @param c card color
         * @return true if cards has similar color false otherwise
         */
        public boolean similarColorTo(Card.Color c){
            if(this == SPADES && c == CLUBS || this == CLUBS && c == SPADES || this == c){
                return true;
            }
            if(this == DIAMONDS && c == HEARTS || this == HEARTS && c == DIAMONDS || this == c){
                return true;
            }
            return false;
        }
    }
    /**
     * Get color of card
     * @return color of card
     */
    Card.Color color();
    /**
     * Compares vallue of card to other card
     * @param c card
     * @return difference in value of cards
     */
    int compareValue(Card c);
    /**
     * Checks if card is turned face up
     * @return true if card is face up
     */
    boolean isTurnedFaceUp();
    /**
     * Compare color of cards
     * @param c card
     * @return true if card color is similar
     */
    boolean similarColorTo(Card c);
    /**
     * Turn card face up
     * @return true if card was turned, false if card was already turned
     */
    boolean turnFaceUp();
    /**
     * Turn card face down.
     * @return true if card was turned, false if card was already face down
     */ 
    boolean turnFaceDown();
    /**
     * Get value of card
     * @return value of card
     */
    int value();
}
