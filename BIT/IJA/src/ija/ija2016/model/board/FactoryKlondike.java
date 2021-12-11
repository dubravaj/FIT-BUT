/**
 * @file FactoryKlondike.java
 * @brief Implementation of klondike factory
 * @author Martin Marusiak
 */
package ija.ija2016.model.board;

import ija.ija2016.model.cards.KlondikeCard;
import ija.ija2016.model.cards.KlondikeCardStack;
import ija.ija2016.model.cards.KlondikeTargetPack;
import ija.ija2016.model.cards.KlondikeWasteDeck;
import ija.ija2016.model.cards.KlondikeCardDeck;
import ija.ija2016.model.cards.Card;
import ija.ija2016.model.cards.CardDeck;
import ija.ija2016.model.cards.CardStack;
/**
 * Class representing klondike factory
 */
public class FactoryKlondike extends AbstractFactorySolitaire {
    /**
     * Create klondike card
     * @param color color of card
     * @param value value of card
     * @return klondike card
     */
    @Override
    public Card createCard(Card.Color color, int value) {
        try {
            return new KlondikeCard(color, value);
        } catch (IllegalArgumentException arg) {
            return null;
        }
    }
    /**
     * Create a standard card deck
     * @return standard card deck
     */
    @Override
    public CardDeck createCardDeck() {
        return KlondikeCardDeck.createStandardDeck();
    }
    /**
     * Create an empty card deck
     * @return empty card deck
     */
    @Override
    public CardDeck createEmptyCardDeck() {
        return new KlondikeCardDeck(52);
    }
    /**
     * Create target pack with size maximum size 13
     * @return taget pack
     */
    @Override
    public CardDeck createTargetPack() {
        return new KlondikeTargetPack(13);
    }
    /**
     * Create 4 empty target packs with maximum size 13 
     * @return target packs
     */
    @Override
    public CardDeck[] createTargetPacks(){
        CardDeck[] targets = new CardDeck[4];
        for(int i=0; i<targets.length; i++){
            targets[i] = this.createTargetPack();
        }
        return targets;
    }
    /**
     * Create an empty working stack with maximum size 19
     * @return working stack
     */
    @Override
    public CardStack createWorkingPack() {
        return new KlondikeCardStack(19);
    }
    /**
     * Create 7 empty working packs with maximum size 19
     * @return working stacks
     */
    @Override
    public CardStack[] createWorkingPacks(){
        CardStack[] workingPacks = new CardStack[7];
        
        for(int i=0; i < workingPacks.length; i++){
            workingPacks[i] = this.createWorkingPack();
        }
        
        return workingPacks;  
    }
    /**
     * Create 7 working stacks that are filled with cards from card deck according to klondike rules
     * @param deck form which cards will be taken to working stacks
     * @return working stacks
     */
    @Override
    public CardStack[] createWorkingPacks(CardDeck deck){
        CardStack[] workingPacks = new CardStack[7];
        
        for(int i=0; i < workingPacks.length; i++){
            Card c;
            workingPacks[i] = this.createWorkingPack();
            for(int j=0; j < i + 1; j++){
                workingPacks[i].put(deck.pop());
            }
            c = workingPacks[i].get();
            c.turnFaceUp();
        }
        
        return workingPacks;
    }
    /**
     * Create an empty waste deck
     * @return waste deck
     */
    @Override
    public CardDeck createCardWaste(){
        return new KlondikeWasteDeck(24);
    }
}
