public class Move {
    
    /*
        possible moves (types)
      
        1 damage stage 1
        2 add armor
        3 add poison
        4 add slow
        5 escape
        6 (Boss) sommon enemies
     */

    private int type;
    private int value;

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Move(int a, int b) {
        this.type = a;
        this.value = b;
    }
}
