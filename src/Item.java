//April 29, 2022
//Roni Shae
//Defining an Item
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class Item {
	private Identifier itemID; //unique ID for each type of item
	private static int numberOfItems = 0; //the number of items
	private int realID; //unique ID for every item created
	private String itemName; //unique name for each item
	private int rarity; //0 - common; 1 - uncommon; 2 - rare; 3 - legendary; 4 - relic
	private int size;
	private String type; //the class of item that this is
	private int energy;
	private int rotate = 0; //0 - no rotation; 1 - 90 degrees clockwise; 2 - 180 degrees clockwise; 3 - 270 degrees clockwise;
	private BufferedImage bipic;
	private BufferedImage[] rotatedPics = new BufferedImage[4];
	private String description; //the description of the item
	private Space[] rotations = new Space[4]; //stores the spatial orientation of the item for all 4 rotations of the item
	private Point loc = new Point (5,5); //the x,y location of the top left of the image drawn on screen
	private boolean inBag = false;
	private int yBagPos;
	private int xBagPos;
	private boolean oncePerTurn = false;
	private boolean used = false;
	public String toString() {
		return String.format("ID: %d; Component: %s; realID: %d; Name: %s; Rarity: %d; Size: %d; Description: %s", itemID.getPrim(), ""+itemID.getSupp(),realID, itemName, rarity, size, description);
	}
	
	//https://www.geeksforgeeks.org/java-program-to-rotate-an-image/
	private static BufferedImage rotate(BufferedImage img) {
        // Getting Dimensions of image
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D g2 = newImage.createGraphics();

        g2.rotate(Math.toRadians(90), width / 2, height / 2);
        g2.drawImage(img, null, 0, 0);
 
        return newImage;
    }
	
	Item(Identifier ID, String name, int rare, int size, BufferedImage pic, String effect, int energy, String type, Space[] rotations){
		itemID = ID;
		realID = numberOfItems;
		numberOfItems++;
		itemName = name;
		rarity = rare;
		this.size = size;
		rotate = 0;
		this.bipic = pic;
		description = effect;
		this.type = type;
		this.energy = energy;
		this.rotations = rotations;
		rotatedPics[0] = bipic;
		for(int i = 1; i < 4; ++i) {
			rotatedPics[i] = rotate(rotatedPics[i-1]);
		}
		xBagPos = -1;
		yBagPos = -1;
		oncePerTurn = itemID.getPrim() == 10 || itemID.getPrim() == 12 || itemID.getPrim() == 14;
	}
	Item(Item copy){
		oncePerTurn = copy.oncePerTurn;
		itemID = copy.itemID;
		itemName = copy.itemName;
		realID = numberOfItems;
		numberOfItems++;
		size = copy.size;
		rarity = copy.rarity;
		rotate = copy.rotate;
		rotations = copy.rotations;
		type = copy.type;
		bipic = copy.bipic;
		description = copy.description;
		energy = copy.energy;
		loc = copy.loc;
		inBag = copy.inBag;
		rotatedPics = copy.rotatedPics;
		xBagPos = copy.xBagPos;
		yBagPos = copy.yBagPos;
	}
	
	public void setUsed(boolean v) {
		used = v;
	}
	public void use() {
		//TODO: do various things depending on the itemID of the item
		int rage = Main.getHero().getStatus()[3];
		int weak = Main.getHero().getStatus()[4];
		int bottleArmor = 1;
		int bottleDmg = 0;
		int citrineDmg = 0;
		if(type.equals("Consumable")) {
			Main.retrieveItem(yBagPos, xBagPos, false);
			Main.purge();
		}
		else if(type.equals("Weapon")) { 

		}
		else if(type.equals("Shield")) {
			if(Main.bagHasItem(17)) Main.getHero().getStatus()[5] += 2; //checking for presence of coral, adding 2 spike
		}
		citrineDmg = -3*numAdjacent(11); //checking for adjacent Citrine
		if(Main.bagHasItem(16)) { //checking for glass bottle presence
			System.out.println("bottle");
			bottleDmg = -6; 
			bottleArmor = 0;
		}

		if(Main.getEnergy() - energy < 0) {
			System.out.println("not enough energy!");
			return; //if it would use more energy than we have, stop.
		}
		if(oncePerTurn && used) {
			System.out.println("already used this turn");
			return; //stop if this is a once per turn item and it has already been used
		}
		Main.decreaseEnergy(energy);
		int typeID = this.itemID.getPrim();
		System.out.println(typeID);
		System.out.println("selected enemy: "+Main.getSelectedEnemy());
//		System.out.println(typeID); 
		if(typeID == 0) {
			System.out.println("Empty was used.");
		}
		else if(typeID == 2) { //Bluefin
			Main.getHero().changeHP(12);
		}
		else if(typeID == 3) { //Club
			int damage = Math.min(0, -7+bottleDmg+citrineDmg-rage+weak);
			Main.getEnemies()[Main.getSelectedEnemy()].changeHP(damage);
			Main.getEnemies()[Main.getSelectedEnemy()].getStatus()[4]++;
		}
		else if(typeID == 4) { //Cleaver
			int damage = Math.min(0, -3+bottleDmg+citrineDmg-rage+weak);
			Main.getEnemies()[Main.getSelectedEnemy()].changeHP(damage);
		}
		else if(typeID == 5) { //Wood Sword
			int damage = Math.min(0, -8+bottleDmg+citrineDmg-rage+weak);
			Main.getEnemies()[Main.getSelectedEnemy()].changeHP(damage);
		}
		else if(typeID == 6) { //Tough Buckler
			Main.getHero().changeArmor(7*bottleArmor);
		}
		else if(typeID == 7) { //Meal
			Main.increaseEnergy(2);
		}
		else if(typeID == 9) { //My First Wand
			int damage = Math.min(0, -6+bottleDmg+citrineDmg-rage+weak);
			Main.getEnemies()[Main.getSelectedEnemy()].changeHP(damage);
			Main.getHero().changeHP(2);
		}
		else if(typeID == 10) { //Golden Dagger
			int damage = Math.min(0,-2+bottleDmg+citrineDmg-rage+weak);
			Main.getEnemies()[Main.getSelectedEnemy()].changeHP(damage);
			//TODO add gold
		}
		else if(typeID == 12) { //Dagger
			int damage = Math.min(0,-2+bottleDmg+citrineDmg-rage+weak);
			Main.getEnemies()[Main.getSelectedEnemy()].changeHP(damage);
			Main.getEnemies()[Main.getSelectedEnemy()].getStatus()[0] += 8;
		}
		else if(typeID == 13) { //Li'l Buckler
			Main.getHero().changeArmor(8*bottleArmor);
		}
		else if(typeID == 14) { //Hatchet
			int damage = -5;
			if(Main.bagHasArmor()) damage += 4;
			damage = Math.min(0, damage+bottleDmg+citrineDmg-rage+weak);
			Main.getEnemies()[Main.getSelectedEnemy()].changeHP(damage);
		}
		else if(typeID == 15) { //Rare Herb
			Main.getHero().setMaxHP(Main.getHero().getMaxHP()+3);
		}
		else if(typeID == 18) { //Rapier
			int damage = Math.min(0,-25+bottleDmg+citrineDmg-rage+weak);
			Main.getEnemies()[Main.getSelectedEnemy()].pierceHP(damage);
		}
		
		if(oncePerTurn) used = true;
		
		Main.setStopFight(Main.checkEnemies());
	}
	public void auto() {
		int typeID = this.itemID.getPrim();
		int bottleArmor = 1;
		int bottleDmg = 0;
		if(Main.bagHasItem(16)) { //checking for glass bottle presence
			bottleDmg = -6; 
			bottleArmor = 0;
		}
		
		if(typeID == 1) { //Iron Helmet
			int armor = 2;
			if(yBagPos > 0) {
				if(Main.getBag().getUnlocked()[yBagPos-1][xBagPos]) armor -= 2;
			}
			Main.getHero().changeArmor(armor*bottleArmor);
		}
		else if(typeID == 8) { //Rose of Thorns
			Main.getHero().getStatus()[2]++;
		}
	}
	public int numAdjacent(int id) {
		int out = 0;
		int x = xBagPos;
		int y = yBagPos;
		for(int i = 0; i < 4; ++i) {
			Pair2 cur = ortho[i];
			if(Main.inBagBounds(x+cur.getFirst(), y+cur.getSecond())) {
				if(Main.getBag().getContents()[x+cur.getFirst()][y+cur.getSecond()].getIdentifier().getPrim() == id) out++;
			}
		}
		for(int j = 0; j < rotations[rotate].getRelative().length; ++j) {
			x += rotations[rotate].getRelative()[j].getFirst();
			y += rotations[rotate].getRelative()[j].getSecond();
			for(int i = 0; i < 4; ++i) {
				Pair2 cur = ortho[i];
				if(Main.inBagBounds(x+cur.getFirst(), y+cur.getSecond())) {
					if(Main.getBag().getContents()[x+cur.getFirst()][y+cur.getSecond()].getIdentifier().getPrim() == id) out++;
				}
			}
			x = xBagPos;
			y = yBagPos;
		}
		return out;
	}
	private Pair2[] ortho = {new Pair2(-1, 0), new Pair2(1, 0), new Pair2(0, -1), new Pair2(0, 1)}; 
	public void setY(int y) {
		yBagPos = y;
	}
	public void setX(int x) {
		xBagPos = x;
	}
	public int getY() {
		return yBagPos;
	}
	public int getX() {
		return xBagPos;
	}
	public void rotate(int i) {
		rotate += i;
		rotate = (rotate%4);
	}
	public Space[] getRotations() {
		return rotations;
	}
	public boolean getInBag() {
		return inBag;
	}
	public String getType() {
		return type;
	}
	public String getDescription() {
		return description;
	}
	public String getName() {
		return itemName;
	}
	public Point getLoc() {
		return this.loc;	
	}
	public void setInBag(boolean v) {
		inBag = v;
	}
	public BufferedImage getPic() {
		return rotatedPics[rotate];
	}
	public void setRealID(int id) {
		this.realID = id;
	}
	public int getRealID() {
		return this.realID;
	}
	public Identifier getIdentifier() {
		return this.itemID;
	}
	
	public int getEnergy() {
		return this.energy;
	}
	public int getRotate() {
		return rotate;
	}
	public int getSize() {
		return this.size;
	}
	public void setPoint(Point p) {
		this.loc = p;
	}
	public Point getPoint() {
		return this.loc;
	}
	public void changePoint(int x, int y) {
		loc.x += x;
		loc.y += y;
	}
}