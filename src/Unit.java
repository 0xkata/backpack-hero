import javax.swing.*;

public abstract class Unit {
    
    /*

        0 poison
        1 regeneration
        2 spikes 
        3 rage
        4 weak

     */
	
    protected int hp;
    protected int maxHP;
    protected int armor;
    protected int[] status;
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

    public int[] getStatus() {
        return this.status;
    }

    public void setStatus(int[] status) {
        this.status = status;
    }

    public ImageIcon getPic() {
        return this.pic;
    }

    public void setPic(ImageIcon pic) {
        this.pic = pic;
    }

    public void changeHP(int n) { //make negative values subtract from armor first, need to consider the rage and weak of the person who caused the hp change
        if(n > 0) {
            this.hp += n;
            if (this.hp > this.maxHP) this.hp = this.maxHP;
        }
        else {
        	int pierced = armor + n;
        	armor += n;
        	if(armor < 0) armor = 0;
        	if(pierced < 0) this.hp += pierced;
        }
    }

    public void pierceHP(int n) {
    	this.hp += n;
    }
    public void changeArmor(int n) {
        this.armor += n;
    }

    public Unit(int maxHP, ImageIcon i) {
        this.hp = maxHP;
        this.maxHP = maxHP;
        this.armor = 0;
        this.status = new int[5];
        this.pic = i;
    }

    public boolean alive() {
        return this.hp > 0;
    }
    public void tick() {
    	changeHP(status[1]); //heal from regen
    	pierceHP(-status[0]); //damage from poison (goes through armor)
    	for(int i = 0; i < status.length; ++i) { //remove 1 from each status effect
    		status[i] = Math.max(status[i]-1, 0); //make sure it doesn't go negative
    	}
    	armor = 0; //reset armor to 0
    }
}