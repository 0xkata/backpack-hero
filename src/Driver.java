import java.util.*;
import java.io.*;

import java.awt.*;
import javax.swing.*;


public class Driver {
    
    private static JFrame frame;
    private static JPanel panel;
    private static JPanel mapPanel;
    private static JPanel highscores;
    private static JButton button;
    private static JLabel label;

    private static BufferedReader read;
    private static StringTokenizer st;
    private static String line, s;

    private static int mouseX, mouseY;
    private static int score;
    
    private static int itemChosen;
    private static int stage;
    private static int[] chances;
    private static Item[] itemList;
    private static ArrayList<ArrayList<Room>> map = new ArrayList<>();

    private static Map<Integer, Integer> numFights;
    private static int numEnemies;
    private static Enemy[] enemies;
    private static boolean fighting = false;

    private static Hero hero;

    public Driver() {
        frame = new JFrame();
        panel = new JPanel();
    }

    public static void main(String[] args) throws IOException { 
        frame = new JFrame();

        frame.setPreferredSize(new Dimension(1100, 500));

        generateMap(0);

        frame.pack();
        frame.setVisible(true);
        frame.add(mapPanel);
    }

    // stage 0: 1 enemy, 1 chest room
    // stage 1: 4 enemies, 4 chest/shop, 2 heal/unknown
    // stage 2: 6 enemies, 4 chest/shop, 2 heal/unknown
    public static void generateMap(int stage) {
        mapPanel = new JPanel(new GridLayout(5, 11));

        map.clear();

        // start from (3, 3)
        // end room at (
        if (stage == 0) {
            updateMap(0);
        }
        // start from (0, 2)
        // end room at (10, 1)
        else if (stage == 1) {
            updateMap(1);
        }
        // start from (10, 2)
        // end room at (0, 1)
        else if (stage == 2) {
            updateMap(5);
        }
        // start from (5, 4)
        // end room at (4, 0)
        else {
            updateMap(9);
        }
    }

    public static void updateMap(int n) {
        try {
            read = new BufferedReader(new FileReader("map" + n + ".txt"));

            for (int i = 0; i < 5; ++i) {
                
                map.add(new ArrayList<>());

                line = read.readLine();
                st = new StringTokenizer(line);

                for (int j = 0; j < 11; ++j) map.get(i).add(new Room(Integer.parseInt(st.nextToken())));

            }

            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 11; ++j) mapPanel.add(new JLabel(map.get(i).get(j).getPic()));
            }
        }
        catch (Exception e) { System.out.println(e); }
    }

    public static void runEnemyMove(Enemy e, int nextMove) {
        int type = e.getPossibleMoves()[nextMove].getType();
        int value = e.getPossibleMoves()[nextMove].getValue();

        if (type == 1) {
            hero.setHp(hero.getHp() - value);
        }
        if (type == 2) {
            e.setArmor(e.getArmor() + value);
        }
        if (type == 3) {

        }
    }

    /**
     * Generate random number with the given range, both min and max are inclusive
     * @param min lower bound
     * @param max upper bound
     * @return a random number
     */
    public static int randomNum(int min, int max) {
        int range = max - min + 1;
        return (int) Math.random() * range + min;
    }
}
