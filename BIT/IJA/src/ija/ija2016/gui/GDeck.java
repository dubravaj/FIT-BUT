/**
 * @file GDeck.java
 * @brief Deck representation in GUI
 * @author Juraj Ondrej Dubrava
 */

package ija.ija2016.gui;
import ija.ija2016.model.board.Board;
import ija.ija2016.model.board.Game;
import ija.ija2016.model.board.Pack;
import ija.ija2016.model.cards.Card;
import ija.ija2016.model.cards.CardDeck;
import javax.swing.JLabel;

/**
 * Class representing deck of cards in GUI
 */
public class GDeck {
    
    
    public int deckId;
    public int gameId;
    private Game game;
    private Board gameBoard;
    private Pack deckType;
    private GUIGameBoard board;
    private CardDeck[] currentTargetDeck;
    private int gameIndex;
    
    /**
     * Create new GUI deck
     * @param gameBoard current gameboard
     * @param game current game
     * @param deckType type of deck
     * @param board graphic gameboard
     * @param gameIndex index of current game
     */
    public GDeck(Board gameBoard ,Game game,Pack deckType,GUIGameBoard board,int gameIndex) {
         
       this.gameBoard = gameBoard; 
       this.game = game;
       this.deckType = deckType;
       this.board = board;
       this.gameIndex = gameIndex;
       
    }
    
   
    /**
     * Display actual state of all types of decks
     */
    public void display(){
    
     this.currentTargetDeck = this.game.getTargetPacks();
     
     //display source deck
     if(deckType == Pack.DECK){
         
         if(game.getDeck().isEmpty()){
             
             JLabel b = null;
             if(gameIndex == 0){
               b = board.getDeckByName("SourceDeckG11");
             }
             if(gameIndex == 1){
               b = board.getDeckByName("SourceDeckG21");
             }
             if(gameIndex == 2){
               b = board.getDeckByName("SourceDeckG31");
             }
             if(gameIndex == 3){
               b = board.getDeckByName("SourceDeckG41");
             }
         
            b.setIcon(null);
             
         }
         else{
            
            GCard topGCard = new GCard(80,100,null);
            topGCard.setFaceDownImage();
            JLabel b = null;
             if(gameIndex == 0){
               b = board.getDeckByName("SourceDeckG11");
             }
             if(gameIndex == 1){
               b = board.getDeckByName("SourceDeckG21");
             }
             if(gameIndex == 2){
               b = board.getDeckByName("SourceDeckG31");
             }
             if(gameIndex == 3){
               b = board.getDeckByName("SourceDeckG41");
             }
            
             b.setIcon(topGCard.getCardImage());
         }
     }
     
     
     //display waste deck
     if(deckType == Pack.WASTE){
         
         
         
         if(game.getWaste().isEmpty()){
             JLabel b = null;
              
             if(gameIndex == 0){
                
               b = board.getDeckByName("WasteDeckG1");
             }
             if(gameIndex == 1){
               
               b = board.getDeckByName("WasteDeckG2");
             }
             if(gameIndex == 2){
               
               b = board.getDeckByName("WasteDeckG3");
               
             }
             if(gameIndex == 3){
             
               b = board.getDeckByName("WasteDeckG4");
             }
             
            b.setIcon(null);
             
         }
         else{
            Card topCard = game.getWaste().get();
            GCard topGCard = new GCard(80,100,topCard);
            topGCard.setFrontImage();
            JLabel b = null;
             if(gameIndex == 0){
               b = board.getDeckByName("WasteDeckG1");
             }
             if(gameIndex == 1){
               b = board.getDeckByName("WasteDeckG2");
             }
             if(gameIndex == 2){
               b = board.getDeckByName("WasteDeckG3");
             }
             if(gameIndex == 3){
               b = board.getDeckByName("WasteDeckG4");
             }
            
             b.setIcon(topGCard.getCardImage());
         }
     }
     //display targetdeck 1
     else if(deckType == Pack.TARGET1){
         
         
         if(this.currentTargetDeck[0].isEmpty()){
            
             JLabel b = null;
             if(gameIndex == 0){
               b = board.getDeckByName("TargetDeckG11");
             }
             if(gameIndex == 1){
               b = board.getDeckByName("TargetDeckG21");
             }
             if(gameIndex == 2){
               b = board.getDeckByName("TargetDeckG31");
             }
             if(gameIndex == 3){
               b = board.getDeckByName("TargetDeckG41");
             }
             
            b.setIcon(null);
         }
         else{
             Card topCard = this.currentTargetDeck[0].get();
             GCard topGCard = new GCard(80,100,topCard);
             topGCard.setFrontImage();
             JLabel b = null;
             if(gameIndex == 0){
               b = board.getDeckByName("TargetDeckG11");
             }
             if(gameIndex == 1){
               b = board.getDeckByName("TargetDeckG21");
             }
             if(gameIndex == 2){
               b = board.getDeckByName("TargetDeckG31");
             }
             if(gameIndex == 3){
               b = board.getDeckByName("TargetDeckG41");
             }
             b.setIcon(topGCard.getCardImage());
         
         }
     }
     //display targetdeck2
     else if(deckType == Pack.TARGET2){
         
         if(this.currentTargetDeck[1].isEmpty()){
            
             JLabel b = null;
             if(gameIndex == 0){
               b = board.getDeckByName("TargetDeckG12");
             }
             if(gameIndex == 1){
               b = board.getDeckByName("TargetDeckG22");
             }
             if(gameIndex == 2){
               b = board.getDeckByName("TargetDeckG32");
             }
             if(gameIndex == 3){
               b = board.getDeckByName("TargetDeckG42");
             }
             
            b.setIcon(null);
         }
         else{
            Card topCard = this.currentTargetDeck[1].get();
            GCard topGCard = new GCard(80,100,topCard);
            topGCard.setFrontImage();
            JLabel b = null;
             if(gameIndex == 0){
               b = board.getDeckByName("TargetDeckG12");
             }
             if(gameIndex == 1){
               b = board.getDeckByName("TargetDeckG22");
             }
             if(gameIndex == 2){
               b = board.getDeckByName("TargetDeckG32");
             }
             if(gameIndex == 3){
               b = board.getDeckByName("TargetDeckG42");
             }
         b.setIcon(topGCard.getCardImage());
         
         }
     }
     //display tergetdeck3
      else if(deckType == Pack.TARGET3){
         
          if(this.currentTargetDeck[2].isEmpty()){
            
             JLabel b = null;
             if(gameIndex == 0){
               b = board.getDeckByName("TargetDeckG13");
             }
             if(gameIndex == 1){
               b = board.getDeckByName("TargetDeckG23");
             }
             if(gameIndex == 2){
               b = board.getDeckByName("TargetDeckG33");
             }
             if(gameIndex == 3){
               b = board.getDeckByName("TargetDeckG43");
             }
             
            b.setIcon(null);
         }
         else{
            Card topCard = this.currentTargetDeck[2].get();
            GCard topGCard = new GCard(80,100,topCard);
            topGCard.setFrontImage();
            JLabel b = null;
             if(gameIndex == 0){
               b = board.getDeckByName("TargetDeckG13");
             }
             if(gameIndex == 1){
               b = board.getDeckByName("TargetDeckG23");
             }
             if(gameIndex == 2){
               b = board.getDeckByName("TargetDeckG33");
             }
             if(gameIndex == 3){
               b = board.getDeckByName("TargetDeckG43");
             }
             b.setIcon(topGCard.getCardImage());
         
        }
     }
      //display targetdeck4
      else if(deckType == Pack.TARGET4){
         
          
        if(this.currentTargetDeck[3].isEmpty()){
            
             JLabel b = null;
             if(gameIndex == 0){
               b = board.getDeckByName("TargetDeckG14");
             }
             if(gameIndex == 1){
               b = board.getDeckByName("TargetDeckG24");
             }
             if(gameIndex == 2){
               b = board.getDeckByName("TargetDeckG34");
             }
             if(gameIndex == 3){
               b = board.getDeckByName("TargetDeckG44");
             }
             
            b.setIcon(null);
         }
        else{
         
           Card topCard = this.currentTargetDeck[3].get();
           GCard topGCard = new GCard(80,100,topCard);
           topGCard.setFrontImage();
           JLabel b = null;
             if(gameIndex == 0){
               b = board.getDeckByName("TargetDeckG14");
             }
             if(gameIndex == 1){
               b = board.getDeckByName("TargetDeckG24");
             }
             if(gameIndex == 2){
               b = board.getDeckByName("TargetDeckG34");
             }
             if(gameIndex == 3){
               b = board.getDeckByName("TargetDeckG44");
             }
         b.setIcon(topGCard.getCardImage());
         
        }
     }
     
    
    
    }
    
    
    
    
}
