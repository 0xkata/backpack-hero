import javax.swing.*;

public class Hero extends Character {
    
    private int xp;
    private int[] maxXP = {10, 15, 20};
    private int level;

    public Hero() {
        super(15, new ImageIcon());
        this.xp = 0;
        this.level = 1;
    }

    public void levelUP() {
        if (this.xp >= this.maxXP[this.level - 1]) {
            this.xp = 0;
            this.level++;
        }
    }
}
