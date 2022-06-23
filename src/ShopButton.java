import javax.swing.*;

public class ShopButton implements Comparable<ShopButton> {
    
    private ImageIcon pic;
    private int rarity;
    private int price;

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

    public int compareTo(ShopButton o) {
        return this.price - o.price;
    }

    public ShopButton(ImageIcon i, int rarity, int price) {
        this.pic = i;
        this.rarity = rarity;
        this.price = price;
    }

}
