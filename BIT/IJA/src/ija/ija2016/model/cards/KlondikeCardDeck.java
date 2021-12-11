/**
 * @file KlondikeCardDeck.java
 * @brief Implementation of card deck
 * @author Martin Marusiak
 */
package ija.ija2016.model.cards;

import java.util.Collections;

import java.util.Stack;
/**
 * Class representing klondike card deck
 */
public class KlondikeCardDeck implements CardDeck {

    protected Stack<Card> cardPackage;
    protected int maxSize;

    /**
     * Initialize an empty deck of cards
     * @param size maximum number of card in newly created card deck
     */
    public KlondikeCardDeck(int size){
        cardPackage = new Stack<>();
        maxSize = size;
    }
   
    /**
     * Create a standart card deck - deck containing 52 cards
     * @return standart card deck that is shuffled
     */
    public static KlondikeCardDeck createStandardDeck(){

        KlondikeCardDeck newDeck = new KlondikeCardDeck(52);
        KlondikeCard card;
        for(Card.Color color : Card.Color.values()){
            for(int value=1; value <= 13; value++){
                card = new KlondikeCard(color, value);
                newDeck.cardPackage.push(card);
            }
        }
        Collections.shuffle(newDeck.cardPackage);

        return newDeck;
    }
    /**
     * Get size of deck - number of card in deck
     * @return size of deck
     */
    @Override
    public int size(){
        return cardPackage.size();
    }
    /**
     * Put card on deck
     * @param card Card to be put
     * @return true in case of success
     */
    @Override
    public boolean put(Card card){

        if(this.size() + 1 > maxSize){
            return false;
        }
        card.turnFaceDown();
        this.cardPackage.push(card);
        return true;
    }
    /**
     * Get card
     * @return card
     */
    @Override
    public Card get() {
        if(!cardPackage.empty())
            return cardPackage.peek();
        return null;
    }
    /**
     * Get card on given index
     * @param index index in deck
     * @return card on given index
     */
    @Override
    public Card get(int index) {
        if(this.isEmpty()){
            return null;
        } else {
            try {
                return cardPackage.get(index);
            } catch (ArrayIndexOutOfBoundsException e){
                return null;
            }
        }
    }
    /**
     * Check if deck is empty
     * @return true if deck is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return this.cardPackage.empty();
    }
    /**
     * Pop card from deck
     * @return poped card
     */
    @Override
    public Card pop(){
        if(this.cardPackage.empty()){
            return null;
        } else {
            return this.cardPackage.pop();
        }
    }
    
    /**
     * Check if card can be put on stack
     * @param card card that will be checked
     * @return true if card can be put to stack 
     */
    @Override
    public boolean putCheck(Card card){
        return true;
    }
}
