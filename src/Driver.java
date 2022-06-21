import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Driver implements Runnable, KeyListener, MouseListener {
    
    private static JFrame frame;
    private static JPanel mainPanel;
    private static MapPanel mapPanel;
    private static JPanel fightPanel;
    private static JPanel highscores;
    private static JButton button;
    private static JLabel label;

    private static BufferedReader read;
    private static StringTokenizer st;
    private static String line;

    private static int screen = 0; // 0 menu 1 high score 2 help 3 map 4 game
    private static int score;
    
    private static int itemChosen;
    private static int stage;
    private static int[] chances;
    private static ArrayList<Item> itemList = new ArrayList<>();
    private static ArrayList<Enemy> enemyList = new ArrayList<>();
    private static ArrayList<ArrayList<Room>> map = new ArrayList<>();

    private static ImageIcon movingIcon;
    private static Pair movingCoord;
    private static Pair destCoord;
    private static Pair currentRoom;
    private static boolean[][] visited = new boolean[5][11];
    private static LinkedList<Pair> path = new LinkedList<>();
    private static boolean moving;

    private static Map<Integer, Integer> numFights;
    private static int numEnemies;
    private static Enemy[] enemies;
    private static boolean fighting = false;

    private static Hero hero;

    public Driver() {
        frame = new JFrame();
        mainPanel = new JPanel();

        mapPanel = new MapPanel();
        mapPanel.addMouseListener(this);
        enemyList.add(new Enemy(30, new ImageIcon("Snake.png"), new Move[]{new Move(1, 5), new Move(2, 7)}));
        enemyList.add(new Enemy(20, new ImageIcon("Hyena.png"), new Move[]{new Move(1, 6), new Move(2, 4), new Move(5, 0)}));
        enemyList.add(new Enemy(35, new ImageIcon("Scorpio.png"), new Move[]{new Move(1, 5), new Move(2, 7), new Move(3, 3)}));
        enemyList.add(new Enemy(40, new ImageIcon("Vulture.png"), new Move[]{new Move(1, 4), new Move(2, 5), new Move(4, 2)}));
        enemyList.add(new Enemy(30, new ImageIcon("Mummy.png"), new Move[]{new Move(1, 7), new Move(2, 3)}));
        enemyList.add(new Enemy(120, new ImageIcon("Deceased.png"), new Move[]{new Move(1, 8), new Move(2, 8), new Move(6, 1)}));

        hero = new Hero(new ImageIcon("Hero.png"));

        Thread thread = new Thread(this);
        thread.start();
    }

    class MapPanel extends JPanel {
        
        public MapPanel() {
            setPreferredSize(new Dimension(1115, 539));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 11; ++j) {
                    map.get(i).get(j).getPic().paintIcon(this, g, j * 100, i * 100);
                }
            }

            movingIcon.paintIcon(this, g, movingCoord.getCol(), movingCoord.getRow());

            if (moving) movingIcon();
        }

    }

    // Threading Method
    @Override
    public void run() {
        while (screen == 4) {
            mapPanel.repaint();
            try {
                Thread.sleep(5); // The commands after while(true) but before try will execute once every 10 milliseconds
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException { 
        Driver d = new Driver();

        frame.setPreferredSize(new Dimension(1115, 539));

        screen = 4;
        generateMap(3);

        frame.pack();
        frame.setVisible(true);
        frame.add(mapPanel);
    }

    // stage 0: 1 enemy, 1 chest room
    // stage 1: 4 enemies, 4 chest/shop, 2 heal/unknown
    // stage 2: 6 enemies, 4 chest/shop, 2 heal/unknown
    public static void generateMap(int stage) {
        mapPanel.removeAll();
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
                for (int j = 0; j < 11; ++j) {
                    if (map.get(i).get(j).getType() > 70) {
                        currentRoom = new Pair(i, j);
                        movingCoord = new Pair(i * 100 + 50 - 16, j * 100 + 50 - 16);
                        movingIcon = new ImageIcon("CharacterIcon.png");
                    }
                }
            }
        }
        catch (Exception e) { System.out.println(e); }
    }

    public static void fight(int stage) {
        // pick item
        for (Enemy e : enemies) runEnemyMove(e);

        // update panel
        boolean check = false;
        for (Enemy e : enemies) if (e.alive()) check = true;
        if (!check) stageWin(map.get(currentRoom.getRow()).get(currentRoom.getCol()));
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

    public static void bfs(Pair p) {
        path.clear();
        Queue<Pair> q = new LinkedList<>();
        Stack<Pair> s = new Stack<>();
        visited[currentRoom.getRow()][currentRoom.getCol()] = true;
        q.add(currentRoom);
        
        int[][] d =  {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        Pair temp = new Pair(0, 0, 0, 0);
        
        while (!q.isEmpty()) {
            currentRoom = q.poll();
            s.push(currentRoom);

            for (int i = 0; i < 4; ++i) {
                temp.setRow(currentRoom.getRow() + d[i][0]);
                temp.setCol(currentRoom.getCol() + d[i][1]);
                temp.setPrev_row(currentRoom.getRow());
                temp.setPrev_col(currentRoom.getCol());

                // idk maybe something's wrong here
                if (temp.getRow() == p.getRow() && temp.getCol() == p.getCol()) {
                    while (!s.isEmpty()) {
                        Pair temp2 = s.pop();
                        if (temp.getPrev_row() == temp2.getRow() && temp.getPrev_col() == temp2.getCol()) {
                            path.addFirst(temp2);
                            temp = temp2;
                        }
                    }
                    return;
                }

                if (temp.getRow() >= 0 && temp.getRow() < 5 && temp.getCol() >= 0 && temp.getCol() < 11
                    && map.get(temp.getRow()).get(temp.getCol()).getType() != 0
                    && !visited[temp.getRow()][temp.getCol()]) {
                    Pair move = new Pair(temp.getRow(), temp.getCol(), temp.getPrev_row(), temp.getPrev_col());
                    q.offer(move);
                    visited[temp.getRow()][temp.getCol()] = true;
                }
            }

        }
    }

    public static void movingIcon() {
        if (path.isEmpty()) moving = false;
        if (movingCoord.equals(destCoord)) {
            destCoord = path.removeFirst();;
            destCoord.setRow(destCoord.getRow() * 100 + 50 - 16);
            destCoord.setCol(destCoord.getCol() * 100 + 50 - 16);
        }

        System.out.println(movingCoord);
        System.out.println(destCoord);

        if (movingCoord.getRow() < destCoord.getRow()) movingCoord.setRow(movingCoord.getRow() + 1);
        else if (movingCoord.getRow() > destCoord.getRow()) movingCoord.setRow(movingCoord.getRow() - 1);
        else if (movingCoord.getCol() < destCoord.getCol()) movingCoord.setCol(movingCoord.getCol() + 1);
        else if (movingCoord.getCol() > destCoord.getCol()) movingCoord.setCol(movingCoord.getCol() - 1);
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

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) {
        if (screen == 4) {
            if (!moving) {
                int col = e.getX() / 100;
                int row = e.getY() / 100;

                if (map.get(row).get(col).getType() != 0) {
                    for (int i = 0; i < 5; ++i) for (int j = 0; j < 11; ++j) visited[i][j] = false;

                    bfs(new Pair(row, col));

                    System.out.println(path);
                    
                    destCoord = path.removeFirst();;
                    destCoord.setRow(destCoord.getRow() * 100 + 50 - 16);
                    destCoord.setCol(destCoord.getCol() * 100 + 50 - 16);
                    
                    moving = true;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }


    
}
