/**
 * @file AbstractFactorySolitaire.java
 * @brief Implementation of abstract factory
 * @author Martin Marusiak
 */
package ija.ija2016.model.board;

import ija.ija2016.model.cards.Card;
import ija.ija2016.model.cards.CardDeck;
import ija.ija2016.model.cards.CardStack;


/**
 * Abstract class representing solitaire abstract factory
 */
public abstract class AbstractFactorySolitaire {
    /**
     * Create card
     * @param color color of card
     * @param value value of card
     * @return klondike card
     */
    public abstract Card createCard(Card.Color color, int value);
    /**
     * Create a standard card deck
     * @return standard card deck
     */
    public abstract CardDeck createCardDeck();
     /**
     * Create an empty card deck
     * @return empty card deck
     */   
    public abstract CardDeck createEmptyCardDeck();
    /**
     * Create target pack with size maximum size 13
     * @return taget pack
     */
    public abstract CardDeck createTargetPack();
    /**
     * Create 4 empty target packs with maximum size 13 
     * @return target packs
     */
    public abstract CardDeck[] createTargetPacks();
    /**
     * Create an empty working stack with maximum size 19
     * @return working stack
     */
    public abstract CardStack createWorkingPack();
     /**
     * Create 7 empty working packs with maximum size 19
     * @return working stacks
     */
    public abstract CardStack[] createWorkingPacks();
    /**
     * Create 7 working stacks that are filled with cards from card deck according to klondike rules
     * @param deck form which cards will be taken to working stacks
     * @return working stacks
     */
    public abstract CardStack[] createWorkingPacks(CardDeck deck);
    /**
     * Create an empty waste deck
     * @return waste deck
     */
    public abstract CardDeck createCardWaste();
}