public class Space {
	char[][] grid = new char[4][4];
	int itemSize;
	private Pair2[] relative;
	Pair2 origin;
	
	Space(char[][] load, int size) {
		grid = load;
		itemSize = size;
		relative = new Pair2[itemSize-1];
	}
	public Pair2[] getRelative(){
		return relative;
	}
	public void print() {
		for(int i = 0; i < 4; ++i) {
			for(int j = 0; j < 4; ++j) {
				System.out.print(grid[i][j]);
			}
			System.out.println();
		}
	}
	public void setOrigin(Pair2 in) {
		origin = in;
	}
	public Pair2 getOrigin() {
		return origin;
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
	public char[][] getGrid(){
		return grid;
	}
	public void findOrigin() {
		for(int i = 0; i < 4; ++i) {
			for(int j = 0; j < 4; ++j) {
				if(grid[i][j] == 'a') origin = new Pair2(i, j);
			}
		}
	}
}
