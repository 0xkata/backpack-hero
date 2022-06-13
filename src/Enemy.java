import java.util.*;
import javax.swing.*;

public class Enemy extends Character {
    
    private int nextMove;
    private Move[] possibleMoves;

    public Enemy(int maxHP, Move[] moves) {
        super(maxHP, new ImageIcon());
        possibleMoves = moves;
        nextMove = pickNextMove();
    }

    int pickNextMove() {
        return (int) Math.random(0, this.possibleMoves.length);
    }

    void run() {

    }
}
