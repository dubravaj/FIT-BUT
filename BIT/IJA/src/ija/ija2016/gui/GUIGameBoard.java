/**
 * @file GUIGameBoard.java
 * @brief Graphic representation of gameboard
 * @author Juraj Ondrej Dubrava
 */

package ija.ija2016.gui;
import ija.ija2016.model.board.Board;
import ija.ija2016.model.board.Game;
import ija.ija2016.model.board.GetPossibleMoves;
import ija.ija2016.model.board.Pack;
import ija.ija2016.model.cards.Card;
import ija.ija2016.model.cards.CardDeck;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.*;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.JOptionPane;


/**
 * Class representing gameboard graphically
 * 
 */
public class GUIGameBoard extends JFrame {

    private int actualNumberOfGames = 0;    
    private int DeckId;
    private int StackId;
    private static String clickedDeck;
    private static String clickedStack;
    private static String actualGame;
    public Card clickedStackCard;
    private Board gameBoard;
    public String parentStack;
    public int[] gamePanel;
 
    
   /**
    * Creates new graphic gameboard 
    */
    public GUIGameBoard() {
      
        initComponents();
        setListeners();

       gameBoard = new Board();
       //array representing actual state of array with games in board
       this.gamePanel = new int[4];
       this.gamePanel[0] = -1;
       this.gamePanel[1] = -1;
       this.gamePanel[2] = -1;
       this.gamePanel[3] = -1;


       //set default card images of source decks
        GCard icon1 = new GCard(80,110,null);
        icon1.setFaceDownImage();
        this.SourceDeckG1.setIcon(icon1.getCardImage());
        GCard icon2 = new GCard(100,110,null);
        icon2.setFaceDownImage();
        this.SourceDeckG2.setIcon(icon2.getCardImage());
        GCard icon3 = new GCard(100,110,null);
        icon3.setFaceDownImage();
        this.SourceDeckG3.setIcon(icon3.getCardImage());
        GCard icon4 = new GCard(100,110,null);
        icon4.setFaceDownImage();
        this.SourceDeckG4.setIcon(icon4.getCardImage());
       
    }
     
    /**
     * Remove game from gamePanel array,which represents
     * actual state of an array with played games
     * @param i index of game to be removed
     * @return true game was removed
     * @return false index is wrong
     */
    public boolean cancelGamePanel(int i){
        
         if(this.actualNumberOfGames> 0 && i < this.actualNumberOfGames && i >= 0){
            
            for(int j=i; j < this.actualNumberOfGames- 1; j++){
                this.gamePanel[j] =  this.gamePanel[j+1];
                
            }
            
            this.gamePanel[this.actualNumberOfGames-1] = -1;
           
            return true;
        }
              
        return false;
    }
 
    /**
     * Get index of certain game in gamePanel array
     * @param arr array to be searched
     * @param value represents id of game
     * @return index of game in an array
     */
  public int getArrayIndex(int[] arr,int value) {

        int k=0;
        for(int i=0;i<arr.length;i++){

            if(arr[i]==value){
                k=i;
                break;
            }
        }
    return k;
}
 /**
  * Search for given game in target array
  * @param arr searched array
  * @param targetValue id of searched game
  * @return true game is in an array
  * @return false game is not in an array
  */
    
 public boolean findValue(int[] arr, int targetValue) {
	for(int s: arr){
            if(s == targetValue) {
                return true;
            } 
                
	}
    return false;
}   
     
     
 /**
  * Get game by id
  * @param gameId game id
  * @return game with gameId
  */    
 public Game getGameById(int gameId){
        
     Game boardGame = null;
        if(gameId == 0){
           boardGame = this.gameBoard.getGame(0);
        }
        if(gameId == 1){
            boardGame = this.gameBoard.getGame(1);
        }
        if(gameId == 2){
            boardGame = this.gameBoard.getGame(2);
        }
        if(gameId == 3){
            boardGame = this.gameBoard.getGame(3);
        }
        return boardGame;
    }
     
  /**
   * Get JLabel representing deck by name
   * @param name name of deck
   * @return label representing deck
   */   
  public JLabel getDeckByName(String name){
        
        if(name.equals("SourceDeckG11")){
            return this.SourceDeckG1;
        }
        if(name.equals("WasteDeckG1")){
            return this.WasteDeckG1;
        }
        if(name.equals("TargetDeckG11")){
            return this.TargetDeckG11;
        }
        if(name.equals("TargetDeckG12")){
            return this.TargetDeckG12;
        }
        if(name.equals("TargetDeckG13")){
            return this.TargetDeckG13;
        }
        if(name.equals("TargetDeckG14")){
            return this.TargetDeckG14;
        }
        if(name.equals("SourceDeckG21")){
            return this.SourceDeckG2;
        }
        if(name.equals("WasteDeckG2")){
            return this.WasteDeckG2;
        }
        if(name.equals("TargetDeckG21")){
            return this.TargetDeckG21;
        }
        if(name.equals("TargetDeckG22")){
            return this.TargetDeckG22;
        }
        if(name.equals("TargetDeckG23")){
            return this.TargetDeckG23;
        }
        if(name.equals("TargetDeckG24")){
            return this.TargetDeckG24;
        }
        if(name.equals("SourceDeckG31")){
            return this.SourceDeckG3;
        }
        if(name.equals("WasteDeckG3")){
            return this.WasteDeckG3;
        }
        if(name.equals("TargetDeckG31")){
            return this.TargetDeckG31;
        }
        if(name.equals("TargetDeckG32")){
            return this.TargetDeckG32;
        }
        if(name.equals("TargetDeckG33")){
            return this.TargetDeckG33;
        }
        if(name.equals("TargetDeckG34")){
            return this.TargetDeckG34;
        }
        if(name.equals("SourceDeckG41")){
            return this.SourceDeckG4;
        }
        if(name.equals("WasteDeckG4")){
            return this.WasteDeckG4;
        }
        if(name.equals("TargetDeckG41")){
            return this.TargetDeckG41;
        }
        if(name.equals("TargetDeckG42")){
            return this.TargetDeckG42;
        }
        if(name.equals("TargetDeckG43")){
            return this.TargetDeckG43;
        }
        if(name.equals("TargetDeckG44")){
            return this.TargetDeckG44;
        }
        return null;
        
    } 
  
  private JButton getScoreButton(int i){
      if(i == 0){
          return this.ScoreG1;
      }
      if(i == 1){
          return this.ScoreG2;
      }
      if(i == 2){
          return this.ScoreG3;
      }
      if(i == 3){
          return this.ScoreG4;
      }
      return null;
  }
     
   /**
    * Get graphic object representing stack 
    * @param currentGame current game
    * @param currentStack id of the stack 
    * @return JLayeredPane representing stack
    */
    public JLayeredPane getStackById(int currentGame, int currentStack){
          
            if(currentGame == 0){
                switch(currentStack){
                    case 0:
                        return this.StackG11;
                    case 1:
                        return this.StackG12;
                    case 2:
                        return this.StackG13;  
                    case 3:
                        return this.StackG14;
                    case 4:
                        return this.StackG15;
                    case 5:
                        return this.StackG16;   
                    case 6:
                        return this.StackG17;     
                }
                        
            }
            if(currentGame == 1){
                switch(currentStack){
                    case 0:
                        return this.StackG21;
                    case 1:
                        return this.StackG22;
                    case 2:
                        return this.StackG23;  
                    case 3:
                        return this.StackG24;
                    case 4:
                        return this.StackG25;
                    case 5:
                        return this.StackG26;   
                    case 6:
                        return this.StackG27;     
                }
                        
            }
            if(currentGame == 2){
                switch(currentStack){
                    case 0:
                        return this.StackG31;
                    case 1:
                        return this.StackG32;
                    case 2:
                        return this.StackG33;  
                    case 3:
                        return this.StackG34;
                    case 4:
                        return this.StackG35;
                    case 5:
                        return this.StackG36;   
                    case 6:
                        return this.StackG37;     
                }
                        
            }
            if(currentGame == 3){
                switch(currentStack){
                    case 0:
                        return this.StackG41;
                    case 1:
                        return this.StackG42;
                    case 2:
                        return this.StackG43;  
                    case 3:
                        return this.StackG44;
                    case 4:
                        return this.StackG45;
                    case 5:
                        return this.StackG46;   
                    case 6:
                        return this.StackG47;     
                }
                        
            }
                    
       
        return null;
        
    } 
     /**
      * Check if played game was won
      * @param playedGame current played game
      */
     public void winGame(Game playedGame){
         
         if(playedGame.won()){
            JOptionPane.showMessageDialog(this,"You won this game!","Game",JOptionPane.INFORMATION_MESSAGE);
         }
         
     }
    
     /**
      * Set listeners for graphic components
      */
     public void setListeners(){
        this.SourceDeckG1.setName("SourceDeck1");
        this.SourceDeckG1.addMouseListener(ml);
        this.SourceDeckG2.setName("SourceDeck2");
        this.SourceDeckG2.addMouseListener(ml);
        this.SourceDeckG3.setName("SourceDeck3");
        this.SourceDeckG3.addMouseListener(ml);
        this.SourceDeckG4.setName("SourceDeck4");
        this.SourceDeckG4.addMouseListener(ml);
        
        this.WasteDeckG1.setName("WasteDeck1");
        this.WasteDeckG1.addMouseListener(ml);
        this.WasteDeckG2.setName("WasteDeck2");
        this.WasteDeckG2.addMouseListener(ml);
        this.WasteDeckG3.setName("WasteDeck3");
        this.WasteDeckG3.addMouseListener(ml);
        this.WasteDeckG4.setName("WasteDeck4");
        this.WasteDeckG4.addMouseListener(ml);
        
        this.TargetDeckG11.setName("TargetDeck11");
        this.TargetDeckG11.addMouseListener(ml);
        this.TargetDeckG12.setName("TargetDeck12");
        this.TargetDeckG12.addMouseListener(ml);
        this.TargetDeckG13.setName("TargetDeck13");
        this.TargetDeckG13.addMouseListener(ml);
        this.TargetDeckG14.setName("TargetDeck14");
        this.TargetDeckG14.addMouseListener(ml);
        
        this.TargetDeckG21.setName("TargetDeck21");
        this.TargetDeckG21.addMouseListener(ml);
        this.TargetDeckG22.setName("TargetDeck22");
        this.TargetDeckG22.addMouseListener(ml);
        this.TargetDeckG23.setName("TargetDeck23");
        this.TargetDeckG23.addMouseListener(ml);
        this.TargetDeckG24.setName("TargetDeck24");
        this.TargetDeckG24.addMouseListener(ml);
        
        this.TargetDeckG31.setName("TargetDeck31");
        this.TargetDeckG31.addMouseListener(ml);
        this.TargetDeckG32.setName("TargetDeck32");
        this.TargetDeckG32.addMouseListener(ml);
        this.TargetDeckG33.setName("TargetDeck33");
        this.TargetDeckG33.addMouseListener(ml);
        this.TargetDeckG34.setName("TargetDeck34");
        this.TargetDeckG34.addMouseListener(ml);
        
        this.TargetDeckG41.setName("TargetDeck41");
        this.TargetDeckG41.addMouseListener(ml);
        this.TargetDeckG42.setName("TargetDeck42");
        this.TargetDeckG42.addMouseListener(ml);
        this.TargetDeckG43.setName("TargetDeck43");
        this.TargetDeckG43.addMouseListener(ml);
        this.TargetDeckG44.setName("TargetDeck44");
        this.TargetDeckG44.addMouseListener(ml);
        
        this.StackG11.setName("StackG11");
        this.StackG11.addMouseListener(mlStack);
        this.StackG12.setName("StackG12");
        this.StackG12.addMouseListener(mlStack);
        this.StackG13.setName("StackG13");
        this.StackG13.addMouseListener(mlStack);
        this.StackG14.setName("StackG14");
        this.StackG14.addMouseListener(mlStack);
        this.StackG15.setName("StackG15");
        this.StackG15.addMouseListener(mlStack);
        this.StackG16.setName("StackG16");
        this.StackG16.addMouseListener(mlStack);
        this.StackG17.setName("StackG17");
        this.StackG17.addMouseListener(mlStack);
        
        this.StackG21.setName("StackG21");
        this.StackG21.addMouseListener(mlStack);
        this.StackG22.setName("StackG22");
        this.StackG22.addMouseListener(mlStack);
        this.StackG23.setName("StackG23");
        this.StackG23.addMouseListener(mlStack);
        this.StackG24.setName("StackG24");
        this.StackG24.addMouseListener(mlStack);
        this.StackG25.setName("StackG25");
        this.StackG25.addMouseListener(mlStack);
        this.StackG26.setName("StackG26");
        this.StackG26.addMouseListener(mlStack);
        this.StackG27.setName("StackG27");
        this.StackG27.addMouseListener(mlStack);
      
        this.StackG31.setName("StackG31");
        this.StackG31.addMouseListener(mlStack);
        this.StackG32.setName("StackG32");
        this.StackG32.addMouseListener(mlStack);
        this.StackG33.setName("StackG33");
        this.StackG33.addMouseListener(mlStack);
        this.StackG34.setName("StackG34");
        this.StackG34.addMouseListener(mlStack);
        this.StackG35.setName("StackG35");
        this.StackG35.addMouseListener(mlStack);
        this.StackG36.setName("StackG36");
        this.StackG36.addMouseListener(mlStack);
        this.StackG37.setName("StackG37");
        this.StackG37.addMouseListener(mlStack);
      
        this.StackG41.setName("StackG41");
        this.StackG41.addMouseListener(mlStack);
        this.StackG42.setName("StackG42");
        this.StackG42.addMouseListener(mlStack);
        this.StackG43.setName("StackG43");
        this.StackG43.addMouseListener(mlStack);
        this.StackG44.setName("StackG44");
        this.StackG44.addMouseListener(mlStack);
        this.StackG45.setName("StackG45");
        this.StackG45.addMouseListener(mlStack);
        this.StackG46.setName("StackG46");
        this.StackG46.addMouseListener(mlStack);
        this.StackG47.setName("StackG47");
        this.StackG47.addMouseListener(mlStack);
      
     }
     
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Game1 = new javax.swing.JPanel();
        SaveGame1 = new javax.swing.JButton();
        UndoGame1 = new javax.swing.JButton();
        HintGame1 = new javax.swing.JButton();
        CanceGame1 = new javax.swing.JButton();
        SourceDeckG1 = new javax.swing.JLabel();
        WasteDeckG1 = new javax.swing.JLabel();
        TargetDeckG11 = new javax.swing.JLabel();
        TargetDeckG12 = new javax.swing.JLabel();
        TargetDeckG13 = new javax.swing.JLabel();
        TargetDeckG14 = new javax.swing.JLabel();
        StackG11 = new javax.swing.JLayeredPane();
        StackG12 = new javax.swing.JLayeredPane();
        StackG13 = new javax.swing.JLayeredPane();
        StackG14 = new javax.swing.JLayeredPane();
        StackG15 = new javax.swing.JLayeredPane();
        StackG16 = new javax.swing.JLayeredPane();
        StackG17 = new javax.swing.JLayeredPane();
        ScoreG1 = new javax.swing.JButton();
        Game2 = new javax.swing.JPanel();
        SaveGame2 = new javax.swing.JButton();
        UndoGame2 = new javax.swing.JButton();
        HintGame2 = new javax.swing.JButton();
        CancelGame2 = new javax.swing.JButton();
        SourceDeckG2 = new javax.swing.JLabel();
        WasteDeckG2 = new javax.swing.JLabel();
        TargetDeckG21 = new javax.swing.JLabel();
        TargetDeckG22 = new javax.swing.JLabel();
        TargetDeckG23 = new javax.swing.JLabel();
        TargetDeckG24 = new javax.swing.JLabel();
        StackG21 = new javax.swing.JLayeredPane();
        StackG22 = new javax.swing.JLayeredPane();
        StackG23 = new javax.swing.JLayeredPane();
        StackG24 = new javax.swing.JLayeredPane();
        StackG25 = new javax.swing.JLayeredPane();
        StackG26 = new javax.swing.JLayeredPane();
        StackG27 = new javax.swing.JLayeredPane();
        ScoreG2 = new javax.swing.JButton();
        Game3 = new javax.swing.JPanel();
        SaveGame3 = new javax.swing.JButton();
        UndoGame3 = new javax.swing.JButton();
        HintGame3 = new javax.swing.JButton();
        CancelGame3 = new javax.swing.JButton();
        SourceDeckG3 = new javax.swing.JLabel();
        WasteDeckG3 = new javax.swing.JLabel();
        TargetDeckG31 = new javax.swing.JLabel();
        TargetDeckG32 = new javax.swing.JLabel();
        TargetDeckG33 = new javax.swing.JLabel();
        TargetDeckG34 = new javax.swing.JLabel();
        StackG31 = new javax.swing.JLayeredPane();
        StackG32 = new javax.swing.JLayeredPane();
        StackG33 = new javax.swing.JLayeredPane();
        StackG34 = new javax.swing.JLayeredPane();
        StackG35 = new javax.swing.JLayeredPane();
        StackG36 = new javax.swing.JLayeredPane();
        StackG37 = new javax.swing.JLayeredPane();
        ScoreG3 = new javax.swing.JButton();
        Game4 = new javax.swing.JPanel();
        SaveGame4 = new javax.swing.JButton();
        UndoGame4 = new javax.swing.JButton();
        HintGame4 = new javax.swing.JButton();
        CancelGame4 = new javax.swing.JButton();
        SourceDeckG4 = new javax.swing.JLabel();
        WasteDeckG4 = new javax.swing.JLabel();
        TargetDeckG41 = new javax.swing.JLabel();
        TargetDeckG42 = new javax.swing.JLabel();
        TargetDeckG43 = new javax.swing.JLabel();
        TargetDeckG44 = new javax.swing.JLabel();
        StackG41 = new javax.swing.JLayeredPane();
        StackG42 = new javax.swing.JLayeredPane();
        StackG43 = new javax.swing.JLayeredPane();
        StackG44 = new javax.swing.JLayeredPane();
        StackG45 = new javax.swing.JLayeredPane();
        StackG46 = new javax.swing.JLayeredPane();
        StackG47 = new javax.swing.JLayeredPane();
        ScoreG4 = new javax.swing.JButton();
        NewGame = new javax.swing.JButton();
        LoadGame = new javax.swing.JButton();
        MainBoard = new javax.swing.JPanel();

        Game1.setBackground(new java.awt.Color(0, 153, 51));

        SaveGame1.setText("Save Game");
        SaveGame1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SaveGame1MouseClicked(evt);
            }
        });

        UndoGame1.setText("Undo");
        UndoGame1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                UndoGame1MouseClicked(evt);
            }
        });

        HintGame1.setText("Hint");
        HintGame1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                HintGame1MouseClicked(evt);
            }
        });

        CanceGame1.setText("Cancel");
        CanceGame1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CanceGame1MouseClicked(evt);
            }
        });

        SourceDeckG1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        SourceDeckG1.setFocusable(false);
        SourceDeckG1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SourceDeckG1MouseClicked(evt);
            }
        });

        WasteDeckG1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        WasteDeckG1.setFocusable(false);
        WasteDeckG1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                WasteDeckG1MouseClicked(evt);
            }
        });

        TargetDeckG11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        TargetDeckG11.setFocusable(false);

        TargetDeckG12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        TargetDeckG12.setFocusable(false);

        TargetDeckG13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        TargetDeckG13.setFocusable(false);

        TargetDeckG14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        TargetDeckG14.setFocusable(false);

        StackG11.setFocusable(false);

        javax.swing.GroupLayout StackG11Layout = new javax.swing.GroupLayout(StackG11);
        StackG11.setLayout(StackG11Layout);
        StackG11Layout.setHorizontalGroup(
            StackG11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        StackG11Layout.setVerticalGroup(
            StackG11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 346, Short.MAX_VALUE)
        );

        StackG12.setFocusable(false);

        javax.swing.GroupLayout StackG12Layout = new javax.swing.GroupLayout(StackG12);
        StackG12.setLayout(StackG12Layout);
        StackG12Layout.setHorizontalGroup(
            StackG12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        StackG12Layout.setVerticalGroup(
            StackG12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 348, Short.MAX_VALUE)
        );

        StackG13.setFocusable(false);

        javax.swing.GroupLayout StackG13Layout = new javax.swing.GroupLayout(StackG13);
        StackG13.setLayout(StackG13Layout);
        StackG13Layout.setHorizontalGroup(
            StackG13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        StackG13Layout.setVerticalGroup(
            StackG13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        StackG14.setFocusable(false);

        javax.swing.GroupLayout StackG14Layout = new javax.swing.GroupLayout(StackG14);
        StackG14.setLayout(StackG14Layout);
        StackG14Layout.setHorizontalGroup(
            StackG14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        StackG14Layout.setVerticalGroup(
            StackG14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        StackG15.setFocusable(false);

        javax.swing.GroupLayout StackG15Layout = new javax.swing.GroupLayout(StackG15);
        StackG15.setLayout(StackG15Layout);
        StackG15Layout.setHorizontalGroup(
            StackG15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        StackG15Layout.setVerticalGroup(
            StackG15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        StackG16.setFocusable(false);

        javax.swing.GroupLayout StackG16Layout = new javax.swing.GroupLayout(StackG16);
        StackG16.setLayout(StackG16Layout);
        StackG16Layout.setHorizontalGroup(
            StackG16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        StackG16Layout.setVerticalGroup(
            StackG16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        StackG17.setAutoscrolls(true);
        StackG17.setFocusable(false);

        javax.swing.GroupLayout StackG17Layout = new javax.swing.GroupLayout(StackG17);
        StackG17.setLayout(StackG17Layout);
        StackG17Layout.setHorizontalGroup(
            StackG17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 90, Short.MAX_VALUE)
        );
        StackG17Layout.setVerticalGroup(
            StackG17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        ScoreG1.setText("Score: -1");

        javax.swing.GroupLayout Game1Layout = new javax.swing.GroupLayout(Game1);
        Game1.setLayout(Game1Layout);
        Game1Layout.setHorizontalGroup(
            Game1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Game1Layout.createSequentialGroup()
                .addGroup(Game1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Game1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(Game1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(SourceDeckG1, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                            .addComponent(StackG11))
                        .addGroup(Game1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(Game1Layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(StackG12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(StackG13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(StackG14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(StackG15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(StackG16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(StackG17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(Game1Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(WasteDeckG1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(Game1Layout.createSequentialGroup()
                        .addComponent(SaveGame1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(UndoGame1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(Game1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(Game1Layout.createSequentialGroup()
                                .addComponent(TargetDeckG11, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(TargetDeckG12, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(TargetDeckG13, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(TargetDeckG14, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(Game1Layout.createSequentialGroup()
                                .addComponent(HintGame1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CanceGame1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(169, 169, 169)
                                .addComponent(ScoreG1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(70, 70, 70))
        );
        Game1Layout.setVerticalGroup(
            Game1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Game1Layout.createSequentialGroup()
                .addGroup(Game1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SaveGame1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UndoGame1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HintGame1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CanceGame1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ScoreG1))
                .addGroup(Game1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Game1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(Game1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(TargetDeckG14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TargetDeckG13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TargetDeckG12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TargetDeckG11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(Game1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(Game1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SourceDeckG1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(WasteDeckG1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Game1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Game1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(StackG12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(StackG14)
                        .addComponent(StackG15)
                        .addComponent(StackG16)
                        .addComponent(StackG13)
                        .addComponent(StackG17))
                    .addComponent(StackG11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(122, Short.MAX_VALUE))
        );

        Game2.setBackground(new java.awt.Color(0, 153, 51));

        SaveGame2.setText("Save Game");
        SaveGame2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SaveGame2MouseClicked(evt);
            }
        });

        UndoGame2.setText("Undo");
        UndoGame2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                UndoGame2MouseClicked(evt);
            }
        });

        HintGame2.setText("Hint");
        HintGame2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                HintGame2MouseClicked(evt);
            }
        });

        CancelGame2.setText("Cancel");
        CancelGame2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CancelGame2MouseClicked(evt);
            }
        });

        SourceDeckG2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        SourceDeckG2.setFocusable(false);

        WasteDeckG2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        WasteDeckG2.setFocusable(false);

        TargetDeckG21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        TargetDeckG21.setFocusable(false);

        TargetDeckG22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        TargetDeckG22.setFocusable(false);

        TargetDeckG23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        TargetDeckG23.setFocusable(false);

        TargetDeckG24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        TargetDeckG24.setFocusable(false);
        TargetDeckG24.setPreferredSize(new java.awt.Dimension(3, 3));

        StackG21.setFocusable(false);

        javax.swing.GroupLayout StackG21Layout = new javax.swing.GroupLayout(StackG21);
        StackG21.setLayout(StackG21Layout);
        StackG21Layout.setHorizontalGroup(
            StackG21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        StackG21Layout.setVerticalGroup(
            StackG21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        StackG22.setFocusable(false);

        javax.swing.GroupLayout StackG22Layout = new javax.swing.GroupLayout(StackG22);
        StackG22.setLayout(StackG22Layout);
        StackG22Layout.setHorizontalGroup(
            StackG22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        StackG22Layout.setVerticalGroup(
            StackG22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 352, Short.MAX_VALUE)
        );

        StackG23.setFocusable(false);

        javax.swing.GroupLayout StackG23Layout = new javax.swing.GroupLayout(StackG23);
        StackG23.setLayout(StackG23Layout);
        StackG23Layout.setHorizontalGroup(
            StackG23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 102, Short.MAX_VALUE)
        );
        StackG23Layout.setVerticalGroup(
            StackG23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        StackG24.setFocusable(false);

        javax.swing.GroupLayout StackG24Layout = new javax.swing.GroupLayout(StackG24);
        StackG24.setLayout(StackG24Layout);
        StackG24Layout.setHorizontalGroup(
            StackG24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        StackG24Layout.setVerticalGroup(
            StackG24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        StackG25.setFocusable(false);

        javax.swing.GroupLayout StackG25Layout = new javax.swing.GroupLayout(StackG25);
        StackG25.setLayout(StackG25Layout);
        StackG25Layout.setHorizontalGroup(
            StackG25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 99, Short.MAX_VALUE)
        );
        StackG25Layout.setVerticalGroup(
            StackG25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        StackG26.setFocusable(false);

        javax.swing.GroupLayout StackG26Layout = new javax.swing.GroupLayout(StackG26);
        StackG26.setLayout(StackG26Layout);
        StackG26Layout.setHorizontalGroup(
            StackG26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        StackG26Layout.setVerticalGroup(
            StackG26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        StackG27.setAutoscrolls(true);
        StackG27.setFocusable(false);

        javax.swing.GroupLayout StackG27Layout = new javax.swing.GroupLayout(StackG27);
        StackG27.setLayout(StackG27Layout);
        StackG27Layout.setHorizontalGroup(
            StackG27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 101, Short.MAX_VALUE)
        );
        StackG27Layout.setVerticalGroup(
            StackG27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 345, Short.MAX_VALUE)
        );

        ScoreG2.setText("Score: -1");

        javax.swing.GroupLayout Game2Layout = new javax.swing.GroupLayout(Game2);
        Game2.setLayout(Game2Layout);
        Game2Layout.setHorizontalGroup(
            Game2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Game2Layout.createSequentialGroup()
                .addGroup(Game2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, Game2Layout.createSequentialGroup()
                        .addComponent(SaveGame2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(UndoGame2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(HintGame2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CancelGame2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ScoreG2, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, Game2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(Game2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(SourceDeckG2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(StackG21))
                        .addGap(30, 30, 30)
                        .addGroup(Game2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(StackG22)
                            .addComponent(WasteDeckG2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(StackG23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(Game2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(Game2Layout.createSequentialGroup()
                                .addComponent(StackG24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(StackG25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(StackG26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(StackG27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(Game2Layout.createSequentialGroup()
                                .addComponent(TargetDeckG21, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(TargetDeckG22, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(TargetDeckG23, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(TargetDeckG24, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(1, 1, 1)))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        Game2Layout.setVerticalGroup(
            Game2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Game2Layout.createSequentialGroup()
                .addGroup(Game2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SaveGame2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UndoGame2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HintGame2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CancelGame2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ScoreG2))
                .addGap(14, 14, 14)
                .addGroup(Game2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(WasteDeckG2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SourceDeckG2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TargetDeckG22, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TargetDeckG23, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TargetDeckG21, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TargetDeckG24, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Game2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Game2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(StackG22)
                        .addComponent(StackG23)
                        .addComponent(StackG24)
                        .addComponent(StackG25)
                        .addComponent(StackG21)
                        .addComponent(StackG26))
                    .addGroup(Game2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(StackG27)))
                .addContainerGap(275, Short.MAX_VALUE))
        );

        Game3.setBackground(new java.awt.Color(0, 153, 51));

        SaveGame3.setText("Save Game");
        SaveGame3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SaveGame3MouseClicked(evt);
            }
        });

        UndoGame3.setText("Undo");
        UndoGame3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                UndoGame3MouseClicked(evt);
            }
        });

        HintGame3.setText("Hint");
        HintGame3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                HintGame3MouseClicked(evt);
            }
        });

        CancelGame3.setText("Cancel");
        CancelGame3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CancelGame3MouseClicked(evt);
            }
        });

        SourceDeckG3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        WasteDeckG3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        TargetDeckG31.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        TargetDeckG32.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        TargetDeckG33.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        TargetDeckG34.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        javax.swing.GroupLayout StackG31Layout = new javax.swing.GroupLayout(StackG31);
        StackG31.setLayout(StackG31Layout);
        StackG31Layout.setHorizontalGroup(
            StackG31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        StackG31Layout.setVerticalGroup(
            StackG31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 349, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout StackG32Layout = new javax.swing.GroupLayout(StackG32);
        StackG32.setLayout(StackG32Layout);
        StackG32Layout.setHorizontalGroup(
            StackG32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 98, Short.MAX_VALUE)
        );
        StackG32Layout.setVerticalGroup(
            StackG32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 348, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout StackG33Layout = new javax.swing.GroupLayout(StackG33);
        StackG33.setLayout(StackG33Layout);
        StackG33Layout.setHorizontalGroup(
            StackG33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 94, Short.MAX_VALUE)
        );
        StackG33Layout.setVerticalGroup(
            StackG33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout StackG34Layout = new javax.swing.GroupLayout(StackG34);
        StackG34.setLayout(StackG34Layout);
        StackG34Layout.setHorizontalGroup(
            StackG34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 104, Short.MAX_VALUE)
        );
        StackG34Layout.setVerticalGroup(
            StackG34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout StackG35Layout = new javax.swing.GroupLayout(StackG35);
        StackG35.setLayout(StackG35Layout);
        StackG35Layout.setHorizontalGroup(
            StackG35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 99, Short.MAX_VALUE)
        );
        StackG35Layout.setVerticalGroup(
            StackG35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout StackG36Layout = new javax.swing.GroupLayout(StackG36);
        StackG36.setLayout(StackG36Layout);
        StackG36Layout.setHorizontalGroup(
            StackG36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 104, Short.MAX_VALUE)
        );
        StackG36Layout.setVerticalGroup(
            StackG36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        StackG37.setAutoscrolls(true);

        javax.swing.GroupLayout StackG37Layout = new javax.swing.GroupLayout(StackG37);
        StackG37.setLayout(StackG37Layout);
        StackG37Layout.setHorizontalGroup(
            StackG37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        StackG37Layout.setVerticalGroup(
            StackG37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        ScoreG3.setText("Score: -1");

        javax.swing.GroupLayout Game3Layout = new javax.swing.GroupLayout(Game3);
        Game3.setLayout(Game3Layout);
        Game3Layout.setHorizontalGroup(
            Game3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Game3Layout.createSequentialGroup()
                .addGroup(Game3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Game3Layout.createSequentialGroup()
                        .addComponent(SaveGame3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(UndoGame3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(HintGame3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CancelGame3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(184, 184, 184)
                        .addComponent(ScoreG3, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Game3Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(Game3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(SourceDeckG3, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                            .addComponent(StackG31))
                        .addGap(30, 30, 30)
                        .addGroup(Game3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(Game3Layout.createSequentialGroup()
                                .addComponent(StackG32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(StackG33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(StackG34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(StackG35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(StackG36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(StackG37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(Game3Layout.createSequentialGroup()
                                .addComponent(WasteDeckG3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(TargetDeckG31, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(TargetDeckG32, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(TargetDeckG33, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(TargetDeckG34, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        Game3Layout.setVerticalGroup(
            Game3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Game3Layout.createSequentialGroup()
                .addGroup(Game3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SaveGame3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UndoGame3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HintGame3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CancelGame3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ScoreG3))
                .addGap(11, 11, 11)
                .addGroup(Game3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TargetDeckG32, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TargetDeckG33, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TargetDeckG34, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TargetDeckG31, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(Game3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(WasteDeckG3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(SourceDeckG3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addGroup(Game3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Game3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(StackG32)
                        .addComponent(StackG35)
                        .addComponent(StackG36)
                        .addComponent(StackG37)
                        .addComponent(StackG33)
                        .addComponent(StackG34))
                    .addComponent(StackG31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(266, Short.MAX_VALUE))
        );

        Game3.getAccessibleContext().setAccessibleName("");

        Game4.setBackground(new java.awt.Color(0, 153, 51));

        SaveGame4.setText("Save Game");
        SaveGame4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SaveGame4MouseClicked(evt);
            }
        });

        UndoGame4.setText("Undo");
        UndoGame4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                UndoGame4MouseClicked(evt);
            }
        });

        HintGame4.setText("Hint");
        HintGame4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                HintGame4MouseClicked(evt);
            }
        });

        CancelGame4.setText("Cancel");
        CancelGame4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CancelGame4MouseClicked(evt);
            }
        });

        SourceDeckG4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        SourceDeckG4.setFocusable(false);

        WasteDeckG4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        WasteDeckG4.setFocusable(false);

        TargetDeckG41.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        TargetDeckG41.setFocusable(false);

        TargetDeckG42.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        TargetDeckG42.setFocusable(false);

        TargetDeckG43.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        TargetDeckG43.setFocusable(false);

        TargetDeckG44.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        TargetDeckG44.setFocusable(false);

        StackG41.setFocusable(false);

        javax.swing.GroupLayout StackG41Layout = new javax.swing.GroupLayout(StackG41);
        StackG41.setLayout(StackG41Layout);
        StackG41Layout.setHorizontalGroup(
            StackG41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        StackG41Layout.setVerticalGroup(
            StackG41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        StackG42.setFocusable(false);

        javax.swing.GroupLayout StackG42Layout = new javax.swing.GroupLayout(StackG42);
        StackG42.setLayout(StackG42Layout);
        StackG42Layout.setHorizontalGroup(
            StackG42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        StackG42Layout.setVerticalGroup(
            StackG42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        StackG43.setFocusable(false);

        javax.swing.GroupLayout StackG43Layout = new javax.swing.GroupLayout(StackG43);
        StackG43.setLayout(StackG43Layout);
        StackG43Layout.setHorizontalGroup(
            StackG43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 104, Short.MAX_VALUE)
        );
        StackG43Layout.setVerticalGroup(
            StackG43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 356, Short.MAX_VALUE)
        );

        StackG44.setFocusable(false);

        javax.swing.GroupLayout StackG44Layout = new javax.swing.GroupLayout(StackG44);
        StackG44.setLayout(StackG44Layout);
        StackG44Layout.setHorizontalGroup(
            StackG44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 103, Short.MAX_VALUE)
        );
        StackG44Layout.setVerticalGroup(
            StackG44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 360, Short.MAX_VALUE)
        );

        StackG45.setFocusable(false);

        javax.swing.GroupLayout StackG45Layout = new javax.swing.GroupLayout(StackG45);
        StackG45.setLayout(StackG45Layout);
        StackG45Layout.setHorizontalGroup(
            StackG45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 103, Short.MAX_VALUE)
        );
        StackG45Layout.setVerticalGroup(
            StackG45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 356, Short.MAX_VALUE)
        );

        StackG46.setFocusable(false);

        javax.swing.GroupLayout StackG46Layout = new javax.swing.GroupLayout(StackG46);
        StackG46.setLayout(StackG46Layout);
        StackG46Layout.setHorizontalGroup(
            StackG46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 102, Short.MAX_VALUE)
        );
        StackG46Layout.setVerticalGroup(
            StackG46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 356, Short.MAX_VALUE)
        );

        StackG47.setAutoscrolls(true);
        StackG47.setFocusable(false);

        javax.swing.GroupLayout StackG47Layout = new javax.swing.GroupLayout(StackG47);
        StackG47.setLayout(StackG47Layout);
        StackG47Layout.setHorizontalGroup(
            StackG47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        StackG47Layout.setVerticalGroup(
            StackG47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 356, Short.MAX_VALUE)
        );

        ScoreG4.setText("Score: -1");

        javax.swing.GroupLayout Game4Layout = new javax.swing.GroupLayout(Game4);
        Game4.setLayout(Game4Layout);
        Game4Layout.setHorizontalGroup(
            Game4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Game4Layout.createSequentialGroup()
                .addGroup(Game4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(Game4Layout.createSequentialGroup()
                        .addComponent(SaveGame4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(UndoGame4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(HintGame4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CancelGame4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ScoreG4, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Game4Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(Game4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(SourceDeckG4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(StackG41))
                        .addGap(30, 30, 30)
                        .addGroup(Game4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(WasteDeckG4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(StackG42))
                        .addGap(24, 24, 24)
                        .addGroup(Game4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(Game4Layout.createSequentialGroup()
                                .addComponent(StackG43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(StackG44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(StackG45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(StackG46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(StackG47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(Game4Layout.createSequentialGroup()
                                .addComponent(TargetDeckG41, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(TargetDeckG42, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(TargetDeckG43, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(TargetDeckG44, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(97, Short.MAX_VALUE))
        );
        Game4Layout.setVerticalGroup(
            Game4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Game4Layout.createSequentialGroup()
                .addGroup(Game4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SaveGame4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UndoGame4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HintGame4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CancelGame4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ScoreG4))
                .addGroup(Game4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Game4Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(Game4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(WasteDeckG4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SourceDeckG4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Game4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(Game4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TargetDeckG44, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TargetDeckG41, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TargetDeckG42, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TargetDeckG43, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(Game4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Game4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Game4Layout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(StackG43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(Game4Layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(Game4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(Game4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(StackG41)
                                    .addComponent(StackG42))
                                .addComponent(StackG44, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Game4Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(Game4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(StackG45, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(StackG46, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(StackG47, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(200, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(51, 153, 0));
        setSize(new java.awt.Dimension(1000, 1000));

        NewGame.setText("New Game");
        NewGame.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        NewGame.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NewGameMouseClicked(evt);
            }
        });

        LoadGame.setText("Load Game");
        LoadGame.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        LoadGame.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LoadGameMouseClicked(evt);
            }
        });

        MainBoard.setBackground(new java.awt.Color(0, 153, 51));
        MainBoard.setLayout(new java.awt.GridLayout(0, 1, 20, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(NewGame, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LoadGame, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(828, Short.MAX_VALUE))
            .addComponent(MainBoard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NewGame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LoadGame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MainBoard, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    /**
     * Create new graphic game on graphic gameboard
     * @param i id of game to be created
     */
    private void createGame(int i){
        
        if(i == 0){
            this.createGame1();
        }
        if(i == 1){
            this.createGame2();
            
        }
        if(i == 2){
            this.createGame3();
        }
        if(i == 3){
            this.createGame4();
        }
    }
    
   /**
   * Create game 1
   */
   private void createGame1(){
        this.MainBoard.add(this.Game1);
        
   }
   /**
   * Create game 2
   */
   private void createGame2(){
        this.MainBoard.add(this.Game2);
   }
   
   /**
   * Create game 3
   */
   private void createGame3(){
        this.MainBoard.add(this.Game3);
   }
   
   /**
   * Create game 4
   */
   private void createGame4(){
        this.MainBoard.add(this.Game4);
       
   }
   
   
   /**
    * Create new game when New Game button is clicked according the actual number of games
    * @param evt mouse event
    */ 
    private void NewGameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NewGameMouseClicked
        
        Game playedGame;
        GUIGame actualGame;
        switch(this.actualNumberOfGames){
            
            
            case 0:
              
                this.createGame1();
                this.gameBoard.createGame();
                playedGame = this.gameBoard.getGame(0);
                actualGame = new GUIGame(this.gameBoard,playedGame,this,0);
                int scoreValue = playedGame.getScore().getScore();
                GUIGameBoard.this.ScoreG1.setText("Score: " + scoreValue);
                GUIGameBoard.this.ScoreG1.repaint();
                GUIGameBoard.this.ScoreG1.revalidate();
                actualGame.display();
                repaint();
                revalidate();
                this.gamePanel[0] = 0;
                actualNumberOfGames++;
                break;
                
            case 1:
               
             
                this.setExtendedState(Frame.MAXIMIZED_BOTH);
                GridLayout layout = (GridLayout)this.MainBoard.getLayout();
                layout.setColumns(2);
                layout.setRows(2);
                this.gameBoard.createGame();
                
                for(int i = 0; i < 4; i++){
                   if(this.findValue(this.gamePanel,i)){
                   
                       continue;
                   }
                   else{
                       this.createGame(i);
                       this.gamePanel[1] = i;
                       break;
                   }
                   
                } 
              
                for(int j = 0; j < 2; j++){
                    playedGame = this.gameBoard.getGame(j);
                    actualGame = new GUIGame(this.gameBoard,playedGame,this,this.gamePanel[j]);
                    actualGame.display();
                    scoreValue = playedGame.getScore().getScore();
                    GUIGameBoard.this.getScoreButton(this.gamePanel[j]).setText("Score: " + scoreValue);
                    GUIGameBoard.this.getScoreButton(this.gamePanel[j]).repaint();
                    GUIGameBoard.this.getScoreButton(this.gamePanel[j]).revalidate();
                }
                            
                
                repaint();
                revalidate();
                
                actualNumberOfGames++;
                break;
                
            case 2:
           
              this.gameBoard.createGame();
              for(int i = 0; i < 4; i++){
                   if(this.findValue(this.gamePanel,i)){
                    
                       continue;
                   }
                   else{
                       this.createGame(i);
                       this.gamePanel[2] = i;
                      
                       break;
                   }
                   
               } 
              
               for(int j = 0; j < 3; j++){
                    playedGame = this.gameBoard.getGame(j);
                    actualGame = new GUIGame(this.gameBoard,playedGame,this,this.gamePanel[j]);
                    actualGame.display();
                    scoreValue = playedGame.getScore().getScore();
                    GUIGameBoard.this.getScoreButton(this.gamePanel[j]).setText("Score: " + scoreValue);
                    GUIGameBoard.this.getScoreButton(this.gamePanel[j]).repaint();
                    GUIGameBoard.this.getScoreButton(this.gamePanel[j]).revalidate();
                    repaint();
                    revalidate();
               }
              
                repaint();
                revalidate();
           
                actualNumberOfGames++;
                break;
                
            case 3:
             
               this.gameBoard.createGame();
               for(int i = 0; i < 4; i++){
                   if(this.findValue(this.gamePanel,i)){
                       
                   }
                   else{
                       this.createGame(i);
                       this.gamePanel[3] = i;
                       break;    
                   }
                   
                }  
                
               for(int j = 0; j < 4; j++){
                    playedGame = this.gameBoard.getGame(j);
                    actualGame = new GUIGame(this.gameBoard,playedGame,this,this.gamePanel[j]);
                    scoreValue = playedGame.getScore().getScore();
                    GUIGameBoard.this.getScoreButton(this.gamePanel[j]).setText("Score: " + scoreValue);
                    GUIGameBoard.this.getScoreButton(this.gamePanel[j]).repaint();
                    GUIGameBoard.this.getScoreButton(this.gamePanel[j]).revalidate();
                    actualGame.display();
                 
               }
              
             
                repaint();
                revalidate();
         
                actualNumberOfGames++;
                break;
            default:
                JOptionPane.showMessageDialog(this,"Maximum number of games reached.","Maximum Games",JOptionPane.INFORMATION_MESSAGE);
                return;
        }

    }//GEN-LAST:event_NewGameMouseClicked


    /**
     * Remove game 1 from graphic gameboard
     * @param evt mouse event
     */
    private void CanceGame1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CanceGame1MouseClicked
      
        
        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,0);
        this.cancelGamePanel(index);
        this.gameBoard.cancelGame(index);
        actualNumberOfGames--;
        if(actualNumberOfGames == 0){
         
        }
       //remove all games
       this.MainBoard.removeAll();
       Game playedGame;
       GUIGame actualGame;
       
       
       //add games back to board and display their state
       for(int j = 0 ;j < actualNumberOfGames; j++){
           this.createGame(this.gamePanel[j]);
            playedGame = this.gameBoard.getGame(j);
                    actualGame = new GUIGame(this.gameBoard,playedGame,this,this.gamePanel[j]);
                    actualGame.display();
       }
       
       repaint();
       revalidate();
       this.MainBoard.validate();
       this.MainBoard.repaint();
      
	
    }//GEN-LAST:event_CanceGame1MouseClicked

    /**
     * Remove game 3 from graphic gameboard
     * @param evt mouse event
     */                                    
    private void CancelGame3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CancelGame3MouseClicked
      
        
        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,2);
        this.gameBoard.cancelGame(index);
        this.cancelGamePanel(index);
        actualNumberOfGames--;
        if(actualNumberOfGames == 0){
        
        }
        
       //remove all games
       this.MainBoard.removeAll();
       Game playedGame;
       GUIGame actualGame;
       
       //add games back to graphic gameboard and display their state
       for(int j = 0 ;j < actualNumberOfGames; j++){
           this.createGame(this.gamePanel[j]);
            playedGame = this.gameBoard.getGame(j);
            actualGame = new GUIGame(this.gameBoard,playedGame,this,this.gamePanel[j]);
            actualGame.display();
       }
       
       repaint();
       revalidate();
       this.MainBoard.validate();
       this.MainBoard.repaint();
       
    }//GEN-LAST:event_CancelGame3MouseClicked

     /**
     * Remove game 4 from graphic gameboard
     * @param evt mouse event
     */   
    private void CancelGame4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CancelGame4MouseClicked
        
       
         int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,3);
         this.MainBoard.remove(index);
         this.cancelGamePanel(index);
         this.gameBoard.cancelGame(index);
         actualNumberOfGames--;
         if(actualNumberOfGames == 0){
           
         }
       
         //remove all games
         this.MainBoard.removeAll();
         Game playedGame;
         GUIGame actualGame;
       
         //add games back to graphic gameboard and display their state
         for(int j = 0 ;j < actualNumberOfGames; j++){
           this.createGame(this.gamePanel[j]);
            playedGame = this.gameBoard.getGame(j);
                    actualGame = new GUIGame(this.gameBoard,playedGame,this,this.gamePanel[j]);
                    actualGame.display();
         }
       
       repaint();
       revalidate();
  
       this.MainBoard.validate();         
       this.MainBoard.repaint();
       
        
    }//GEN-LAST:event_CancelGame4MouseClicked

    /**
     * Show hint for game 3 
     * @param evt mouse event
     */
    private void HintGame3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HintGame3MouseClicked
        
       
        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,2);
        Game actualGame = this.gameBoard.getGame(index);
        GetPossibleMoves moves = new GetPossibleMoves(actualGame.getDeck(),actualGame.getWaste(), actualGame.getTargetPacks(),actualGame.getWorkingPacks());
        String possibleMoves = moves.getMoves();
        JOptionPane.showMessageDialog(this,possibleMoves,"Hint",JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_HintGame3MouseClicked

    /**
     * Show hint for game 1
     * @param evt mouse event
     */
    private void HintGame1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HintGame1MouseClicked
       
        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,0);
        Game actualGame = this.gameBoard.getGame(index);
        GetPossibleMoves moves = new GetPossibleMoves(actualGame.getDeck(),actualGame.getWaste(), actualGame.getTargetPacks(),actualGame.getWorkingPacks());
        String possibleMoves = moves.getMoves();
        JOptionPane.showMessageDialog(this,possibleMoves,"Hint",JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_HintGame1MouseClicked

    private void SourceDeckG1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SourceDeckG1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_SourceDeckG1MouseClicked

    private void WasteDeckG1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_WasteDeckG1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_WasteDeckG1MouseClicked

    /**
     * Load game from chosen file and add it to graphic gameboard
     * @param evt mouse event
     */
    private void LoadGameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LoadGameMouseClicked
       
        
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("./"));
        Game playedGame;
        GUIGame actualGame;
        int result = chooser.showOpenDialog(new JFrame());
        
        //choose file to be loaded
        if(result == JFileChooser.APPROVE_OPTION){
            File loadGame = chooser.getSelectedFile();
           
           
                
           //add loaded game to graphic gameboard according to actual number of games
           switch(this.actualNumberOfGames){
            
                case 0:

                   
                    this.createGame1();
                    if(this.gameBoard.createGame(loadGame)){
                        playedGame = this.gameBoard.getGame(0);
                        actualGame = new GUIGame(this.gameBoard,playedGame,this,0);
                        actualGame.display();
                        
                        int scoreValue = playedGame.getScore().getScore();
                        GUIGameBoard.this.ScoreG1.setText("Score: " + scoreValue);
                        GUIGameBoard.this.ScoreG1.repaint();
                        GUIGameBoard.this.ScoreG1.revalidate();
                        
                    }
                    else{
                        JOptionPane.showMessageDialog(this,"Cannot open chosen file.","ERROR",JOptionPane.INFORMATION_MESSAGE);
                        
                    }
                    
                    

                    repaint();
                    revalidate();
                    this.gamePanel[0] = 0;
                    actualNumberOfGames++;
                    break;

                case 1:

                    this.setExtendedState(Frame.MAXIMIZED_BOTH);
                    GridLayout layout = (GridLayout)this.MainBoard.getLayout();
                    layout.setColumns(2);
                    layout.setRows(2);

                    if(this.gameBoard.createGame(loadGame)){

                        for(int i = 0; i < 4; i++){
                            if(this.findValue(this.gamePanel,i)){

                                continue;
                            }
                            else{
                                this.createGame(i);
                                this.gamePanel[1] = i;
                                break;
                            }

                        } 

                        for(int j = 0; j < 2; j++){
                           
                           playedGame = this.gameBoard.getGame(j);
                           actualGame = new GUIGame(this.gameBoard,playedGame,this,this.gamePanel[j]);
                           actualGame.display();
                           int scoreValue = playedGame.getScore().getScore();
                           GUIGameBoard.this.getScoreButton(this.gamePanel[j]).setText("Score: " + scoreValue);
                           GUIGameBoard.this.getScoreButton(this.gamePanel[j]).repaint();
                           GUIGameBoard.this.getScoreButton(this.gamePanel[j]).revalidate();
                           
                        }
                    }


                    repaint();
                    revalidate();
                    actualNumberOfGames++;
                    break;

                case 2:


                    if(this.gameBoard.createGame(loadGame)){
                           
                        for(int i = 0; i < 4; i++){
                                if(this.findValue(this.gamePanel,i)){
                                    continue;
                                }
                                else{
                                    this.createGame(i);
                                    this.gamePanel[2] = i;
                                    break;
                                }

                        } 

                          
                        for(int j = 0; j < 3; j++){
                              
                                 playedGame = this.gameBoard.getGame(j);
                                 actualGame = new GUIGame(this.gameBoard,playedGame,this,this.gamePanel[j]);
                                 actualGame.display();
                                 int scoreValue = playedGame.getScore().getScore();
                                 GUIGameBoard.this.getScoreButton(this.gamePanel[j]).setText("Score: " + scoreValue);
                                 GUIGameBoard.this.getScoreButton(this.gamePanel[j]).repaint();
                                 GUIGameBoard.this.getScoreButton(this.gamePanel[j]).revalidate();
                                 repaint();
                                 revalidate();
                            
                        }
                    }
                    repaint();
                    revalidate();

                    actualNumberOfGames++;
                    break;

                case 3:


                    if(this.gameBoard.createGame(loadGame)){
                           
                        for(int i = 0; i < 4; i++){
                              if(this.findValue(this.gamePanel,i)){

                                }
                                else{
                                 
                                    this.createGame(i);
                                    this.gamePanel[3] = i;
                                    break;

                                }

                         } 
                            
                        for(int j = 0; j < 4; j++){
                               
                                 playedGame = this.gameBoard.getGame(j);
                                 actualGame = new GUIGame(this.gameBoard,playedGame,this,this.gamePanel[j]);
                                 actualGame.display();
                                 int scoreValue = playedGame.getScore().getScore();
                                 GUIGameBoard.this.getScoreButton(this.gamePanel[j]).setText("Score: " + scoreValue);
                                 GUIGameBoard.this.getScoreButton(this.gamePanel[j]).repaint();
                                 GUIGameBoard.this.getScoreButton(this.gamePanel[j]).revalidate();

                          }

                    }
                    repaint();
                    revalidate();             
                    actualNumberOfGames++;
                    break;
                
                default:
                    JOptionPane.showMessageDialog(this,"Maximum number of games reached.","Maximum Games",JOptionPane.INFORMATION_MESSAGE);
                    return;
           }
        
        }  
    }//GEN-LAST:event_LoadGameMouseClicked
    
    /**
     * Undo event for game 1, display previous state of game
     * @param evt mouse event
     */
    private void UndoGame1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UndoGame1MouseClicked
        
        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,0);
        Game playedGame = this.gameBoard.getGame(index);
        playedGame.undo();
         
        //display game state
        GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,0);
        actualGame.display();
         
        //display score
        int scoreValue = playedGame.getScore().getScore();
        GUIGameBoard.this.ScoreG1.setText("Score: " + scoreValue);
        GUIGameBoard.this.ScoreG1.repaint();
        GUIGameBoard.this.ScoreG1.revalidate();
         
         
        repaint();
        revalidate();
    }//GEN-LAST:event_UndoGame1MouseClicked
    
    /**
     * Save state of game 1 to file
     * @param evt mouse event
     */
    private void SaveGame1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SaveGame1MouseClicked
        
        JFileChooser chooser=new JFileChooser();
        chooser.setCurrentDirectory(new File("./"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.showSaveDialog(null);
       
        //filepath where file is stored
        String path=chooser.getSelectedFile().getAbsolutePath();
        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,0);
        Game playedGame = this.gameBoard.getGame(index);
        playedGame.saveGame(path);
        
        
        
    }//GEN-LAST:event_SaveGame1MouseClicked

    /**
     * Undo event for game 2, display previous state of game
     * @param evt mouse event
     */
    private void UndoGame2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UndoGame2MouseClicked
        
        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,1);
        Game playedGame = this.gameBoard.getGame(index);
        playedGame.undo();
        
        //display game state
        GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,1);
        actualGame.display();
        
        //display score
        int scoreValue = playedGame.getScore().getScore();
        GUIGameBoard.this.ScoreG2.setText("Score: " + scoreValue);
        GUIGameBoard.this.ScoreG2.repaint();
        GUIGameBoard.this.ScoreG2.revalidate();
         
        repaint();
        revalidate();
    }//GEN-LAST:event_UndoGame2MouseClicked

    /**
     * Undo event for game 3, display previous state of game
     * @param evt mouse event
     */
    private void UndoGame3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UndoGame3MouseClicked
        
        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,2);
        Game playedGame = this.gameBoard.getGame(index);
        playedGame.undo();
        
        //display game state
        GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,2);
        actualGame.display();
        
        //display score
        int scoreValue = playedGame.getScore().getScore();
        GUIGameBoard.this.ScoreG3.setText("Score: " + scoreValue);
        GUIGameBoard.this.ScoreG3.repaint();
        GUIGameBoard.this.ScoreG3.revalidate();
         
        repaint();
        revalidate();
    }//GEN-LAST:event_UndoGame3MouseClicked

    /**
     * Undo event for game 4, display previous state of game
     * @param evt mouse event
     */
    private void UndoGame4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UndoGame4MouseClicked
        
        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,3);
        Game playedGame = this.gameBoard.getGame(index);
        playedGame.undo();
        
        //display game state
        GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,3);
        actualGame.display();
        
        //display score
        int scoreValue = playedGame.getScore().getScore();
        GUIGameBoard.this.ScoreG4.setText("Score: " + scoreValue);
        GUIGameBoard.this.ScoreG4.repaint();
        GUIGameBoard.this.ScoreG4.revalidate();
         
        repaint();
        revalidate();
    }//GEN-LAST:event_UndoGame4MouseClicked

    /**
     * Remove game 2 from graphic game board
     * @param evt mouse event
     */
    private void CancelGame2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CancelGame2MouseClicked
      
       int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,1);
       this.cancelGamePanel(index);
       this.gameBoard.cancelGame(index);
       actualNumberOfGames--;
       if(actualNumberOfGames == 0){
         
       }
      //remove all games
       this.MainBoard.removeAll();
       Game playedGame;
       GUIGame actualGame;
      
       //add games back and display their state
       for(int j = 0 ;j < actualNumberOfGames; j++){
            this.createGame(this.gamePanel[j]);
            playedGame = this.gameBoard.getGame(j);
            actualGame = new GUIGame(this.gameBoard,playedGame,this,this.gamePanel[j]);
            actualGame.display();
       }
       
       repaint();
       revalidate();
       
       
       this.MainBoard.validate();
       this.MainBoard.repaint();
      
    }//GEN-LAST:event_CancelGame2MouseClicked

    /**
     * Save state of game 2 to file
     * @param evt mouse event
     */
    private void SaveGame2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SaveGame2MouseClicked
       
        JFileChooser chooser=new JFileChooser();
        chooser.setCurrentDirectory(new File("./"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.showSaveDialog(null);
  
        //place where file is stored
        String path=chooser.getSelectedFile().getAbsolutePath();
        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,1);
        Game playedGame = this.gameBoard.getGame(index);
        playedGame.saveGame(path);
    }//GEN-LAST:event_SaveGame2MouseClicked

    /**
     * Save state of game 3 to file
     * @param evt mouse event
     */
    private void SaveGame3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SaveGame3MouseClicked
        
        JFileChooser chooser=new JFileChooser();
        chooser.setCurrentDirectory(new File("./"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.showSaveDialog(null);
       
        
        String path=chooser.getSelectedFile().getAbsolutePath();
        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,2);
        Game playedGame = this.gameBoard.getGame(index);
        playedGame.saveGame(path);
    }//GEN-LAST:event_SaveGame3MouseClicked

    /**
     * Save state of game 4 to file
     * @param evt mouse event
     */
    private void SaveGame4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SaveGame4MouseClicked
        
        JFileChooser chooser=new JFileChooser();
        chooser.setCurrentDirectory(new File("./"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.showSaveDialog(null);
       
        
        String path=chooser.getSelectedFile().getAbsolutePath();
        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,3);
        Game playedGame = this.gameBoard.getGame(index);
        playedGame.saveGame(path);
    }//GEN-LAST:event_SaveGame4MouseClicked

    /**
     * Show hint for game 2
     * @param evt mouse event
     */
    private void HintGame2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HintGame2MouseClicked
        
        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,1);
        Game actualGame = this.gameBoard.getGame(index);
        GetPossibleMoves moves = new GetPossibleMoves(actualGame.getDeck(),actualGame.getWaste(), actualGame.getTargetPacks(),actualGame.getWorkingPacks());
        String possibleMoves = moves.getMoves();
        JOptionPane.showMessageDialog(this,possibleMoves,"Hint",JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_HintGame2MouseClicked
    
    /**
     * Show hint for game 4
     * @param evt mouse event
     */
    private void HintGame4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HintGame4MouseClicked
      
        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,3);
        Game actualGame = this.gameBoard.getGame(index);
        GetPossibleMoves moves = new GetPossibleMoves(actualGame.getDeck(),actualGame.getWaste(), actualGame.getTargetPacks(),actualGame.getWorkingPacks());
        String possibleMoves = moves.getMoves();
        JOptionPane.showMessageDialog(this,possibleMoves,"Hint",JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_HintGame4MouseClicked

        
    
    

    /**
     * Mouse listener controling decks in each game
     */
    MouseListener ml = new MouseListener() {
         
        @Override
        public void mouseReleased(MouseEvent e) {
            
            JLabel senderName = (JLabel) e.getSource();
            Pack source = null;
            Pack destination = null;
            
            /**
             * Actions performed in game 1
             */
             if(GUIGameBoard.this.actualGame == "Game1"){
                
                /**
                 * Move card from stack to deck
                 */
                if(GUIGameBoard.this.clickedStackCard != null){
                 
                    
                    switch(GUIGameBoard.this.parentStack){
                    
                        case "StackG11":
                            
                            source = Pack.WORKING1;
                             break;
                        case "StackG12":
                            
                            source = Pack.WORKING2;
                             break;
                        case "StackG13":
                            
                            source = Pack.WORKING3;
                             break;
                       case "StackG14":
                           
                            source = Pack.WORKING4;
                             break;
                        case "StackG15":
                            
                            source = Pack.WORKING5;
                             break;
                        case "StackG16":
                            
                            source = Pack.WORKING6;
                             break;
                        case "StackG17":
                            
                            source = Pack.WORKING7;
                             break;
                        default:
                           
                             GUIGameBoard.this.clickedStackCard = null;
                             GUIGameBoard.this.parentStack = null;
                             GUIGameBoard.this.clickedDeck = null;
                             return;
                    
                     }
                   
                    
                     switch(senderName.getName()){
                    
                        case "TargetDeck11":
                             
                                destination = Pack.TARGET1;
                                break;
                         case "TargetDeck12":
                            
                                destination = Pack.TARGET2;
                                break;
                         case "TargetDeck13":
                                
                                destination = Pack.TARGET3;
                                break;
                         case "TargetDeck14":
                               
                                destination = Pack.TARGET4;
                                break;
                        
                    
                     }
                  
                     
                     int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,0);
                     Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                     
                     
                     
                     if(senderName.getName() == "SourceDeck1"){
                         
                     }
                     else if(senderName.getName() == "WasteDeck1"){
                         
                     }
                     else{
                         if(playedGame.moveWorkingToTarget(source,destination)){
                             GUIGameBoard.this.winGame(playedGame);
                         }
                         

                     }
                     
                     /**
                      * Display state of game after command
                      */
                     
                     int scoreValue = playedGame.getScore().getScore();
                     GUIGameBoard.this.ScoreG1.setText("Score: " + scoreValue);
                     GUIGameBoard.this.ScoreG1.repaint();
                     GUIGameBoard.this.ScoreG1.revalidate();
                     
                     GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,0);
                     actualGame.display();
                     repaint();
                     revalidate();
                      
               
                     GUIGameBoard.this.clickedStackCard = null;
                     GUIGameBoard.this.parentStack = null;
                     GUIGameBoard.this.clickedDeck = null;
                }
                else{
                    /**
                     * Move card from source deck to waste
                     */
                    
                   if(GUIGameBoard.this.clickedDeck == "SourceDeck1"){
                        
                         source = Pack.DECK;
                         destination = Pack.WASTE;
                         
                        
                         
                         int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,0);
                        
                         Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                         CardDeck waste1 = playedGame.getWaste();
                         CardDeck source1 = playedGame.getDeck();
                         int scoreValue = playedGame.getScore().getScore();
                    
                         if(!source1.isEmpty()){
                            
                            if(playedGame.moveDeckToWaste()){
                                GUIGameBoard.this.winGame(playedGame);
                            }
                         }
                         else{
                        
                             playedGame.reloadDeck();
                             GUIGameBoard.this.winGame(playedGame);
                         }
                        
                         /**
                          * Display game state after command
                          */
                 
                            GUIGameBoard.this.ScoreG1.setText("Score: " + scoreValue);
                            GUIGameBoard.this.ScoreG1.repaint();
                            GUIGameBoard.this.ScoreG1.revalidate();

                            GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,0);
                            actualGame.display();


                             repaint();
                             revalidate();
                             
                             GUIGameBoard.this.clickedDeck = null;     
                   
                   }
                   /**
                    * Move card from waste to target deck
                    */
                   else if(GUIGameBoard.this.clickedDeck != senderName.getName()){
                         switch(GUIGameBoard.this.clickedDeck){
                          
                            case "WasteDeck1":
                                 
                                  source = Pack.WASTE;
                                  break; 
                            default:
                                break;
                    
                        }
                         
                        switch(senderName.getName()){
                    
                            case "SourceDeckG1":
                               return;
                            case "TargetDeck11":
                               
                                 destination = Pack.TARGET1;
                                break;
                            case "TargetDeck12":
                                
                                   destination = Pack.TARGET2;
                                break;
                            case "TargetDeck13":
                               
                                   destination = Pack.TARGET3;
                                break;
                            case "TargetDeck14":
                              
                                   destination = Pack.TARGET4;
                                break;
                            default:
                                break;
                    
                     }
                       
                   
                  
                      int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,0);
                      Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                      CardDeck waste1 = playedGame.getWaste();
                      if(destination != null){
                            
                        if(playedGame.moveWasteToTarget(destination)){
                            
                             GUIGameBoard.this.winGame(playedGame);
                             /**
                             * Display game state after command
                             */
                            GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,0);
                            actualGame.display();
                   
                            
                        }
                        else{
                           
                        }
                      
                     }
                      
                        int scoreValue = playedGame.getScore().getScore();
                      
                        GUIGameBoard.this.ScoreG1.setText("Score: " + scoreValue);
                        GUIGameBoard.this.ScoreG1.repaint();
                        repaint();
                        revalidate();

                        GUIGameBoard.this.clickedDeck = null;     
                        
                        
                    }
                    else{
                        if(e.getClickCount() == 2){
                            GUIGameBoard.this.clickedDeck = null;
                        }
                    }
     
                    
                }
            //end of GAME1   
            }
            
            if(GUIGameBoard.this.actualGame == "Game2"){
                
                /**
                 * Move card from stack to deck
                 */
               if(GUIGameBoard.this.clickedStackCard != null){
             
                   
                    switch(GUIGameBoard.this.parentStack){
                    
                        case "StackG21":
                            
                            source = Pack.WORKING1;
                             break;
                        case "StackG22":
                           
                            source = Pack.WORKING2;
                             break;
                        case "StackG23":
                          
                            source = Pack.WORKING3;
                             break;
                       case "StackG24":
                          
                            source = Pack.WORKING4;
                             break;
                        case "StackG25":
                           
                            source = Pack.WORKING5;
                             break;
                        case "StackG26":
                           
                            source = Pack.WORKING6;
                             break;
                        case "StackG27":
                            
                            source = Pack.WORKING7;
                             break;
                        default:
                           
                             GUIGameBoard.this.clickedStackCard = null;
                             GUIGameBoard.this.parentStack = null;
                             GUIGameBoard.this.clickedDeck = null;
                             return;
                    
                     }
                    
                     switch(senderName.getName()){
                    
                        case "TargetDeck21":
                                
                                destination = Pack.TARGET1;
                                break;
                         case "TargetDeck22":
                                
                                destination = Pack.TARGET2;
                                break;
                         case "TargetDeck23":
                                
                                destination = Pack.TARGET3;
                                break;
                         case "TargetDeck24":
                                
                                destination = Pack.TARGET4;
                                break;
                         default:
                              GUIGameBoard.this.clickedStackCard = null;
                              GUIGameBoard.this.parentStack = null;
                              GUIGameBoard.this.clickedDeck = null;
                              return;
                    
                     }
                  
                     
                       int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,1);
                       Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                     
                       if(senderName.getName() == "SourceDeck2"){

                       }else if(senderName.getName() == "WasteDeck2"){

                       }
                       else{
                           if(playedGame.moveWorkingToTarget(source,destination)){
                               GUIGameBoard.this.winGame(playedGame);
                           }
                       }
                     
                        int scoreValue = playedGame.getScore().getScore();
                        GUIGameBoard.this.ScoreG2.setText("Score: " + scoreValue);
                        GUIGameBoard.this.ScoreG2.repaint();
                        GUIGameBoard.this.ScoreG2.revalidate();
                        
                        
                        //display game
                        GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,1);
                        actualGame.display();
                        repaint();
                        revalidate();
                      
               
                        GUIGameBoard.this.clickedStackCard = null;
                        GUIGameBoard.this.parentStack = null;
                        GUIGameBoard.this.clickedDeck = null;
                
               
                }
                else{
                    
                   /**
                    * Move card from source to waste deck
                    */
                   if(GUIGameBoard.this.clickedDeck == "SourceDeck2"){
                         
                        source = Pack.DECK;
                        destination = Pack.WASTE;
                        
                                               
                        int  index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,1);
                        Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                        CardDeck waste1 = playedGame.getWaste();
                        CardDeck source1 = playedGame.getDeck();
                          
                        
                            
                        if(!source1.isEmpty()){
                              
                                if(playedGame.moveDeckToWaste()){
                                    GUIGameBoard.this.winGame(playedGame);
                                 }
                            }
                            else{
                               playedGame.reloadDeck();
                               GUIGameBoard.this.winGame(playedGame);
                            }
                        
                        int scoreValue = playedGame.getScore().getScore();
                        GUIGameBoard.this.ScoreG2.setText("Score: " + scoreValue);
                        GUIGameBoard.this.ScoreG2.repaint();
                        GUIGameBoard.this.ScoreG2.revalidate();
                        
                        //display game
                        GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,1);
                        actualGame.display();
                        repaint();
                        revalidate();
                        
                        GUIGameBoard.this.clickedDeck = null;  
                       
                       
                       
                       
                       
                   }
                   /**
                    * Move card from waste to deck
                    */
                   else if(GUIGameBoard.this.clickedDeck != senderName.getName()){
                        
                       switch(GUIGameBoard.this.clickedDeck){
                          
                            case "WasteDeck2":
                                
                                  source = Pack.WASTE;
                                  break; 
                            default:
                                break;
                    
                        }
                         
                        switch(senderName.getName()){
                    
                            case "SourceDeckG2":
                               return;
                            case "TargetDeck21":
                              
                                 destination = Pack.TARGET1;
                                break;
                            case "TargetDeck22":
                               
                                   destination = Pack.TARGET2;
                                break;
                            case "TargetDeck23":
                               
                                   destination = Pack.TARGET3;
                                break;
                            case "TargetDeck24":
                              
                                   destination = Pack.TARGET4;
                                break;
                            default:
                                break;
                    
                     }
                       
                     int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,1);
                     Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                     
                     CardDeck waste1 = playedGame.getWaste();
                      
                      if(destination != null){
                            
                            if(playedGame.moveWasteToTarget(destination)){
                                
                                GUIGameBoard.this.winGame(playedGame);
                                //display game
                                GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,1);
                                actualGame.display();
                   
                            
                            }
                            else{
                                
                            }
                      
                       }
                        int scoreValue = playedGame.getScore().getScore();
                        GUIGameBoard.this.ScoreG2.setText("Score: " + scoreValue);
                        GUIGameBoard.this.ScoreG2.repaint();
                        GUIGameBoard.this.ScoreG2.revalidate();
                        repaint();
                        revalidate();
                        GUIGameBoard.this.clickedDeck = null;     
                       
                    }
                    else{
                        if(e.getClickCount() == 2){                  
                            GUIGameBoard.this.clickedDeck = null;
                        }
                    }
     
                    
                }
            //end of GAME2   
            }
            
            if(GUIGameBoard.this.actualGame == "Game3"){
                
                /**
                 * Move card from stack to deck
                 */
               if(GUIGameBoard.this.clickedStackCard != null){
                 
                    switch(GUIGameBoard.this.parentStack){
                    
                        case "StackG31":
                            
                            source = Pack.WORKING1;
                             break;
                        case "StackG32":
                           
                            source = Pack.WORKING2;
                             break;
                        case "StackG33":
                           
                            source = Pack.WORKING3;
                             break;
                       case "StackG34":
                            
                            source = Pack.WORKING4;
                             break;
                        case "StackG35":
                           
                            source = Pack.WORKING5;
                             break;
                        case "StackG36":
                            
                            source = Pack.WORKING6;
                             break;
                        case "StackG37":
                            
                            source = Pack.WORKING7;
                             break;
                        default:
                            
                             GUIGameBoard.this.clickedStackCard = null;
                             GUIGameBoard.this.parentStack = null;
                             GUIGameBoard.this.clickedDeck = null;
                             return;
                    
                     }
                    
                     switch(senderName.getName()){
                    
                        case "TargetDeck31":
                               
                                destination = Pack.TARGET1;
                                break;
                         case "TargetDeck32":
                               
                                destination = Pack.TARGET2;
                                break;
                         case "TargetDeck33":
                               
                                destination = Pack.TARGET3;
                                break;
                         case "TargetDeck34":
                               
                                destination = Pack.TARGET4;
                                break;
                    
                     }
                    
                    int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,2);
                    Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                 
                     
                     if(senderName.getName() == "SourceDeck3"){
                         
                     }
                     else if(senderName.getName() == "WasteDeck3"){
                         
                     }
                     else{
                         if(playedGame.moveWorkingToTarget(source,destination)){
                             GUIGameBoard.this.winGame(playedGame);
                         }
                     }
                     //display game
                     GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,2);
                     actualGame.display();
                     
                     int scoreValue = playedGame.getScore().getScore();
                     GUIGameBoard.this.ScoreG3.setText("Score: " + scoreValue);
                     GUIGameBoard.this.ScoreG3.repaint();
                     GUIGameBoard.this.ScoreG3.revalidate();
                     repaint();
                     revalidate();
                      
               
                     GUIGameBoard.this.clickedStackCard = null;
                     GUIGameBoard.this.parentStack = null;
                     GUIGameBoard.this.clickedDeck = null;
                
               
                }
                else{
                    
                   /**
                    * Move card from source deck to waste
                    */
                   if(GUIGameBoard.this.clickedDeck == "SourceDeck3"){
                       
                         source = Pack.DECK;
                         destination = Pack.WASTE;
                         
                         int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,2);
                         Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                        
                         CardDeck waste1 = playedGame.getWaste();
                         CardDeck source1 = playedGame.getDeck();
                         
                            if(!source1.isEmpty()){
                                
                                if(playedGame.moveDeckToWaste()){
                                   GUIGameBoard.this.winGame(playedGame);
                                }
                            }
                            else{
                                 playedGame.reloadDeck();
                                 GUIGameBoard.this.winGame(playedGame);
                            }
                        
                           //display game
                            GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,2);
                            actualGame.display();
                            
                            int scoreValue = playedGame.getScore().getScore();
                            GUIGameBoard.this.ScoreG3.setText("Score: " + scoreValue);
                            GUIGameBoard.this.ScoreG3.repaint();
                            GUIGameBoard.this.ScoreG3.revalidate();
                            
                            
                            repaint();
                            revalidate();
                           
                            GUIGameBoard.this.clickedDeck = null;  
     
                   }
                   /**
                    * Move card from waste to target deck
                    */
                   else if(GUIGameBoard.this.clickedDeck != senderName.getName()){
                         
                       
                            switch(GUIGameBoard.this.clickedDeck){
                          
                                case "WasteDeck3":
                                    
                                    source = Pack.WASTE;
                                    break; 
                                default:
                                    break;
                    
                            }
                         
                            switch(senderName.getName()){
                    
                                case "SourceDeckG3":
                                    return;
                                case "TargetDeck31":
                                   
                                    destination = Pack.TARGET1;
                                    break;
                                case "TargetDeck32":
                                    
                                    destination = Pack.TARGET2;
                                    break;
                                case "TargetDeck33":
                                   
                                    destination = Pack.TARGET3;
                                    break;
                                case "TargetDeck34":
                                   
                                    destination = Pack.TARGET4;
                                    break;
                                default:
                                    break;
                            }
                       
                             int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,2);
                             Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                           
                             CardDeck waste1 = playedGame.getWaste();
                             if(destination != null){
                            
                                if(playedGame.moveWasteToTarget(destination)){
                                    
                                    GUIGameBoard.this.winGame(playedGame);
                                    //display game
                                    GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,2);
                                    actualGame.display();
                                }
                            
                                else{
                                    
                                }
                      
                            }
                            
                            int scoreValue = playedGame.getScore().getScore();
                            GUIGameBoard.this.ScoreG3.setText("Score: " + scoreValue);
                            GUIGameBoard.this.ScoreG3.repaint();
                            GUIGameBoard.this.ScoreG3.revalidate();
          
                            repaint();
                            revalidate();
                            GUIGameBoard.this.clickedDeck = null;     
                           
                    }
                    else{
                        if(e.getClickCount() == 2){
                       
                             GUIGameBoard.this.clickedDeck = null;
                        }
  
                    }

                }
            //end of GAME3   
            }
            
            if(GUIGameBoard.this.actualGame == "Game4"){
                
                /**
                 * Move card from stack to deck
                 */
               if(GUIGameBoard.this.clickedStackCard != null){
                 
                    switch(GUIGameBoard.this.parentStack){
                    
                        case "StackG41":
                            
                            source = Pack.WORKING1;
                             break;
                        case "StackG42":
                            
                            source = Pack.WORKING2;
                             break;
                        case "StackG43":
                           
                            source = Pack.WORKING3;
                             break;
                       case "StackG44":
                            
                            source = Pack.WORKING4;
                             break;
                        case "StackG45":
                            
                            source = Pack.WORKING5;
                             break;
                        case "StackG46":
                            
                            source = Pack.WORKING6;
                             break;
                        case "StackG47":
                            
                            source = Pack.WORKING7;
                             break;
                        default:
                             GUIGameBoard.this.clickedStackCard = null;
                             GUIGameBoard.this.parentStack = null;
                             GUIGameBoard.this.clickedDeck = null;
                             return;   
                          
                    
                     }
                    
                     switch(senderName.getName()){
                    
                        case "TargetDeck41":
                                
                                destination = Pack.TARGET1;
                                break;
                         case "TargetDeck42":
                               
                                destination = Pack.TARGET2;
                                break;
                         case "TargetDeck43":
                                
                                destination = Pack.TARGET3;
                                break;
                         case "TargetDeck44":
                                
                                destination = Pack.TARGET4;
                                break;
                    
                     }
                     
                     int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,3);
                     Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                    
                     
                     if(senderName.getName() == "SourceDeck4"){
                         
                     }
                     else if(senderName.getName() == "WasteDeck4"){
                         
                     }
                     else{
                         if(playedGame.moveWorkingToTarget(source,destination)){
                             GUIGameBoard.this.winGame(playedGame);
                         }
                     }
                     //display game
                     GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,3);
                     actualGame.display();
                     
                     int scoreValue = playedGame.getScore().getScore();
                     GUIGameBoard.this.ScoreG4.setText("Score: " + scoreValue);
                     GUIGameBoard.this.ScoreG4.repaint();
                     GUIGameBoard.this.ScoreG4.revalidate();
                     
                     repaint();
                     revalidate();
                      
               
                     GUIGameBoard.this.clickedStackCard = null;
                     GUIGameBoard.this.parentStack = null;
                     GUIGameBoard.this.clickedDeck = null;
                
               
                }
                else{
                    /**
                     * Move card from source deck to waste
                     */
                   if(GUIGameBoard.this.clickedDeck == "SourceDeck4"){
        
                        source = Pack.DECK;
                        destination = Pack.WASTE;
                        
                        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,3);
                        Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                      
                        
                        CardDeck waste1 = playedGame.getWaste();
                        CardDeck source1 = playedGame.getDeck();
                         
                            if(!source1.isEmpty()){
                               
                                if(playedGame.moveDeckToWaste()){
                                   GUIGameBoard.this.winGame(playedGame);
                                }
                            }
                            else{
                                playedGame.reloadDeck();
                                GUIGameBoard.this.winGame(playedGame);
                            }
                        
                           //display game
                            GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,3);
                            actualGame.display();
                            
                            int scoreValue = playedGame.getScore().getScore();
                            GUIGameBoard.this.ScoreG4.setText("Score: " + scoreValue);
                            GUIGameBoard.this.ScoreG4.repaint();
                            GUIGameBoard.this.ScoreG4.revalidate();
                            
                            repaint();
                            revalidate();
                            GUIGameBoard.this.clickedDeck = null;  
       
                   }
                   /**
                    * Move card from waste to target deck
                    */
                   else if(GUIGameBoard.this.clickedDeck != senderName.getName()){
                        
                       
                            switch(GUIGameBoard.this.clickedDeck){

                                case "WasteDeck4":
                                     
                                      source = Pack.WASTE;
                                      break; 
                                default:
                                    break;

                            }

                            switch(senderName.getName()){

                                case "SourceDeckG4":
                                   return;
                                case "TargetDeck41":
                                  
                                     destination = Pack.TARGET1;
                                    break;
                                case "TargetDeck42":
                                    
                                    destination = Pack.TARGET2;
                                    break;
                                case "TargetDeck43":
                                    
                                    destination = Pack.TARGET3;
                                    break;
                                case "TargetDeck44":
                                    
                                    destination = Pack.TARGET4;
                                    break;
                                default:
                                    break;

                         }

                           int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,3);
                           Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                         
                           CardDeck waste1 = playedGame.getWaste();
                           if(destination != null){

                                if(playedGame.moveWasteToTarget(destination)){
                                    
                                    GUIGameBoard.this.winGame(playedGame);
                                    //display game
                                    GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,3);
                                    actualGame.display();

                                }
                                else{
                                    
                                }

                           }
                          
                            int scoreValue = playedGame.getScore().getScore();
                            GUIGameBoard.this.ScoreG4.setText("Score: " + scoreValue);
                            GUIGameBoard.this.ScoreG4.repaint();
                            GUIGameBoard.this.ScoreG4.revalidate();
                          

                            repaint();
                            revalidate();
                            GUIGameBoard.this.clickedDeck = null;
                    }
                    else{
                        if(e.getClickCount() == 2){
                            GUIGameBoard.this.clickedDeck = null;
                        }
                    }
                   
                }
            //end of GAME4
            }
        
        }

        @Override
        public void mousePressed(MouseEvent e) {
            
         
             JLabel senderName = (JLabel) e.getSource();
              if(GUIGameBoard.this.clickedDeck == null){
                 GUIGameBoard.this.clickedDeck = senderName.getName();
                 setActualGame(senderName.getName());
               
              }
          
            
        }

        @Override
        public void mouseExited(MouseEvent e) {
        
        }  

        @Override
        public void mouseEntered(MouseEvent e) {
           
            
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        
        
        }
    };
    
   

    /**
     * Mouse listener for handling stack to stack, deck to stack movement for each game
     */
     MouseListener stackCard = new MouseListener() {
         
         
         
         
       @Override
        public void mouseClicked(MouseEvent e) {
           
            
             
            }  

        
        @Override
        public void mousePressed(MouseEvent e) {
            
 
            
            /**
             * Set source stack 
             */
            CardLabel card = (CardLabel) e.getSource();
            Card clickedCard;
            JLayeredPane parent = (JLayeredPane) card.getParent();
             if(GUIGameBoard.this.clickedStackCard == null){
                
                GUIGameBoard.this.clickedStackCard = card.getStackCard();
                GUIGameBoard.this.parentStack = parent.getName();
                clickedCard = card.getStackCard();
                GUIGameBoard.this.setActualGame(parent.getName());
                
             
             }

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            
            CardLabel card = (CardLabel) e.getSource();
            
            /**
             * Set destination stack
             */
            JLayeredPane parent = (JLayeredPane) card.getParent();
            Pack source = null;
            Pack destination = null;
            
            
             
            if(GUIGameBoard.this.actualGame == "Game1"){
             
                /**
                 * Move card from deck to stack
                 */
                if(GUIGameBoard.this.clickedDeck != null){
                   
                    switch(GUIGameBoard.this.clickedDeck){
                    
                        case "WasteDeck1":
                           
                            source = Pack.WASTE;
                             break;
                        case "TargetDeck11":
                            
                            source = Pack.TARGET1;
                             break;
                        case "TargetDeck12":
                           
                            source = Pack.TARGET2;
                            break;
                        case "TargetDeck13":
                             
                             source = Pack.TARGET3;
                             break;
                        case "TargetDeck14":
                             
                             source = Pack.TARGET4;
                             break;
                        default:
                            
                            GUIGameBoard.this.clickedStackCard = null;
                            GUIGameBoard.this.parentStack = null;
                            GUIGameBoard.this.clickedDeck = null;
                            return;
                    
                     }
                    
                     switch(GUIGameBoard.this.parentStack){
                    
                          case "StackG11":
                                
                                destination = Pack.WORKING1;
                                break;
                         case "StackG12":
                               
                                destination = Pack.WORKING2;
                                break;
                         case "StackG13":
                                
                                destination = Pack.WORKING3;
                                break;
                         case "StackG14":
                               
                                destination = Pack.WORKING4;
                                break;
                         case "StackG15":
                               
                                destination = Pack.WORKING5;
                                break;
                         case "StackG16":
                               
                                destination = Pack.WORKING6;
                                break;
                         case "StackG17":
                                
                                destination = Pack.WORKING7;
                                break;
                          /*default:
                                    System.out.println("PRECOOOOOOO");
                                    System.out.println("Presun zo:" + destination);
                                    GUIGameBoard.this.clickedStackCard = null;
                                    GUIGameBoard.this.parentStack = null;
                                    GUIGameBoard.this.clickedDeck = null;
                                    return;*/
                            
                    
                     }
                     
                     int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,0);
                     Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                    
                     if(source == Pack.WASTE){
                          if(playedGame.moveWasteToWorking(destination)){
                              GUIGameBoard.this.winGame(playedGame);
                     
                          }
                     }
                     else{
                          if(playedGame.moveTargetToWorking(source,destination)){
                              GUIGameBoard.this.winGame(playedGame);
                     
                          }
                     }
                  
                     
                        int scoreValue = playedGame.getScore().getScore();
                        GUIGameBoard.this.ScoreG1.setText("Score: " + scoreValue);
                        GUIGameBoard.this.ScoreG1.repaint();
                        GUIGameBoard.this.ScoreG1.revalidate();
                        
                        //display game
                        GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,0);
                        actualGame.display();

                        repaint();
                        revalidate();
                
                        
                        GUIGameBoard.this.parentStack = null;
                        GUIGameBoard.this.clickedStackCard = null;
                        GUIGameBoard.this.clickedDeck = null;
                    
                }
                else{
                    /**
                     * Move card from stack to another stack in game 1
                     */
                    if(GUIGameBoard.this.parentStack != null){
                        
                        
                        if(GUIGameBoard.this.parentStack != parent.getName()){
                          
                            
                            switch(GUIGameBoard.this.parentStack){
                    
                                case "StackG11":
                                   
                                   source= Pack.WORKING1;
                                    break;
                                case "StackG12":
                                    
                                    source = Pack.WORKING2;
                                    break;
                                case "StackG13":
                                   
                                    source = Pack.WORKING3;
                                    break;
                                case "StackG14":
                                   
                                    source = Pack.WORKING4;
                                    break;
                                case "StackG15":
                                   
                                    source = Pack.WORKING5;
                                    break;
                                case "StackG16":
                                    
                                    source = Pack.WORKING6;
                                    break;
                                case "StackG17":
                                   
                                    source = Pack.WORKING7;
                                     break;
                                
                            }
                            
                            
                            switch(parent.getName()){
                    
                                case "StackG11":
                                   
                                    destination = Pack.WORKING1;
                                    break;
                                case "StackG12":
                                   
                                    destination = Pack.WORKING2;
                                    break;
                                case "StackG13":
                                    
                                    destination = Pack.WORKING3;
                                    break;
                                case "StackG14":
                                   
                                    destination = Pack.WORKING4;
                                    break;
                                case "StackG15":
                                    
                                    destination = Pack.WORKING5;
                                    break;
                                case "StackG16":
                                    
                                    destination = Pack.WORKING6;
                                    break;
                                case "StackG17":
                                    
                                    destination = Pack.WORKING7;
                                    break;
                                default:
                                    
                                    GUIGameBoard.this.clickedStackCard = null;
                                    GUIGameBoard.this.parentStack = null;
                                    return;
                            }
                            
                           int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,0);
                           Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                          
                            if(GUIGameBoard.this.clickedStackCard.isTurnedFaceUp()){
                                if(playedGame.moveWorkingToWorking(source,destination,GUIGameBoard.this.clickedStackCard)){
                                    GUIGameBoard.this.winGame(playedGame);
                                }
                            }
                            
                           
                            
                            int scoreValue = playedGame.getScore().getScore();
                            GUIGameBoard.this.ScoreG1.setText("Score: " + scoreValue);
                            GUIGameBoard.this.ScoreG1.repaint();
                            GUIGameBoard.this.ScoreG1.revalidate();
                            
                            //display game
                            GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,0);
                            actualGame.display();

                            repaint();
                            revalidate();
                            
                            
                            GUIGameBoard.this.parentStack = null;
                            GUIGameBoard.this.clickedStackCard = null;
                            
                            
                        }
                    }
                    else{
                        if(e.getClickCount() == 2){
                            GUIGameBoard.this.clickedStack = null;
                            GUIGameBoard.this.clickedStackCard = null;
                        }
                    }
                }
              }
            
            if(GUIGameBoard.this.actualGame == "Game2"){
             
                /**
                 * Move card from deck to stack in game 2
                 */
             if(GUIGameBoard.this.clickedDeck != null){
                   
                    switch(GUIGameBoard.this.clickedDeck){
                    
                        case "WasteDeck2":
                          
                            source = Pack.WASTE;
                             break;
                        case "TargetDeck21":
                           
                            source = Pack.TARGET1;
                             break;
                        case "TargetDeck22":
                            
                            source = Pack.TARGET2;
                            break;
                        case "TargetDeck23":
                            
                             source = Pack.TARGET3;
                             break;
                        case "TargetDeck24":
                             
                             source = Pack.TARGET4;
                             break;
                        default:
                            
                            GUIGameBoard.this.clickedStackCard = null;
                            GUIGameBoard.this.parentStack = null;
                            GUIGameBoard.this.clickedDeck = null;
                            return;
                    
                     }
                    
                     switch(GUIGameBoard.this.parentStack){
                    
                          case "StackG21":
                               
                                destination = Pack.WORKING1;
                                break;
                         case "StackG22":
                                
                                destination = Pack.WORKING2;
                                break;
                         case "StackG23":
                                
                                destination = Pack.WORKING3;
                                break;
                         case "StackG24":
                                
                                destination = Pack.WORKING4;
                                break;
                         case "StackG25":
                               
                                destination = Pack.WORKING5;
                                break;
                         case "StackG26":
                               
                                destination = Pack.WORKING6;
                                break;
                         case "StackG27":
                                
                                destination = Pack.WORKING7;
                                break;
               
                    
                     }
                        
                        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,1);
                        Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                     
                        if(source == Pack.WASTE){
                             if(playedGame.moveWasteToWorking(destination)){
                                 GUIGameBoard.this.winGame(playedGame);

                             }
                        }
                        else{
                             if(playedGame.moveTargetToWorking(source,destination)){
                                 GUIGameBoard.this.winGame(playedGame);

                             }
                        }
                        
                        //display game
                        GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,1);
                        actualGame.display();
                       
                        int scoreValue = playedGame.getScore().getScore();
                        GUIGameBoard.this.ScoreG2.setText("Score: " + scoreValue);
                        GUIGameBoard.this.ScoreG2.repaint();
                        GUIGameBoard.this.ScoreG2.revalidate();
                       

                        repaint();
                        revalidate();

                       
                        GUIGameBoard.this.parentStack = null;
                        GUIGameBoard.this.clickedStackCard = null;
                        GUIGameBoard.this.clickedDeck = null;
                    
                }
                else{
                 /**
                  * Move card from stack to another stack in game 2
                  */
                    if(GUIGameBoard.this.parentStack != null){
                        
                      
                        if(GUIGameBoard.this.parentStack != parent.getName()){
                            
                            
                            switch(GUIGameBoard.this.parentStack){
                    
                                case "StackG21":
                                    
                                   source= Pack.WORKING1;
                                    break;
                                case "StackG22":
                                   
                                    source = Pack.WORKING2;
                                    break;
                                case "StackG23":
                                    
                                    source = Pack.WORKING3;
                                    break;
                                case "StackG24":
                                    
                                    source = Pack.WORKING4;
                                    break;
                                case "StackG25":
                                   
                                    source = Pack.WORKING5;
                                    break;
                                case "StackG26":
                                   
                                    source = Pack.WORKING6;
                                    break;
                                case "StackG27":
                                   
                                    source = Pack.WORKING7;
                                    break;
                                default:
                                 
                                    GUIGameBoard.this.clickedStackCard = null;
                                    GUIGameBoard.this.parentStack = null;
                                    return;
                            }
                            
                            switch(parent.getName()){
                    
                                case "StackG21":
                                    
                                    destination = Pack.WORKING1;
                                    break;
                                case "StackG22":
                                    
                                    destination = Pack.WORKING2;
                                    break;
                                case "StackG23":
                                    
                                    destination = Pack.WORKING3;
                                    break;
                                case "StackG24":
                                    
                                    destination = Pack.WORKING4;
                                    break;
                                case "StackG25":
                                    
                                    destination = Pack.WORKING5;
                                    break;
                                case "StackG26":
                                    
                                    destination = Pack.WORKING6;
                                    break;
                                case "StackG27":
                                    
                                    destination = Pack.WORKING7;
                                    break;
                                default:
                                   
                                    GUIGameBoard.this.clickedStackCard = null;
                                    GUIGameBoard.this.parentStack = null;
                                    return;
                            }
                            
                            int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,1);
                            Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                                             
                            if(GUIGameBoard.this.clickedStackCard.isTurnedFaceUp()){
                                if(playedGame.moveWorkingToWorking(source,destination,GUIGameBoard.this.clickedStackCard)){
                                    GUIGameBoard.this.winGame(playedGame);
                                }
                            }
                            //display game
                            GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,1);
                            actualGame.display();
                            
                            int scoreValue = playedGame.getScore().getScore();
                            GUIGameBoard.this.ScoreG2.setText("Score: " + scoreValue);
                            GUIGameBoard.this.ScoreG2.repaint();
                            GUIGameBoard.this.ScoreG2.revalidate();
                            

                            repaint();
                            revalidate();
                            GUIGameBoard.this.parentStack = null;
                            GUIGameBoard.this.clickedStackCard = null;
                            
                            
                        }
                    }
                    else{
                        if(e.getClickCount() == 2){
                           
                            GUIGameBoard.this.clickedStack = null;
                            GUIGameBoard.this.clickedStackCard = null;
                        }
                    }
                }
              }
                
             if(GUIGameBoard.this.actualGame == "Game3"){
             
               /**
                * Move card from deck to stack in game 3
                */
               if(GUIGameBoard.this.clickedDeck != null){
                   
                    switch(GUIGameBoard.this.clickedDeck){
                    
                        case "WasteDeck3":
                           
                            source = Pack.WASTE;
                             break;
                        case "TargetDeck31":
                           
                            source = Pack.TARGET1;
                             break;
                        case "TargetDeck32":
                           
                            source = Pack.TARGET2;
                            break;
                        case "TargetDeck33":
                             
                             source = Pack.TARGET3;
                             break;
                        case "TargetDeck34":
                             
                             source = Pack.TARGET4;
                             break;
                        default:
                            
                            GUIGameBoard.this.clickedStackCard = null;
                            GUIGameBoard.this.parentStack = null;
                            GUIGameBoard.this.clickedDeck = null;
                            return;
                    
                     }
                    
                     switch(GUIGameBoard.this.parentStack){
                    
                          case "StackG31":
                                
                                destination = Pack.WORKING1;
                                break;
                         case "StackG32":
                                
                                destination = Pack.WORKING2;
                                break;
                         case "StackG33":
                                
                                destination = Pack.WORKING3;
                                break;
                         case "StackG34":
                                
                                destination = Pack.WORKING4;
                                break;
                         case "StackG35":
                                
                                destination = Pack.WORKING5;
                                break;
                         case "StackG36":
                               
                                destination = Pack.WORKING6;
                                break;
                         case "StackG37":
                               
                                destination = Pack.WORKING7;
                                break;
                        default:
                                
                                GUIGameBoard.this.clickedStackCard = null;
                                GUIGameBoard.this.parentStack = null;
                                GUIGameBoard.this.clickedDeck = null;
                                return;
                    
                     }
                        
                     
                        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,2);
                        Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                      
                        if(source == Pack.WASTE){
                             if(playedGame.moveWasteToWorking(destination)){
                                 GUIGameBoard.this.winGame(playedGame);

                             }
                        }
                        else{
                             if(playedGame.moveTargetToWorking(source,destination)){
                                 GUIGameBoard.this.winGame(playedGame);

                             }
                        }
                        //display game
                        GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,2);
                        actualGame.display();

                        int scoreValue = playedGame.getScore().getScore();
                        GUIGameBoard.this.ScoreG3.setText("Score: " + scoreValue);
                        GUIGameBoard.this.ScoreG3.repaint();
                        GUIGameBoard.this.ScoreG3.revalidate();


                        repaint();
                        revalidate();

                       
                        GUIGameBoard.this.parentStack = null;
                        GUIGameBoard.this.clickedStackCard = null;
                        GUIGameBoard.this.clickedDeck = null;
                    
                }
                else{
                   /**
                    * Move card from stack to another stack in game 3
                    */
                    if(GUIGameBoard.this.parentStack != null){
                        
                        
                       
                        if(GUIGameBoard.this.parentStack != parent.getName()){
                          
                            
                            switch(GUIGameBoard.this.parentStack){
                    
                                case "StackG31":
                                   
                                   source= Pack.WORKING1;
                                    break;
                                case "StackG32":
                                    
                                    source = Pack.WORKING2;
                                    break;
                                case "StackG33":
                                   
                                    source = Pack.WORKING3;
                                    break;
                                case "StackG34":
                                   
                                    source = Pack.WORKING4;
                                    break;
                                case "StackG35":
                                    
                                    source = Pack.WORKING5;
                                    break;
                                case "StackG36":
                                    
                                    source = Pack.WORKING6;
                                    break;
                                case "StackG37":
                                    
                                    source = Pack.WORKING7;
                                    break;
                                default:
                                    
                                    GUIGameBoard.this.clickedStackCard = null;
                                    GUIGameBoard.this.parentStack = null;
                                    return;
                            }
                            
                            switch(parent.getName()){
                    
                                case "StackG31":
                                    
                                    destination = Pack.WORKING1;
                                    break;
                                case "StackG32":
                                    
                                    destination = Pack.WORKING2;
                                    break;
                                case "StackG33":
                                    
                                    destination = Pack.WORKING3;
                                    break;
                                case "StackG34":
                                   
                                    destination = Pack.WORKING4;
                                    break;
                                case "StackG35":
                                   
                                    destination = Pack.WORKING5;
                                    break;
                                case "StackG36":
                                    
                                    destination = Pack.WORKING6;
                                    break;
                                case "StackG37":
                                    
                                    destination = Pack.WORKING7;
                                    break;
                                default:
                                    
                                    GUIGameBoard.this.clickedStackCard = null;
                                    GUIGameBoard.this.parentStack = null;
                                    return;
                            }
                            
                            int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,2);
                            Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                           
                            if(GUIGameBoard.this.clickedStackCard.isTurnedFaceUp()){
                                if(playedGame.moveWorkingToWorking(source,destination,GUIGameBoard.this.clickedStackCard)){
                                   GUIGameBoard.this.winGame(playedGame);
                                }
                            }
                            
                            //display game
                            GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,2);
                            actualGame.display();
                                
                                
                            int scoreValue = playedGame.getScore().getScore();
                            GUIGameBoard.this.ScoreG3.setText("Score: " + scoreValue);
                            GUIGameBoard.this.ScoreG3.repaint();
                            GUIGameBoard.this.ScoreG3.revalidate();

                            repaint();
                            revalidate();
                            GUIGameBoard.this.parentStack = null;
                            GUIGameBoard.this.clickedStackCard = null;
                            
                            
                        }
                    }
                    else{
                        if(e.getClickCount() == 2){
                            
                            GUIGameBoard.this.clickedStack = null;
                            GUIGameBoard.this.clickedStackCard = null;
                        }
                    }
                }
              }
             
             if(GUIGameBoard.this.actualGame == "Game4"){
             
                 /**
                  * Move card from deck to stack game 4
                  */
                 if(GUIGameBoard.this.clickedDeck != null){
                   
                    switch(GUIGameBoard.this.clickedDeck){
                    
                        case "WasteDeck4":
                            
                            source = Pack.WASTE;
                             break;
                        case "TargetDeck41":
                           
                            source = Pack.TARGET1;
                             break;
                        case "TargetDeck42":
                            
                            source = Pack.TARGET2;
                            break;
                        case "TargetDeck43":
                            
                             source = Pack.TARGET3;
                             break;
                        case "TargetDeck44":
                            
                             source = Pack.TARGET4;
                             break;
                         default:
                            
                            GUIGameBoard.this.clickedStackCard = null;
                            GUIGameBoard.this.parentStack = null;
                            GUIGameBoard.this.clickedDeck = null;
                            return;
                    
                     }
                    
                     switch(GUIGameBoard.this.parentStack){
                    
                          case "StackG41":
                                
                                destination = Pack.WORKING1;
                                break;
                         case "StackG42":
                                
                                destination = Pack.WORKING2;
                                break;
                         case "StackG43":
                                
                                destination = Pack.WORKING3;
                                break;
                         case "StackG44":
                                
                                destination = Pack.WORKING4;
                                break;
                         case "StackG45":
                                
                                destination = Pack.WORKING5;
                                break;
                         case "StackG46":
                                
                                destination = Pack.WORKING6;
                                break;
                         case "StackG47":
                              
                                destination = Pack.WORKING7;
                                break;
                          default:
                                
                                GUIGameBoard.this.clickedStackCard = null;
                                GUIGameBoard.this.parentStack = null;
                                GUIGameBoard.this.clickedDeck = null;
                                return;
                            
                    
                     }
                     
                     int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,3);
                     Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                    
                     if(source == Pack.WASTE){
                          if(playedGame.moveWasteToWorking(destination)){
                           
                              GUIGameBoard.this.winGame(playedGame);
                     
                          }
                     }
                     else{
                          if(playedGame.moveTargetToWorking(source,destination)){
                           
                              GUIGameBoard.this.winGame(playedGame);
                     
                          }
                     }
                     
                    //display game
                    GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,3);
                    actualGame.display();

                    int scoreValue = playedGame.getScore().getScore();
                    GUIGameBoard.this.ScoreG4.setText("Score: " + scoreValue);
                    GUIGameBoard.this.ScoreG4.repaint();
                    GUIGameBoard.this.ScoreG4.revalidate();
                    
                    
                    repaint();
                    revalidate();
     
                    
                    GUIGameBoard.this.parentStack = null;
                    GUIGameBoard.this.clickedStackCard = null;
                    GUIGameBoard.this.clickedDeck = null;
                }
                else{
                     /**
                      * Move card from stack to another stack in game 4
                      */
                    if(GUIGameBoard.this.parentStack != null){
                        
                    
                        if(GUIGameBoard.this.parentStack != parent.getName()){
                           
                          
                            
                            switch(GUIGameBoard.this.parentStack){
                    
                                case "StackG41":
                                    
                                   source= Pack.WORKING1;
                                    break;
                                case "StackG42":
                                    
                                    source = Pack.WORKING2;
                                    break;
                                case "StackG43":
                                   
                                    source = Pack.WORKING3;
                                    break;
                                case "StackG44":
                                    
                                    source = Pack.WORKING4;
                                    break;
                                case "StackG45":
                                   
                                    source = Pack.WORKING5;
                                    break;
                                case "StackG46":
                                  
                                    source = Pack.WORKING6;
                                    break;
                                case "StackG47":
                                   
                                    source = Pack.WORKING7;
                                    break;
                                default:
                                    
                                    GUIGameBoard.this.clickedStackCard = null;
                                    GUIGameBoard.this.parentStack = null;
                                    return;
                            }
                            
                            switch(parent.getName()){
                    
                                case "StackG41":
                                    
                                    destination = Pack.WORKING1;
                                    break;
                                case "StackG42":
                                    
                                    destination = Pack.WORKING2;
                                    break;
                                case "StackG43":
                                   
                                    destination = Pack.WORKING3;
                                    break;
                                case "StackG44":
                                  
                                    destination = Pack.WORKING4;
                                    break;
                                case "StackG45":
                                    
                                    destination = Pack.WORKING5;
                                    break;
                                case "StackG46":
                                   
                                    destination = Pack.WORKING6;
                                    break;
                                case "StackG47":
                                    
                                    destination = Pack.WORKING7;
                                    break;
                                default:
                                    
                                    GUIGameBoard.this.clickedStackCard = null;
                                    GUIGameBoard.this.parentStack = null;
                                    return;
                            }
                            
                          
                            int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,3);
                            Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                           
                            if(GUIGameBoard.this.clickedStackCard.isTurnedFaceUp()){
                                if(playedGame.moveWorkingToWorking(source,destination,GUIGameBoard.this.clickedStackCard)){
                                    
                                    GUIGameBoard.this.winGame(playedGame);
                                }
                            }
                            
                            //display game
                            GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,3);
                            actualGame.display();
                            
                            int scoreValue = playedGame.getScore().getScore();
                            GUIGameBoard.this.ScoreG4.setText("Score: " + scoreValue);
                            GUIGameBoard.this.ScoreG4.repaint();
                            GUIGameBoard.this.ScoreG4.revalidate();


                            repaint();
                            revalidate();
                            GUIGameBoard.this.parentStack = null;
                            GUIGameBoard.this.clickedStackCard = null;
                            
                            
                        }
                    }
                    else{
                        if(e.getClickCount() == 2){
                           
                            GUIGameBoard.this.clickedStack = null;
                            GUIGameBoard.this.clickedStackCard = null;
                        }
                    }
                }
              }
            
            
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            
        }

        @Override
        public void mouseExited(MouseEvent e) {
            
        }
         
         
     };
    
    
    
    /**
     * Mouse listner for handling situation when moving cards to empty stack
     */
    MouseListener mlStack = new MouseListener() {
        @Override
        public void mouseReleased(MouseEvent e) {
        
            JLayeredPane senderName = (JLayeredPane) e.getSource();
            
            Pack source = null;
            Pack destination = null;
           
            if(GUIGameBoard.this.actualGame == "Game1"){
                
                /**
                 * Move card from deck to empty stack in game 1
                 */
                if(GUIGameBoard.this.clickedDeck != null){
                 
                    switch(GUIGameBoard.this.clickedDeck){
                    
                        case "WasteDeck1":
                           
                            source = Pack.WASTE;
                             break;
                        case "TargetDeck11":
                           
                            source = Pack.TARGET1;
                             break;
                        case "TargetDeck12":
                            
                            source = Pack.TARGET2;
                            break;
                        case "TargetDeck13":
                             
                             source = Pack.TARGET3;
                             break;
                        case "TargetDeck14":
                             
                             source = Pack.TARGET4;
                             break;
                        default:
                            
                            GUIGameBoard.this.clickedDeck = null;
                            GUIGameBoard.this.clickedStack = null;
                            return;
                    
                     }
                    
                     switch(senderName.getName()){
                    
                          case "StackG11":
                               
                                destination = Pack.WORKING1;
                                break;
                         case "StackG12":
                                
                                destination = Pack.WORKING2;
                                break;
                         case "StackG13":
                               
                                destination = Pack.WORKING3;
                                break;
                         case "StackG14":
                                
                                destination = Pack.WORKING4;
                                break;
                         case "StackG15":
                                
                                destination = Pack.WORKING5;
                                break;
                         case "StackG16":
                               
                                destination = Pack.WORKING6;
                                break;
                         case "StackG17":
                              
                                destination = Pack.WORKING7;
                                break;
                         
                         default:
                            
                            GUIGameBoard.this.clickedDeck = null;
                            GUIGameBoard.this.clickedStack = null;
                            return; 
                    
                     }
                      
                     int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,0);
                     Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                     
                     if(source == Pack.WASTE){
                          if(playedGame.moveWasteToWorking(destination)){
                         
                              GUIGameBoard.this.winGame(playedGame);
                     
                          }
                     }
                     else{
                          if(playedGame.moveTargetToWorking(source,destination)){
                            
                              GUIGameBoard.this.winGame(playedGame);
                     
                          }
                     }
                    //display game
                    GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,0);
                    actualGame.display();

                    int scoreValue = playedGame.getScore().getScore();
                    GUIGameBoard.this.ScoreG1.setText("Score: " + scoreValue);
                    GUIGameBoard.this.ScoreG1.repaint();
                    GUIGameBoard.this.ScoreG1.revalidate();
                    

                    repaint();
                    revalidate();
                
                    GUIGameBoard.this.clickedStack = null;
                    GUIGameBoard.this.clickedDeck = null;
                    GUIGameBoard.this.clickedStackCard = null;
                    GUIGameBoard.this.parentStack = null;
                }
                else{
                  /**
                   * Move card from stack to empty stack in game 1
                   */
                    if(GUIGameBoard.this.parentStack != null){
                         switch(GUIGameBoard.this.parentStack){
                    
                          case "StackG11":
                                
                                source = Pack.WORKING1;
                                break;
                         case "StackG12":
                                
                                source = Pack.WORKING2;
                                break;
                         case "StackG13":
                               
                                source = Pack.WORKING3;
                                break;
                         case "StackG14":
                               
                                source = Pack.WORKING4;
                                break;
                         case "StackG15":
                               
                                source  = Pack.WORKING5;
                                break;
                         case "StackG16":
                               
                               source  = Pack.WORKING6;
                                break;
                         case "StackG17":
                               
                                source = Pack.WORKING7;
                                break;
                          default:
                               
                                GUIGameBoard.this.clickedStackCard = null; 
                                GUIGameBoard.this.parentStack = null;
                                GUIGameBoard.this.clickedStack = null;
                                return;
                            
                    
                     }
                         
                      switch(senderName.getName()){
                    
                          case "StackG11":
                               
                                destination = Pack.WORKING1;
                                break;
                         case "StackG12":
                                
                                destination = Pack.WORKING2;
                                break;
                         case "StackG13":
                             
                                destination = Pack.WORKING3;
                                break;
                         case "StackG14":
                               
                                destination = Pack.WORKING4;
                                break;
                         case "StackG15":
                               
                                destination = Pack.WORKING5;
                                break;
                         case "StackG16":
                                
                                destination = Pack.WORKING6;
                                break;
                         case "StackG17":
                                
                                destination = Pack.WORKING7;
                                break;
                         default:
                                
                                GUIGameBoard.this.clickedStackCard = null;
                                GUIGameBoard.this.parentStack = null;
                                GUIGameBoard.this.clickedStack = null;
                                return;
                    
                     }
                      
                     
                       
                      int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,0);
                      Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                      
                      if(playedGame.moveWorkingToWorking(source,destination,GUIGameBoard.this.clickedStackCard)){
                           GUIGameBoard.this.winGame(playedGame);
                     
                      }
                      //display game
                      GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,0);
                      actualGame.display();

                      int scoreValue = playedGame.getScore().getScore();
                      GUIGameBoard.this.ScoreG1.setText("Score: " + scoreValue);
                      GUIGameBoard.this.ScoreG1.repaint();
                      GUIGameBoard.this.ScoreG1.revalidate();
        
                      repaint();
                      revalidate();
                         
                      GUIGameBoard.this.clickedStack = null;     
                      GUIGameBoard.this.clickedStackCard = null;
                      GUIGameBoard.this.parentStack = null;
                        
                    }
                    else{
                        if(e.getClickCount() == 2){
                            GUIGameBoard.this.clickedStack = null;
                        }
                    }
   
                }
            //end of GAME1   
            }
            
            
            if(GUIGameBoard.this.actualGame == "Game2"){
                
                /**
                 * Move card from deck to empty stack in game 2
                 */
                if(GUIGameBoard.this.clickedDeck != null){
                 
                    switch(GUIGameBoard.this.clickedDeck){
                    
                        case "WasteDeck2":
                            
                            source = Pack.WASTE;
                             break;
                        case "TargetDeck21":
                           
                            source = Pack.TARGET1;
                             break;
                        case "TargetDeck22":
                           
                            source = Pack.TARGET2;
                            break;
                        case "TargetDeck23":
                             
                             source = Pack.TARGET3;
                             break;
                        case "TargetDeck24":
                            
                             source = Pack.TARGET4;
                             break;
                        default:
                            
                            GUIGameBoard.this.clickedDeck = null;
                            GUIGameBoard.this.clickedStack = null;
                            return;
                    
                     }
                    
                     switch(senderName.getName()){
                    
                          case "StackG21":
                                
                                destination = Pack.WORKING1;
                                break;
                         case "StackG22":
                                
                                destination = Pack.WORKING2;
                                break;
                         case "StackG23":
                                
                                destination = Pack.WORKING3;
                                break;
                         case "StackG24":
                                
                                destination = Pack.WORKING4;
                                break;
                         case "StackG25":
                                
                                destination = Pack.WORKING5;
                                break;
                         case "StackG26":
                                
                                destination = Pack.WORKING6;
                                break;
                         case "StackG27":
                                
                                destination = Pack.WORKING7;
                                break;
                          default:
                                
                                GUIGameBoard.this.clickedDeck = null;
                                GUIGameBoard.this.clickedStack = null;
                                return;
                            
                    
                     }
                      
                     int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,1);
                     Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                     
                        if(source == Pack.WASTE){
                             if(playedGame.moveWasteToWorking(destination)){
                                 GUIGameBoard.this.winGame(playedGame);

                             }
                        }
                        else{
                             if(playedGame.moveTargetToWorking(source,destination)){
                                 GUIGameBoard.this.winGame(playedGame);

                             }
                        }
                        
                        //display game
                       GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,1);
                       actualGame.display();
                   
                       int scoreValue = playedGame.getScore().getScore();
                       GUIGameBoard.this.ScoreG2.setText("Score: " + scoreValue);
                       GUIGameBoard.this.ScoreG2.repaint();
                       GUIGameBoard.this.ScoreG2.revalidate();
            

                       repaint();
                       revalidate();
                       GUIGameBoard.this.clickedStack = null;
                       GUIGameBoard.this.clickedDeck = null;
                       GUIGameBoard.this.clickedStackCard = null;
                       GUIGameBoard.this.parentStack = null;
                }
                else{
                    /**
                     * Move card from stack to empty stack in game 2
                     */
                   if(GUIGameBoard.this.parentStack != null){
                         switch(GUIGameBoard.this.parentStack){
                    
                          case "StackG21":
                               
                                source = Pack.WORKING1;
                                break;
                         case "StackG22":
                              
                                source = Pack.WORKING2;
                                break;
                         case "StackG23":
                               
                                source = Pack.WORKING3;
                                break;
                         case "StackG24":
                                
                                source = Pack.WORKING4;
                                break;
                         case "StackG25":
                                
                                source  = Pack.WORKING5;
                                break;
                         case "StackG26":
                               
                               source  = Pack.WORKING6;
                                break;
                         case "StackG27":
                               
                                source = Pack.WORKING7;
                                break;
                          default:
                                
                                GUIGameBoard.this.clickedStackCard = null;
                                GUIGameBoard.this.parentStack = null;
                                GUIGameBoard.this.clickedStack = null;
                                return;
                            
                    
                     }
                         
                      switch(senderName.getName()){
                    
                          case "StackG21":
                                
                                destination = Pack.WORKING1;
                                break;
                         case "StackG22":
                                
                                destination = Pack.WORKING2;
                                break;
                         case "StackG23":
                             
                                destination = Pack.WORKING3;
                                break;
                         case "StackG24":
                                
                                destination = Pack.WORKING4;
                                break;
                         case "StackG25":
                                
                                destination = Pack.WORKING5;
                                break;
                         case "StackG26":
                                
                                destination = Pack.WORKING6;
                                break;
                         case "StackG27":
                               
                                destination = Pack.WORKING7;
                                break;
                         default:
                              
                               GUIGameBoard.this.clickedStackCard = null;
                               GUIGameBoard.this.parentStack = null;
                               GUIGameBoard.this.clickedStack = null;
                               return;
                            
                    
                     }
                      
                     
                     int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,1);
                     Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                       
                     if(playedGame.moveWorkingToWorking(source,destination,GUIGameBoard.this.clickedStackCard)){
                         GUIGameBoard.this.winGame(playedGame);
                     }
                      //display game
                     GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,1);
                     actualGame.display();
                          
                     int scoreValue = playedGame.getScore().getScore();
                     GUIGameBoard.this.ScoreG2.setText("Score: " + scoreValue);
                     GUIGameBoard.this.ScoreG2.repaint();
                     GUIGameBoard.this.ScoreG2.revalidate();
 
                     repaint();
                     revalidate();

                    GUIGameBoard.this.clickedStack = null;     
                    GUIGameBoard.this.clickedStackCard = null;
                    GUIGameBoard.this.parentStack = null;


                        
                    }
                    else{
                        if(e.getClickCount() == 2){
                            GUIGameBoard.this.clickedStack = null;
                        }
                    }
                    
                    
                    
                    
                    
                }
            //end of GAME2  
            }
            
            if(GUIGameBoard.this.actualGame == "Game3"){
                
                /**
                 * Move card from deck to empty stack game 3
                 */
                 if(GUIGameBoard.this.clickedDeck != null){
                 
                    switch(GUIGameBoard.this.clickedDeck){
                    
                        case "WasteDeck3":
                          
                            source = Pack.WASTE;
                             break;
                        case "TargetDeck31":
                           
                            source = Pack.TARGET1;
                             break;
                        case "TargetDeck32":
                           
                            source = Pack.TARGET2;
                            break;
                        case "TargetDeck33":
                            
                             source = Pack.TARGET3;
                             break;
                        case "TargetDeck34":
                             
                             source = Pack.TARGET4;
                             break;
                        default:
                           
                            GUIGameBoard.this.clickedDeck = null;
                            GUIGameBoard.this.clickedStack = null;
                            return;
                    
                     }
                    
                     switch(senderName.getName()){
                    
                          case "StackG31":
                             
                                destination = Pack.WORKING1;
                                break;
                         case "StackG32":
                              
                                destination = Pack.WORKING2;
                                break;
                         case "StackG33":
                               
                                destination = Pack.WORKING3;
                                break;
                         case "StackG34":
                                
                                destination = Pack.WORKING4;
                                break;
                         case "StackG35":
                                
                                destination = Pack.WORKING5;
                                break;
                         case "StackG36":
                               
                                destination = Pack.WORKING6;
                                break;
                         case "StackG37":
                                
                                destination = Pack.WORKING7;
                                break;
                         default:
                               
                                GUIGameBoard.this.clickedDeck = null;
                                GUIGameBoard.this.clickedStack = null;
                                return;
                            
                    
                     }
                      
                     int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,2);
                     Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                      
                        if(source == Pack.WASTE){
                             if(playedGame.moveWasteToWorking(destination)){
                              
                                 GUIGameBoard.this.winGame(playedGame);

                             }
                        }
                        else{
                             if(playedGame.moveTargetToWorking(source,destination)){
                               
                                 GUIGameBoard.this.winGame(playedGame);
                             }
                        }

                      //display game
                        GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,2);
                        actualGame.display();
                    
                        int scoreValue = playedGame.getScore().getScore();
                        GUIGameBoard.this.ScoreG3.setText("Score: " + scoreValue);
                        GUIGameBoard.this.ScoreG3.repaint();
                        GUIGameBoard.this.ScoreG3.revalidate();
                       
                       
                        repaint();
                        revalidate();

                        GUIGameBoard.this.clickedStack = null;
                        GUIGameBoard.this.clickedDeck = null;
                        GUIGameBoard.this.clickedStackCard = null;
                        GUIGameBoard.this.parentStack = null;
                }
                else{
                    /**
                     * Move card from stack to empty stack in game 3
                     */
                    if(GUIGameBoard.this.parentStack != null){
                         switch(GUIGameBoard.this.parentStack){
                    
                          case "StackG31":
                                
                                source = Pack.WORKING1;
                                break;
                         case "StackG32":
                                
                                source = Pack.WORKING2;
                                break;
                         case "StackG33":
                               
                                source = Pack.WORKING3;
                                break;
                         case "StackG34":
                              
                                source = Pack.WORKING4;
                                break;
                         case "StackG35":
                               
                                source  = Pack.WORKING5;
                                break;
                         case "StackG36":
                               
                               source  = Pack.WORKING6;
                                break;
                         case "StackG37":
                                
                                source = Pack.WORKING7;
                                break;
                          default:
                               
                                GUIGameBoard.this.clickedStackCard = null;
                                GUIGameBoard.this.parentStack = null;
                                GUIGameBoard.this.clickedStack = null;
                                return;
                            
                    
                     }
                         
                      switch(senderName.getName()){
                    
                         case "StackG31":
                                
                                destination = Pack.WORKING1;
                                break;
                         case "StackG32":
                                
                                destination = Pack.WORKING2;
                                break;
                         case "StackG33":
                               
                                destination = Pack.WORKING3;
                                break;
                         case "StackG34":
                              
                                destination = Pack.WORKING4;
                                break;
                         case "StackG35":
                               
                                destination = Pack.WORKING5;
                                break;
                         case "StackG36":
                               
                                destination = Pack.WORKING6;
                                break;
                         case "StackG37":
                               
                                destination = Pack.WORKING7;
                                break;
                          default:
                               
                                GUIGameBoard.this.clickedStackCard = null;
                                GUIGameBoard.this.parentStack = null;
                                GUIGameBoard.this.clickedStack = null;
                                return;
                            
                    
                     }
                      
                       
                         
                        int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,2);
                        Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                        
                        if(playedGame.moveWorkingToWorking(source,destination,GUIGameBoard.this.clickedStackCard)){
                              
                            GUIGameBoard.this.winGame(playedGame);

                         }
                         //display game
                        GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,2);
                        actualGame.display();
                         
                        int scoreValue = playedGame.getScore().getScore();
                        GUIGameBoard.this.ScoreG3.setText("Score: " + scoreValue);
                        GUIGameBoard.this.ScoreG3.repaint();
                        GUIGameBoard.this.ScoreG3.revalidate();
                         

                        repaint();
                        revalidate();

                        GUIGameBoard.this.clickedStack = null;     
                        GUIGameBoard.this.clickedStackCard = null;
                        GUIGameBoard.this.parentStack = null;


                        
                    }
                    else{
                        if(e.getClickCount() == 2){
                       
                        GUIGameBoard.this.clickedStack = null;
                        }
                    }
                    
                    
                    
                    
                    
                }
            //end of GAME3   
            }
          
           if(GUIGameBoard.this.actualGame == "Game4"){
                
               /**
                * Move card from deck to empty stack in game 4
                */
                 if(GUIGameBoard.this.clickedDeck != null){
                 
                    switch(GUIGameBoard.this.clickedDeck){
                    
                        case "WasteDeck4":
                          
                            source = Pack.WASTE;
                             break;
                        case "TargetDeck41":
                           
                            source = Pack.TARGET1;
                             break;
                        case "TargetDeck42":
                           
                            source = Pack.TARGET2;
                            break;
                        case "TargetDeck43":
                            
                             source = Pack.TARGET3;
                             break;
                        case "TargetDeck44":
                             
                             source = Pack.TARGET4;
                             break;
                        default:
                          
                            GUIGameBoard.this.clickedDeck = null;
                            GUIGameBoard.this.clickedStack = null;
                            return;
                    
                     }
                    
                     switch(senderName.getName()){
                    
                          case "StackG41":
                                
                                destination = Pack.WORKING1;
                                break;
                         case "StackG42":
                                
                                destination = Pack.WORKING2;
                                break;
                         case "StackG43":
                               
                                destination = Pack.WORKING3;
                                break;
                         case "StackG44":
                               
                                destination = Pack.WORKING4;
                                break;
                         case "StackG45":
                              
                                destination = Pack.WORKING5;
                                break;
                         case "StackG46":
                               
                                destination = Pack.WORKING6;
                                break;
                         case "StackG47":
                                
                                destination = Pack.WORKING7;
                                break;
                         default:
                               
                                GUIGameBoard.this.clickedDeck = null;
                                GUIGameBoard.this.clickedStack = null;
                                return;
                            
                    
                     }
                     
                     int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,3);
                     Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                     
                     
                        if(source == Pack.WASTE){
                             if(playedGame.moveWasteToWorking(destination)){
                                 
                                 GUIGameBoard.this.winGame(playedGame);

                             }
                        }
                        else{
                             if(playedGame.moveTargetToWorking(source,destination)){
                                 
                                 GUIGameBoard.this.winGame(playedGame);

                             }
                        }

                        //display game
                        GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,3);
                        actualGame.display();
                        
                       
                        int scoreValue = playedGame.getScore().getScore();
                        GUIGameBoard.this.ScoreG4.setText("Score: " + scoreValue);
                        GUIGameBoard.this.ScoreG4.repaint();
                        GUIGameBoard.this.ScoreG4.revalidate();
                
                        repaint();
                        revalidate();
                        
                        GUIGameBoard.this.clickedStack = null;
                        GUIGameBoard.this.clickedDeck = null;
                        GUIGameBoard.this.clickedStackCard = null;
                        GUIGameBoard.this.parentStack = null;
               
                 }
                else{
                    /**
                     * Move card from stack to empty stack in game 4
                     */
                   if(GUIGameBoard.this.parentStack != null){
                         switch(GUIGameBoard.this.parentStack){
                    
                          case "StackG41":
                                
                                source = Pack.WORKING1;
                                break;
                         case "StackG42":
                               
                                source = Pack.WORKING2;
                                break;
                         case "StackG43":
                               
                                source = Pack.WORKING3;
                                break;
                         case "StackG44":
                                
                                source = Pack.WORKING4;
                                break;
                         case "StackG45":
                              
                                source  = Pack.WORKING5;
                                break;
                         case "StackG46":
                             
                               source  = Pack.WORKING6;
                                break;
                         case "StackG47":
                               
                                source = Pack.WORKING7;
                                break;
                          default:
                               
                               GUIGameBoard.this.clickedStackCard = null;
                               GUIGameBoard.this.parentStack = null;
                               GUIGameBoard.this.clickedStack = null;
                               return;
                            
                    
                     }
                         
                      switch(senderName.getName()){
                    
                          case "StackG41":
                               
                                destination = Pack.WORKING1;
                                break;
                         case "StackG42":
                               
                                destination = Pack.WORKING2;
                                break;
                         case "StackG43":
                             
                                destination = Pack.WORKING3;
                                break;
                         case "StackG44":
                                
                                destination = Pack.WORKING4;
                                break;
                         case "StackG45":
                                
                                destination = Pack.WORKING5;
                                break;
                         case "StackG46":
                                
                                destination = Pack.WORKING6;
                                break;
                         case "StackG47":
                               
                                destination = Pack.WORKING7;
                                break;
                         default:
                                
                                GUIGameBoard.this.clickedStackCard = null;
                                GUIGameBoard.this.parentStack = null;
                                GUIGameBoard.this.clickedStack = null;
                                return;
                            
                    
                     }

                        
                         
                      int index = GUIGameBoard.this.getArrayIndex(GUIGameBoard.this.gamePanel,3);
                      Game playedGame = GUIGameBoard.this.gameBoard.getGame(index);
                       

                      if(playedGame.moveWorkingToWorking(source,destination,GUIGameBoard.this.clickedStackCard)){
                                GUIGameBoard.this.winGame(playedGame);
                      }
                         
                         //display game  
                        GUIGame actualGame = new GUIGame(GUIGameBoard.this.gameBoard,playedGame,GUIGameBoard.this,3);
                        actualGame.display();
                        int scoreValue = playedGame.getScore().getScore();
                        GUIGameBoard.this.ScoreG4.setText("Score: " + scoreValue);
                        GUIGameBoard.this.ScoreG4.repaint();
                        GUIGameBoard.this.ScoreG4.revalidate();
                        repaint();
                        revalidate();

                        GUIGameBoard.this.clickedStack = null;     
                        GUIGameBoard.this.clickedStackCard = null;
                        GUIGameBoard.this.parentStack = null;

                        
                        
                    }
                    else{
                        if(e.getClickCount() == 2){
                        
                        GUIGameBoard.this.clickedStack = null;
                        }
                    }
                    
                    
                    
                    
                    
                }
            //end of GAME4   
            }
            
            
            
        }
        
        
        /**
         * Set clicked stack and actual game
         * @param e mouse event
         */
        @Override
        public void mousePressed(MouseEvent e) {
              JLayeredPane senderName = (JLayeredPane) e.getSource();
               if(GUIGameBoard.this.clickedStack == null){
                 GUIGameBoard.this.clickedStack = senderName.getName();
                 setActualGame(senderName.getName());
                
           
              }
  
            
         
        }

        @Override
        public void mouseExited(MouseEvent e) {
        
        }

        @Override
        public void mouseEntered(MouseEvent e) {
          
        }

        @Override
        public void mouseClicked(MouseEvent e) {
             
            }
    };
    
    
    
    
    public int getDeckById(){
        return this.DeckId;
    }
    
    public int getStackById(){
        return this.StackId;
    }
    
    /**
     * Set actual played game according to clicked componenets
     * @param name clicked component name
     */
    public void setActualGame(String name){
        switch(name){
            case "SourceDeck1":
            case "WasteDeck1":
            case "TargetDeck11":
            case "TargetDeck12":
            case "TargetDeck13":
            case "TargetDeck14":
            case "StackG11":
            case "StackG12":
            case "StackG13":
            case "StackG14":
            case "StackG15":
            case "StackG16":
            case "StackG17":
               GUIGameBoard.this.actualGame = "Game1";
               break;
            case "SourceDeck2":
            case "WasteDeck2":
            case "TargetDeck21":
            case "TargetDeck22":
            case "TargetDeck23":
            case "TargetDeck24":
            case "StackG21":
            case "StackG22":
            case "StackG23":
            case "StackG24":
            case "StackG25":
            case "StackG26":
            case "StackG27":
               GUIGameBoard.this.actualGame = "Game2";
               break;
            case "SourceDeck3":
            case "WasteDeck3":
            case "TargetDeck31":
            case "TargetDeck32":
            case "TargetDeck33":
            case "TargetDeck34":
            case "StackG31":
            case "StackG32":
            case "StackG33":
            case "StackG34":
            case "StackG35":
            case "StackG36":
            case "StackG37":
               GUIGameBoard.this.actualGame = "Game3";
               break;
            case "SourceDeck4":
            case "WasteDeck4":
            case "TargetDeck41":
            case "TargetDeck42":
            case "TargetDeck43":
            case "TargetDeck44":
            case "StackG41":
            case "StackG42":
            case "StackG43":
            case "StackG44":
            case "StackG45":
            case "StackG46":
            case "StackG47":
               GUIGameBoard.this.actualGame = "Game4";
               break;
                            
                            
                 
        }
    }
    
       
 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CanceGame1;
    private javax.swing.JButton CancelGame2;
    private javax.swing.JButton CancelGame3;
    private javax.swing.JButton CancelGame4;
    private javax.swing.JPanel Game1;
    private javax.swing.JPanel Game2;
    private javax.swing.JPanel Game3;
    private javax.swing.JPanel Game4;
    private javax.swing.JButton HintGame1;
    private javax.swing.JButton HintGame2;
    private javax.swing.JButton HintGame3;
    private javax.swing.JButton HintGame4;
    private javax.swing.JButton LoadGame;
    private javax.swing.JPanel MainBoard;
    private javax.swing.JButton NewGame;
    private javax.swing.JButton SaveGame1;
    private javax.swing.JButton SaveGame2;
    private javax.swing.JButton SaveGame3;
    private javax.swing.JButton SaveGame4;
    private javax.swing.JButton ScoreG1;
    private javax.swing.JButton ScoreG2;
    private javax.swing.JButton ScoreG3;
    private javax.swing.JButton ScoreG4;
    private javax.swing.JLabel SourceDeckG1;
    private javax.swing.JLabel SourceDeckG2;
    private javax.swing.JLabel SourceDeckG3;
    private javax.swing.JLabel SourceDeckG4;
    private javax.swing.JLayeredPane StackG11;
    private javax.swing.JLayeredPane StackG12;
    private javax.swing.JLayeredPane StackG13;
    private javax.swing.JLayeredPane StackG14;
    private javax.swing.JLayeredPane StackG15;
    private javax.swing.JLayeredPane StackG16;
    private javax.swing.JLayeredPane StackG17;
    private javax.swing.JLayeredPane StackG21;
    private javax.swing.JLayeredPane StackG22;
    private javax.swing.JLayeredPane StackG23;
    private javax.swing.JLayeredPane StackG24;
    private javax.swing.JLayeredPane StackG25;
    private javax.swing.JLayeredPane StackG26;
    private javax.swing.JLayeredPane StackG27;
    private javax.swing.JLayeredPane StackG31;
    private javax.swing.JLayeredPane StackG32;
    private javax.swing.JLayeredPane StackG33;
    private javax.swing.JLayeredPane StackG34;
    private javax.swing.JLayeredPane StackG35;
    private javax.swing.JLayeredPane StackG36;
    private javax.swing.JLayeredPane StackG37;
    private javax.swing.JLayeredPane StackG41;
    private javax.swing.JLayeredPane StackG42;
    private javax.swing.JLayeredPane StackG43;
    private javax.swing.JLayeredPane StackG44;
    private javax.swing.JLayeredPane StackG45;
    private javax.swing.JLayeredPane StackG46;
    private javax.swing.JLayeredPane StackG47;
    private javax.swing.JLabel TargetDeckG11;
    private javax.swing.JLabel TargetDeckG12;
    private javax.swing.JLabel TargetDeckG13;
    private javax.swing.JLabel TargetDeckG14;
    private javax.swing.JLabel TargetDeckG21;
    private javax.swing.JLabel TargetDeckG22;
    private javax.swing.JLabel TargetDeckG23;
    private javax.swing.JLabel TargetDeckG24;
    private javax.swing.JLabel TargetDeckG31;
    private javax.swing.JLabel TargetDeckG32;
    private javax.swing.JLabel TargetDeckG33;
    private javax.swing.JLabel TargetDeckG34;
    private javax.swing.JLabel TargetDeckG41;
    private javax.swing.JLabel TargetDeckG42;
    private javax.swing.JLabel TargetDeckG43;
    private javax.swing.JLabel TargetDeckG44;
    private javax.swing.JButton UndoGame1;
    private javax.swing.JButton UndoGame2;
    private javax.swing.JButton UndoGame3;
    private javax.swing.JButton UndoGame4;
    private javax.swing.JLabel WasteDeckG1;
    private javax.swing.JLabel WasteDeckG2;
    private javax.swing.JLabel WasteDeckG3;
    private javax.swing.JLabel WasteDeckG4;
    // End of variables declaration//GEN-END:variables

    
   
public static void main(String args[]) {
     
    java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUIGameBoard board;
     
                    board = new GUIGameBoard();
                     board.setTitle("Solitaire");
                     board.setVisible(true);

            }
        });
       
    }


}

  


