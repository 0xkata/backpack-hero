import javax.swing.*;

public class Enemy extends Character {
    
    private int nextMove;
    private Move[] possibleMoves;

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

    public Enemy(int maxHP, Move[] moves) {
        super(maxHP, new ImageIcon());
        this.possibleMoves = moves;
        this.nextMove = pickNextMove();
    }

    public int pickNextMove() {
        return Driver.randomNum(0, possibleMoves.length);
    }


    // will escape if only cowards remain
}