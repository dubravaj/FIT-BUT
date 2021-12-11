/*
 */
package ija.ija2016;


import ija.ija2016.commands.Print;
import ija.ija2016.model.board.AbstractFactorySolitaire;
import ija.ija2016.model.board.Board;
import ija.ija2016.model.board.FactoryKlondike;
import ija.ija2016.model.board.Game;
import ija.ija2016.model.board.Pack;
import ija.ija2016.model.board.Score;
import ija.ija2016.model.cards.Card;
import ija.ija2016.model.cards.CardDeck;
import ija.ija2016.model.cards.CardStack;
import java.io.Console;
import java.io.File;


/**
 * IJA 2016/2017: Testovaci trida pro ukol c. 2.
 * @author koci
 */
public class Solitaire {

    
    public static void main(String[] args) throws InterruptedException {
        /*
            AbstractFactorySolitaire factory = new FactoryKlondike();
            
            CommandManager cm = new CommandManager();
        
            CardDeck deck = factory.createCardDeck();
            
            CardStack[] workingPacks = factory.createWorkingPacks(deck);
            
            Print p = new Print();
*
          //  workingPacks[2].get().turnFaceDown();
          //  workingPacks[2].forcePut(factory.createCard(Card.Color.HEARTS, 1));
         //   workingPacks[2].get().turnFaceUp();
            CardDeck[] targetPacks = factory.createTargetPacks();
      
                
            System.out.println(deck.size());
                            
            CardDeck waste = factory.createCardWaste();
            Score score = new Score(0);
            
            p.printState(targetPacks, deck, waste, workingPacks, score);
            p.printDeck(waste);

            Command moveToWaste = new MoveDeckToWaste(deck,waste);
            
            //cm.addCommand(moveToWaste);

            cm.execute(moveToWaste);
            p.printState(targetPacks, deck, waste, workingPacks, score);
            cm.execute(moveToWaste);
            p.printState(targetPacks, deck, waste, workingPacks, score);
            cm.execute(moveToWaste);
            p.printState(targetPacks, deck, waste, workingPacks, score);
            cm.undo();
            cm.undo();
            p.printState(targetPacks, deck, waste, workingPacks, score);
            cm.undo();
            p.printState(targetPacks, deck, waste, workingPacks, score);
            cm.execute(moveToWaste);
            cm.execute(moveToWaste);
            p.printState(targetPacks, deck, waste, workingPacks, score);
          //Command moveWasteToWP = new MoveWasteToWorking(waste, workingPacks[0], score);
           // cm.execute(moveWasteToWP);
          //  p.printState(targetPacks, deck, waste, workingPacks, score);
            
            Command[] moveWasteToWP = new Command[7];
            for(int i=0; i<7; i++)
                moveWasteToWP[i] = new MoveWasteToWorking(waste, workingPacks[i], score);
            Console console = System.console();
            String s = "";
            int w=0;
            */
            
            /*move to target from waste */
            
      /*
            Command[] moveWasteToT= new Command[4];
            for(int i=0; i < 4; i++){
                moveWasteToT[i] = new MoveWasteToTarget(waste, targetPacks[i], score);
            }
            */
            //relaod 
            /*
            Command reload = new ReloadDeck(deck, waste);
            Save save = new Save(deck, waste, targetPacks, workingPacks, score);
            */
             Print p = new Print();
             Console console = System.console();
            Board board = new Board();
            board.createGame(); // vracia boolean
          /*  board.createGame(); 
            board.createGame(); 
            board.createGame();
            System.out.println(board.getCurrentGames());
            board.cancelGame(2);*/
            Game game = board.getGame();
            System.out.println(board.getCurrentGames());
            int w = 0;  
             String s = "";

              AbstractFactorySolitaire factory = new FactoryKlondike();
            while(!"fokuMe".equals(s)){
                CardDeck[] targetPacks = game.getTargetPacks();
                CardDeck deck = game.getDeck();
                CardDeck waste = game.getWaste();
                CardStack[] workingPacks =game.getWorkingPacks();
                Score score = game.getScore();
                s = console.readLine();
                String[] splitted = s.split(" ");
                String c = splitted[0];
                p.printState(targetPacks, deck, waste, workingPacks, score);
                try{
                switch(c){
                    case "wa2w":
                            w = Integer.parseInt(splitted[1]);
                            game.moveWasteToWorking(Pack.valueOf("WORKING"+(w+1)));
                            //cm.execute(moveWasteToWP[w]);
                            break;
                    case "2wa":
                            //cm.execute(moveToWaste);
                            game.moveDeckToWaste();
                            break;
                    case "w2w":
                            int i = Integer.parseInt(splitted[1]);
                            int j = Integer.parseInt(splitted[2]);
                            String colorS = splitted[3];
                            Card.Color color = null;
                            switch(colorS){
                                case "S":
                                    color = Card.Color.SPADES;
                                    break;
                                case "D":
                                    color = Card.Color.DIAMONDS;
                                    break;
                                case "H":
                                    color = Card.Color.HEARTS;
                                    break;
                                case "C":
                                    color = Card.Color.CLUBS;
                                    break;
                            }
                            int val = Integer.parseInt(splitted[4]);
                            //cm.execute(new MoveWorkingToWorking(workingPacks[i],workingPacks[j],factory.createCard(color, val),score));                    
                            game.moveWorkingToWorking(Pack.valueOf("WORKING"+(i+1)),Pack.valueOf("WORKING"+(j+1)),factory.createCard(color, val));
                            break; 
                    case "wa2t":
                        w = Integer.parseInt(splitted[1]);
                       // cm.execute(moveWasteToT[w]);
                        game.moveWasteToTarget(Pack.valueOf("TARGET"+(w+1)));
                        break;
                    case "w2t":
                        i = Integer.parseInt(splitted[1]);
                        j = Integer.parseInt(splitted[2]);
                        //cm.execute(new MoveWorkingToTarget(workingPacks[i], targetPacks[j], score));
                        game.moveWorkingToTarget(Pack.valueOf("WORKING"+(i+1)),Pack.valueOf("TARGET"+(j+1)));
                        break;
                    case "t2w":
                        i = Integer.parseInt(splitted[1]);
                        j = Integer.parseInt(splitted[2]);
                       // cm.execute(new MoveTargetToWorking(targetPacks[j],workingPacks[i], score));                       
                        game.moveTargetToWorking(Pack.valueOf("TARGET"+(i+1)),Pack.valueOf("WORKING"+(j+1)));
                    case "r":
                           /// cm.execute(reload);
                            game.reloadDeck();
                            break;
                    case "s":
                            //save.save();
                            game.saveGame(splitted[1]);
                            break;
                    case "l":
                                File f = new File(splitted[1]);
                                board.createGame(f); // vracia boolean
                                game = board.getGame();
                   
                            break;
                    case "g":
                            i = Integer.parseInt(splitted[1]);
                            game = board.getGame(i);
                            break;
                    case "m":
                        System.out.println(game.getPossibleMoves());
                        
                        break;
                    case "u":
                            //cm.undo();
                            game.undo();
                            break;
                }}
                catch (ArrayIndexOutOfBoundsException e){
                    System.out.println("\n--ARR-\n");
                }
                p.printState(targetPacks, deck, waste, workingPacks, score);
           
            }
            System.out.println("louPe");
            
            
    }
}
