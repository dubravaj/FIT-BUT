package ija.ija2016.model.cards;

/**
 * @file CardDeck.java
 * @brief Interface for card deck
 * @author Martin Marusiak
 */

/**
 * Interface representing card deck
 */
public interface CardDeck {
    /**
     * Get card
     * @return card
     */
    Card get();
    /**
     * Get card on given index
     * @param index index in deck
     * @return card on given index
     */
    Card get(int index);
    /**
     * Check if deck is empty
     * @return true if deck is empty, false otherwise
     */
    boolean isEmpty();
    /**
     * Pop card from deck
     * @return popped card
     */
    Card pop();
    /**
     * Put card on deck
     * @param card Card to be put
     * @return true in case of success
     */
    boolean put(Card card);
    /**
     * Get size of deck - number of card in deck
     * @return size of deck
     */
    int size();
    /**
     * Check if card can be put on stack
     * @param card card that will be checked
     * @return true if card can be put to stack 
     */
    boolean putCheck(Card card);
}
