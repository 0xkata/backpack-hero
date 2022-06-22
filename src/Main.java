import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.swing.*;

import javax.imageio.*;

@SuppressWarnings("serial")
public class Main extends JPanel implements Runnable, MouseListener, ActionListener, KeyListener{

	//item related variables
	public static ArrayList<Item> iList = new ArrayList<>(); //stores the data for all items in existence; filled up during the initialize() method
	public static HashMap<Identifier, Item> iMap = new HashMap<Identifier, Item>();
	private static ArrayList<ArrayList<Item>> rarityList = new ArrayList<ArrayList<Item>>(5);
	int[] hi = new int[5];
	public static ArrayList<Item> firstList = new ArrayList<>(); //stores the data on only the first component of each item i.e 0a, 1a, 2a, etc..
	private static Backpack realBag; //the backpack of the user throughout the game
	private static Map<Integer,ArrayList<Item>> realItems = new HashMap<>();
	private static int selectedItem = -1; //the index in screenItems of the item last dragged by the user
	private static Point selectedPoint; //the point where the selected item is before being dragged
	private int oxTile, oyTile; //tile the origin of the item is in
	private int selectedComponent = -1; //the component of the realItem that the mouse has selected, a (origin) --> -1, b --> 0, c --> 1, etc

	//mouse status variables
	private int mouseX, mouseY; //x and y position of the mouse
	private Point mouseLoc; //another tracker for the position of the mouse
	private int xTile, yTile; //grid tile of the bag the mouse is over
	private int moveX, moveY; //the distance the mouse has moved between clicking and releasing
	private boolean mouseInSquare = false; //whether or not the mouse is pressed on the square hit box of an item

	private static boolean reorganize = true; //whether or not the player is allowed to reorganize right now TODO reorganize should be false when doing tile unlocking

	//unlocking tiles in the backpack
	private boolean unlockable = true; //whether or not the player is allowed to unlock tiles TODO make it based on level up
	private int[] levelTiles = {4, 4, 3, 2, 2, 1}; //the number of tiles the player is allowed to unlock per level
	private int level = 0; //TODO replace the hero level with this
	private int tiles = levelTiles[level]; //the current amount of tiles allowed to unlock at the moment

	//adjustment variables
	private Thread thread;
	private int FPS = 60;
	private static int squareSize = 100; //length and width of each space in the backpack
	private int xBagIndent = 610, yBagIndent = 1; //horizontal and vertical distance between the respective edge and where the bag starts to be drawn
	static Dimension screenSize = new Dimension(1920, 1080);
	
	//fight variables
	private static boolean playerTurn; //whether or not it is the player's turn
	private static boolean fighting; //whether or not the player is in fight mode
	private static int energy = 3;
	public static void decreaseEnergy(int n) {
		energy -= n;
	}
	private static JLabel energyLabel;

	//the various panels for the screens
	static JFrame frame, frame2;
	static Main main;
	static JPanel title;
	static JPanel mapPanel;

	//testing
	public static int enemyHP = 10;

	private static Enemy[] enemies;
	private static ArrayList<Enemy> enemyList = new ArrayList<>();
	private static ArrayList<ArrayList<Room>> map = new ArrayList<>();
	private static Hero hero = new Hero(new ImageIcon("Hero.png"));
	private static int numEnemies = 3;
	private static int stage = 0;

	private static BufferedReader read;
    private static StringTokenizer st;
    private static String line;

	private static ImageIcon movingIcon;
    private static Pair movingCoord;
    private static Pair destCoord;
    private static Pair currentRoom;
    private static boolean[][] visited = new boolean[5][11];
    private static LinkedList<Pair> path = new LinkedList<>();
    private static boolean moving;

	private static int[] enemyPos = {1600, 1300, 1000, 700};

	public static void main(String[] args) throws IOException {
		initialize(); //initialize the game

		//creating the frame
		frame = new JFrame ("Backpack Monkey");
		frame.setPreferredSize(screenSize);
		frame.setLocation(0, 0);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		frame.setUndecorated(true);

		frame2 = new JFrame ("Map Monkey");
		frame2.setPreferredSize(new Dimension(1100, 500));
		frame2.setLocation(0, 0); 
		frame2.setUndecorated(true);

		//creating and adding the necessary components
		main = new Main ();
		mapPanel = new MapPanel();

		generateMap(0);
	
		frame.add(title);
		frame.addKeyListener(main);

		//final packing and settings
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		frame2.add(mapPanel);

		frame2.setVisible(true);
		frame2.pack();
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame2.setResizable(false);

		
//		JFrame mapFrame = new JFrame("bruh");
//		JPanel aaa = new MapPanel();
//		mapFrame.add(aaa);
//		mapFrame.setUndecorated(true);
//		mapFrame.pack();
//		mapFrame.setVisible(true);
//		mapFrame.setPreferredSize(new Dimension(1115, 539));
//		mapFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		mapFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		
	}
	public JPanel makeTitle() {

		JPanel out = new JPanel();
		this.setFocusable(true);
		this.requestFocusInWindow();

		addKeyListener(this);
		out.setLayout(null);

		ImageIcon pic = new ImageIcon(new ImageIcon("Game Name.png").getImage().getScaledInstance(868, 406, java.awt.Image.SCALE_SMOOTH));
		JLabel gameName = new JLabel(pic);
		out.add(gameName);
		gameName.setBounds(526, 100, 868, 406);

		JButton startGame = new JButton("Start Game!");
		out.add(startGame);
		startGame.setBounds(810, 550, 300, 100);
		startGame.setActionCommand ("START GAME");
		startGame.addActionListener(this);

		JButton rulesButton = new JButton("Rules");
		out.add(rulesButton);
		rulesButton.setBounds(910, 750, 100, 50);
		rulesButton.setActionCommand ("RULES");
		rulesButton.addActionListener(this);

		JButton quit = new JButton("Quit");
		out.add(quit);
		quit.setBounds(910, 850, 100, 50);
		quit.setActionCommand ("QUIT");
		quit.addActionListener(this);

		return out;
	}

	//initializes the game
	//no parameters
	//returns void
	public static void initialize() {
		for(int i = 0; i < 5; ++i) {
			rarityList.add(new ArrayList<Item>());
		}
		readItemInfo();
		realBag = new Backpack();
		for(int i = 1; i < firstList.size(); ++i) {
			createItem(randomRarity());
		}
		readEnemyInfo();
	}

	public static void generateEnemies(int stage) {
        numEnemies = map.get(currentRoom.getRow()).get(currentRoom.getCol()).getType() - 10;
		
		
        enemies = new Enemy[numEnemies];

        for (int i = 0; i < numEnemies; ++i) {
            if (stage == 0) {
                enemies[i] = enemyList.get(0);
            }
            if (stage == 1) {
                int index = rand(0, 1);
                enemies[i] = enemyList.get(index);
            }
            if (stage == 2) {
                int index = rand(0, 3);
                enemies[i] = enemyList.get(index);
            }
        }
    }
	
	public static void readEnemyInfo() {
		enemyList.add(new Enemy(30, new ImageIcon("Snake.png"), new Move[]{new Move(1, 5), new Move(2, 7)}));
		enemyList.add(new Enemy(20, new ImageIcon("Hyena.png"), new Move[]{new Move(1, 6), new Move(2, 4), new Move(5, 0)}));
		enemyList.add(new Enemy(35, new ImageIcon("Scorpio.png"), new Move[]{new Move(1, 5), new Move(2, 7), new Move(3, 3)}));
		enemyList.add(new Enemy(40, new ImageIcon("Vulture.png"), new Move[]{new Move(1, 4), new Move(2, 5), new Move(4, 2)}));
		enemyList.add(new Enemy(30, new ImageIcon("Mummy.png"), new Move[]{new Move(1, 7), new Move(2, 3)}));
		enemyList.add(new Enemy(120, new ImageIcon("Deceased.png"), new Move[]{new Move(1, 8), new Move(2, 8), new Move(6, 1)}));
	}
	//reads the item info from the text file and adds the information to various collections
	//no parameters
	//returns void
	public static void readItemInfo() {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("ItemInfo.txt"));
			String line = "";

			while((line = br.readLine()) != null) { //looping until the file is empty

				//reading in the attributes of each item

				//size
				int size = Integer.parseInt(line);

				//item type id
				int curID = Integer.parseInt(br.readLine());

				//item name
				String name = br.readLine();

				//item rarity
				int rarity = Integer.parseInt(br.readLine());

				//item effect
				String desc = br.readLine();

				//item energy
				int itemEnergy = Integer.parseInt(br.readLine());

				//item spatial requirements
				char[][] space = new char[4][4];
				Space tempSpace = new Space(space, size);
				Pair2 origin;
				for(int i = 0; i < 4; ++i) { //looping through the 4x4 of characters
					StringTokenizer st = new StringTokenizer(br.readLine());
					for(int j = 0; j < 4; ++j) {
						char cur = st.nextToken().charAt(0); //the current character
						if(cur == 'a') { //a represents the origin of the item; it is always the first component read in; when rotating, the position of the origin will be calculated and the relative arrayList will be recomputed based on that
							origin = new Pair2(j,i);
							tempSpace.setOrigin(origin);
						}
						else if(Character.isAlphabetic(cur)) { //if it is 'b,' 'c,' etc.
							//calculating the y and x distance from the origin
							tempSpace.getRelative()[(int)cur-98] = new Pair2(j-tempSpace.getOrigin().first, i-tempSpace.getOrigin().second);
						}
						space[i][j] = cur; //storing the characters
					}
				}
				//making the array for rotations
				Space[] rotations = new Space[4];
				rotations[0] = tempSpace; //the first index is the array given in the info file
				for(int k = 1; k < 4; ++k) {
					rotations[k] = new Space(rotations[k-1].rotate(), size);
					rotations[k].findOrigin();
					for(int i = 0; i < 4; ++i) {
						for(int j = 0; j < 4; ++j) {
							char cur = rotations[k].getGrid()[j][i];
							if(Character.isAlphabetic(cur) && cur != 'a') { //if it is 'b,' 'c,' etc.
								//calculating the new y and x distance from the origin
								rotations[k].getRelative()[(int)cur-98] = new Pair2(i-rotations[k].getOrigin().second, j-rotations[k].getOrigin().first);
							}
						}
					}
				}

				for(int i = 0; i < size; ++i) {
					BufferedImage bipic = ImageIO.read(new FileInputStream(curID+((char)(97+i)+".png")));
					BufferedImage resized = new BufferedImage(squareSize, squareSize, BufferedImage.TRANSLUCENT);
					Graphics2D g2 = resized.createGraphics();
					g2.drawImage(bipic, 0, 0, squareSize, squareSize, null);
					g2.dispose();

					Identifier id = new Identifier(curID, (char)(97+i));
					Item add = new Item(id, name, rarity, size, resized, desc, itemEnergy, rotations);
					if(i == 0) firstList.add(add);
					iMap.put(id, add);
					rarityList.get(rarity).add(add);
				}
			}
			br.close();
		} 
		catch (FileNotFoundException e) {
			System.out.println("ItemInfo.txt is missing");
		}
		catch (IOException e) {
			System.out.println("IOException in reading item info");
		}
	}

	//creates a new item at a random location
	//item: the id of that type of item (not the real id)
	//returns void
	public static void createItem(int item) {
		//randomizing an item to base the item off of
		Item template = firstList.get(item);

		//random point to spawn the item
		Point first = new Point(rand(200,1000),rand(200,500));

		//adding the first item to the screen
		Item newest = new Item(template);
		int lastID = newest.getRealID();
		realItems.put(lastID, new ArrayList<Item>());
		realItems.get(lastID).add(newest);

		//setting the location of the item's origin to the randomly generated point
		realItems.get(lastID).get(0).setPoint(first);

		for(int j = 0; j < template.getSize()-1; ++j) { //for each tile that is not the origin
			int nextX = (int)first.getX() + (squareSize*template.getRotations()[template.getRotate()].getRelative()[j].first);
			int nextY = (int)first.getY() + (squareSize*template.getRotations()[template.getRotate()].getRelative()[j].second);

			Identifier nextID = new Identifier(template.getIdentifier().getPrim(),(char)(98+j));
			Item addition = new Item(iMap.get(nextID));
			realItems.get(lastID).add(addition);
			realItems.get(lastID).get(j).setPoint(new Point(nextX, nextY));
			//sets the real id of this component to the same as the component before it (the origin)
			realItems.get(lastID).get(j+1).setRealID(realItems.get(lastID).get(0).getRealID());
		}
	}

	//creates the main panel
	//no parameters
	//no return
	public Main() {
		setVisible(true);
		addMouseListener(this);
		this.setPreferredSize(screenSize);
		this.setLayout(null);

		JButton toggleReorganize = new JButton("Reorganize");
		this.add(toggleReorganize);
		toggleReorganize.setBounds(40, 40, 100, 50);
		toggleReorganize.setActionCommand ("REORGANIZE");
		toggleReorganize.addActionListener(this);

		JButton end = new JButton("End Turn!");
		this.add(end);
		end.setBounds(1500, 300, 100, 50);
		end.setActionCommand("END TURN");
		end.addActionListener(this);

		energyLabel = new JLabel("3");
		this.add(energyLabel);
		energyLabel.setBounds(1000, 500, 100, 50);

		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if(mouseInSquare && reorganize) {
					moveX = e.getX() - mouseLoc.x;
					moveY = e.getY() - mouseLoc.y;

					realItems.get(selectedItem).get(0).changePoint(moveX, moveY);
					realItems.get(selectedItem).get(0).setInBag(false);
					for(int i = 0; i < realItems.get(selectedItem).get(0).getSize()-1; ++i) {
						realItems.get(selectedItem).get(1+i).changePoint(moveX, moveY);
						realItems.get(selectedItem).get(1+i).setInBag(false);
					}
				}
				mouseLoc = e.getPoint();
			}
		});

		title = makeTitle();

		thread = new Thread(this);
		thread.start();
	}

	static class MapPanel extends JPanel implements MouseListener {
        
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

                bfs(new Pair(row, col));
                
				System.out.println(path);

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
                    numEnemies = type - 10;
                    generateEnemies(stage);
					startFight();
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

	//runs the game loop
	//no parameters
	//returns void
	public void run() { //TODO
		while(true) {
			//main game loop
			trackMouse();
			this.repaint();
			if(fighting) {
				//pick enemy moves
				if(playerTurn) {

					//do the start of turn items

					//user can now use items

					//after each item used, check if enemies are alive
					if(enemyHP == 0) { //when all enemies die or player dies, fighting = false;
						fighting = false;
						System.out.println("no longer fighting!!");
					}
				}
				else { //must not be player turn

					//use enemy moves
					//check if player / enemies are alive after each attack
					energy = 3; //resetting the energy (after enemies do their attacks)
					playerTurn = true;
				}

			}
			try {
				Thread.sleep(1000/FPS);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	//updates the x and y position and the tile of the backpack the mouse is in
	//no parameters
	//returns void
	public void trackMouse() {
		//obtaining the point the mouse is on
		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();

		//getting the x and y values of the mouse
		mouseX = (int) b.getX();
		mouseY = (int) b.getY();

		//converting the x and y values of the mouse into what tile it is on
		xTile = (mouseX-xBagIndent)/squareSize;
		yTile = (mouseY-yBagIndent)/squareSize;
	}

	//paints the various components onto the screen
	//g: graphics variable
	//returns void
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		//draws the backpack based on the contents
		for(int i = 0; i < 7; ++i) { //7 tiles across
			for(int j = 0; j < 5; ++j) { //5 tiles down

				//this line accounts for when dragging an item out of the backpack, the image will move but the actual backpack will not be updated
				//(fills the square with empty)
				if(realBag.getContents()[j][i].getInBag() == false) realBag.addItem(i, j, firstList.get(0));
				g.drawRect(xBagIndent+squareSize*i, yBagIndent+squareSize*j, squareSize, squareSize); //drawing the square around each backpack space
				if(realBag.getContents()[j][i] != null) g.drawImage(realBag.getContents()[j][i].getPic(),xBagIndent+squareSize*i,yBagIndent+squareSize*j,this); //drawing the image of the item in each backpack space
				if(!realBag.getUnlocked()[j][i]) {
					g.setColor(Color.BLUE);
					g.fillRect(xBagIndent+squareSize*i, yBagIndent+squareSize*j,squareSize,squareSize); 
					g.setColor(Color.BLACK);
				}
			}
		}

		//responsible for highlighting the square the mouse is on
		if(overBag()) { 
			//fill the respective tile with red
			g.setColor(Color.RED);
			g.fillRect(xBagIndent+squareSize*xTile, yBagIndent+squareSize*yTile,squareSize,squareSize); 

			//redraw the image that was painted over
			if(realBag.getContents()[yTile][xTile] != null) g.drawImage(realBag.getContents()[yTile][xTile].getPic(),xBagIndent+squareSize*xTile,yBagIndent+squareSize*yTile,this);
			g.setColor(Color.BLACK); //reset the colour
		}

		//draws the items "floating around"
		for(ArrayList<Item> components : realItems.values()) {
			Item cur = components.get(0); // the origin of each item
			if(cur.getInBag() == true) continue;
			g.drawImage(cur.getPic(), (int)cur.getPoint().getX(),(int)cur.getPoint().getY(), this); //draws the origin
			for(int c = 0; c < cur.getSize()-1; ++c) {
				int xShift = squareSize * cur.getRotations()[cur.getRotate()].getRelative()[c].first; //the horizontal difference from the origin in pixels
				int yShift = squareSize * cur.getRotations()[cur.getRotate()].getRelative()[c].second; //the vertical difference from the origin in pixels
				g.drawImage(components.get(1+c).getPic(), (int)cur.getPoint().getX() + xShift, (int)cur.getPoint().getY()+yShift, this); //paint the next component
			}
		}
		if(fighting) {
			hero.getPic().paintIcon(this, g, 0, 700);

            for (int i = 0; i < numEnemies; ++i) {
                enemies[i].getPic().paintIcon(this, g, enemyPos[i], 700);
            }
		}
		if(playerTurn) {
			g.drawRect(1, 1, 100, 300);
		}
		energyLabel.setText(""+energy);

	}

	//Removes the entire item (all a, b, c, ... etc. of an item will be removed from the bag if one component is at the given location) at the given location.
	//yRem: the y component of the location in the bag to remove
	//xRem: the x component of the location in the bag to remove
	//component: indicates whether or not the item doing the removing is a sub-component (not the origin) of the item
	//returns void
	public void retrieveItem(int yRem, int xRem, boolean component) {
		Item cur = realBag.getContents()[yRem][xRem];

		//finding the location of the complete item's origin
		int oy = yRem; //oy, ox = origin's y and x position in the bag
		int ox = xRem;
		char comp = cur.getIdentifier().getSupp(); //which component of the item the selected tile is
		if(comp != 'a') { //if the selected tile is not the origin of the item
			//calculating the location of the origin
			int index = (int)comp - 98; //0 if comp is 'b'
			oy -= cur.getRotations()[cur.getRotate()].getRelative()[index].second;
			ox -= cur.getRotations()[cur.getRotate()].getRelative()[index].first;
			cur = realBag.getContents()[oy][ox]; //setting cur to the origin if it changed
		}
		//retrieving the item in the tile the origin is on if it is not empty
		if(!realBag.getContents()[oy][ox].getName().equals("Empty")) {
			//this ensures that the items do not end up exactly on top of each other (although it is technically possible, it is unlikely)
			//TODO: ^^make it absolutely impossible for this to happen (reroll if its the same)
			//if the one doing the removing is a component, pick a random point to move the replaced item
			if(component) realItems.get(realBag.getContents()[oy][ox].getRealID()).get(0).setPoint(new Point(rand(100,1000),rand(500,800)));
			//if the one doing the removing is the origin, move the replaced item to where the item doing the replacing started (selectedPoint)
			else realItems.get(realBag.getContents()[oy][ox].getRealID()).get(0).setPoint(selectedPoint);
			realItems.get(realBag.getContents()[oy][ox].getRealID()).get(0).setInBag(false); //internally take it out of the bag
		}

		realBag.addItem(ox, oy, firstList.get(0)); //setting the origin to empty		

		for(int i = 0; i < cur.getSize()-1; ++i) { //setting the related components to empty
			//calculates the next square to remove
			int clearY = oy + cur.getRotations()[cur.getRotate()].getRelative()[i].second;
			int clearX = ox + cur.getRotations()[cur.getRotate()].getRelative()[i].first;
			if(!realBag.getContents()[clearY][clearX].getName().equals("Empty")) { //make sure the square is not already empty
				realItems.get(realBag.getContents()[clearY][clearX].getRealID()).get(i).setInBag(false); //internally take it out of the bag
				realBag.addItem(clearX, clearY, firstList.get(0)); //replace the item in the backpack with the empty item
			}
		}
	}

	//updates information on the selected item and component
	//e: mouse event that happened
	//returns void
	public void getSelectedItem(MouseEvent e) {
		for(ArrayList<Item> components : realItems.values()) {
			Item cur = components.get(0);

			boolean inItem = inRect(mouseLoc, cur.getPoint(), squareSize, squareSize);
			int xMove = 0;
			int yMove = 0;

			for(int c = 0; c < cur.getSize()-1; ++c) {
				int xShift = squareSize * cur.getRotations()[cur.getRotate()].getRelative()[c].first; //the horizontal difference from the origin in pixels
				int yShift = squareSize * cur.getRotations()[cur.getRotate()].getRelative()[c].second; //the vertical difference from the origin in pixels
				if(inRect(mouseLoc, new Point(cur.getPoint().x+xShift, cur.getPoint().y+yShift), squareSize, squareSize)) {
					xMove = xShift;
					yMove = yShift;
					selectedComponent = c;
				}
				inItem = (inItem || inRect(mouseLoc, new Point(cur.getPoint().x+xShift, cur.getPoint().y+yShift), squareSize, squareSize));
			}

			if(inItem) {
				selectedItem = cur.getRealID();
				selectedPoint = new Point(realItems.get(selectedItem).get(0).getPoint().x + xMove, realItems.get(selectedItem).get(0).getPoint().y + yMove);
				mouseInSquare = true;	
			}
		}
	}

	//called when mouse pressed
	//e: mouse event that happened
	//returns void
	public void mousePressed(MouseEvent e) {
		mouseLoc = e.getPoint();
		getSelectedItem(e);
	}

	//called when mouse released
	//e: mouse event that happened
	//returns void
	public void mouseReleased(MouseEvent e) {
		if(overBag() && selectedItem >= 0 && reorganize) { //mouse must be released over the bag and have selected an item and organizing is allowed
			Item origin = realItems.get(selectedItem).get(0);

			//the tile the origin is located at
			int oxLoc = xTile;
			int oyLoc = yTile;
			if(selectedComponent > -1) {
				oxLoc -= origin.getRotations()[origin.getRotate()].getRelative()[selectedComponent].first;
				oyLoc -= origin.getRotations()[origin.getRotate()].getRelative()[selectedComponent].second;
			}

			//computing if the entire item will be within bounds of the bag
			boolean allowed = inBagBounds(oxLoc, oyLoc);
			for(int i = 0; i < origin.getSize()-1; ++i) {
				int tempY = oyLoc+origin.getRotations()[origin.getRotate()].getRelative()[i].second;
				int tempX = oxLoc+origin.getRotations()[origin.getRotate()].getRelative()[i].first;
				allowed = allowed && inBagBounds(tempX, tempY);
			}

			if(allowed) {
				//retrieves the item in the selected tile
				retrieveItem(oyLoc, oxLoc, false);
				//retrieves item in other related cells if the item is bigger
				for(int i = 0; i < origin.getSize()-1; ++i) {
					int tempY = oyLoc+origin.getRotations()[origin.getRotate()].getRelative()[i].second;
					int tempX = oxLoc+origin.getRotations()[origin.getRotate()].getRelative()[i].first;

					if(!realBag.getContents()[tempY][tempX].getName().equals("Empty")) {
						retrieveItem(tempY, tempX, true);
					}
				}

				//sets the selected tile to the selected item
				oxTile = xTile;
				oyTile = yTile;
				if(selectedComponent >= 0) {
					oxTile -=  origin.getRotations()[origin.getRotate()].getRelative()[selectedComponent].first;
					oyTile -= origin.getRotations()[origin.getRotate()].getRelative()[selectedComponent].second;
				}
				realBag.addItem(oxTile, oyTile, origin);
				origin.setInBag(true);

				//adding the rest of the components (if any)
				int oxCopy = oxTile;
				int oyCopy = oyTile;
				for(int i = 0; i < origin.getSize()-1; ++i) {
					oxTile += origin.getRotations()[origin.getRotate()].getRelative()[i].first;
					oyTile += origin.getRotations()[origin.getRotate()].getRelative()[i].second;
					realBag.addItem(oxTile, oyTile, realItems.get(selectedItem).get(1+i));
					realItems.get(selectedItem).get(1+i).setInBag(true);
					oxTile = oxCopy;
					oyTile = oyCopy;
				}
			}
			else System.out.println("not allowed"); //TODO: maybe do something different when not allowed

		}
		//resetting the logic variables
		mouseInSquare = false;
		selectedItem = -1;
		selectedComponent = -1;
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	//called when mouse clicked
	//e: mouse event that happened
	//returns void
	public void mouseClicked(MouseEvent e) {
		//if the mouse click is within the reorganize button's bounds
		if(!reorganize && overBag() && playerTurn) { //if unable to reorganize, player must be in combat mode
			getSelectedItem(e);

			//calls the abilities of the item when used
			realItems.get(selectedItem).get(0).use();

			mouseInSquare = false;
			selectedItem = -1;
			selectedComponent = -1;
		}
		else if(overBag() && unlockable) { //if the player can unlock tiles and clicks over a tile in the bag
			if(!realBag.getUnlocked()[yTile][xTile]) tiles--; //if the tile was not previously unlocked
			realBag.setUnlocked(yTile, xTile, true);
			if(tiles == 0) unlockable = false; //after all the allowed tiles are unlocked, unlockable is false
		}

	}
	//Tracks when a specific action is performed
	//e: the action that happened
	//returns void
	public void actionPerformed(ActionEvent e) {
		String eventName = e.getActionCommand();
		if(eventName.equals("REORGANIZE")) {
			System.out.println("REORGANIZE clicked");
			reorganize = !reorganize;
		}
		else if(eventName.equals("START GAME")) {
			System.out.println("start");
			title.setVisible(false);
			frame.add(main);
		}
		else if(eventName.equals("END TURN")) {
			System.out.println("end");
			playerTurn = false;
		}
		else if(eventName.equals("QUIT")) {
			System.exit(0);
		}
		main.requestFocusInWindow();

	}
	//called when key pressed
	//e: the key event that happened
	//returns void
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_W) {
			if(selectedItem > -1) {
				Item cur = realItems.get(selectedItem).get(0);
				cur.rotate(1);
				for(int i = 0; i < cur.getSize()-1; ++i) {
					realItems.get(selectedItem).get(1+i).rotate(1);
				}
			}
		}
		else if(key == KeyEvent.VK_F) { //TODO remove later
			startFight();
		}
		else if(key == KeyEvent.VK_P) { //TODO remove later
			purge();
		}
		else if(key == KeyEvent.VK_E) { //TODO remove later
			playerTurn = !playerTurn;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	public static void startFight() { //TODO
		fighting = true;
		playerTurn = true;
		//		//randomly create enemies
		reorganize = false;

	}
	public static void endFight() {
		fighting = false;
		playerTurn = false;
		reorganize = true;
	}
	//Random number generator
	//min: the lower bound of integers, max: the upper bound of integers
	//returns a random integer between the numbers inclusive
	public static int rand(int min, int max) {
		return (int)(Math.random()*(max-min+1)+min);
	}

	//determines if the mouse is currently over any square of the backpack
	//no parameters
	//returns true if the mouse is over the backpack
	public boolean overBag() {
		return mouseX > xBagIndent && mouseX < xBagIndent+7*squareSize && mouseY > yBagIndent && mouseY < yBagIndent+5*squareSize;
	}

	//checks if a point is in a given rectangle formed by a point
	//corner: the point representing the top left corner of the rectangle
	//check: the location to check if it is in the rectangle
	//xSize: the width of the rectangle to check in
	//ySize: the height of the rectangle to check in
	//returns true or false depending on whether or not the point is or is not in the square
	public boolean inRect(Point check, Point corner, int xSize, int ySize) {
		return check.getX() > corner.getX() && check.getX() < corner.getX()+xSize && check.getY() > corner.getY() && check.getY() < corner.getY()+ySize;
	}

	//checks if a given tile is within the bag and unlocked by the users
	//xLoc, yLoc: the x, y position of the tile
	//returns true or false depending on if it is or is not in the bag
	public boolean inBagBounds(int xLoc, int yLoc) {
		return xLoc >= 0 && xLoc < 7 && yLoc >= 0 && yLoc < 5 && realBag.getUnlocked()[yLoc][xLoc];
	}

	//removes all items that are not on the screen
	//no parameters
	//returns void
	public void purge() {
		ArrayList<Integer> remove = new ArrayList<Integer>();
		for(ArrayList<Item> i : realItems.values()) {
			if(i.get(0).getInBag() == false) remove.add(i.get(0).getRealID());
		}
		for(int i = 0; i < remove.size(); ++i) {
			realItems.remove(remove.get(i));
		}
		System.out.println("purged");
	}

	//generates a random number corresponding to the type ID of an item while considering the rarity of each item
	//no parameters
	//returns the type ID of the random item
	public static int randomRarity() {
		int cur = rand(1,100);
		int rarity = 0;
		if(cur > 90) rarity = 3;
		else if(cur > 50) rarity = 2;
		else if(cur > 20) rarity = 1;
		int out = rand(0, rarityList.get(rarity).size()-1);
		Identifier id = rarityList.get(rarity).get(out).getIdentifier();
		return id.getPrim();
	} //TODO
	
	
}
