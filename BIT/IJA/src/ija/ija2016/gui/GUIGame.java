/**
 * @file GUIGame.java
 * @brief Graphic representation of current game
 * @author Juraj Ondrej Dubrava
 */



package ija.ija2016.gui;
import ija.ija2016.model.board.Board;
import ija.ija2016.model.board.Game;
import ija.ija2016.model.board.Pack;
import ija.ija2016.model.cards.CardDeck;
import ija.ija2016.model.cards.CardStack;


/**
 * Class representing graphic game
 * 
 */
public class GUIGame {
    
    private int id;
    private GDeck gameDeck;
    private GStack gameStack;
    private CardStack currentStack;
    private CardDeck currentDeck;
    private Board gameBoard;
    private int gameId;     
    private GUIGameBoard board;
    private Game actualGame;
    
   /**
    * Create new graphic game
    * @param gameBoard current gameboard
    * @param actualGame current game
    * @param board  current graphic board
    * @param gameId id of current game
    */
    public GUIGame(Board gameBoard,Game actualGame,GUIGameBoard board,int gameId) {
        
        this.actualGame = actualGame;
        this.gameBoard = gameBoard;
        this.board = board;
        this.gameId = gameId;
    }

    /**
     * Set value for actual game
     * @param game current game
     */
    public void setActualGame(Game game){
        this.actualGame = game;
    }
    
    /**
     * Display current state of whole game - display state of decks and stacks
     */
    public void display(){
        
        Game currentGame = this.actualGame;
        CardDeck waste = currentGame.getWaste();
        CardDeck source = currentGame.getDeck();
        GDeck wasteDeck = new GDeck(this.gameBoard,currentGame,Pack.WASTE,this.board,this.gameId);
        
        //display state of waste deck
        wasteDeck.display();
        GDeck sourceDeck = new GDeck(this.gameBoard,currentGame,Pack.DECK,this.board,this.gameId);
        //display state of source deck
        sourceDeck.display();
        CardDeck[] targets = currentGame.getTargetPacks();
        Pack deckType = null;
        int i;
        int j;
        //display state of each target deck
        for(i=0; i < 4; i++){
            if(i == 0){
                deckType = Pack.TARGET1;
            }
            if(i == 1){
                deckType = Pack.TARGET2;
            }
            if(i == 2){
                deckType = Pack.TARGET3;
            }
            if(i == 3){
                deckType = Pack.TARGET4;
            }
            CardDeck targetDeck = targets[i];
            GDeck tDeck = new GDeck(this.gameBoard,currentGame,deckType,this.board,this.gameId);
            tDeck.display();
            
        }
        
       //display state of each stack in game
        for (j =0; j < 7; j++){
            GStack stack = new GStack(this.gameBoard,this.board,this.gameId,j);
            stack.display();
        }
        
     
        
        
    }
   
    
    
    
}
