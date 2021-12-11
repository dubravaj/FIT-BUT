/**
 * @file Load.java
 * @brief Implementation of load of game
 * @author Martin Marusiak
 */
package ija.ija2016.model.board;

import ija.ija2016.model.cards.Card;
import ija.ija2016.model.cards.CardDeck;
import ija.ija2016.model.cards.CardStack;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Class represing load of game
 */
public class Load {
    
    protected CardDeck deck;
    protected CardDeck waste;
    protected CardDeck[] targets;
    protected CardStack[] workingStacks;
    protected Score score;
    protected Set<Card> allCards;
    protected boolean cardOk;

    /**
     * Constructor of load
     */
    public Load(){
        this.allCards = new HashSet<>();
        AbstractFactorySolitaire factory = new FactoryKlondike();
        /* this cards will be used later to check if file contains rigt cards */
        for (int i = 1; i <= 13; i++) { this.allCards.add(factory.createCard(Card.Color.CLUBS,i)); }
        for (int i = 1; i <= 13; i++) { this.allCards.add(factory.createCard(Card.Color.DIAMONDS,i)); }
        for (int i = 1; i <= 13; i++) { this.allCards.add(factory.createCard(Card.Color.HEARTS,i)); }
        for (int i = 1; i <= 13; i++) { this.allCards.add(factory.createCard(Card.Color.SPADES,i)); }  
        this.cardOk = true;
    }
    /**
     * Get card deck
     * @return card deck
     */
    public CardDeck getDeck() {
        return deck;
    }
    /** 
     * Get card waste
     * @return card waste
     */
    public CardDeck getWaste() {
        return waste;
    }
    /**
     * Get target packs
     * @return target packs
     */
    public CardDeck[] getTargets() {
        return targets;
    }
    /**
     * Get working packs
     * @return working packs
     */
    public CardStack[] getWorkingStacks() {
        return workingStacks;
    }
    /**
     * Get score
     * @return score
     */
    public Score getScore() {
        return score;
    }
    /**
     * Fills deck with cards that are stored in xml file
     * @param deck deck to be filled with cards
     * @param deckE element that represents deck
     * @param factory klondike factory
     */
    private void fillDeck(CardDeck deck, Element deckE, AbstractFactorySolitaire factory){
         NodeList cardsNode = deckE.getElementsByTagName("card");
         for(int i=0; i < cardsNode.getLength(); i++){
            Element cardE = (Element) cardsNode.item(i);
            int value = Integer.parseInt(cardE.getAttribute("value"));
            Card.Color c = Card.Color.valueOf(cardE.getAttribute("color"));
            Card card = factory.createCard(c, value);
            if(!this.allCards.remove(card))
                this.cardOk = false;
            boolean turned = Boolean.parseBoolean(cardE.getAttribute("turned"));
            if(turned){
                card.turnFaceUp();              
            }
            deck.put(card);
         }
    }
    
    /**
     * Load game from file
     * @param file file with stored game
     * @return true if game was loaded, false if an error occured
     */
    public boolean load(File file) {
        
      AbstractFactorySolitaire factory; 
      factory = new FactoryKlondike();
      

      try {	
         this.deck = factory.createEmptyCardDeck();
         this.waste =  factory.createCardWaste();
         this.workingStacks = factory.createWorkingPacks();
         this.targets = factory.createTargetPacks();
         this.score = new Score();
         
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.parse(file);
         doc.getDocumentElement().normalize();
         
         //loading target packs
         NodeList deckNode = doc.getElementsByTagName("targetPack");
         
         if(deckNode.getLength() == this.targets.length){      
             for(int i=0; i<this.targets.length; i++){
                Element deckE = (Element) deckNode.item(i);
                fillDeck(this.targets[i], deckE, factory);
             }
         }
         else
             return false;
         
          //loading working packs
         deckNode = doc.getElementsByTagName("workingStack");
         
         if(deckNode.getLength() == this.workingStacks.length){      
             for(int i=0; i<this.workingStacks.length; i++){
                Element deckE = (Element) deckNode.item(i);
                fillDeck(this.workingStacks[i], deckE, factory);
             }
         }
         else
             return false;
         
         //loading deck
         deckNode = doc.getElementsByTagName("deck");
               
         if(deckNode.getLength() == 1){
             Element deckE = (Element) deckNode.item(0);
             fillDeck(this.deck, deckE, factory);
         }
         else
             return false;
         
         //loading waste
         deckNode = doc.getElementsByTagName("waste");
         
         if(deckNode.getLength() == 1){
             Element deckE = (Element) deckNode.item(0);
             fillDeck(this.waste, deckE, factory);
         }
         else
             return false;
         
         //loading score 
         deckNode = doc.getElementsByTagName("score");
         if(deckNode.getLength() == 1){
             Element scoreE = (Element) deckNode.item(0);
             int value = Integer.parseInt(scoreE.getAttribute("value"));
             this.score.setScore(value);
         }
         else
             return false;
         
    
      } catch (IOException | ParserConfigurationException | DOMException | SAXException e) {
          return false;
      }
      return this.allCards.isEmpty() && this.cardOk;
   }


}
