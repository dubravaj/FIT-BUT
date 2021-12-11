/**
 * @file GStack.java
 * @brief Stack representation in GUI
 * @author Juraj Ondrej Dubrava
 */

package ija.ija2016.gui;

import ija.ija2016.model.board.Board;
import ija.ija2016.model.board.Game;
import ija.ija2016.model.cards.Card;
import ija.ija2016.model.cards.CardStack;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;

/**
 * Class representing stack of cards in GUI
 */
public class GStack {
    
    private Board gameBoard;
    private int gameIndex;
    private int workingStackId;
    private GUIGameBoard board;
    
    /**
     * Creates new graphic stack
     * @param gameBoard current gameboard
     * @param board current graphic gameboard
     * @param gameIndex index of current game
     * @param workingStackId index of working stack
     */
    public GStack(Board gameBoard, GUIGameBoard board,int gameIndex, int workingStackId){
        
        this.gameBoard = gameBoard;
        this.board = board;
        this.gameIndex = gameIndex;
        this.workingStackId = workingStackId;
        
        
    }
    
    /**
     * Display current state of cards in stack with workingStackId
     */
    public void display(){
        
     CardLabel workingStackCard;  
     ImageIcon stackCardImage;
     int index = board.getArrayIndex(board.gamePanel,gameIndex);
     Game actualGame =gameBoard.getGame(index);

     CardStack[] workingStack = actualGame.getWorkingPacks();
     CardStack actualStack = workingStack[workingStackId];
     int size = actualStack.size();
 
     
     JLayeredPane stack = board.getStackById(this.gameIndex, this.workingStackId);
     int offset = 0;
     stack.removeAll();
     stack.repaint();
     
     Card topCard;
     
     offset += size*10;
     
     //add cards to stack and display stack
     for(int i=size- 1 ; i >= 0; --i){
        
         workingStackCard = new CardLabel();
         workingStackCard.addMouseListener(this.board.stackCard);
         topCard = actualStack.get(i);
         workingStackCard.setStackCard(topCard);
         if(topCard == null){
             continue;
         }
         //set front image of card
         if(topCard.isTurnedFaceUp()){
        
             GCard top = new GCard(80,100,topCard);
             top.setFrontImage();
             stackCardImage = top.getCardImage(); 
         
           
        
         }
         //set back image for card
         else{
           
             GCard top = new GCard(80,100,null);
             top.setFaceDownImage();
             stackCardImage = top.getCardImage();
             
         }
         //set image of card and add it to stack
         workingStackCard.setIcon(stackCardImage);
         workingStackCard.setBounds(0,offset,80,100);
         stack.add(workingStackCard);
         offset -=10;
         
         
         
         
     }
    
     
     
     
     
        
        
    }
    
    
    
    
    
    
}
