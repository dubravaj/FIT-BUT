/**
 * @file Score.java
 * @brief Implementation of score
 * @author Martin Marusiak
 */
package ija.ija2016.model.board;
/**
 * Class represing score of game 
 */
public class Score {
    int score;
    /**
     * Constructor of score
     * @param score initial value of score
     */
    public Score(int score){
        this.score = score;
    }
    /**
     * Constructor without parameters, value of score will be setted to -1
     */
    public Score(){
        this.score = -1;
    }
    /**
     * Get value of score
     * @return value of score
     */
    public int getScore(){
        return this.score;
    }
    /**
     * Set value of score
     * @param score value of score
     */
    public void setScore(int score) {
        this.score = score;
    }
    /**
     * Add value to score
     * @param value value by which score will be increased
     */
    public void addScore(int value){
        this.score += value;
        if(this.score < 0)
            this.score = 0;
    }
    
}
