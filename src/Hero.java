import javax.swing.*;

public class Hero extends Character {
    
    private int xp;
    private int[] maxXP = { 10, 15, 20 };
    private int energy;
    private int maxEnergy;
    private int level;

    public int getXp() {
        return this.xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int[] getMaxXP() {
        return this.maxXP;
    }

    public void setMaxXP(int[] maxXP) {
        this.maxXP = maxXP;
    }

    public int getEnergy() {
        return this.energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getMaxEnergy() {
        return this.maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    
    public Hero(ImageIcon i) {
        super(35, i);
        this.xp = 0;
        this.level = 1;
    }

    public void checkLevelUP() {
        if (this.xp >= this.maxXP[this.level - 1]) {
            this.xp = 0;
            this.level++;
            this.maxHP += 5;
        }
    }
}
