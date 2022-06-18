import javax.swing.*;

public class Room {

     /*
        0 = empty
        1 = path
        2 = chest
        3 = shop
        4 = healer
        5 = troll
        6 = boss room
        7 = start room
        8 = next stage
        9 = win
        Enemies starting at 1X, X indicates the number of enemies.
    */

    private int type;
    private ImageIcon pic;

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ImageIcon getPic() {
        return this.pic;
    }

    public void setPic(ImageIcon pic) {
        this.pic = pic;
    }

    public String toString() {
        return this.type + " " + this.pic;
    }

    public Room(int type) {
        this.type = type;
        if (type > 10) this.pic = new ImageIcon("room10.png");
        else if (type > 7) this.pic = new ImageIcon("room8.png");
        else this.pic = new ImageIcon("room" + type + ".png");
    }
}