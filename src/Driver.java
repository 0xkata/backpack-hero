import java.util.*;
import java.io.*;

import java.awt.*;
import javax.swing.*;


public class Driver {
    
    private static JFrame frame;
    private static JPanel mainPanel;
    private static JPanel mapPanel;
    private static JPanel fightPanel;
    private static JPanel highscores;
    private static JButton button;
    private static JLabel label;

    private static BufferedReader read;
    private static StringTokenizer st;
    private static String line;

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
        mainPanel = new JPanel();
    }

    public static void main(String[] args) throws IOException { 
        frame = new JFrame();

        frame.setPreferredSize(new Dimension(1100, 500));

        generateMap(3);

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

        if (stage == 0) {
            updateMap(0);
        }
        else if (stage == 1) {
            updateMap(1);
        }
        else if (stage == 2) {
            updateMap(4);
        }
        else {
            updateMap(7);
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

    public static void updateMapPanel() {
        mapPanel.removeAll();

        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 11; ++j) mapPanel.add(new JLabel(map.get(i).get(j).getPic()));
        }
    }

    public static void fight(int stage, Room r) {
        // pick item
        for (Enemy e : enemies) runEnemyMove(e);

        // update panel
        boolean check = false;
        for (Enemy e : enemies) if (e.alive()) check = true;
        if (!check) stageWin(r);
    }

    public static void updateFightPanel() {
        fightPanel.removeAll();

        // hud stuff
    }

    public static void stageWin(Room r) {
        hero.checkLevelUP();
        r.clear();
    }

    public static void runEnemyMove(Enemy e) {
        int type = e.getPossibleMoves()[e.getNextMove()].getType();
        int value = e.getPossibleMoves()[e.getNextMove()].getValue();

        switch (type) {

            case 1: hero.setHp(hero.getHp() - value);
        
            case 2: e.setArmor(e.getArmor() + value);

            // case 3: hero.getStatus()[poison index] = value;

            // case 4: hero.getStatus()[slow index] = value;
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
