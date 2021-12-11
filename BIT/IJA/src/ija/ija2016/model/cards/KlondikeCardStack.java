/**
 * @file KlondikeCardStack.java
 * @brief Implementation of card stack
 * @author Martin Marusiak
 */
package ija.ija2016.model.cards;

import java.util.LinkedList;
import java.util.Objects;
/**
 * Class representing klondike stack of cards
 */
public class KlondikeCardStack implements CardStack {

    protected int maxSize;
    protected LinkedList<Card> cardPackage;

    /**
     * Create an empty klondike stack of cards
     * @param size maximum number of cards in stack
     */
    public KlondikeCardStack(int size) {
        cardPackage = new LinkedList<>();
        this.maxSize = size;
    }

    /**
     * Check if card can be put on stack
     * @param card card that will be checked
     * @return true if card can be put to stack 
     */
    @Override
    public boolean putCheck(Card card) {

        if(this.size() + 1 > this.maxSize){
            return false;
        }

        Card top = (Card) this.get();
    
        if(!card.isTurnedFaceUp())
            return false;
        if(top != null) {
            if (card.similarColorTo(top))
                return false;
            if (top.value() - 1 != card.value()) {
                return false;
            }

        } else {
            //only king can be putted to empty stack
            if (13 != card.value()) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * Pop cards from stack that starts by given card
     * @param card card from which cards will be poped
     * @return stack that contains poped cards
     */
    @Override
    public boolean put(Card card) {
      
        cardPackage.addFirst(card);
        
        return true;
    }
    
    /**
     * Put stack on top of other stack
     * @param stack card stack that will be put on top of another stack
     * @return true in case of success
     */
    @Override
    public boolean put(CardStack stack) {

        KlondikeCardStack klondikeStack = (KlondikeCardStack) stack;

        if(stack == null){
            return false;
        }
        
        if(stack.isEmpty()){
            return false;
        }    

        if(this.size() + stack.size() > this.maxSize){
            return false;
        }

        for(int i = stack.size() - 1; i >= 0; i--){
            cardPackage.addFirst((klondikeStack.cardPackage.get(i)));
        }
        
        return true;
    }
    /**
     * Get top card
     * @return card
     */
    @Override
    public Card get() {
        if(!cardPackage.isEmpty())
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
        if(this.isEmpty()) {
            return null;
        } else {
            try {
                if(index < 0){
                    index = -index;
                } else {
                    index = cardPackage.size() - 1 - index;
                }
                return cardPackage.get(index);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }
    }

    /**
     * Check if a stack is empty
     * @return true if deck is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return cardPackage.isEmpty();
    }
    
    /**
     * Pop card from stack
     * @return popped card
     */
    @Override
    public Card pop() {
        if (cardPackage.isEmpty()) {
            return null;
        } else {  
            return cardPackage.removeFirst();
        }
    }
    
    /**
     * Get size of stack - number of card in stack
     * @return size of deck
     */
    @Override
    public int size() {
        return cardPackage.size();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KlondikeCardStack) {
            KlondikeCardStack stack = (KlondikeCardStack) obj;
            if(stack.size() == cardPackage.size()){
                for(int i=0; i<stack.size(); i++){
                    if(!stack.get(i).equals(cardPackage.get(i))){
                        return false;
                    }
                }
            }
            return true;

        } else {
            return super.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.cardPackage);
        return hash;
    }
    
    /**
     * Pop cards from stack that starts by given card
     * @param card card from which cards of stack will be popped
     * @return stack that contains popped cards
     */
    @Override
    public CardStack pop(Card card) {
        
        if(cardPackage.contains(card)) {
            KlondikeCardStack cardSeq = new KlondikeCardStack(0);
            for(Card c: cardPackage){
                cardSeq.cardPackage.addLast(c);
                if(c.equals(card)){
                    break;
                }
            }
            
            for(int i=0; i<cardSeq.size(); i++){
                cardPackage.removeFirst();
            }

            cardSeq.maxSize = cardSeq.size();

            return cardSeq;
        } else {
            return null;
        }
    }
}
