//------------------------------------------------------------------------------
// @author       Anthony Sin
// Date          Unknown
// Description   Specially made for the bfs method in Main to store the path,
//               also easier access the coordinates
//------------------------------------------------------------------------------
public class Pair {
    
    // instance variables
    private int row;
    private int col;
    private int prev_row;
    private int prev_col;

    // getters and setters
    public int getRow() {
        return this.row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return this.col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getPrev_row() {
        return this.prev_row;
    }

    public void setPrev_row(int prev_row) {
        this.prev_row = prev_row;
    }

    public int getPrev_col() {
        return this.prev_col;
    }

    public void setPrev_col(int prev_col) {
        this.prev_col = prev_col;
    }

    // equals method, comparing the coordinates
    public boolean equals(Object o) { 
        Pair p = (Pair) o;
        return this.row == p.row && this.col == p.col;
    }

    // toString
    public String toString() {
        return String.format("%d, %d", this.row, this.col);
    }

    // constructors
    public Pair(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Pair(int row, int col, int prev_row, int prev_col) {
        this.row = row;
        this.col = col;
        this.prev_row = prev_row;
        this.prev_col = prev_col;
    }
}
