/*
 */
package ija.ija2016.homework2;
/*
import ija.ija2016.homework2.commands.Command;
import ija.ija2016.homework2.commands.CommandManager;
import ija.ija2016.homework2.commands.MoveToWasteCommand;
import ija.ija2016.homework2.commands.MoveWasteCardToWorkingPack;
import ija.ija2016.homework2.model.board.AbstractFactorySolitaire;
import ija.ija2016.homework2.model.board.FactoryKlondike;
import ija.ija2016.homework2.model.cards.Card;
import ija.ija2016.homework2.model.cards.CardDeck;
import ija.ija2016.homework2.model.cards.CardStack;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
*/
/**
 * IJA 2016/2017: Testovaci trida pro ukol c. 2.
 * @author koci
 *//*
public class MyTest {
    
    protected AbstractFactorySolitaire factory;

    @Before
    public void setUp() {
         factory = new FactoryKlondike();
    }
    
    @After
    public void tearDown() {
    }
    
    public class Print{
        public void printDeck(CardDeck deck){
            int i=1;
            Card card = deck.get(0);
           // System.out.println("==============");
            int size = deck.size();
            if(card != null)
                while(i <= size){              
                 System.out.print(card.toString()+"\t"+card.isTurnedFaceUp()+"\t");
                 card = deck.get(i);
                    i++;
                    if((i - 1) % 6 == 0){
                        System.out.println();
                    }
                }   
           // System.out.println("\n-------M-------\n");
        }
        
        public void printStack(CardStack stack){
     
          //  System.out.println("==============");
            int size = stack.size();
            Card card;
            for(int i=size-1; i >= 0; i--){
                card = stack.get(i);
                System.out.print(card.toString()+"\t"+card.isTurnedFaceUp()+"\t");
            }
           // System.out.println("\n-------M-------\n");
        }
        
        public void printState(CardDeck[] targets, CardDeck deck, CardDeck waste, CardStack[] stack, Integer score){
            
            System.out.println("===============================================");
            System.out.println("S: ");
            this.printDeck(targets[0]);
            System.out.println("D: ");
            this.printDeck(targets[1]);
            System.out.println("H: ");
            this.printDeck(targets[2]);
            System.out.println("C: ");
            this.printDeck(targets[3]);
            System.out.print('\n');
            for(int i=0; i<7; i++){
                int size = stack[i].size();
                Card card;
                for(int j=size-1; j >= 0; j--){
                    card = stack[i].get(j);
                    System.out.print(card.toString()+"\t"+card.isTurnedFaceUp()+"\t");
                } 
                System.out.print('\n');
            }
            System.out.print('\n');
            this.printDeck(deck);
            System.out.print('\n');
            System.out.print('\n');
            this.printDeck(waste);
            System.out.print('\n');
            System.out.println(score);
            System.out.println("\n---------------------M------------------------\n");
            if(false)
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MyTest.class.getName()).log(Level.SEVERE, null, ex);
                }

        }
    }
*//*
    @Test
    public void testCard() throws IOException {
        
            CommandManager cm = new CommandManager();
        
            CardDeck deck = factory.createCardDeck();
            
            CardStack[] workingPacks = new CardStack[7];
            
            MyTest.Print p = new MyTest.Print();
            p.printDeck(deck);
            
            //filling working packs
            for(int i=0; i < 7; i++){
                workingPacks[i] = factory.createWorkingPack();
                for(int j=0; j < i + 1; j++){
                    workingPacks[i].forcePut(deck.pop());
                }
                p.printStack(workingPacks[i]);
                workingPacks[i].get(0);
            }
            CardDeck[] targetPacks = new CardDeck[4];
            
            targetPacks[0] = factory.createTargetPack(Card.Color.SPADES);
            targetPacks[1] = factory.createTargetPack(Card.Color.DIAMONDS);
            targetPacks[2] = factory.createTargetPack(Card.Color.HEARTS);
            targetPacks[3] = factory.createTargetPack(Card.Color.CLUBS);
                
            System.out.println(deck.size());
                            
            CardDeck waste = factory.createCardWaste();
            Integer score = 0;
            p.printState(targetPacks, deck, waste, workingPacks, score);
            p.printDeck(waste);

            Command moveToWaste = new MoveToWasteCommand(deck,waste);
            
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
            Command moveWasteToWP = new MoveWasteCardToWorkingPack(waste, workingPacks[0], score);
            cm.execute(moveWasteToWP);
            p.printState(targetPacks, deck, waste, workingPacks, score);
            
            
          //  Console console = System.console();
           // String s = console.readLine();
            
            
    }
}*/
