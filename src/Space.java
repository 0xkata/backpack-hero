//defines a space for an item
//Roni Shae
//June 22, 2022

public class Space {
	char[][] grid = new char[4][4];
	int itemSize;
	private Pair2[] relative;
	Pair2 origin;
	
	//constructor
	Space(char[][] load, int size) {
		grid = load;
		itemSize = size;
		relative = new Pair2[itemSize-1];
	}

	//rotates the grid clockwise 90 degrees
	//no parameters
	//returns the rotated grid
	public char[][] rotate(){
		char[][] rotated = new char[4][4];
		for(int i = 0, f = 3; i < 4; ++i, --f) {
			for(int j = 0, k = 0; j < 4; ++j, ++k) {
				rotated[k][f] = grid[i][j];
			}
		}
		return rotated;
	}

	//updates the origin of the item
	//no parameters
	//returns void
	public void findOrigin() {
		for(int i = 0; i < 4; ++i) {
			for(int j = 0; j < 4; ++j) {
				if(grid[i][j] == 'a') origin = new Pair2(i, j);
			}
		}
	}
	
	//getters & setters
	public void setOrigin(Pair2 in) {
		origin = in;
	}
	public char[][] getGrid(){
		return grid;
	}
	public Pair2 getOrigin() {
		return origin;
	}
	public Pair2[] getRelative(){
		return relative;
	}
}
