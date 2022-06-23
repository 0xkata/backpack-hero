import javax.swing.*;

public class Enemy extends Unit {
    
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

    public Enemy(int maxHP, ImageIcon i, Move[] moves) {
        super(maxHP, i);
        this.possibleMoves = moves;
        this.nextMove = pickNextMove();
    }

    public int pickNextMove() {
        return Driver.randomNum(0, possibleMoves.length);
    }
    
    public Enemy(Enemy e) {
        super(e.maxHP, e.pic);
        this.possibleMoves = e.possibleMoves;
        this.nextMove = pickNextMove();
    }

}