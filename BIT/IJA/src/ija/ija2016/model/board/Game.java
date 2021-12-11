/**
 * @file Game.java
 * @brief Implementation of game
 * @author Martin Marusiak
 */
package ija.ija2016.model.board;

import ija.ija2016.commands.CommandManager;
import ija.ija2016.commands.MoveDeckToWaste;
import ija.ija2016.commands.MoveTargetToWorking;
import ija.ija2016.commands.MoveWasteToTarget;
import ija.ija2016.commands.MoveWasteToWorking;
import ija.ija2016.commands.MoveWorkingToTarget;
import ija.ija2016.commands.MoveWorkingToWorking;
import ija.ija2016.commands.ReloadDeck;
import ija.ija2016.model.cards.Card;
import ija.ija2016.model.cards.CardDeck;
import ija.ija2016.model.cards.CardStack;
import java.io.File;
import java.io.IOException;

/**
 * Class representing game
 */
public class Game {
    
    protected AbstractFactorySolitaire factory = new FactoryKlondike();
    protected CommandManager commandManager = new CommandManager();

    /**
     * Get score
     * @return score
     */
    public Score getScore() {
        return score;
    }
    /**
     * Get card deck
     * @return card deck
     */
    public CardDeck getDeck() {
        return deck;
    }
    /**
     * Get waste deck
     * @return waste deck
     */
    public CardDeck getWaste() {
        return waste;
    }
    /**
     * Get target packs
     * @return target packs
     */
    public CardDeck[] getTargetPacks() {
        return targetPacks;
    }
    /**
     * Get working packs
     * @return working packs
     */
    public CardStack[] getWorkingPacks() {
        return workingPacks;
    }
        

    protected Score score;
    protected CardDeck deck;
    protected CardDeck waste;
    protected CardDeck[] targetPacks;
    protected CardStack[] workingPacks;
    protected Save savedGame;

    /**
     * Creates new game
     */
    public Game() {
        this.score = new Score(0);
        this.deck = factory.createCardDeck();
        this.waste = factory.createCardWaste();
        this.targetPacks = factory.createTargetPacks();
        this.workingPacks = factory.createWorkingPacks(this.deck);
        this.savedGame = new Save(this.deck, this.waste, this.targetPacks, this.workingPacks, this.score);
    }
    
    /**
     * Check if game is won
     * @return true if game is won false if not
     */
    public boolean won(){
        
        if(this.waste.isEmpty() && this.deck.isEmpty()){
            for (CardStack workingPack : this.workingPacks) {
                if (!workingPack.isEmpty()) {
                    return false;
                }
            } 
        } else {
            return false;
        }
        
        return true;
    }
    
    /**
     * Load game from file
     * @param file file where game is stored
     * @throws IOException game can not be loaded
     */
    public Game(File file) throws IOException {
        Load loadGame = new Load();
        if(!loadGame.load(file)){
            IOException e = new IOException();
            throw e;
        }
            
        this.score = loadGame.getScore();
        this.deck = loadGame.getDeck();
        this.waste = loadGame.getWaste();
        this.targetPacks = loadGame.getTargets();
        this.workingPacks = loadGame.getWorkingStacks();
        this.savedGame = new Save(this.deck, this.waste, this.targetPacks, this.workingPacks, this.score);
    }
    /**
     * Save game
     * @param filePath path to file
     * @return true if game was saved successfully, false otherwise
     */
    public boolean saveGame(String filePath){
        return this.savedGame.save(filePath);
    }
    /**
     * Get hint
     * @return hint in string representation
     */
    public String getPossibleMoves(){
        GetPossibleMoves moves = new GetPossibleMoves(this.deck, this.waste, this.targetPacks, this.workingPacks);
        return moves.getMoves();
    }
    
    /**
     * Move card from waste to deck
     * @return true in case of success, false otherwise
     */
    public boolean moveDeckToWaste(){
        return this.commandManager.execute(new MoveDeckToWaste(this.deck, this.waste));
    }
    /**
     * Move card from target pack to working stack
     * @param target target pack
     * @param working working stack
     * @return true in case of success, false otherwise
     */
    public boolean moveTargetToWorking(Pack target, Pack working){
        if(target.getPackType() != PackType.TARGET || working.getPackType() != PackType.WORKING)
            return false;
        return this.commandManager.execute(
                new MoveTargetToWorking(this.targetPacks[target.getIndex()], this.workingPacks[working.getIndex()], this.score));
    }
    /**
     * Move card from waste to target pack
     * @param target target pack
     * @return true in case of success, false otherwise
     */
    public boolean moveWasteToTarget(Pack target){
        if(target.getPackType() != PackType.TARGET)
            return false;
        return this.commandManager.execute(
                new MoveWasteToTarget(this.waste, this.targetPacks[target.getIndex()],score));
    }
    /**
     * Move card from waste deck to working stack
     * @param working working stack
     * @return true in case of success, false otherwise
     */
    public boolean moveWasteToWorking(Pack working){
        if(working.getPackType() != PackType.WORKING)
            return false;
        return this.commandManager.execute(
                new MoveWasteToWorking(this.waste, this.workingPacks[working.getIndex()],score));
    }
    /**
     * Move card from working stack to target pack
     * @param working working stack
     * @param target target pack
     * @return true in case of success, false otherwise
     */
    public boolean moveWorkingToTarget(Pack working, Pack target){
        if(target.getPackType() != PackType.TARGET || working.getPackType() != PackType.WORKING)
            return false;
        return this.commandManager.execute(
                new MoveWorkingToTarget(this.workingPacks[working.getIndex()],this.targetPacks[target.getIndex()],this.score));
    }
    /**
     * Move all card from given cards from one working pack to another
     * @param workingFrom working pack from which cards will be taken
     * @param workingTo working pack where cards will be put
     * @param card card from which other cards should be taken
     * @return true in case of success, false otherwise
     */
    public boolean moveWorkingToWorking(Pack workingFrom, Pack workingTo, Card card){
        if(workingFrom.getPackType() != PackType.WORKING || workingTo.getPackType() != PackType.WORKING)
            return false;
         return this.commandManager.execute(
                new MoveWorkingToWorking(this.workingPacks[workingFrom.getIndex()],this.workingPacks[workingTo.getIndex()],card,this.score));       
    }
    /**
     * Reload deck
     * @return true in case of success, false otherwise
     */
    public boolean reloadDeck(){
        return this.commandManager.execute(new ReloadDeck(this.deck, this.waste,this.score));
    }
    /**
     * Undo move
     * @return true in case of success, false otherwise
     */
    public boolean undo(){
        return this.commandManager.undo();
    }
    
    
}
