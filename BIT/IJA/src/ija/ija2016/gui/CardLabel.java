
/**
 *  @file CardLabel.java
 *  @brief Representation of card in GUI
 *  @author Juraj Ondrej Dubrava
 */
package ija.ija2016.gui;
import ija.ija2016.model.cards.Card;
import javax.swing.JLabel;


/**
 * Class representing image of card
 */
public class CardLabel extends JLabel{
    
    private Card stackCard;
    
    /**
     * Creates new card
     */
    public CardLabel(){
        
        this.stackCard = null;
        
    }
    /**
     * Get represented card object 
     * @return card object represeneted by GUI
     */
    public Card getStackCard(){
        return this.stackCard;
    }
   /**
    * Set stack card 
    * @param c represented card
    */
    public void setStackCard(Card c){
        this.stackCard = c;
    }
    
    
}
