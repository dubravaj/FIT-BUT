/**
 * @file Save.java
 * @brief Implementation of game saving
 * @author Martin Marusiak
 */
package ija.ija2016.model.board;

import ija.ija2016.model.cards.Card;
import ija.ija2016.model.cards.CardDeck;
import ija.ija2016.model.cards.CardStack;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMException;

/**
 * Save game
 */
public class Save {
    
    protected CardDeck deck;
    protected CardDeck waste;
    protected CardDeck[] targets;
    protected CardStack[] workingStacks;
    protected Score score;

    /**
     * Constructor
     * @param deck card deck
     * @param waste waste deck
     * @param targets target deck
     * @param workingStacks working stack
     * @param score score
     */
    public Save(CardDeck deck, CardDeck waste, CardDeck[] targets, CardStack[] workingStacks, Score score) {
        this.deck = deck;
        this.waste = waste;
        this.targets = targets;
        this.workingStacks = workingStacks;
        this.score = score;
    }
    
    /**
     * This method reads all card of card deck and creates card elements and insert th
     * @param deckEl element representing deck
     * @param doc document used for creating elements
     * @param deck card deck which content - cards will be saved as elements to deckEl
     */
    private void fillDeck(Element deckEl, Document doc, CardDeck deck){
         int size = deck.size();
         for(int i=0; i < size; i++){
             Card card = deck.get(i);
             Element cardEl = doc.createElement("card");
             Attr attrValue = doc.createAttribute("value");
             attrValue.setValue(Integer.toString(card.value()));
             cardEl.setAttributeNode(attrValue);
             Attr attrColor = doc.createAttribute("color");
             attrColor.setValue(card.color().name());
             cardEl.setAttributeNode(attrColor);       
             Attr attrFaceUp = doc.createAttribute("turned");
             attrFaceUp.setValue(Boolean.toString(card.isTurnedFaceUp()));
             cardEl.setAttributeNode(attrFaceUp);
             cardEl.appendChild(doc.createTextNode(card.toString()));
             deckEl.appendChild(cardEl);
         }
    }
    /**
     * Save game to file
     * @param filePath path to file
     * @return true if game was saved
     */
    public boolean save(String filePath){

      try {
          
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.newDocument();
         
         // creating root element
         Element root = doc.createElement("Solitaire");
         doc.appendChild(root);

         // deck element

         Element deckEl = doc.createElement("deck");
         root.appendChild(deckEl);
         this.fillDeck(deckEl, doc, this.deck);
         
         // waste element 
         
         Element wasteEl = doc.createElement("waste");
         root.appendChild(wasteEl);
         this.fillDeck(wasteEl, doc, this.waste);   
         
         // target packs elements
         
         int i = 0;
         for (CardDeck target : this.targets) {
             Element targetEl = doc.createElement("targetPack");
             root.appendChild(targetEl);
             Attr attrTarget = doc.createAttribute("number");
             attrTarget.setValue(Integer.toString(i));
             targetEl.setAttributeNode(attrTarget);
             this.fillDeck(targetEl, doc, target);
             i++;
         }
         
         // working stacks elements
         
         i = 0;
         for (CardDeck stack : this.workingStacks) {
             Element stackEl = doc.createElement("workingStack");
             root.appendChild(stackEl);
             Attr attrStack = doc.createAttribute("number");
             attrStack.setValue(Integer.toString(i));
             stackEl.setAttributeNode(attrStack);
             this.fillDeck(stackEl, doc, stack);
             i++;
         }
         
         // score element
         
         Element scoreEl = doc.createElement("score");
         root.appendChild(scoreEl);
         Attr attrScore= doc.createAttribute("value");
         attrScore.setValue(Integer.toString(this.score.getScore()));
         scoreEl.setAttributeNode(attrScore);
        
         // writing xml to file
         
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         Transformer transformer = transformerFactory.newTransformer();
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
         DOMSource source = new DOMSource(doc);
         StreamResult result = new StreamResult(new File(filePath));
         transformer.transform(source, result);
         
      } catch (ParserConfigurationException | TransformerException | DOMException e) {
         return false;
      }
      return true;
    }
    
}

