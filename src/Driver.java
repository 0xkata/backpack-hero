import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Driver implements Runnable, KeyListener, MouseListener {
    
    private static JFrame frame, frame2;
    private static JPanel mainPanel;
    private static JPanel mapPanel;
    private static JPanel fightPanel;
    private static JPanel highscores;
    private static JButton button;
    private static JLabel label;

    private static BufferedReader read;
    private static StringTokenizer st;
    private static String line;

    private static int screen = 0; // 0 menu 1 high score 2 help 3 map 4 fight
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
    private static int money = 0;

    public Driver() {
        frame = new JFrame();
        frame2 = new JFrame();
        mainPanel = new JPanel();

        mapPanel = new MapPanel();
        mapPanel.addMouseListener(this);

        enemyList.add(new Enemy(30, new ImageIcon("Snake.png"), new Move[]{new Move(1, 5), new Move(2, 7)}));
        enemyList.add(new Enemy(20, new ImageIcon("Hyena.png"), new Move[]{new Move(1, 6), new Move(2, 4), new Move(5, 5)}));
        enemyList.add(new Enemy(35, new ImageIcon("Scorpio.png"), new Move[]{new Move(1, 5), new Move(2, 7), new Move(3, 3)}));
        enemyList.add(new Enemy(40, new ImageIcon("Vulture.png"), new Move[]{new Move(1, 4), new Move(2, 5), new Move(4, 2)}));
        enemyList.add(new Enemy(30, new ImageIcon("Mummy.png"), new Move[]{new Move(1, 7), new Move(2, 3)}));
        enemyList.add(new Enemy(120, new ImageIcon("Deceased.png"), new Move[]{new Move(1, 8), new Move(2, 8), new Move(6, 1)}));

        hero = new Hero(new ImageIcon("Hero.png"));

        fightPanel = new FightPanel();
    
    }

    class MapPanel extends JPanel implements MouseListener {
        
        public MapPanel() {
            setPreferredSize(new Dimension(1920, 1080));
            addMouseListener(this);
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

            // if (moving) movingIcon();
            // Thread thread = new Thread(this);
            // thread.start();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (!fighting) {
                int col = e.getX() / 100;
                int row = e.getY() / 100;

                System.out.println(row + " " + col);

                bfs(new Pair(row, col));
                
                currentRoom = notSkipping();
                movingCoord.setRow(currentRoom.getRow() * 100 + 50 - 16);
                movingCoord.setCol(currentRoom.getCol() * 100 + 50 - 16);

                mapPanel.repaint();

                int type = map.get(currentRoom.getRow()).get(currentRoom.getCol()).getType();

                if (type == 2) {

                }
                else if (type == 3) {

                }
                else if (type == 4) {

                }
                else if (type == 5) {

                }
                else if (type == 6) {

                }
                else if (type == 8) {

                }
                else if (type == 9) {

                }
                else if (type > 10 && type < 70) {
                    fightPanel.setVisible(true);
                    numEnemies = type - 10;
                    generateEnemies(stage);
                    fightPanel.repaint();
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

    }

    // Threading Method
    @Override
    public void run() {
        while (screen == 3) {
            mapPanel.repaint();
            try {
                Thread.sleep( 5);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        while (screen == 4) {
            fightPanel.repaint();
            try {
                Thread.sleep( 5);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }

    class FightPanel extends JPanel {
        
        int[] enemyPos = {1600, 1300, 1000, 700};

        public FightPanel() {
            setPreferredSize(new Dimension(1920, 1080));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            hero.getPic().paintIcon(this, g, 0, 700);

            for (int i = 0; i < numEnemies; ++i) {
                enemies[i].getPic().paintIcon(this, g, enemyPos[i], 700);
            }
        }

    }

    public static void main(String[] args) throws IOException { 
        Driver d = new Driver();

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        frame.setUndecorated(true);

        frame2.setPreferredSize(new Dimension(1100, 500)); 
        frame2.setUndecorated(true);

        generateMap(0);

        frame.add(fightPanel);

        frame2.add(mapPanel);
        
        frame.pack();
        frame.setVisible(true);
        frame2.pack();
        frame2.setVisible(true);
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

    public static void generateEnemies(int stage) {
        numEnemies = map.get(currentRoom.getRow()).get(currentRoom.getCol()).getType() - 10;

        enemies = new Enemy[numEnemies];

        for (int i = 0; i < numEnemies; ++i) {
            if (stage == 0) {
                enemies[i] = enemyList.get(0);
            }
            if (stage == 1) {
                int index = randomNum(0, 1);
                enemies[i] = enemyList.get(index);
            }
            if (stage == 2) {
                int index = randomNum(0, 3);
                enemies[i] = enemyList.get(index);
            }
        }
    }

    public static void generateBoss() {
        enemies = new Enemy[4];
        enemies[0] = enemyList.get(5);
    }

    public static void fight() {
        for (Enemy e : enemies) e.pickNextMove();
        
        // pick item
        // for (Enemy e : enemies) runEnemyMove(e);

        // update panel
        boolean check = false;
        for (Enemy e : enemies) if (e.alive()) check = true;
        if (!check) stageWin(map.get(currentRoom.getRow()).get(currentRoom.getCol()));

    }

    public static void stageWin(Room r) {
        hero.checkLevelUP();
        r.clear();
    }

    public static void runEnemyMove(Enemy e) {
        int type = e.getPossibleMoves()[e.getNextMove()].getType();
        int value = e.getPossibleMoves()[e.getNextMove()].getValue();

        if (type == 1)
            hero.changeHP(-value);;
        if (type == 2)
            e.changeArmor(value);
        if (type == 3)
            hero.getStatus()[1] = value;
        if (type == 4)
            hero.getStatus()[3] = value;
        if (type == 5) {
            e.changeHP(value);
        }
        if (type == 6) {
            boolean flag = true;
            for (int i = 2; i >= 0 && flag; --i) {
                if (enemies[i] == null) {
                    enemies[i] = enemyList.get(4);
                    flag = false;
                }
            }
        }
             
    }

    public static void bfs(Pair p) {
        path.clear();
        Queue<Pair> q = new LinkedList<>();
        Stack<Pair> s = new Stack<>();
        visited[currentRoom.getRow()][currentRoom.getCol()] = true;
        q.offer(currentRoom);
        
        int[][] d =  {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        Pair cur = new Pair(currentRoom.getRow(), currentRoom.getCol());
        Pair temp = new Pair(0, 0, 0, 0);
        
        while (!q.isEmpty()) {
            cur = q.poll();
            s.push(cur);

            for (int i = 0; i < 4; ++i) {
                temp.setRow(cur.getRow() + d[i][0]);
                temp.setCol(cur.getCol() + d[i][1]);
                temp.setPrev_row(cur.getRow());
                temp.setPrev_col(cur.getCol());

                // idk maybe something's wrong here
                if (temp.getRow() == p.getRow() && temp.getCol() == p.getCol()) {
                    s.push(temp);
                    while (!s.isEmpty()) {
                        Pair temp2 = s.pop();
                        if (temp.getPrev_row() == temp2.getRow() 
                            && temp.getPrev_col() == temp2.getCol()) {
                            path.addFirst(temp);
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

    public static Pair notSkipping() {
        Pair p = new Pair(0, 0);

        while (!path.isEmpty()) {
            p = path.poll();
            if (map.get(p.getRow()).get(p.getCol()).getType() != 1) {
                path.clear();
                return p;
            }
        }

        return currentRoom;
    }

    public static void movingIcon() {
        System.out.println(moving);

        if (path.isEmpty()) moving = false;
        if (movingCoord.equals(destCoord)) {
            destCoord = path.removeFirst();;
            destCoord.setRow(destCoord.getRow() * 100 + 50 - 16);
            destCoord.setCol(destCoord.getCol() * 100 + 50 - 16);
        }

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
        // if (screen == 4) {
            // if (!moving) {
            //     int col = e.getX() / 100;
            //     int row = e.getY() / 100;

            //     if (!(currentRoom.getRow() == row && currentRoom.getCol() == col) 
            //         && map.get(row).get(col).getType() != 0) {

            //         for (int i = 0; i < 5; ++i) for (int j = 0; j < 11; ++j) visited[i][j] = false;

            //         bfs(new Pair(row, col));

            //         destCoord = path.removeFirst();;
            //         destCoord.setRow(destCoord.getRow() * 100 + 50 - 16);
            //         destCoord.setCol(destCoord.getCol() * 100 + 50 - 16);

            //         moving = true;
                    
            //     }    
            // }
        //     int col = e.getX() / 100;
        //     int row = e.getY() / 100;

        //     System.out.println(row + " " + col);

        //     bfs(new Pair(row, col));
            
        //     currentRoom = notSkipping();
        //     movingCoord.setRow(currentRoom.getRow() * 100 + 50 - 16);
        //     movingCoord.setCol(currentRoom.getCol() * 100 + 50 - 16);

        //     mapPanel.repaint();

        //     int type = map.get(currentRoom.getRow()).get(currentRoom.getCol()).getType();

        //     if (type == 2) {

        //     }
        //     else if (type == 3) {

        //     }
        //     else if (type == 4) {

        //     }
        //     else if (type == 5) {

        //     }
        //     else if (type == 6) {

        //     }
        //     else if (type == 8) {

        //     }
        //     else if (type == 9) {

        //     }
        //     else if (type > 10 && type < 70) {
        //         screen = 4;
        //         mapPanel.setVisible(false);
        //         fightPanel.setVisible(true);
        //         numEnemies = type - 10;
        //         generateEnemies(stage);
        //     }
        // }
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
