//Due June 22, 2022
//Authors Roni Shae && Anthony Sin
//The main class of the game

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
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
public class Main extends JPanel implements Runnable, MouseListener, ActionListener, KeyListener {

	//item related variables
	public static ArrayList<Item> iList = new ArrayList<>(); //stores the data for all items in existence; filled up during the initialize() method
	public static HashMap<Identifier, Item> iMap = new HashMap<Identifier, Item>();
	private static ArrayList<ArrayList<Item>> rarityList = new ArrayList<ArrayList<Item>>(5);
	public static ArrayList<Item> firstList = new ArrayList<>(); //stores the data on only the first component of each item i.e 0a, 1a, 2a, etc..
	private static Backpack realBag; //the backpack of the user throughout the game
	private static Map<Integer,ArrayList<Item>> realItems = new HashMap<>();
	private static int selectedItem = -1; //the index in screenItems of the item last dragged by the user
	private static Point selectedPoint; //the point where the selected item is before being dragged
	private int oxTile, oyTile; //tile the origin of the item is in
	private int selectedComponent = -1; //the component of the realItem that the mouse has selected, a (origin) --> -1, b --> 0, c --> 1, etc
	private JTextArea itemDescription = new JTextArea("");

	//mouse status variables
	private int mouseX, mouseY; //x and y position of the mouse
	private Point mouseLoc; //another tracker for the position of the mouse
	private int xTile, yTile; //grid tile of the bag the mouse is over
	private int moveX, moveY; //the distance the mouse has moved between clicking and releasing
	private boolean mouseInSquare = false; //whether or not the mouse is pressed on the square hit box of an item
	private boolean rightClick;

	//organizing and expanding backpack
	private static boolean unlockable = false; //whether or not the player is allowed to unlock tiles
	private static boolean reorganize = true; //whether or not the player is allowed to reorganize right now
	private static int[] levelTiles = {0, 0, 4, 4, 4, 3, 1}; //the number of tiles the player is allowed to unlock per level
	private static int tiles;
	private static JLabel tilesLabel;
	private JLabel xpLabel;
	private ImageIcon xpIcon;

	//adjustment variables
	private Thread thread;
	private int FPS = 60;
	private static int squareSize = 100; //length and width of each space in the backpack
	private int xBagIndent = 610, yBagIndent = 1; //horizontal and vertical distance between the respective edge and where the bag starts to be drawn
	static Dimension screenSize = new Dimension(1920, 1080);
	
	// fight variables
	private static int turn; // 0 = pick enemy moves 1 = player turn 2 = enemy turn
	private static boolean fighting; //whether or not the player is in fight mode
	private static int energy = 3;
	private static int selectedEnemy = 0; 
	private static boolean stopFight = false;
	private static boolean defeated = false;

	//graphics related
	static JFrame frame, frame2;
	static Main main;
	static JPanel title;
	static JPanel mapPanel;
	static ImageIcon background;
	static Font coolFont20;
	static Font coolFont40;
	static Font coolFont60;
	private static JButton finishedReorganizing;

	//fight labels
	private static ImageIcon[] moveIcons = new ImageIcon[7];
	private static ImageIcon[] enemyMoveDisplay = new ImageIcon[4];
	private static JLabel[] moveInfo = new JLabel[4];
	private static JLabel energyLabel;
	private static ImageIcon energyIcon;
	private static JTextArea heroHPLabel;
	private static JTextArea[] enemyHPLabels = new JTextArea[4];
	private static ArrayList<Enemy> enemyList = new ArrayList<>();
	
	//unit related variables
	public static int enemyHP = 10;
	private static int[] enemyPos = {1600, 1300, 1000, 700};
	private static Enemy[] enemies;
	private static int numEnemies = 3;
	private static int stage = 0;
	private static Hero hero = new Hero(new ImageIcon("Hero.png"));

	// map related
	private static ArrayList<ArrayList<Room>> map = new ArrayList<>();
	private static BufferedReader read;
    private static StringTokenizer st;
    private static String line;
	private static ImageIcon movingIcon;
    private static Pair movingCoord;
    private static Pair currentRoom;
    private static boolean[][] visited = new boolean[5][11];
    private static LinkedList<Pair> path = new LinkedList<>();

	// chest room 
	private static boolean chest;
	private static ImageIcon chest1;

	// shop room
	private static boolean shop;
	private static ImageIcon blacksmith;
	private static ArrayList<ShopButton> shopButtons = new ArrayList<>();
	private static ShopButton[] shopChosenButtons;

	// heal room
	private static boolean heal;
	private static ImageIcon alchemist;
	private static int healCost;
	private static ImageIcon healPic;

	// economy
	private static int money = 0;
	private static JLabel moneyLabel;
	private static ImageIcon moneyIcon;
	
	//The main method
	public static void main(String[] args) throws IOException {
		initialize(); //initialize the game

		//creating the frame
		frame = new JFrame ("Backpack Monkey");
		frame.setPreferredSize(screenSize);
		frame.setLocation(0, 0);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		frame.setUndecorated(true);

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
		
	}

	//Creates the title screen
	//no parameters
	//returns the JPanel with the title screen on it
	public JPanel makeTitle() {

		//creating the panel with the settings
		JPanel out = new JPanel();
		this.setFocusable(true);
		this.requestFocusInWindow();
		addKeyListener(this);

		//adding the components
		out.setLayout(null);

		//background
		JLabel backgroundLabel = new JLabel(background);
		out.add(backgroundLabel);
		backgroundLabel.setBounds(0, 0, 1920, 1080);

		//Name image
		ImageIcon pic = new ImageIcon(new ImageIcon("Game Name.png").getImage().getScaledInstance(868, 406, java.awt.Image.SCALE_SMOOTH));
		JLabel gameName = new JLabel(pic);
		backgroundLabel.add(gameName);
		gameName.setBounds(526, 100, 868, 406);

		//start game button
		JButton startGame = new JButton("Start Game!");
		startGame.setFont(coolFont60);
		backgroundLabel.add(startGame);
		startGame.setBounds(810, 550, 300, 100);
		startGame.setActionCommand ("START GAME");
		startGame.addActionListener(this);

		//rules button
		JButton rulesButton = new JButton("Rules");
		rulesButton.setFont(coolFont60);
		backgroundLabel.add(rulesButton);
		rulesButton.setBounds(810, 650, 300, 100);
		rulesButton.setActionCommand ("RULES");
		rulesButton.addActionListener(this);

		//quit button
		JButton quit = new JButton("Quit");
		quit.setFont(coolFont60);
		backgroundLabel.add(quit);
		quit.setBounds(810, 750, 300, 100);
		quit.setActionCommand ("QUIT");
		quit.addActionListener(this);

		return out;
	}

	//initializes the game
	//no parameters
	//returns void
	public static void initialize() {
		//creating fonts
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			coolFont20 = Font.createFont(Font.TRUETYPE_FONT, new File("coolfont.ttf")).deriveFont(20f);
			coolFont40 = Font.createFont(Font.TRUETYPE_FONT, new File("coolfont.ttf")).deriveFont(40f);
			coolFont60 = Font.createFont(Font.TRUETYPE_FONT, new File("coolfont.ttf")).deriveFont(60f);
			
			ge.registerFont(coolFont20);
			ge.registerFont(coolFont40);
			ge.registerFont(coolFont60);
		}
		catch (Exception e) {
			System.out.println(e);
		}

		//initializing the rarityList
		for(int i = 0; i < 5; ++i) {
			rarityList.add(new ArrayList<Item>());
		}
		readItemInfo();
		readEnemyInfo();
		readRoomInfo();
		realBag = new Backpack();
		for(int i = 1; i < firstList.size(); ++i) {
			// createItem(randomRarity());
			createItem(i);
		}
		
		for(int i = 1; i < 7; ++i) {
			moveIcons[i] = new ImageIcon(new ImageIcon("moveIcon" + i + ".png").getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_REPLICATE));
		}
		background = new ImageIcon("background.png");
	}

	//updates the enemies array for the given stage
	//stage: the stage to generate enemies for
	//returns void
	public static void generateEnemies(int stage) {
		//number of enemies
        numEnemies = map.get(currentRoom.getRow()).get(currentRoom.getCol()).getType() - 10;
        enemies = new Enemy[numEnemies];

        //generate different enemies depending on stage
        for (int i = 0; i < numEnemies; ++i) {
            if (stage == 0) {
                enemies[i] = new Enemy(enemyList.get(0));
            }
            if (stage == 1) {
                int index = rand(0, 1);
                enemies[i] = new Enemy(enemyList.get(index));
            }
            if (stage == 2) {
                int index = rand(0, 3);
                enemies[i] = new Enemy(enemyList.get(index));
            }
			if (stage == 3) {
				int index = rand(2, 4);
				enemies[i] = new Enemy(enemyList.get(index));
			}
        }
    }
	
	//processes enemy info
	//no parameters
	//returns void
	public static void readEnemyInfo() {
		enemyList.add(new Enemy(30, new ImageIcon("Snake.png"), new Move[]{new Move(1, 9), new Move(2, 14)}));
		enemyList.add(new Enemy(20, new ImageIcon("Hyena.png"), new Move[]{new Move(1, 6), new Move(2, 4), new Move(5, 6)}));
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
				
				String type = br.readLine();
				
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
							tempSpace.getRelative()[(int)cur-98] = new Pair2(j-tempSpace.getOrigin().getFirst(), i-tempSpace.getOrigin().getSecond());
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
								rotations[k].getRelative()[(int)cur-98] = new Pair2(i-rotations[k].getOrigin().getSecond(), j-rotations[k].getOrigin().getFirst());
							}
						}
					}
				}

				//reading the image and creating their rotations
				for(int i = 0; i < size; ++i) {
					BufferedImage bipic = ImageIO.read(new FileInputStream(curID+((char)(97+i)+".png")));
					BufferedImage resized = new BufferedImage(squareSize, squareSize, BufferedImage.TRANSLUCENT);
					Graphics2D g2 = resized.createGraphics();
					g2.drawImage(bipic, 0, 0, squareSize, squareSize, null);
					g2.dispose();

					Identifier id = new Identifier(curID, (char)(97+i));
					Item add = new Item(id, name, rarity, size, resized, desc, itemEnergy, type, rotations);
					
					//adding the items to the lists
					if(i == 0) {
						firstList.add(add);
						rarityList.get(rarity).add(add);
					}
					iMap.put(id, add);
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
			int nextX = (int)first.getX() + (squareSize*template.getRotations()[template.getRotate()].getRelative()[j].getFirst());
			int nextY = (int)first.getY() + (squareSize*template.getRotations()[template.getRotate()].getRelative()[j].getSecond());

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
		//settings
		setVisible(true);
		addMouseListener(this);
		this.setPreferredSize(screenSize);
		this.setLayout(null);

		//reorganize button
		JButton toggleReorganize = new JButton("Reorganize");
		toggleReorganize.setFont(coolFont20);
		this.add(toggleReorganize);
		toggleReorganize.setBounds(40, 40, 200, 50);
		toggleReorganize.setActionCommand ("REORGANIZE");
		toggleReorganize.addActionListener(this);

		//finished reorganizing button (hidden on start)
		finishedReorganizing = new JButton("Finished Reorganizing");
		finishedReorganizing.setFont(coolFont20);
		this.add(finishedReorganizing);
		finishedReorganizing.setBounds(40, 100, 0, 0);
		finishedReorganizing.setActionCommand ("REORGANIZING DONE");
		finishedReorganizing.addActionListener(this);
		
		//scratch button
		JButton scratchButton = new JButton("Scratch!");
		scratchButton.setFont(coolFont20);
		this.add(scratchButton);
		scratchButton.setBounds(40, 180, 200, 50);
		scratchButton.setActionCommand ("SCRATCH");
		scratchButton.addActionListener(this);
		
		//end turn button
		JButton end = new JButton("End Turn!");
		end.setFont(coolFont20);
		this.add(end);
		end.setBounds(1500, 300, 200, 50);
		end.setActionCommand("END TURN");
		end.addActionListener(this);

		//xp info
		xpLabel = new JLabel();
		this.add(xpLabel);
		xpLabel.setBounds(1755, 30, 150, 75);
		xpLabel.setFont(coolFont20);
		xpIcon = new ImageIcon(new ImageIcon("XP.png").getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_REPLICATE));

		//gold info
		moneyLabel = new JLabel();
		this.add(moneyLabel);
		moneyLabel.setBounds(1550, 28, 150, 75);
		moneyLabel.setFont(coolFont20);
		moneyIcon = new ImageIcon(new ImageIcon("Gold.png").getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_REPLICATE));

		//settings on the text box that appears on right click
		itemDescription.setFont(coolFont20);
		itemDescription.setLineWrap(true);
        itemDescription.setWrapStyleWord(true);
		this.add(itemDescription);
		
		//energy info
		energyLabel = new JLabel("3");
		this.add(energyLabel);
		energyLabel.setVisible(false);
		energyLabel.setFont(coolFont20);
		energyLabel.setBounds(1555, 95, 150, 75);
		energyIcon = new ImageIcon(new ImageIcon("energyIcon.png").getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_REPLICATE));

		//tiles info
		tilesLabel = new JLabel();
		this.add(tilesLabel);
		tilesLabel.setVisible(false);
		tilesLabel.setFont(coolFont20);
		tilesLabel.setBounds(1725, 100, 150, 75);
		
		//hero status
		heroHPLabel = new JTextArea(hero.getHp() + "/" + hero.getMaxHP());
		heroHPLabel.setFont(coolFont20);
		this.add(heroHPLabel);
		
		//enemy status
		for(int i = 0; i < 4; ++i) {
			enemyHPLabels[i] = new JTextArea();
			enemyHPLabels[i].setFont(coolFont20);
			this.add(enemyHPLabels[i]);
		}
		
		//the enemy move preview
		for(int i = 0; i < 4; ++i) {
			moveInfo[i] = new JLabel();
			moveInfo[i].setFont(coolFont40);
			this.add(moveInfo[i]);
		}
		
		this.addMouseMotionListener(new MouseMotionAdapter() {
			//used to track mouse movement while mouse is pressed down
			//e: the MouseEvent to process
			//returns void
			public void mouseDragged(MouseEvent e) {
				if(mouseInSquare && reorganize) {
					//calculating mouse movement
					moveX = e.getX() - mouseLoc.x;
					moveY = e.getY() - mouseLoc.y;

					//item movement
					realItems.get(selectedItem).get(0).changePoint(moveX, moveY);
					realItems.get(selectedItem).get(0).setInBag(false);
					for(int i = 0; i < realItems.get(selectedItem).get(0).getSize()-1; ++i) {
						realItems.get(selectedItem).get(1+i).changePoint(moveX, moveY);
						realItems.get(selectedItem).get(1+i).setInBag(false);
					}
					if(rightClick) itemDescription.setBounds(realItems.get(selectedItem).get(0).getPoint().x+100, realItems.get(selectedItem).get(0).getPoint().y, 100, 200);
				}
				mouseLoc = e.getPoint();
			}
		});

		//make title screen
		title = makeTitle();

		thread = new Thread(this);
		thread.start();
	}

	//TODO
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

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (!(chest || shop || heal || fighting)) {
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
					System.out.println("chest");
					purge();
					chest = true;
					reorganize = true;
					for (int i = 0; i < 3; ++i) createItem(randomRarity());
                }
                else if (type == 3) {
					System.out.println("shop");
					purge();
					shop = true;
					reorganize = true;
					generateShop();
                }
                else if (type == 4) {
					System.out.println("heal");
					purge();
					heal = true;
					generateHeal();
                }
                else if (type == 6) {
					System.out.println("boss");
					purge();
					generateBoss();
					startFight();
                }
                else if (type == 8) {
					System.out.println("Next Stage");
					purge();
					generateMap(++stage);
					System.out.println(currentRoom);
                }
                else if (type == 9) {

                }
                else if (type > 10 && type < 70) {
					System.out.println("fight");
                    numEnemies = type - 10;
                    generateEnemies(stage);
					startFight();
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            
        }

        @Override
        public void mouseExited(MouseEvent e) {
            
        }

    }

	//TODO
	public static void generateMap(int stage) {
        mapPanel.removeAll();
        map.clear();

        if (stage == 0) {
            updateMap(0);
        }
        else if (stage == 1) {
            updateMap(randomNum(1, 3));
        }
        else if (stage == 2) {
            updateMap(randomNum(4,6));
        }
        else {
            updateMap(7);
        }
    }
	
	//generates a random number
	//min: the lower bound
	//max: the upper bound
	//returns the random number
	public static int randomNum(int min, int max) {
        int range = max - min + 1;
        return (int) (Math.random() * range + min);
    }
	
	//TODO
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

	
    //TODO
    public static void bfs(Pair p) {
        path.clear();
        for (int i = 0; i < 5; ++i) for (int j = 0; j < 11; ++j) visited[i][j] = false;
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

    //TODO
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

    //Processes information on each type of room
    //no parameters
    //returns void
	public static void readRoomInfo() { 
		chest1 = new ImageIcon("chest1.png");
		blacksmith = new ImageIcon("blacksmith.png");
		alchemist = new ImageIcon("alchemist.png");
		shopButtons.add(new ShopButton(new ImageIcon("commonButton.png"), 0, 3));
		shopButtons.add(new ShopButton(new ImageIcon("uncommonButton.png"), 1, 5));
		shopButtons.add(new ShopButton(new ImageIcon("rareButton.png"), 2, 8));
		shopButtons.add(new ShopButton(new ImageIcon("legendaryButton.png"), 3, 15));
		shopButtons.add(new ShopButton(new ImageIcon("relicButton.png"), 4, 20));
	}

	//TODO
	public static void generateShop() {
		shopChosenButtons = new ShopButton[3];

		for (int i = 0; i < 3; ++i) {
			shopChosenButtons[i] = shopButtons.get(randomNum(0, 4));
		}

		Arrays.sort(shopChosenButtons);
	}

	//TODO
	public static void generateHeal() {
		if (stage == 0) {
			healCost = 0;
			healPic = new ImageIcon("heal0.png");
		}
		else if (stage == 1) {
			healCost = 2;
			healPic = new ImageIcon("heal1.png");			
		}
		else if (stage == 2) {
			healCost = 5;
			healPic = new ImageIcon("heal2.png");
		}
		else if (stage == 3) {
			healCost = 10;
			healPic = new ImageIcon("heal3.png");
		}
	}

	//TODO
	public static void generateBoss() {
		numEnemies = 4;
		enemies = new Enemy[4];
		enemies[0] = new Enemy(enemyList.get(5));		
	}
	
	//Checks if all the enemies are alive
	//no parameters
	//returns whether or not they are all alive
	public static boolean checkEnemies() {
 		boolean check = true;
		for (int i = 0; i < numEnemies; ++i) {
			if (enemies[i] == null) continue;
			if (enemies[i].alive()) check = false;
			else {
				enemies[i] = null;
				if (selectedEnemy == i) {
					for (int j = 0; j < numEnemies; ++j) {
						if (enemies[j] == null) continue;
						if (enemies[j].alive()) selectedEnemy = j;
					}
				}
			}
		}
		return check;
	}

	
	//runs the game loop
	//no parameters
	//returns void
	public void run() {
		while(true) {
			//main game loop
			
			trackMouse();
			this.repaint();
			
			//fight loop
			turn = turn%4; 
			if(fighting) {
			 	if(turn == 0) {
			 		tickEnemies2(); //updates weak on enemies
			 		//pick enemy moves
			 	 	for (Enemy e : enemies) {
						if (e == null) continue;
						e.pickNextMove();
					}
			 		System.out.println("enemy moves picked");
			 		energy = 3;
			 		for(ArrayList<Item> i : realItems.values()) { //resetting the used boolean
			 			i.get(0).setUsed(false);
			 		}
			 		hero.tick(); //updates non-weak status effects on hero
			 		turn++;
			 	}
			 	if(turn == 1) { //automatic items
			 		autoUse();
			 		System.out.println("start of turn items used"); //do the start of turn items
			 		turn++;
			 		
			 	}
			 	if (turn == 2) { //user input phase
			 		//user can now use items
			 		//after each item used, calls the 'use' method in the Item class
			 	}
			 	if(stopFight) {
			 		endFight();
			 		continue;
			 	}
			 	if(turn == 3) {  //enemy phase
			 		hero.tick2(); //tick hero weakness

			 		tickEnemies();
			 		if(stopFight) {
				 		endFight();
				 		continue;
				 	}
			 		
			 		//use enemy moves
			 		for (Enemy e : enemies) {
						if (e == null) continue;
						runEnemyMove(e); 
					}
			 		turn++;
			 	}

			}
			try {
				Thread.sleep(1000/FPS);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//runs an enemy move
	//e: the enemy to run
	//returns void
	public static void runEnemyMove(Enemy e) {

		//acquire info
		int type = e.getPossibleMoves()[e.getNextMove()].getType();
		int value = e.getPossibleMoves()[e.getNextMove()].getValue();
		int rage = e.getStatus()[3];
		int weak = e.getStatus()[4];
		
		if(type == 0) e.getStatus()[1] += value; //regen
		if (type == 1) {
			int damage = Math.min(0, -value-rage+weak);
			hero.changeHP(damage); //damage
		}
		if (type == 2)
			e.changeArmor(value); //gain armor
		if (type == 3)
			hero.getStatus()[0] += value; //poison
		if (type == 4)
			hero.getStatus()[4] += value; //weak
		if (type == 5) {
			e.changeHP(value); //heal
		}
		if (type == 6) {
			boolean flag = true;
			for (int i = 2; i >= 0 && flag; --i) {
				if (enemies[i] == null) {
					enemies[i] = new Enemy(enemyList.get(4));
					flag = false;
				}
			}
		}
		defeated = !hero.alive();
		if(defeated) System.exit(0); //TODO add death screen
	}
	
	//decreases and applies status (non weak) effects on all enemies
	//no parameters
	//returns void
	public void tickEnemies() {
		for(Enemy e : enemies) {
			if (e == null) continue;
			e.tick();
		}
	}
	
	//decreases weak on all enemies
	//no parameters
	//returns void
	public void tickEnemies2() {
		for(Enemy e : enemies) {
			if (e == null) continue;
			e.tick2();
		}
	}
	
	//uses all the automatic items in the user's bag
	//no parameters
	//returns void
	public void autoUse() {
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < 7; ++j) {
				if(realBag.getContents()[i][j] != null) realBag.getContents()[i][j].auto();
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
	//g: the graphics variable
	//returns void
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		//bg
		background.paintIcon(main, g, 0, 0);

		//money
		moneyIcon.paintIcon(main, g, 1500, 40);
		moneyLabel.setText(": " + money);
		moneyLabel.setBounds(1550, 28, 150, 75);

		//xp
		xpIcon.paintIcon(main, g, 1700, 40);
		xpLabel.setText(": " + hero.getXp() + "/ " + hero.getMaxXP()[hero.getLevel()] + " (" + hero.getLevel() + ")");
		xpLabel.setBounds(1755, 30, 150, 75);

		//draws the backpack based on the contents
		for(int i = 0; i < 7; ++i) { //7 tiles across
			for(int j = 0; j < 5; ++j) { //5 tiles down

				//this line accounts for when dragging an item out of the backpack, the image will move but the actual backpack will not be updated
				//(fills the square with empty)
				if(realBag.getContents()[j][i].getInBag() == false) realBag.addItem(i, j, firstList.get(0));
				
				//drawing the square around each backpack space
				g.drawRect(xBagIndent+squareSize*i, yBagIndent+squareSize*j, squareSize, squareSize); 
				
				//drawing the image of the item in each backpack space
				if(realBag.getContents()[j][i] != null) g.drawImage(realBag.getContents()[j][i].getPic(),xBagIndent+squareSize*i,yBagIndent+squareSize*j,this); 
				
				//fills with a blue square if the tile is locked
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
			if(cur.getInBag() == true) continue; //skip the item if it is in the bag
			
			//draws the origin
			g.drawImage(cur.getPic(), (int)cur.getPoint().getX(),(int)cur.getPoint().getY(), this); 
			
			//for each non-origin component
			for(int c = 0; c < cur.getSize()-1; ++c) {
				int xShift = squareSize * cur.getRotations()[cur.getRotate()].getRelative()[c].getFirst(); //the horizontal difference from the origin in pixels
				int yShift = squareSize * cur.getRotations()[cur.getRotate()].getRelative()[c].getSecond(); //the vertical difference from the origin in pixels
				g.drawImage(components.get(1+c).getPic(), (int)cur.getPoint().getX() + xShift, (int)cur.getPoint().getY()+yShift, this); //paint the next component
			}
		}
		//if statements for drawing the various rooms
		
		//chest room
		if (chest) {
			chest1.paintIcon(main, g, 1600, 700);
		}
		
		//shop room
		if (shop) {
			blacksmith.paintIcon(main, g, 1600, 700);
			shopChosenButtons[0].getPic().paintIcon(main, g, 200, 700);
			shopChosenButtons[1].getPic().paintIcon(main, g, 200, 800);
			shopChosenButtons[2].getPic().paintIcon(main, g, 200, 900);
		}

		//healer room
		if (heal) { 
			alchemist.paintIcon(main, g, 1600, 700);
			healPic.paintIcon(main, g, 200, 700);
		}

		//drawing fight components if fighting
		if(fighting) {

			//hero
			hero.getPic().paintIcon(this, g, 0, 700);
			
			//enemy pictures
            for (int i = 0; i < numEnemies; ++i) {
				if (enemies[i] == null) continue;
                enemies[i].getPic().paintIcon(this, g, enemyPos[i], 700);
            }
            
            //status effect on each enemy
            for(int i = 0; i < numEnemies; ++i) {
    			if(enemies[i] != null) { //must not be empty
    				if(enemies[i].alive()) { //must be alive
    			
    					//setting content and location of status text area
    					enemyHPLabels[i].setBounds(enemyPos[i], 900, 100, 110);
    					enemyHPLabels[i].setText(enemies[i].getHp()+"/"+enemies[i].getMaxHP() +"\nArmor:" + enemies[i].getArmor() + "\nPoison: " + enemies[i].getStatus()[0] 
    							+ "\nRegen: " + enemies[i].getStatus()[1] + "\nSpikes: " + enemies[i].getStatus()[2] 
    									+ "\nRage: " + enemies[i].getStatus()[3] + "\nWeak: " + enemies[i].getStatus()[4]);
    					
    					//setting content and location of enemy move preview icon and number
    					moveInfo[i].setBounds(enemyPos[i], 600, 100, 40);
    					
    					//acquiring info
    					int type = enemies[i].getPossibleMoves()[enemies[i].getNextMove()].getType();
    					int value = enemies[i].getPossibleMoves()[enemies[i].getNextMove()].getValue();
    					
    					//account for rage and weakness in the number shown on screen
    					int display = value;
    					if(type == 1) display = display + enemies[i].getStatus()[3]-enemies[i].getStatus()[4];
    					if(display < 0) display = 0; //make sure it's not negative
    					
    					//draw and set
    					moveInfo[i].setText(""+display);
    					enemyMoveDisplay[i] = moveIcons[type];
    					enemyMoveDisplay[i].paintIcon(this, g, enemyPos[i], 600);
    				}
    			}
				else { //hide the respective component if not alive
					moveInfo[i].setBounds(0, 0, 0, 0);
					enemyHPLabels[i].setBounds(0, 0, 0, 0);
				}
    		}
            
            //hero status location and info
            heroHPLabel.setBounds(100, 900, 100, 110);
            heroHPLabel.setText(hero.getHp()+"/"+hero.getMaxHP() + "\nArmor:" + hero.getArmor() +"\nPoison: " + hero.getStatus()[0] 
					+ "\nRegen: " + hero.getStatus()[1] + "\nSpikes: " + hero.getStatus()[2] 
							+ "\nRage: " + hero.getStatus()[3] + "\nWeak: " + hero.getStatus()[4]);
            
            //rectangle to indicate the selected enemy
			g.drawRect(enemyPos[selectedEnemy], 700, 320, 320);

			// energy
			energyIcon.paintIcon(main, g, 1500, 100);
			energyLabel.setText(""+energy);
		}

		// If there is unlockable tiles
		if (unlockable) {
			tilesLabel.setText("Unlockable: " + tiles);
		}

	}

	//Removes the entire item (all a, b, c, ... etc. of an item will be removed from the bag if one component is at the given location) at the given location.
	//yRem: the y component of the location in the bag to remove
	//xRem: the x component of the location in the bag to remove
	//component: indicates whether or not the item doing the removing is a sub-component (not the origin) of the item
	//returns void
	public static void retrieveItem(int yRem, int xRem, boolean component) {
		Item cur = realBag.getContents()[yRem][xRem];

		//finding the location of the complete item's origin
		int oy = yRem; //oy, ox = origin's y and x position in the bag
		int ox = xRem;
		char comp = cur.getIdentifier().getSupp(); //which component of the item the selected tile is
		if(comp != 'a') { //if the selected tile is not the origin of the item
			
			//calculating the location of the origin
			int index = (int)comp - 98; //0 if comp is 'b'
			oy -= cur.getRotations()[cur.getRotate()].getRelative()[index].getSecond();
			ox -= cur.getRotations()[cur.getRotate()].getRelative()[index].getFirst();
			cur = realBag.getContents()[oy][ox]; //setting cur to the origin if it changed
		}
		//retrieving the item in the tile the origin is on if it is not empty
		if(!realBag.getContents()[oy][ox].getName().equals("Empty")) {
			//if the one doing the removing is a component, pick a random point to move the replaced item
			if(component) realItems.get(realBag.getContents()[oy][ox].getRealID()).get(0).setPoint(new Point(rand(100,1000),rand(500,800)));
			
			//if the one doing the removing is the origin, move the replaced item to where the item doing the replacing started (selectedPoint)
			else realItems.get(realBag.getContents()[oy][ox].getRealID()).get(0).setPoint(selectedPoint);
			realItems.get(realBag.getContents()[oy][ox].getRealID()).get(0).setInBag(false); //internally take it out of the bag
			
			//resetting the position in the bag
			realItems.get(realBag.getContents()[oy][ox].getRealID()).get(0).setX(-1); 
			realItems.get(realBag.getContents()[oy][ox].getRealID()).get(0).setY(-1);
		}

		realBag.addItem(ox, oy, firstList.get(0)); //setting the origin to empty		

		for(int i = 0; i < cur.getSize()-1; ++i) { //setting the related components to empty
			
			//calculates the next square to remove
			int clearY = oy + cur.getRotations()[cur.getRotate()].getRelative()[i].getSecond();
			int clearX = ox + cur.getRotations()[cur.getRotate()].getRelative()[i].getFirst();
			
			if(!realBag.getContents()[clearY][clearX].getName().equals("Empty")) { //make sure the square is not already empty
				realItems.get(realBag.getContents()[clearY][clearX].getRealID()).get(i).setInBag(false); //internally take it out of the bag
				
				//resetting the position in the bag
				realItems.get(realBag.getContents()[clearY][clearX].getRealID()).get(i).setY(-1);
				realItems.get(realBag.getContents()[clearY][clearX].getRealID()).get(i).setX(-1);
				
				realBag.addItem(clearX, clearY, firstList.get(0)); //replace the item in the backpack with the "Empty" item
			}
		}
	}

	//updates information on the selected item and component
	//e: mouse event that happened
	//returns void
	public void getSelectedItem(MouseEvent e) {
		for(ArrayList<Item> components : realItems.values()) { //all items check if they have been selected
			Item cur = components.get(0);

			boolean inItem = inRect(mouseLoc, cur.getPoint(), squareSize, squareSize);
			int xMove = 0;
			int yMove = 0;

			//checking if a component was selected
			for(int c = 0; c < cur.getSize()-1; ++c) {
				int xShift = squareSize * cur.getRotations()[cur.getRotate()].getRelative()[c].getFirst(); //the horizontal difference from the origin in pixels
				int yShift = squareSize * cur.getRotations()[cur.getRotate()].getRelative()[c].getSecond(); //the vertical difference from the origin in pixels
				if(inRect(mouseLoc, new Point(cur.getPoint().x+xShift, cur.getPoint().y+yShift), squareSize, squareSize)) {
					xMove = xShift;
					yMove = yShift;
					selectedComponent = c;
				}
				inItem = (inItem || inRect(mouseLoc, new Point(cur.getPoint().x+xShift, cur.getPoint().y+yShift), squareSize, squareSize));
			}

			//if an item was selected, set the variables
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
		
		//if it was a right click
		if(e.getButton() == MouseEvent.BUTTON3) {
			rightClick = true; 
			if(realItems.get(selectedItem) != null) {
			if(realItems.get(selectedItem).get(0) != null) {
				Item cur = realItems.get(selectedItem).get(0);
					//makes the item description text area appear and fills it with the information
				itemDescription.setBounds(cur.getLoc().x+100, cur.getLoc().y, 100, 200);
				itemDescription.setText(cur.getName() + "\n\n" + cur.getDescription());
			}
		}
			
		}
		
		//if it is a left click
		if(e.getButton() == MouseEvent.BUTTON1) rightClick = false; 
		
		//sets selected enemy if fighting
		if (fighting) {
			for (int i = 0; i < numEnemies; ++i) {
				if (inRect(mouseLoc, new Point(enemyPos[i], 700), 300, 300) && enemies[i] != null) {
					selectedEnemy = i; 
				}
			}
		}

		//TODO
		if (shop) {
			for (int i = 0; i < 3; ++i) {
				if (inRect(mouseLoc, new Point(200, 700 + i * 100), 250, 100)) {
					if (money >= shopChosenButtons[i].getPrice()) {
						money -= shopChosenButtons[i].getPrice();
						int rand = randomNum(0, rarityList.get(shopChosenButtons[i].getRarity()).size() - 1);
						createItem(rarityList.get(shopChosenButtons[i].getRarity()).get(rand).getIdentifier().getPrim());
					}
				}
			}
		}

		//TODO
		if (heal) {
			if (inRect(mouseLoc, new Point(200, 700), 500, 200)) {
				if (money >= healCost) {
					money -= healCost;
					hero.setHp(hero.maxHP);
				}
			}
		}
	}

	//called when mouse released
	//e: mouse event that happened
	//returns void
	public void mouseReleased(MouseEvent e) {
		itemDescription.setBounds(0, 0, 0, 0);
		if(overBag() && selectedItem >= 0 && reorganize) { //mouse must be released over the bag and have selected an item and organizing is allowed
			Item origin = realItems.get(selectedItem).get(0);

			//the tile the origin is located at
			int oxLoc = xTile;
			int oyLoc = yTile;
			if(selectedComponent > -1) {
				if(origin.getSize() > 1) {
					oxLoc -= origin.getRotations()[origin.getRotate()].getRelative()[selectedComponent].getFirst();
					oyLoc -= origin.getRotations()[origin.getRotate()].getRelative()[selectedComponent].getSecond();
				}
			}

			//computing if the entire item will be within bounds of the bag
			boolean allowed = inBagBounds(oxLoc, oyLoc);
			for(int i = 0; i < origin.getSize()-1; ++i) {
				int tempY = oyLoc+origin.getRotations()[origin.getRotate()].getRelative()[i].getSecond();
				int tempX = oxLoc+origin.getRotations()[origin.getRotate()].getRelative()[i].getFirst();
				allowed = allowed && inBagBounds(tempX, tempY);
			}

			if(allowed) {
				//retrieves the item in the selected tile
				retrieveItem(oyLoc, oxLoc, false);
				//retrieves item in other related cells if the item is bigger
				for(int i = 0; i < origin.getSize()-1; ++i) {
					int tempY = oyLoc+origin.getRotations()[origin.getRotate()].getRelative()[i].getSecond();
					int tempX = oxLoc+origin.getRotations()[origin.getRotate()].getRelative()[i].getFirst();

					if(!realBag.getContents()[tempY][tempX].getName().equals("Empty")) {
						retrieveItem(tempY, tempX, true);
					}
				}

				//sets the selected tile to the selected item
				oxTile = xTile;
				oyTile = yTile;
				if(selectedComponent >= 0) {
					if(origin.getSize() > 1) {
						oxTile -=  origin.getRotations()[origin.getRotate()].getRelative()[selectedComponent].getFirst();
						oyTile -= origin.getRotations()[origin.getRotate()].getRelative()[selectedComponent].getSecond();
					}
				}
				realBag.addItem(oxTile, oyTile, origin);
				origin.setX(oxTile);
				origin.setY(oyTile);
				origin.setPoint(new Point(oxTile*squareSize+xBagIndent, oyTile*squareSize+yBagIndent));
				origin.setInBag(true);

				//adding the rest of the components (if any)
				int oxCopy = oxTile;
				int oyCopy = oyTile;
				for(int i = 0; i < origin.getSize()-1; ++i) {
					//calculating position of component
					oxTile += origin.getRotations()[origin.getRotate()].getRelative()[i].getFirst();
					oyTile += origin.getRotations()[origin.getRotate()].getRelative()[i].getSecond();
					
					realBag.addItem(oxTile, oyTile, realItems.get(selectedItem).get(1+i)); //internally adds
					realItems.get(selectedItem).get(1+i).setPoint(new Point(oxTile*squareSize+xBagIndent, oyTile*squareSize+yBagIndent));
					realItems.get(selectedItem).get(1+i).setInBag(true);
					realItems.get(selectedItem).get(1+i).setX(oxTile);
					realItems.get(selectedItem).get(1+i).setY(oyTile);
					
					//resetting variables
					oxTile = oxCopy;
					oyTile = oyCopy;
				}
			}
			else {}

		}
		//resetting the logic variables
		mouseInSquare = false;
		selectedItem = -1;
		selectedComponent = -1;
		
		//TODO remove after fixing bug
		System.out.println();
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < 7; ++j) {
				System.out.print(realBag.getContents()[i][j].getIdentifier()+" ");
			}
			System.out.println();
		}
	}

	//unusued override methods
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}

	//called when mouse clicked
	//e: mouse event that happened
	//returns void
	public void mouseClicked(MouseEvent e) {
		//if the mouse click is within the reorganize button's bounds
		if(!reorganize && overBag() && turn == 2 && realBag.getUnlocked()[yTile][xTile] && !realBag.getContents()[yTile][xTile].getName().equals("Empty")) { //if unable to reorganize, player must be in combat mode
			getSelectedItem(e);

			//calls the abilities of the item when used
			if(realItems.get(selectedItem).get(0) != null) realItems.get(selectedItem).get(0).use();

			//reset selected variables
			mouseInSquare = false;
			selectedItem = -1;
			selectedComponent = -1;
		}
		else if(overBag() && unlockable) { //if the player can unlock tiles and clicks over a tile in the bag
			if(!realBag.getUnlocked()[yTile][xTile]) tiles--; //if the tile was not previously unlocked
			realBag.setUnlocked(yTile, xTile, true);
			if(tiles == 0) {
				unlockable = false; //after all the allowed tiles are unlocked, unlockable is false
				tilesLabel.setVisible(false);
			}
		}

	}
	//Tracks when a specific action is performed
	//e: the action that happened
	//returns void
	
	public void actionPerformed(ActionEvent e) {
		String eventName = e.getActionCommand();
		
		//reorganize button pressed
		if(eventName.equals("REORGANIZE")) {
			//must have 3 energy to use
			if(energy - 3 >= 0) {
				energy -= 3;
				reorganize = true;
				
				//makes the finished reorganizing button visible
				finishedReorganizing.setBounds(40, 100, 200, 50);
			}
			else {}
			}
		
		//start game button pressed
		else if(eventName.equals("START GAME")) {
			
			//setes screen to the main screen
			title.setVisible(false);
			frame.add(main);

			//creates the map frame & panel
			frame2 = new JFrame ("Map Monkey");
			frame2.setPreferredSize(new Dimension(1100, 500));
			frame2.setLocation(0, 0); 
			frame2.setUndecorated(true);
			frame2.add(mapPanel);
			frame2.setVisible(true);
			frame2.pack();
			frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame2.setResizable(false);
		}

		//end turn button pressed (end turn is used for progressing in all rooms)
		else if(eventName.equals("END TURN")) {
			//if it was a chest room
			if (chest) {
				chest = false;
				map.get(currentRoom.getRow()).get(currentRoom.getCol()).clear();
				purge();
			}
			
			//if it was a shop room
			if (shop) {
				shop = false;
				map.get(currentRoom.getRow()).get(currentRoom.getCol()).clear();
				purge();
			}

			//if it was a healer room
			if (heal) { 
				heal = false;
				map.get(currentRoom.getRow()).get(currentRoom.getCol()).clear();
			}

			//if we are fighting
			if (fighting) {
				reorganize = false;
				turn++;
			}
			finishedReorganizing.setBounds(40, 100, 0, 0); //hide finish reorganizing button
		}
		
		//quit button pressed
		else if(eventName.equals("QUIT")) {
			System.exit(0);
		}
		
		//finished reorganizing button pressed
		else if(eventName.equals("REORGANIZING DONE")) {
			finishedReorganizing.setBounds(40, 100, 0, 0);
			reorganize = false;
		}
		
		//scratch button pressed
		else if(eventName.equals("SCRATCH")) {
			if(fighting) { //must be fighting
				if(energy - 1 >= 0) {
					energy -= 1;
					enemies[selectedEnemy].changeHP(-3);
					System.out.println("scratch");
				}
				else {}
			}
			
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
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	public static void startFight() { //TODO
		purge();
		fighting = true;
		selectedEnemy = 0;
		turn = 0;
		reorganize = false;
		energyLabel.setVisible(true);
	}

	public static void endFight() {
		fighting = false;
		turn = 0;
		reorganize = true;
		selectedEnemy = 0;
		stopFight = false;
		map.get(currentRoom.getRow()).get(currentRoom.getCol()).clear();

		//hiding enemy status labels
		for(int i = 0; i < enemyHPLabels.length; ++i) {
			moveInfo[i].setBounds(0, 0, 0, 0);
			enemyHPLabels[i].setBounds(enemyPos[i], 900, 0, 0);
		}
		//hiding hero status label
		heroHPLabel.setBounds(100, 900, 0, 0);
		System.out.println("no longer fighting!!");
		reward();
		hero.checkLevelUP();
		tiles = levelTiles[hero.getLevel()];
		energyLabel.setVisible(false);
	}

	public static void reward() {
		if (stage == 0) {
			money += randomNum(2, 5);
			hero.changeXP(10);;
		}
		else if (stage == 1) {
			money += randomNum(4, 8);
			hero.changeXP(randomNum(7, 15));
		}
		else if (stage == 2) {
			money += randomNum(5, 12);
			hero.changeXP(randomNum(10, 20));
		}
		else if (stage == 3) {
			hero.changeXP(randomNum(15, 25));
		}
	}
	
	public static boolean bagHasArmor() {
		boolean out = false;
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < 7; ++j) {
				if(realBag.getContents()[i][j].getType().equals("Armor")) {
					out = true;
					break;
				}
			}
		}
		return out;
	}
	
	public static boolean bagHasItem(int id) {
		boolean out = false;
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < 7; ++j) {
				if(realBag.getContents()[i][j].getIdentifier().getPrim() == id) {
					out = true;
					break;
				}
			}
		}
		return out;
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
	public static boolean inBagBounds(int xLoc, int yLoc) {
		return xLoc >= 0 && xLoc < 7 && yLoc >= 0 && yLoc < 5 && realBag.getUnlocked()[yLoc][xLoc];
	}

	//removes all items that are not on the screen
	//no parameters
	//returns void
	public static void purge() {
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
	}
	
	//getters TODO
	public static Hero getHero() {
		return hero;
	}
	public static Backpack getBag() {
		return realBag;
	}
	public static int getEnergy() {
		return energy;
	}
	public static Enemy[] getEnemies() {
		return enemies;
	}
	public static int getSelectedEnemy() {
		return selectedEnemy;
	}
	
	//setters TODO
	public static void setUnlockable(boolean v) {
		unlockable = v;
	}
	public static void decreaseEnergy(int n) {
		energy -= n;
	}
	public static void increaseEnergy(int n) {
		energy += n;
	}
	public static void setStopFight(boolean v) {
		stopFight = v;
	}
}