import javax.swing.*;

public class Driver {
    
    static JFrame myFrame;
    static JPanel panel;
    static JPanel highscores;
    static JButton button;
    static JLabel label;
    
    static int mouseX, mouseY;
    static int score;
    
    static int itemChosen;
    static int stage;
    static int[] chances;
    static Item[] itemList;


    public int randomNum(int low, int up) {
        return (int) Math.random() * up + low + 1;
    }
}
