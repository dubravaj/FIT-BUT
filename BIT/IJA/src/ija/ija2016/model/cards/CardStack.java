package ija.ija2016.model.cards;

/**
 * @file CardStack.java
 * @brief Interface representing card stack
 * @author Martin Marusiak
 */

/**
 * Interface representing card stack
 */
public interface CardStack extends CardDeck{
    /**
     * Pop cards from stack that starts by given card
     * @param card card from which cards of stack will be popped
     * @return stack that contains popped cards
     */
    CardStack pop(Card card);
    /**
     * Put stack on top of other stack
     * @param stack card stack that will be put on top of another stack
     * @return true in case of success
     */
    boolean put(CardStack stack);

}
