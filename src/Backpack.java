//June 9, 2022
//Roni Shae
//The state of the backpack and all the things it contains
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class Backpack {

	private boolean[][] unlocked = new boolean[5][7]; //the squares the user has access to
	private Item[][] contents = new Item[5][7]; //the items in the bag

	//Constructor
	Backpack() {
		try {
			//initializing the unlocked squares
			BufferedReader br = makeReader("Original Locked Spaces.txt");
			for(int i = 0; i < 5; ++i) {
				StringTokenizer st = new StringTokenizer(br.readLine());
				for(int j = 0; j < 7; ++j) {
					if(st.nextToken().equals("1")) unlocked[i][j] = true;
				}
			}
			br.close();
		}
		catch(IOException e) {
			System.out.println("IOException in Backpack()");
		}
		//filling with empties
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < 7; ++j) {
				addItem(j, i, new Item(Main.iMap.get(new Identifier(0,'a'))));
			}
		}
	}

	//Convenience method that makes a file reader for a given file; Returns: the file reader; file: the name of the file (include .txt)
	private static BufferedReader makeReader(String file) {
		try {
			return new BufferedReader(new FileReader(file));
		}
		catch(FileNotFoundException e) {
			System.out.println(file);
		}
		return new BufferedReader(null);
	}

	//getters and setters
	public Item[][] getContents(){
		return this.contents;
	}
	public boolean[][] getUnlocked(){
		return unlocked;
	}
	public void setUnlocked(int first, int second, boolean v) {
		unlocked[first][second] = v;
	}
	public void addItem(int xTile, int yTile, Item addition) {
		contents[yTile][xTile] = addition;
	}
}
