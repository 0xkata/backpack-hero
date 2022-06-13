import javax.swing.*;

abstract class Character {
    
    protected int hp;
    protected int maxHP;
    protected int arm;
    protected int status;
    protected ImageIcon pic;

    public Character(int maxHP, ImageIcon i) {
        this.hp = maxHP;
        this.maxHP = maxHP;
        this.arm = 0;
        this.status = 0;
        this.pic = i;
    }

    public boolean alive() {
        return this.hp <= 0;
    }
}