/**
 * @file GetPossibleMoves.java
 * @brief Implementation of hint
 * @author Martin Marusiak
 */
package ija.ija2016.model.board;

import ija.ija2016.model.cards.Card;
import ija.ija2016.model.cards.CardDeck;
import ija.ija2016.model.cards.CardStack;
import java.util.HashMap;

/**
 * Class representing hint
 */
public class GetPossibleMoves {

    protected CardDeck deck;
    protected CardDeck waste;
    protected CardDeck[] targets;
    protected CardStack[] workingStacks;
     
    /**
     * Constructor
     * @param deck card deck
     * @param waste waste deck
     * @param targets target pack
     * @param workingStacks working stack
     */
    public GetPossibleMoves(CardDeck deck, CardDeck waste, CardDeck[] targets, CardStack[] workingStacks) {
        this.deck = deck;
        this.waste = waste;
        this.targets = targets;
        this.workingStacks = workingStacks;        
    }

    private void addCardToTable(HashMap<Card, Pack> cards ,CardDeck d, Pack p){
        for(int i=0; i < d.size(); i++){
            cards.put(d.get(i),p);
        }
    }
    /**
     * Check all possible moves
     * @return moves stored in string
     */
    public String getMoves(){
        
        String moves = "Moves:\n";
        int m = 1;
        
        HashMap<Card, Pack> cards = new HashMap<>();
        AbstractFactorySolitaire factory = new FactoryKlondike();
        
        addCardToTable(cards, this.deck, Pack.DECK);
        addCardToTable(cards, this.waste, Pack.WASTE);
        
        if(this.targets.length == 4){
            addCardToTable(cards, this.targets[0], Pack.TARGET1);
            addCardToTable(cards, this.targets[1], Pack.TARGET2);
            addCardToTable(cards, this.targets[2], Pack.TARGET3);
            addCardToTable(cards, this.targets[3], Pack.TARGET4);
        } else {
            return "";
        }
        
        if(this.workingStacks.length == 7){
            addCardToTable(cards, this.workingStacks[0], Pack.WORKING1);
            addCardToTable(cards, this.workingStacks[1], Pack.WORKING2);
            addCardToTable(cards, this.workingStacks[2], Pack.WORKING3);
            addCardToTable(cards, this.workingStacks[3], Pack.WORKING4);
            addCardToTable(cards, this.workingStacks[4], Pack.WORKING5);
            addCardToTable(cards, this.workingStacks[5], Pack.WORKING6);
            addCardToTable(cards, this.workingStacks[6], Pack.WORKING7);         
        } else {
             return "";
        }
        
        // checking if any card can be put on target
        for(int i=0; i < this.targets.length; i++){    
            //looking for any ACE
            if(this.targets[i].isEmpty()){
                for(Card.Color c: Card.Color.values()){
                    Card card = factory.createCard(c, 1);
                    card.turnFaceUp();
                    Pack p = cards.get(card);
                    //we are not moving ace from one target to another
                    if(p != null && !p.getPackType().equals(Pack.TARGET1.getPackType())) {
                        moves = moves.concat(m++ + ". " + "move "+card.toString() + " from " + p.toString() + " to target "+(i+1)+"\n");
                    }
                }
            } else { // other cards
                Card tCard = targets[i].get();
                Card card = factory.createCard(tCard.color(), tCard.value()+1);
                if(card == null)
                    continue;
                card.turnFaceUp();
                Pack p = cards.get(card);
                if(p != null) {
                    moves = moves.concat(m++ + ". " + "move "+card.toString() + " from " + p.toString() + " to target "+(i+1)+"\n");
                }  
            }        
         
        }
        
      
        for(int i=0; i < this.workingStacks.length; i++){    
            //looking for any KING
            if(this.workingStacks[i].isEmpty()){
                for(Card.Color c: Card.Color.values()){
                    Card card = factory.createCard(c, 13);
                    card.turnFaceUp();
                    Pack p = cards.get(card);
                    if(p != null) {
                        moves = moves.concat(m++ + ". " + "move card/cards starting with "+card.toString() + " from " + p.toString() + " to working stack "+(i+1)+"\n");
                    }
                }
            } else {
                Card tCard = workingStacks[i].get();
                Card card1;
                Card card2;
                if(tCard.color().similarColorTo(Card.Color.HEARTS)){
                    card1 = factory.createCard(Card.Color.SPADES, tCard.value()-1);
                    card2 = factory.createCard(Card.Color.CLUBS, tCard.value()-1);
                } else {
                    card1 = factory.createCard(Card.Color.HEARTS, tCard.value()-1);
                    card2 = factory.createCard(Card.Color.DIAMONDS, tCard.value()-1);
                }
                Pack p;
                if(card1 != null){
                    card1.turnFaceUp();
                    p = cards.get(card1);
                    if(p != null) {
                        if(p.getPackType() == PackType.TARGET){
                            moves = moves.concat(m++ + ". " + "move card "+card1.toString() + " from " + p.toString() + " to working stack"+(i+1)+"\n");
                        } else {
                            moves = moves.concat(m++ + ". " + "move card/cards starting with "+card1.toString() + " from " + p.toString() + " to working stack"+(i+1)+"\n");
                        }
                    }  
                }
                
                card1 = card2;
                if(card1 == null)
                    continue;
                
                card1.turnFaceUp();
                p = cards.get(card1);
                if(p != null) {
                    
                    if(p.getPackType() == PackType.TARGET){
                            moves = moves.concat(m++ + ". " + "move card "+card1.toString() + " from " + p.toString() + " to working stack "+(i+1)+"\n");
                    } else {
                            moves = moves.concat(m++ + ". " + "move card/cards starting with "+card1.toString() + " from " + p.toString() + " to working stack "+(i+1)+"\n");
                    }
                } 
                
            }        
         
        }
        
        if(this.deck.isEmpty() && !this.waste.isEmpty()){
            moves = moves.concat(m++ + ". " + "reaload deck\n");
        } 
        
        if(!this.deck.isEmpty()){
            moves = moves.concat(m++ + ". " + "get card from deck to waste\n");
        }
           
        return moves;   
    }    
}
