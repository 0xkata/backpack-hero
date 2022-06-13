import javax.swing.*;

public class Enemy extends Character {
    
    private int nextMove;
    private Move[] possibleMoves;

    public Enemy(int maxHP, Move[] moves) {
        super(maxHP, new ImageIcon());
        this.possibleMoves = moves;
        this.nextMove = pickNextMove();
    }

    int pickNextMove() {
        return Driver.randomNum(0, possibleMoves.length);
    }

    void run() {
        
    }
}
