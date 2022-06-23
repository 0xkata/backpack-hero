//------------------------------------------------------------------------------
// @author       Anthony Sin
// Date          Unknown
// Description   Defining Enemy, extends from unit class. Holds all the moves and
//               other important variables
//------------------------------------------------------------------------------

import javax.swing.*;

public class Enemy extends Unit {
    
    // instance variables
    private int nextMove;
    private Move[] possibleMoves;

    // getters and setters
    public int getNextMove() {
        return this.nextMove;
    }

    public void setNextMove(int nextMove) {
        this.nextMove = nextMove;
    }

    public Move[] getPossibleMoves() {
        return this.possibleMoves;
    }

    public void setPossibleMoves(Move[] possibleMoves) {
        this.possibleMoves = possibleMoves;
    }


    // constructors
    public Enemy(int maxHP, ImageIcon i, Move[] moves) {
        super(maxHP, i);
        this.possibleMoves = moves;
        pickNextMove();
    }

    public Enemy(Enemy e) {
        super(e.maxHP, e.pic);
        this.possibleMoves = e.possibleMoves;
        pickNextMove();
    }

    /**
     * randomly pick next Move for the Enemy
     */
    public void pickNextMove() {
    	int rand = Main.rand(0, possibleMoves.length-1);
    	System.out.println("picked move: " + rand);
        this.nextMove = rand;
    }

}