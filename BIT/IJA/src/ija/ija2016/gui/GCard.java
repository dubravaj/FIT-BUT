/**
 * @file GCard.java
 * @brief Representation of card informations in GUI
 * @author Juraj Ondrej Dubrava
 */
package ija.ija2016.gui;

import ija.ija2016.model.cards.Card;
import javax.swing.ImageIcon;

import java.awt.*;

/**
 * Class representing card in GUI
 * 
 */
public class GCard extends ImageIcon{
    
  
    private int width;
    private int height;
    private Card c;
    private ImageIcon cardImage;
    
    /**
     *  Creates new card
     * @param width width of card image
     * @param height height of card image
     * @param c represented card
     */
    public GCard(int width,int height,Card c){
        
        this.width = width;
        this.height = height;
        this.c = c;
        
    }
 
    /**
     * Get card to be shown
     * @return shown card
     */
    public Card getCurrentCard(){
        return this.c;
    }
    /**
     * Set front image for represented card
     */
    public void setFrontImage(){
        String path = "./lib/cards/"+String.valueOf(this.c.value())+ this.c.color().toString()+".png";
        this.cardImage = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(this.width,this.height,Image.SCALE_DEFAULT));
    }
    /**
     * Set back image for face down turned card
     */
    public void setFaceDownImage(){
        String path  = "./lib/cards/back_image.png";
        this.cardImage = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(this.width,this.height,Image.SCALE_DEFAULT));
    }
    
    /**
     * Get card image
     * @return card image
     */
    public ImageIcon getCardImage(){
        return this.cardImage;
    }
    
    /**
     * Get width of card image
     * @return image width
     */
    public int getWidth(){
        return this.width;
    }
    
    /**
     * Get height of card image
     * @return image height
     */
    public int getHeight(){
        return this.height;
    }
    
}
