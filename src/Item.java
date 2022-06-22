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
	private int energy;
	private int rotate = 0; //0 - no rotation; 1 - 90 degrees clockwise; 2 - 180 degrees clockwise; 3 - 270 degrees clockwise;
	private BufferedImage bipic;
	private BufferedImage[] rotatedPics = new BufferedImage[4];
	private String description; //the description of the item
	private Space[] rotations = new Space[4]; //stores the spatial orientation of the item for all 4 rotations of the item
	private Point loc = new Point (5,5); //the x,y location of the top left of the image drawn on screen
	private boolean inBag = false;
	
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
	
	Item(Identifier ID, String name, int rare, int size, BufferedImage pic, String effect, int energy, Space[] rotations){
		itemID = ID;
		realID = numberOfItems;
		numberOfItems++;
		itemName = name;
		rarity = rare;
		this.size = size;
		rotate = 0;
		this.bipic = pic;
		description = effect;
		this.energy = energy;
		this.rotations = rotations;
		rotatedPics[0] = bipic;
		for(int i = 1; i < 4; ++i) {
			rotatedPics[i] = rotate(rotatedPics[i-1]);
		}
	}
	Item(Item copy){
		itemID = copy.itemID;
		itemName = copy.itemName;
		realID = numberOfItems;
		numberOfItems++;
		size = copy.size;
		rarity = copy.rarity;
		rotate = copy.rotate;
		rotations = copy.rotations;
		bipic = copy.bipic;
		description = copy.description;
		energy = copy.energy;
		loc = copy.loc;
		inBag = copy.inBag;
		rotatedPics = copy.rotatedPics;
	}
	
	public void use() {
		//TODO: do various things depending on the itemID of the item
//		System.out.println(this.itemID);
		if(itemID.getPrim() == 3) {
			Main.enemyHP -= 2;
			System.out.println(Main.enemyHP);
		}
		Main.decreaseEnergy(energy);
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
	public String getName() {
		return itemName;
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
