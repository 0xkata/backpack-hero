//------------------------------------------------------------------------------
// @author       Anthony Sin
// Date          Unknown
// Description   I thought this wasn't necessary, but linking three variables is too 
//               much work, creating a class for it is the most convenient. Also 
//               implemented Comparable to sort the rarity. 
//------------------------------------------------------------------------------
import javax.swing.*;

public class ShopButton implements Comparable<ShopButton> {
    
    // instance variables
    private ImageIcon pic;
    private int rarity;
    private int price;

    // getters and setters
    public ImageIcon getPic() {
        return this.pic;
    }

    public void setPic(ImageIcon pic) {
        this.pic = pic;
    }

    public int getRarity() {
        return this.rarity;
    }

    public void setRarity(int rarity) {
        this.rarity = rarity;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    // compareTo for sorting
    public int compareTo(ShopButton o) {
        return this.price - o.price;
    }

    // constructor
    public ShopButton(ImageIcon i, int rarity, int price) {
        this.pic = i;
        this.rarity = rarity;
        this.price = price;
    }

}
