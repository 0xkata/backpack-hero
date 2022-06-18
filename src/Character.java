import javax.swing.*;

public abstract class Character {
    
    protected int hp;
    protected int maxHP;
    protected int armor;
    protected int status;
    protected ImageIcon pic;

    public int getHp() {
        return this.hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMaxHP() {
        return this.maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public int getArmor() {
        return this.armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ImageIcon getPic() {
        return this.pic;
    }

    public void setPic(ImageIcon pic) {
        this.pic = pic;
    }

    public Character(int maxHP, ImageIcon i) {
        this.hp = maxHP;
        this.maxHP = maxHP;
        this.armor = 0;
        this.status = 0;
        this.pic = i;
    }

    public boolean alive() {
        return this.hp <= 0;
    }
}