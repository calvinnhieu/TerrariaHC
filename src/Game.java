import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.util.ArrayList;
import java.util.Random;

public class Game extends Applet implements Runnable, KeyListener, MouseListener, MouseWheelListener, MouseMotionListener {
	
	/*
	 * Grade 11 Computer Science Final Project
	 * 
	 *     Game by : Calvin and Hamdan
	 *        Date : 24th January, 2012
	 * Description : This game is based on a game by RE-LOGIC called Terraria. It's a game
	 * 				 where the user can do whatever they want. The game doesn't really have
	 * 				 an ending, it just goes on and on. We wanted to implement as many
	 * 				 features of Terraria as possible, but were only able to implement a few 
	 * 				 due to some difficulties we had along the way both bug related and time
	 * 				 related.
	 *    Features : TERRAIN : Everything you see about the terrain is randomly generated. 
	 *    			 Each time you run the game, you will get a completely different map. 
	 *   			 The map has a few characteristics, it is made up of tiles. There are 
	 *   			 11 different types of tiles, air, dirt, grass, stone, iron, gold, 
	 *   			 diamond, wood, wooden planks, leaves and the boundary tiles. The map has 
	 *   			 randomly generated hills and valleys as well as caves. The ores are put 
	 *   			 on the map randomly in pockets, and the trees also have their own random 
	 *   			 generation. The maps are very big, in fact each map has 1,000,000 tiles.
	 *   			 The maps are completely destructible, just click or drag to remove blocks.
	 *   			 The map terrain also changes dynamically depending on the situation, 
	 *   			 (So it has different looks based on the terrain around it). There are also
	 *   			 some tiles you can walk past, such as wood on trees and leaves.
	 *   			 PLAYER : Your player is a guy who digs and places blocks. We were thinking
	 *   			 about putting in armour, weapons, crafting, health etc. but due to time 
	 *   			 constraints we couldn't. The player has an  inventory. That inventory is 
	 *   			 restricted to 5 spaces. To clear blocks to make space, just click on the 
	 *   			 inventory space to clear it. 
	 *   			 ENEMY : There is only one enemy in the game (again, couldn't put more in 
	 *   			 because of time), and that enemy is the slime. They just jump around, and
	 *   			 if the player is close enough they chase him.
	 *   			 OPTIMIZATIONS : We ran into a lot of issues with our game. This was mainly
	 *   			 due to the fact that a game like this requires big maps. The problem was
	 *   			 that when we tried to store 1,000,000 tiles, the game would run incredibly
	 *   			 slow. So a large amount of time was spent on tweaking the game so that it
	 *   			 actually ran smoothly. Another issue we had was with the terrain generation.
	 *   			 Before it would take at least 2 - 3 minutes to generate a map (depending on
	 *   			 the intensity of the map). We had to completely re-think the way we generated
	 *   			 our map in order to cut that time down to a few seconds. That also took a lot
	 *   			 of time. We also had a BIG problem with collision detection. We spent a good
	 *   			 month on just trying to get perfect collision detection. We got very close, 
	 *   			 but there are still a few issues with it (although its a vast improvement from
	 *   			 what we had before).
	 *   	  Bugs : As mentioned, there are some bugs with the collision detection. We haven't
	 *   			 spent as much time discovering new bugs as we have trying to correct ones we found.
	 *   			 A lot of the bugs that the game had have been fixed, although there are some bugs
	 *   			 that we feel we just can't fix due to the structure of our game (either that or
	 *   			 we have no idea why they are there). There is a bug with the player, that if you
	 *   			 directly jump or fall onto one block, and you line that block up with the center
	 *   			 of the player, the player will just go through that block and become stuck. To 
	 *   			 get out you just jump. There is another bug also to do with the collision of the 
	 *   			 player, where the player's edge can get stuck inside the tiles of the map. There
	 *   			 is a bug with the placement of blocks, where you can place blocks inside of the 
	 *   			 player and enemies (although that's easy to fix, we fixed it and found out that
	 *   			 the solution just created more bugs). There are also some bugs with the collision
	 *   			 of enemies, where they just get stuck randomly and are too dumb to get out.
	 */
	
	// Declare Variables
	ArrayList<Miner> miners; // list to hold miners (creates different tile types)
	ArrayList<InventoryObject>[] inv = new ArrayList[5]; // list to hold inventory spots
	ArrayList<Tile> tilesModified; // list to hold tiles
	ArrayList<Enemy> enemies; // list to hold enemies
	
	
	boolean dontSetModifyingTileID = false;

	Graphics dbg; // for double buffering
	Image dbImage; // for double buffering
	
	Image[] dirtSheet; // image array for dirt sprites
	Image[] grassSheet; // image array for grass sprites
	Image[] stoneSheet; // image array for stone sprites
	Image[] ironSheet; // image array for iron sprites
	Image[] woodSheet; // image array for wood sprites
	Image[] goldSheet; // image array for gold sprites
	Image[] diamondSheet; // image array for diamond sprites
	Image[] plankSheet; // image array for plank sprites
	Image[] leafSheet; // image array for leaf sprites
	Image inventoryImage; // image for inventory
	Image inventorySlotSelectedImage; // image for inventory slot
	Image[] playerSprites; // image array for player sprites
	Image[] enemySprites; // image array for enemy sprites

	int screenWidth; // screen width
	int screenHeight; // screen height
	int tileLength; // length of one tile
	static int mapTileWidth; // width of map in tiles
	static int mapTileHeight; // height of map in tiles
	int mapxOffset; // movement of map(x)
	int mapyOffset; // movement of map(y)
	int[] heightMap; // array of map terrain height
	int sheetCounter; // (needed for sprite cropping) shows the current sprite being cropped
	int playerXDistance; // distance from player to 0 on x axis
	int playerYDistance; // distance from player to 0 on y axis
	int enemyXDistance; // distance from enemy to 0 on x axis
	int enemyYDistance; // distance from enemy to 0 on y axis
	int playerRectsSize; // The radius of collision for the player
	int enemyRectsSize; // The radius of collision for the enemy
	int startPlayerRectsX; // The top left of the collision for the player (x)
	int startPlayerRectsY; // The top left of the collision for the player (y)
	int startEnemyRectsX; // The top left of the collision for the enemy (x)
	int startEnemyRectsY; // The top left of the collision for the enemy (y)
	int inventorySlotSelected; // The current inventory slot selected by the user
	int mouseXClicked; // The x coordinate of the mouse relative to the screen (click)
	int mouseYClicked; // The y coordinate of the mouse relative to the screen (click)
	int mouseXDragged; // The x coordinate of the mouse relative to the screen (drag)
	int mouseYDragged; // The y coordinate of the mouse relative to the screen (drag)
	int mouseXTile; // The x tile that the mouse is in
	int mouseYTile; // The y tile that the mouse is in
	int modifyingTileID; // The current tile ID being modified
	int globalPlayerWalkCounter; // Needed for the walking animation for the player

	Player player; // The player object

	Random random; // A random object used for random number generation

	Rectangle playerRect = new Rectangle(); // The player rectangle
	Rectangle playerMoveRect = new Rectangle(); // The player move rectangle (collision)
	Rectangle enemyRect = new Rectangle(); // The enemy rectangle
	Rectangle enemyMoveRect = new Rectangle(); // The enemy move rectangle (collision)
	Rectangle[][] playerRects; // The player collision checking area
	Rectangle[][] enemyRects; // The enemy collision checking area
	Rectangle[] inventoryRects; // The inventory slot rectangles

	Tile[][] tile; // The tiles are held in this 2D Array

	// This method initializes all the necessary variables
	public void init() {

		//Initialize Variables
		miners = new ArrayList<Miner>();
		enemies = new ArrayList<Enemy>();

		initializeImages();

		screenWidth = 1280;
		screenHeight = 720;
		setSize(screenWidth, screenHeight);
		tileLength = 20;
		mapTileWidth = 1000;
		mapTileHeight = 1000;
		mapxOffset = 30 * tileLength;
		mapyOffset = 80 * tileLength;
		heightMap = new int[mapTileWidth];
		playerRectsSize = 7;
		enemyRectsSize = 11;
		globalPlayerWalkCounter = 0;

		inventoryImage = getImage(getCodeBase(), "Inv.png");
		inventorySlotSelected = 0;
		inventorySlotSelectedImage = getImage(getCodeBase(), "InventorySelectedSlot.png");

		for (int i = 0; i < 5; i++) {
			inv[i] = new ArrayList<InventoryObject>();
		}

		random = new Random();

		// Generate the map
		generateMap(System.currentTimeMillis());

		// Create player
		player = new Player(480, 330);
		int px = 30;
		// Set the player's x and y position
		int playerHeight = player.yPos + player.playerHeight;
		int tileHeight = tile[px][heightMap[px]].getyPos();
		int tileHeight2 = tile[px + 1][heightMap[px + 1]].getyPos();
		if (tileHeight2 < tileHeight) {
			tileHeight = tileHeight2;
		}
		if (playerHeight < tileHeight) {
			int difference = playerHeight - tileHeight;
			mapyOffset -= difference * 2;
		}

		playerRect.setBounds(player.xPos, player.yPos, player.playerWidth, player.playerHeight);
		playerMoveRect.setBounds(playerRect);

		// Create and initialize enemies
		int counter = 0;
		for(int i = 50; i < mapTileWidth - 50; i += 20) {
			enemies.add(new Enemy(0, 0));
			int ex = i;
			tileHeight = tile[ex][heightMap[ex]].getyPos();
			enemies.get(counter).setyPos(tileHeight + mapyOffset);
			enemies.get(counter).setxPos(ex * tileLength);
			counter++;
		}
		
		for(int i = 0; i < enemies.size(); i++) {
			enemies.get(i).setSprite(0);
		}
		// Initialize rectangles (player, enemy, inventory)
		playerRects = new Rectangle[mapTileWidth][mapTileHeight];
		enemyRects = new Rectangle[mapTileWidth][mapTileHeight];
		
		inventoryRects = new Rectangle[5];
		for (int i = 0; i < inventoryRects.length; i++) {
			inventoryRects[i] = new Rectangle();
			inventoryRects[i].setBounds(10 + (i * 60), 10, 40, 40);
		}

		// Update the images of all the tiles
		updateAllTiles();

		// Add all listeners
		addKeyListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		addMouseMotionListener(this);

	}

	public void start() {
		Thread th = new Thread(this);
		th.start();
	}

	/* This method is the main loop of the game, it's responsible for keeping
	 * the game running.*/
	public void run() {

		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		while (true) {

			movePlayer();

			moveEnemies();
			
			repaint();
			
			try {
				Thread.sleep(20);
			} catch (Exception e) {

			}
			
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

		}
	}

	/* This method paints everything onto the screen */
	public void paint(Graphics g) {
		// Paint the tiles
		for (int x = ((mapxOffset) / tileLength); x < ((mapxOffset + screenWidth) / tileLength) + 1; x++) {
			for (int y = ((mapyOffset) / tileLength); y < (((mapyOffset) / tileLength) + (screenHeight / tileLength)) + 1; y++) {
				try {
					if (tile[x][y].getID() == 1) {
						g.drawImage(dirtSheet[tile[x][y].getTileImage()], (x * tileLength) - mapxOffset, (y * tileLength) - mapyOffset, tileLength, tileLength, this);
					} else if (tile[x][y].getID() == 2) {
						g.drawImage(grassSheet[tile[x][y].getTileImage()], (x * tileLength) - mapxOffset, (y * tileLength) - mapyOffset, tileLength, tileLength, this);
					} else if (tile[x][y].getID() == 3) {
						g.drawImage(stoneSheet[tile[x][y].getTileImage()], (x * tileLength) - mapxOffset, (y * tileLength) - mapyOffset, tileLength, tileLength, this);
					} else if (tile[x][y].getID() == 4) {
						g.drawImage(ironSheet[tile[x][y].getTileImage()], (x * tileLength) - mapxOffset, (y * tileLength) - mapyOffset, tileLength, tileLength, this);
					} else if (tile[x][y].getID() == 5) {
						g.drawImage(goldSheet[tile[x][y].getTileImage()], (x * tileLength) - mapxOffset, (y * tileLength) - mapyOffset, tileLength, tileLength, this);
					} else if (tile[x][y].getID() == 6) {
						g.drawImage(diamondSheet[tile[x][y].getTileImage()], (x * tileLength) - mapxOffset, (y * tileLength) - mapyOffset, tileLength, tileLength, this);
					} else if (tile[x][y].getID() == 7) {
						g.drawImage(woodSheet[tile[x][y].getTileImage()], (x * tileLength) - mapxOffset, (y * tileLength) - mapyOffset, tileLength, tileLength, this);
					} else if (tile[x][y].getID() == 8) {
						g.drawImage(plankSheet[tile[x][y].getTileImage()], (x * tileLength) - mapxOffset, (y * tileLength) - mapyOffset, tileLength, tileLength, this);
					} else if (tile[x][y].getID() == 9) {
						g.drawImage(leafSheet[tile[x][y].getTileImage()], (x * tileLength) - mapxOffset, (y * tileLength) - mapyOffset, tileLength, tileLength, this);
					}
				} catch (Exception e) {

				}
			}
		}
		// Draw the Player
		g.drawImage(playerSprites[player.getSprite()], player.xPos, player.yPos, 40, 60, this);
		
		// Draw the inventory
		g.drawImage(inventoryImage, 0, 0, this);
		for (int i = 0; i < 5; i++) {
			g.drawImage(getInventoryImage(i), 15 + (i * 60), 15, 30, 30, this);
		}
		g.drawImage(inventorySlotSelectedImage, 10 + (inventorySlotSelected * 60), 10, 40, 40, this);
		for (int i = 0; i < 5; i++) {
			if (!inv[i].isEmpty()) {
				g.setColor(Color.white);
				if (inv[i].size() < 10) {
					g.drawString("" + inv[i].size(), 40 + (i * 60), 45);
				} else if (inv[i].size() >= 10 && inv[i].size() < 100) {
					g.drawString("" + inv[i].size(), 30 + (i * 60), 45);
				} else {
					g.drawString("" + inv[i].size(), 20 + (i * 60), 45);
				}
			}
		}
		
		// Draw the enemies
		for(int i = 0; i < enemies.size(); i++) {
			g.drawImage(enemySprites[enemies.get(i).getSprite()], enemies.get(i).getxPos() - mapxOffset, enemies.get(i).getyPos() - mapyOffset, enemySprites[enemies.get(i).getSprite()].getWidth(this), enemySprites[enemies.get(i).getSprite()].getHeight(this), this);
		}
	}

	/* This method generates the entire map */
	public void generateMap(long startTime) {
		/* (Everything to do with time is just for optimization purposes) */
		long initialTime = startTime;
		// Create the 2D Array that holds the tiles for the map
		tile = new Tile[mapTileWidth][mapTileHeight];
		for (int y = 0; y < mapTileHeight; y++) {
			for (int x = 0; x < mapTileWidth; x++) {
				tile[x][y] = new Tile(x, y, 1);
			}
		}
		System.out.println("Tiles Created in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		
		// Create the hills and valleys
		for (int i = 0; i < mapTileWidth; i++) {
			heightMap[i] = 150;
		}
		int hillHeightLimit = random.nextInt(75);
		boolean rising = true;
		boolean flatLand = false;
		int flatLandCounter = 0;
		int currentHillHeight = 150;
		int stepLimit = random.nextInt(100) + 1;
		for (int i = 0; i < mapTileWidth; i++) {
			if (currentHillHeight <= hillHeightLimit) {
				rising = true;
			}
			if (currentHillHeight >= 50 + hillHeightLimit) {
				rising = false;
			}
			if (!flatLand) {
				int flatChance = random.nextInt(100) + 1;
				if (flatChance < 5) {
					flatLand = true;
					flatLandCounter = i;
				}
			}
			if (flatLand && flatLandCounter > 15) {
				if (flatLandCounter < 20) {
					int stop = random.nextInt(100) + 1;
					if (stop == 1) {
						flatLand = false;
					}
				} else {
					int stop = random.nextInt(100) + 1;
					if (stop < 5) {
						flatLand = false;
					}
				}
				if (!flatLand) {
					flatLandCounter = 0;
				}
			}
			int step = 0;
			if (flatLand) {
				step = random.nextInt(100) + 1;
			}
			if (stepLimit > 50) {
				step = random.nextInt(30) + 1;
				if (step < 10) {
					step = 1;
				} else if (step > 10 && step < 25) {
					step = 0;
				} else {
					step = 2;
				}
			} else {
				step = random.nextInt(20) + 1;
				if (step < 15) {
					step = 0;
				} else {
					step = 1;
				}
			}
			int direction = random.nextInt(100) + 1;
			if (rising) {
				if (direction > 20) {
					currentHillHeight += step;
				} else {
					currentHillHeight -= step;
				}
			} else {
				if (direction < 20 && currentHillHeight > hillHeightLimit) {
					currentHillHeight += step;
				} else {
					currentHillHeight -= step;
				}
			}
			try {
				tile[i][currentHillHeight].setID(0);
				heightMap[i] = currentHillHeight;
			} catch (Exception e) {

			}
		}
		System.out.println("Hills Created in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		
		// Place the grass
		for (int y = 1; y < mapTileHeight; y++) {
			for (int x = 0; x < mapTileWidth; x++) {
				if (tile[x][y].getID() == 1 && tile[x][y - 1].getID() == 0) {
					tile[x][y].setID(2);
				}
			}
		}
		System.out.println("Grass Placed in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		
		// Add diamond
		for (int y = 400; y < mapTileHeight; y++) {
			for (int x = 0; x < mapTileWidth; x++) {
				int randomSeed = random.nextInt(600) + 1;
				if ((y > 400 && y < 550) && randomSeed == 1) {
					miners.add(new Miner(x, y, 6, "None"));
				} else if ((y > 550 && y < 800) && randomSeed < 3) {
					miners.add(new Miner(x, y, 6, "None"));
				} else if ((y > 800 && y < 1000) && randomSeed < 4) {
					miners.add(new Miner(x, y, 6, "None"));
				}
			}
		}
		System.out.println("Diamond miners Created in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		minersDig(7);
		System.out.println("Diamond placed in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		miners.clear();
		
		// Add gold
		for (int y = 200; y < mapTileHeight; y++) {
			for (int x = 0; x < mapTileWidth; x++) {
				int randomSeed = random.nextInt(600) + 1;
				if ((y > 200 && y < 350) && randomSeed == 1) {
					miners.add(new Miner(x, y, 5, "None"));
				} else if ((y > 350 && y < 800) && randomSeed < 3) {
					miners.add(new Miner(x, y, 5, "None"));
				} else if ((y > 800 && y < 1000) && randomSeed < 4) {
					miners.add(new Miner(x, y, 5, "None"));
				}
			}
		}
		System.out.println("Gold miners Created in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		minersDig(7);
		System.out.println("Gold placed in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		miners.clear();
		
		// Add iron
		for (int y = 150; y < mapTileHeight; y++) {
			for (int x = 0; x < mapTileWidth; x++) {
				int randomSeed = random.nextInt(500) + 1;
				if ((y > 100 && y < 150) && randomSeed == 1) {
					miners.add(new Miner(x, y, 4, "None"));
				} else if ((y > 150 && y < 500) && randomSeed < 3) {
					miners.add(new Miner(x, y, 4, "None"));
				} else if ((y > 500 && y < 1000) && randomSeed < 4) {
					miners.add(new Miner(x, y, 4, "None"));
				}
			}
		}
		System.out.println("Iron miners Created in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		minersDig(10);
		System.out.println("Iron placed in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		miners.clear();
		
		// Add stone
		for (int x = 0; x < mapTileWidth; x++) {
			for (int y = heightMap[x] + 10; y < mapTileHeight; y++) {
				int randomSeed = random.nextInt(100) + 1;
				if ((y > 50 && y < 100) && randomSeed == 1) {
					miners.add(new Miner(x, y, 3, "None"));
				} else if ((y > 100 && y < 200) && randomSeed < 2) {
					miners.add(new Miner(x, y, 3, "None"));
				} else if ((y > 200 && y < 800) && randomSeed < 4) {
					miners.add(new Miner(x, y, 3, "None"));
				} else if ((y > 800 && y < 1000) && randomSeed < 6) {
					miners.add(new Miner(x, y, 3, "None"));
				}
			}
		}
		System.out.println("Stone Miners Created in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		minersDig(40);
		System.out.println("Stone placed in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		miners.clear();
		
		// Create caves
		for (int y = 0; (y * 100) + 50 < mapTileHeight; y++) {
			for (int x = 0; (x * 100) + 50 < mapTileWidth; x++) {
				miners.add(new Miner((x * 100) + 75, (y * 100) + 50, 0, "None"));
				miners.add(new Miner((x * 100) + 75, (y * 100) + 50, 0, "None"));
			}
		}
		System.out.println("Air Miners Created in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		minersDig(5000);
		System.out.println("Air dug in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		miners.clear();
		System.out.println("Miners Removed in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		
		// Clean up any tiles above the height map
		for (int i = 0; i < mapTileWidth; i++) {
			for (int x = heightMap[i] - 1; x > -1; x--) {
				tile[i][x].setID(0);
			}
		}
		
		// Clean up tiles
		for (int x = 0; x < mapTileWidth; x++) {
			for (int y = 0; y < mapTileHeight; y++) {
				try {
					if (tile[x][y].getID() != 0) {
						if (tile[x + 1][y].getID() == 0 && tile[x - 1][y].getID() == 0 && tile[x][y - 1].getID() == 0 && tile[x][y + 1].getID() == 0) {
							tile[x][y].setID(0);
						}
						if (tile[x - 1][y].getID() == 0 && tile[x][y - 1].getID() == 0 && tile[x][y + 1].getID() == 0 && tile[x + 1][y].getID() != 0 && tile[x + 1][y - 1].getID() == 0
								&& tile[x + 1][y + 1].getID() == 0 && tile[x + 2][y].getID() == 0) {
							tile[x][y].setID(0);
							tile[x + 1][y].setID(0);
						}
						if (tile[x][y - 1].getID() == 0 && tile[x - 1][y].getID() == 0 && tile[x + 1][y].getID() == 0 && tile[x][y + 1].getID() != 0 && tile[x - 1][y + 1].getID() == 0
								&& tile[x + 1][y + 1].getID() == 0 && tile[x][y + 2].getID() == 0) {
							tile[x][y].setID(0);
							tile[x][y + 1].setID(0);
						}
					}
				} catch (Exception e) {
					continue;
				}
			}
		}
		System.out.println("Tiles Cleaned in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		
		// Plant tree seeds, then grow trees
		for (int x = 1; x < heightMap.length; x++) {
			int plantTree = random.nextInt(130) + 1;
			if (tile[x][heightMap[x] + 1].getID() != 0) {
				boolean isGood = true;
				for (int i = 0; i < 5; i++) {
					if (tile[i][heightMap[i] + 1].getID() == 0) {
						isGood = false;
					}
				}
				if (isGood) {
					if (plantTree < 10) {
						if (tile[x][heightMap[x] + 2].getID() != 0) {
							tile[x][heightMap[x] + 1].setID(7);
						}
					}
				} else {
					if (plantTree == 1) {
						if (tile[x][heightMap[x] + 2].getID() != 0) {
							tile[x][heightMap[x] + 1].setID(7);
						}
					}
				}
			}
			if (tile[x - 1][heightMap[x - 1] + 1].getID() == 7) {
				tile[x][heightMap[x] + 1].setID(2);
			}
		}
		System.out.println("Tree Seeds Planted in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		
		// Grow trees
		for (int x = 0; x < heightMap.length; x++) {
			if (tile[x][heightMap[x] + 1].getID() == 7) {
				int treeHeight = random.nextInt(15) + 5;
				for (int y = heightMap[x]; y > heightMap[x] - treeHeight; y--) {
					try {
						tile[x][y].setID(7);
						if (y - 1 == heightMap[x] - treeHeight) {
							tile[x][y].setID(2);
							for (int i = 0; i < treeHeight / 3; i++) {
								for (int z = 0; z < treeHeight / 3; z++) {
									tile[x - i][y].setID(9);
									tile[x + i][y].setID(9);
									tile[x][y - i].setID(9);
									tile[x][y + i].setID(9);
									tile[x - i][y - z].setID(9);
									tile[x - i][y + z].setID(9);
									tile[x + i][y - z].setID(9);
									tile[x + i][y + z].setID(9);
								}
							}
						}
					} catch (Exception e) {

					}
				}
			}
		}
		System.out.println("Trees grown in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		
		// Set the map boundaries (so the player can't leave the map)
		for (int x = 0; x < mapTileWidth; x++) {
			for (int y = 0; y < 30; y++) {
				tile[x][y].setID(-1);
			}
			for (int y = mapTileHeight - 30; y < mapTileHeight; y++) {
				tile[x][y].setID(-1);
			}
		}
		for (int y = 0; y < mapTileHeight; y++) {
			for (int x = 0; x < 30; x++) {
				tile[x][y].setID(-1);
			}
			for (int x = mapTileWidth - 30; x < mapTileWidth; x++) {
				tile[x][y].setID(-1);
			}
		}
		System.out.println("Boundaries set in : " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		System.out.println("Map Generated in : " + (System.currentTimeMillis() - initialTime) + " ms");
	}

	/* This method just performs the 'dig' action on the miners array list.
	 * It just places tiles randomly around an initial miner. The tile ID
	 * is defined by the objects in the array list. */
	public void minersDig(int counter) {
		for (int i = 0; i < counter; i++) {
			for (int m = 0; m < miners.size(); m++) {
				try {
					tile[miners.get(m).getxTile()][miners.get(m).getyTile()].setID(miners.get(m).getplaceID());
					int randomDireciton = random.nextInt(100) + 1;
					if (miners.get(m).getPreferedSide().equals("Vertical")) {
						if (randomDireciton < 30) {
							miners.get(m).setyTile(miners.get(m).getyTile() + 1);
						} else if (randomDireciton >= 30 && randomDireciton < 60) {
							miners.get(m).setyTile(miners.get(m).getyTile() - 1);
						} else if (randomDireciton >= 60 && randomDireciton < 80) {
							miners.get(m).setxTile(miners.get(m).getxTile() - 1);
						} else {
							miners.get(m).setxTile(miners.get(m).getxTile() + 1);
						}
					} else if (miners.get(m).getPreferedSide().equals("None")) {
						if (randomDireciton < 25) {
							miners.get(m).setyTile(miners.get(m).getyTile() + 1);
						} else if (randomDireciton >= 25 && randomDireciton < 50) {
							miners.get(m).setyTile(miners.get(m).getyTile() - 1);
						} else if (randomDireciton >= 50 && randomDireciton < 75) {
							miners.get(m).setxTile(miners.get(m).getxTile() - 1);
						} else {
							miners.get(m).setxTile(miners.get(m).getxTile() + 1);
						}
					}
				} catch (Exception e) {

				}
			}
		}
	}

	/* This method initializes all the images used in the game. This is supposed
	 * to be in the init method, we just got annoyed by all the scrolling because
	 * it is really long, so we put it into a method. */
	public void initializeImages() {
		playerSprites = new Image[40];
		playerSprites[0] = getImage(getCodeBase(), "Player Still Right.png");
		playerSprites[1] = getImage(getCodeBase(), "Player Jump Right.png");
		playerSprites[2] = getImage(getCodeBase(), "Player Walk 1 Right.png");
		playerSprites[3] = getImage(getCodeBase(), "Player Walk 2 Right.png");
		playerSprites[4] = getImage(getCodeBase(), "Player Walk 3 Right.png");
		playerSprites[5] = getImage(getCodeBase(), "Player Walk 4 Right.png");
		playerSprites[6] = getImage(getCodeBase(), "Player Walk 5 Right.png");
		playerSprites[7] = getImage(getCodeBase(), "Player Walk 6 Right.png");
		playerSprites[8] = getImage(getCodeBase(), "Player Walk 7 Right.png");
		playerSprites[9] = getImage(getCodeBase(), "Player Walk 8 Right.png");
		playerSprites[10] = getImage(getCodeBase(), "Player Walk 9 Right.png");
		playerSprites[11] = getImage(getCodeBase(), "Player Walk 10 Right.png");
		playerSprites[12] = getImage(getCodeBase(), "Player Walk 11 Right.png");
		playerSprites[13] = getImage(getCodeBase(), "Player Walk 12 Right.png");
		playerSprites[14] = getImage(getCodeBase(), "Player Walk 13 Right.png");
		playerSprites[15] = getImage(getCodeBase(), "Player Walk 14 Right.png");
		playerSprites[16] = getImage(getCodeBase(), "Player Attack 1 Right.png");
		playerSprites[17] = getImage(getCodeBase(), "Player Attack 2 Right.png");
		playerSprites[18] = getImage(getCodeBase(), "Player Attack 3 Right.png");
		playerSprites[19] = getImage(getCodeBase(), "Player Attack 4 Right.png");
		playerSprites[20] = getImage(getCodeBase(), "Player Still Left.png");
		playerSprites[21] = getImage(getCodeBase(), "Player Jump Left.png");
		playerSprites[22] = getImage(getCodeBase(), "Player Walk 1 Left.png");
		playerSprites[23] = getImage(getCodeBase(), "Player Walk 2 Left.png");
		playerSprites[24] = getImage(getCodeBase(), "Player Walk 3 Left.png");
		playerSprites[25] = getImage(getCodeBase(), "Player Walk 4 Left.png");
		playerSprites[26] = getImage(getCodeBase(), "Player Walk 5 Left.png");
		playerSprites[27] = getImage(getCodeBase(), "Player Walk 6 Left.png");
		playerSprites[28] = getImage(getCodeBase(), "Player Walk 7 Left.png");
		playerSprites[29] = getImage(getCodeBase(), "Player Walk 8 Left.png");
		playerSprites[30] = getImage(getCodeBase(), "Player Walk 9 Left.png");
		playerSprites[31] = getImage(getCodeBase(), "Player Walk 10 Left.png");
		playerSprites[32] = getImage(getCodeBase(), "Player Walk 11 Left.png");
		playerSprites[33] = getImage(getCodeBase(), "Player Walk 12 Left.png");
		playerSprites[34] = getImage(getCodeBase(), "Player Walk 13 Left.png");
		playerSprites[35] = getImage(getCodeBase(), "Player Walk 14 Left.png");
		playerSprites[36] = getImage(getCodeBase(), "Player Attack 1 Left.png");
		playerSprites[37] = getImage(getCodeBase(), "Player Attack 2 Left.png");
		playerSprites[38] = getImage(getCodeBase(), "Player Attack 3 Left.png");
		playerSprites[39] = getImage(getCodeBase(), "Player Attack 4 Left.png");
		enemySprites = new Image[2];
		enemySprites[0] = getImage(getCodeBase(), "Enemy.png");
		enemySprites[1] = getImage(getCodeBase(), "Enemy Jump.png");
		Image dirtTileSheet = getImage(getCodeBase(), "Tile-01.png");
		dirtSheet = new Image[48];
		sheetCounter = 0;
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 10; x++) {
				if (sheetCounter < dirtSheet.length) {
					dirtSheet[sheetCounter] = createImage(new FilteredImageSource(dirtTileSheet.getSource(), new CropImageFilter(x * 16, y * 16, 16, 16)));
				}
				sheetCounter++;
			}
		}
		Image grassTileSheet = getImage(getCodeBase(), "Tile-02.png");
		grassSheet = new Image[48];
		sheetCounter = 0;
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 10; x++) {
				if (sheetCounter < grassSheet.length) {
					grassSheet[sheetCounter] = createImage(new FilteredImageSource(grassTileSheet.getSource(), new CropImageFilter(x * 16, y * 16, 16, 16)));
				}
				sheetCounter++;
			}
		}
		Image stoneTileSheet = getImage(getCodeBase(), "Tile-03.png");
		stoneSheet = new Image[48];
		sheetCounter = 0;
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 10; x++) {
				if (sheetCounter < stoneSheet.length) {
					stoneSheet[sheetCounter] = createImage(new FilteredImageSource(stoneTileSheet.getSource(), new CropImageFilter(x * 16, y * 16, 16, 16)));
				}
				sheetCounter++;
			}
		}
		Image ironTileSheet = getImage(getCodeBase(), "Tile-04.png");
		ironSheet = new Image[48];
		sheetCounter = 0;
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 10; x++) {
				if (sheetCounter < ironSheet.length) {
					ironSheet[sheetCounter] = createImage(new FilteredImageSource(ironTileSheet.getSource(), new CropImageFilter(x * 16, y * 16, 16, 16)));
				}
				sheetCounter++;
			}
		}
		Image goldTileSheet = getImage(getCodeBase(), "Tile-05.png");
		goldSheet = new Image[48];
		sheetCounter = 0;
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 10; x++) {
				if (sheetCounter < goldSheet.length) {
					goldSheet[sheetCounter] = createImage(new FilteredImageSource(goldTileSheet.getSource(), new CropImageFilter(x * 16, y * 16, 16, 16)));
				}
				sheetCounter++;
			}
		}
		Image diamondTileSheet = getImage(getCodeBase(), "Tile-06.png");
		diamondSheet = new Image[48];
		sheetCounter = 0;
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 10; x++) {
				if (sheetCounter < diamondSheet.length) {
					diamondSheet[sheetCounter] = createImage(new FilteredImageSource(diamondTileSheet.getSource(), new CropImageFilter(x * 16, y * 16, 16, 16)));
				}
				sheetCounter++;
			}
		}
		Image woodTileSheet = getImage(getCodeBase(), "Tile-07.png");
		woodSheet = new Image[48];
		sheetCounter = 0;
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 10; x++) {
				if (sheetCounter < woodSheet.length) {
					woodSheet[sheetCounter] = createImage(new FilteredImageSource(woodTileSheet.getSource(), new CropImageFilter(x * 16, y * 16, 16, 16)));
				}
				sheetCounter++;
			}
		}
		Image plankTileSheet = getImage(getCodeBase(), "Tile-08.png");
		plankSheet = new Image[48];
		sheetCounter = 0;
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 10; x++) {
				if (sheetCounter < plankSheet.length) {
					plankSheet[sheetCounter] = createImage(new FilteredImageSource(plankTileSheet.getSource(), new CropImageFilter(x * 16, y * 16, 16, 16)));
				}
				sheetCounter++;
			}
		}
		Image leafTileSheet = getImage(getCodeBase(), "Tile-09.png");
		leafSheet = new Image[48];
		sheetCounter = 0;
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 10; x++) {
				if (sheetCounter < leafSheet.length) {
					leafSheet[sheetCounter] = createImage(new FilteredImageSource(leafTileSheet.getSource(), new CropImageFilter(x * 16, y * 16, 16, 16)));
				}
				sheetCounter++;
			}
		}
	}

	/* Goes through the entire map, and updates the images of the tiles. */
	public void updateAllTiles() {
		for (int x = 0; x < mapTileWidth; x++) {
			for (int y = 0; y < mapTileHeight; y++) {
				try {
					updateTile(x, y);
				} catch (Exception e) {

				}
			}
		}
	}

	/* Does the exact same thing as updateAllTiles(), except for only one tile. */
	public void updateTile(int x, int y) {
		int left = tile[x - 1][y].getID();
		int right = tile[x + 1][y].getID();
		int up = tile[x][y - 1].getID();
		int down = tile[x][y + 1].getID();
		int ID = tile[x][y].getID();
		int randomTileImage = 0;
		try {
			// Same ID all around
			if (left == ID && up == ID && right == ID && down == ID) {
				randomTileImage = random.nextInt(3);
			}
			// Up is not the same ID
			if (left == ID && up != ID && right == ID && down == ID) {
				randomTileImage = random.nextInt(3) + 3;
			}
			// Down is not the same ID
			if (left == ID && up == ID && right == ID && down != ID) {
				randomTileImage = random.nextInt(3) + 6;
			}
			// Left is not the same ID
			if (left != ID && up == ID && right == ID && down == ID) {
				randomTileImage = random.nextInt(3) + 9;
			}
			// Right is not the same ID
			if (left == ID && up == ID && right != ID && down == ID) {
				randomTileImage = random.nextInt(3) + 12;
			}
			// Left and Up are not the same ID
			if (left != ID && up != ID && right == ID && down == ID) {
				randomTileImage = random.nextInt(3) + 15;
			}
			// Up and Right are not the same ID
			if (left == ID && up != ID && right != ID && down == ID) {
				randomTileImage = random.nextInt(3) + 18;
			}
			// Left and Down are not the same ID
			if (left != ID && up == ID && right == ID && down != ID) {
				randomTileImage = random.nextInt(3) + 21;
			}
			// Right and Down are not the same ID
			if (left == ID && up == ID && right != ID && down != ID) {
				randomTileImage = random.nextInt(3) + 24;
			}
			// Down is the same ID
			if (left != ID && up != ID && right != ID && down == ID) {
				randomTileImage = random.nextInt(3) + 27;
			}
			// Up is the same ID
			if (left != ID && up == ID && right != ID && down != ID) {
				randomTileImage = random.nextInt(3) + 30;
			}
			// Right is the same ID
			if (left != ID && up != ID && right == ID && down != ID) {
				randomTileImage = random.nextInt(3) + 33;
			}
			// Left is the same ID
			if (left == ID && up != ID && right != ID && down != ID) {
				randomTileImage = random.nextInt(3) + 36;
			}
			// Left and Right are not the same ID
			if (left != ID && up == ID && right != ID && down == ID) {
				randomTileImage = random.nextInt(3) + 39;
			}
			// Up and Down are not the same ID
			if (left == ID && up != ID && right == ID && down != ID) {
				randomTileImage = random.nextInt(3) + 42;
			}
			// All sides are different ID
			if (left != ID && up != ID && right != ID && down != ID) {
				randomTileImage = random.nextInt(3) + 45;
			}

			tile[x][y].setTileImage(randomTileImage);

		} catch (Exception e) {

		}
	}

	// Moves all the enemies
	public void moveEnemies() {
		for (int enemy = 0; enemy < enemies.size(); enemy++) {
			// Set enemy variables
			enemyRect.setBounds(enemies.get(enemy).getxPos() - mapxOffset, enemies.get(enemy).getyPos() - mapyOffset, 40, enemySprites[enemies.get(enemy).getSprite()].getHeight(this));
			enemyXDistance = enemies.get(enemy).getxPos() / tileLength;
			enemyYDistance = enemies.get(enemy).getyPos() / tileLength;
			startEnemyRectsX = enemyXDistance - 5;
			startEnemyRectsY = enemyYDistance - 5;
			for (int x = startEnemyRectsX; x < startEnemyRectsX + enemyRectsSize; x++) {
				for (int y = startEnemyRectsY; y < startEnemyRectsY + enemyRectsSize; y++) {
					enemyRects[x][y] = new Rectangle();
					enemyRects[x][y].setBounds(x * tileLength - mapxOffset, y * tileLength - mapyOffset, tileLength, tileLength);
				}
			}
			enemyMoveRect.setBounds(enemyRect);
			
			// Move enemy right
			if (enemies.get(enemy).enemyDirection[0]) {
				enemyMoveRect.translate(enemies.get(enemy).getxVel(), 0);
				boolean collides = false;
				for (int x = startEnemyRectsX; x < startEnemyRectsX + enemyRectsSize; x++) {
					for (int y = startEnemyRectsY; y < startEnemyRectsY + enemyRectsSize; y++) {
						if (enemyMoveRect.intersects(enemyRects[x][y])) {
							if (tile[x][y].getID() != 0 && tile[x][y].getID() != 7 && tile[x][y].getID() != 9) {
								collides = true;
							}
						}
					}
				}
				if (!collides) {
					enemies.get(enemy).setxPos(enemies.get(enemy).getxPos() + enemies.get(enemy).getxVel());
				} else {
					int jump = random.nextInt(100) + 1;
					if (jump < 10) {
						enemies.get(enemy).enemyDirection[2] = true;
					}
				}
			}
			
			// Move enemy left
			if (enemies.get(enemy).enemyDirection[1]) {
				enemyMoveRect.translate(-enemies.get(enemy).getxVel(), 0);
				boolean collides = false;
				for (int x = startEnemyRectsX; x < startEnemyRectsX + enemyRectsSize; x++) {
					for (int y = startEnemyRectsY; y < startEnemyRectsY + enemyRectsSize; y++) {
						if (enemyMoveRect.intersects(enemyRects[x][y])) {
							if (tile[x][y].getID() != 0 && tile[x][y].getID() != 7 && tile[x][y].getID() != 9) {
								collides = true;
							}
						}
					}
				}
				if (!collides) {
					enemies.get(enemy).setxPos(enemies.get(enemy).getxPos() - enemies.get(enemy).getxVel());
				} else {
					int jump = random.nextInt(100) + 1;
					if (jump < 10) {
						enemies.get(enemy).enemyDirection[2] = true;
					}
				}
			}
			
			// Make enemy jump
			if (enemies.get(enemy).enemyDirection[2]) {
				if (enemies.get(enemy).getyVel() < 0) {
					enemyMoveRect.translate(0, -5);
				} else {
					enemyMoveRect.translate(0, 5);
				}
				boolean collides = false;
				int rectMinY = 0;
				int enemyLeftCenterX = (int) enemyMoveRect.getCenterX() - 5;
				int enemyMinY = (int) enemyMoveRect.getMinY();
				int enemyMaxY = (int) enemyMoveRect.getMaxY();
				for (int x = startEnemyRectsX; x < startEnemyRectsX + enemyRectsSize; x++) {
					for (int y = startEnemyRectsY; y < startEnemyRectsY + enemyRectsSize; y++) {
						if (enemyRects[x][y].contains(enemyLeftCenterX, enemyMaxY) || enemyRects[x][y].contains(enemyLeftCenterX + 10, enemyMaxY)
								|| enemyRects[x][y].contains(enemyLeftCenterX, enemyMinY) || enemyRects[x][y].contains(enemyLeftCenterX + 10, enemyMinY)) {
							if (tile[x][y].getID() != 0 && tile[x][y].getID() != 7 && tile[x][y].getID() != 9) {
								collides = true;
								rectMinY = (int) enemyRects[x][y].getMinY();
							}
						}

					}
				}
				if (!collides) {
					enemies.get(enemy).setyPos(enemies.get(enemy).getyPos() + enemies.get(enemy).getyVel());
					enemies.get(enemy).setSprite(1);
				} else {
					if (enemies.get(enemy).getyVel() < 0) {
						enemies.get(enemy).setyVel(0);
					}
					if (enemies.get(enemy).getyVel() > 0) {
						int difference = rectMinY - (int) enemyRect.getMaxY();
						enemies.get(enemy).setyPos(enemies.get(enemy).getyPos() + difference);
						enemies.get(enemy).setyVel(enemies.get(enemy).getInitialyVel());
						enemies.get(enemy).enemyDirection[2] = false;
					}
					enemies.get(enemy).setSprite(0);
				}
				if (enemies.get(enemy).getyVel() < 10) {
					enemies.get(enemy).setyVel(enemies.get(enemy).getyVel() + enemies.get(enemy).getGravity());
				}
			}
			
			// Make enemy fall
			if (!enemies.get(enemy).enemyDirection[2]) {
				enemyMoveRect.translate(0, 5);
				boolean collides = false;
				for (int x = startEnemyRectsX; x < startEnemyRectsX + enemyRectsSize; x++) {
					for (int y = startEnemyRectsY; y < startEnemyRectsY + enemyRectsSize; y++) {
						if (enemyMoveRect.intersects(enemyRects[x][y])) {
							if (tile[x][y].getID() != 0 && tile[x][y].getID() != 7 && tile[x][y].getID() != 9) {
								collides = true;
							}
						}
					}
				}
				if (!collides) {
					enemies.get(enemy).setyVel(3);
					enemies.get(enemy).enemyDirection[2] = true;
				}
			}
			
			// Enemy AI to follow player or just roam the map
			if (enemies.get(enemy).getxPos() - mapxOffset < player.getxPos() && enemies.get(enemy).getxPos() - mapxOffset > player.getxPos() - 20 * tileLength) {
				enemies.get(enemy).enemyDirection[0] = true;
				enemies.get(enemy).enemyDirection[1] = false;
			} else if (enemies.get(enemy).getxPos() - mapxOffset > player.getxPos() && enemies.get(enemy).getxPos() - mapxOffset < player.getxPos() + 20 * tileLength) {
				enemies.get(enemy).enemyDirection[0] = false;
				enemies.get(enemy).enemyDirection[1] = true;
			} else {
				int directionCounter = (int) (1 + 50 * Math.random());
				if (directionCounter > 49) {
					if (enemies.get(enemy).enemyDirection[0]) {
						enemies.get(enemy).enemyDirection[0] = false;
						enemies.get(enemy).enemyDirection[1] = true;
					} else {
						enemies.get(enemy).enemyDirection[0] = true;
						enemies.get(enemy).enemyDirection[1] = false;
					}
				}

			}
		}
	}

	// Moves the player
	public void movePlayer() {
		// Set player Variables
		int initialMapXOffset = mapxOffset;
		int initialMapYOffset = mapyOffset;
		playerXDistance = (player.xPos + mapxOffset) / tileLength;
		playerYDistance = (player.yPos + mapyOffset) / tileLength;
		startPlayerRectsX = playerXDistance - 3;
		startPlayerRectsY = playerYDistance - 3;
		for (int x = startPlayerRectsX; x < startPlayerRectsX + playerRectsSize; x++) {
			for (int y = startPlayerRectsY; y < startPlayerRectsY + playerRectsSize; y++) {
				playerRects[x][y] = new Rectangle();
				playerRects[x][y].setBounds(x * tileLength - mapxOffset, y * tileLength - mapyOffset, tileLength, tileLength);
			}
		}
		playerMoveRect.setBounds(playerRect);
		
		// Move player right
		if (player.playerDirection[0]) {
			playerMoveRect.translate(player.xVel, 0);
			boolean collides = false;
			for (int x = startPlayerRectsX; x < startPlayerRectsX + playerRectsSize; x++) {
				for (int y = startPlayerRectsY; y < startPlayerRectsY + playerRectsSize; y++) {
					if (playerMoveRect.intersects(playerRects[x][y])) {
						if (tile[x][y].getID() != 0 && tile[x][y].getID() != 7 && tile[x][y].getID() != 9) {
							collides = true;
						}
					}
				}
			}
			if (!collides) {
				mapxOffset += player.xVel;
				player.setSprite(updatePlayerWalkCounter(player.getWalkCounter(), 0));
			}
			player.setLastDirection(0);
		}
		
		// Move player left
		if (player.playerDirection[1]) {
			playerMoveRect.translate(-player.xVel, 0);
			boolean collides = false;
			for (int x = startPlayerRectsX; x < startPlayerRectsX + playerRectsSize; x++) {
				for (int y = startPlayerRectsY; y < startPlayerRectsY + playerRectsSize; y++) {
					if (playerMoveRect.intersects(playerRects[x][y])) {
						if (tile[x][y].getID() != 0 && tile[x][y].getID() != 7 && tile[x][y].getID() != 9) {
							collides = true;
						}
					}
				}
			}
			if (!collides) {
				mapxOffset -= player.xVel;
				player.setSprite(updatePlayerWalkCounter(player.getWalkCounter(), 1));
			}
			player.setLastDirection(1);
		}
		
		// Makes the player jump
		if (player.playerDirection[2]) {
			if (player.yVel < 0) {
				playerMoveRect.translate(0, -5);
			} else {
				playerMoveRect.translate(0, 5);
			}
			boolean collides = false;
			int rectMinY = 0;
			int playerLeftCenterX = (int) playerMoveRect.getMinX() + player.xVel + 1;
			int playerRightCenterX = (int) playerMoveRect.getMaxX() - player.xVel - 1;
			int playerCenterX = (int) playerMoveRect.getCenterX();
			int playerMinY = (int) playerMoveRect.getMinY();
			int playerMaxY = (int) playerMoveRect.getMaxY();
			for (int x = startPlayerRectsX; x < startPlayerRectsX + playerRectsSize; x++) {
				for (int y = startPlayerRectsY; y < startPlayerRectsY + playerRectsSize; y++) {
					if (playerRects[x][y].contains(playerLeftCenterX, playerMaxY) || playerRects[x][y].contains(playerRightCenterX, playerMaxY)
							|| playerRects[x][y].contains(playerCenterX - 2, playerMinY) || playerRects[x][y].contains(playerCenterX + 2, playerMinY)) {
						if (tile[x][y].getID() != 0 && tile[x][y].getID() != 7 && tile[x][y].getID() != 9) {
							collides = true;
							rectMinY = (int) playerRects[x][y].getMinY();
						}
					}

				}
			}
			if (!collides) {
				mapyOffset += player.yVel;
			} else {
				if (player.yVel < 0) {
					player.yVel = 0;
				}
				if (player.yVel > 0) {
					int difference = rectMinY - (int) playerRect.getMaxY();
					mapyOffset += difference;
					player.yVel = player.initialyVel;
					player.playerDirection[2] = false;
				}
			}
			if (player.yVel < 10) {
				player.yVel += player.gravity;
			}
			if (player.getLastDirection() == 0) {
				player.setSprite(1);
			} else {
				player.setSprite(21);
			}
		}
		
		// Makes the player fall
		if (!player.playerDirection[2]) {
			playerMoveRect.translate(0, 5);
			boolean collides = false;
			for (int x = startPlayerRectsX; x < startPlayerRectsX + playerRectsSize; x++) {
				for (int y = startPlayerRectsY; y < startPlayerRectsY + playerRectsSize; y++) {
					if (playerMoveRect.intersects(playerRects[x][y])) {
						if (tile[x][y].getID() != 0 && tile[x][y].getID() != 7 && tile[x][y].getID() != 9) {
							collides = true;
						}
					}
				}
			}
			if (!collides) {
				player.yVel = 3;
				player.playerDirection[2] = true;
			}
		}
		if (initialMapXOffset == mapxOffset && initialMapYOffset == mapyOffset) {
			if (player.getLastDirection() == 0) {
				player.setSprite(0);
			} else {
				player.setSprite(20);
			}
		}
	}

	/* This method is used when the player is walking. It just cycles through
	 * the sprites of the player to give the illusion that the player is walking. */
	public int updatePlayerWalkCounter(int currentWalkCounter, int direction) {
		int walkCounter = currentWalkCounter;
		globalPlayerWalkCounter++;
		if (globalPlayerWalkCounter % 5 == 0) {
			walkCounter++;
			player.setWalkCounter(walkCounter);
			if (walkCounter > 15) {
				walkCounter = 2;
				player.setWalkCounter(2);
				globalPlayerWalkCounter = 0;
			}
		}
		if (direction == 0) {
			return walkCounter;
		} else {
			return walkCounter + 20;
		}
	}

	// Double buffering
	public void update(Graphics g) {
		if (dbImage == null) {
			dbImage = createImage(this.getSize().width, this.getSize().height);
			dbg = dbImage.getGraphics();
		}
		dbg.setColor(getBackground());
		dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);
		dbg.setColor(getForeground());
		paint(dbg);
		g.drawImage(dbImage, 0, 0, this);
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.playerDirection[0] = true;
			player.playerDirection[1] = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.playerDirection[1] = true;
			player.playerDirection[0] = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.playerDirection[2] = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}

	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.playerDirection[0] = false;
		}

		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.playerDirection[1] = false;
		}
	}

	public void keyTyped(KeyEvent e) {

	}

	/* This method is used when determining what to paint in
	 * the place of a certain inventory slot. */
	public Image getInventoryImage(int invSlot) {
		Image currentImage = null;
		if (!inv[invSlot].isEmpty()) {
			if (inv[invSlot].get(0).getItemID() == 1) {
				currentImage = dirtSheet[46];
			} else if (inv[invSlot].get(0).getItemID() == 2) {
				currentImage = grassSheet[46];
			} else if (inv[invSlot].get(0).getItemID() == 3) {
				currentImage = stoneSheet[46];
			} else if (inv[invSlot].get(0).getItemID() == 4) {
				currentImage = ironSheet[46];
			} else if (inv[invSlot].get(0).getItemID() == 5) {
				currentImage = goldSheet[46];
			} else if (inv[invSlot].get(0).getItemID() == 6) {
				currentImage = diamondSheet[46];
			} else if (inv[invSlot].get(0).getItemID() == 7) {
				currentImage = woodSheet[46];
			} else if (inv[invSlot].get(0).getItemID() == 8) {
				currentImage = plankSheet[46];
			} else if (inv[invSlot].get(0).getItemID() == 9) {
				currentImage = leafSheet[46];
			}
		}
		return currentImage;
	}

	/* This method updates the surrounding tiles of one specific tile.
	 * It just calls the updateTile() method a lot, so it simplifies code. */
	public void updateSurroundingTiles(int x, int y, String action) {
		if (action.equals("dig")) {
			tile[x][y].setID(0);
			tile[x][y].setTileImage(0);
		} else if (action.equals("place")) {
			tile[x][y].setID(inv[inventorySlotSelected].get(0).getItemID());
		}
		updateTile(x, y);
		updateTile(x - 1, y);
		updateTile(x + 1, y);
		updateTile(x, y - 1);
		updateTile(x, y + 1);
	}

	/* This method updates the inventory whenever you pick up or place a block. */
	public void updateInventory(String action, int actionID) {
		if (action.equals("dig")) {
			boolean isInInv = false;
			int slot = 0;
			InventoryObject currentItem = new InventoryObject();
			currentItem.setItemID(actionID);
			for (int i = 0; i < 5; i++) {
				if (!inv[i].isEmpty()) {
					if (actionID == inv[i].get(0).getItemID()) {
						isInInv = true;
						slot = i;
						break;
					}
				}
			}
			if (isInInv) {
				inv[slot].add(currentItem);
			} else {
				int openSlot = 0;
				for (int i = 0; i < 5; i++) {
					if (inv[i].isEmpty()) {
						openSlot = i;
						break;
					}
				}
				inv[openSlot].add(currentItem);
			}
		} else {
			if (!inv[actionID].isEmpty()) {
				inv[actionID].remove(inv[actionID].get(0));
			}
		}
	}

	/* Handles all the mouse clicks */
	public void mouseClicked(MouseEvent e) {
		mouseXClicked = e.getX();
		mouseYClicked = e.getY();
		int mouseXTile = (mouseXClicked + mapxOffset) / tileLength;
		int mouseYTile = (mouseYClicked + mapyOffset) / tileLength;
		boolean placeTile = true;
		boolean inventoryClear = false;
		int clearInvSlot = 0;
		try {
			for (int i = 0; i < inventoryRects.length; i++) {
				if (inventoryRects[i].contains(mouseXClicked, mouseYClicked)) {
					inventoryClear = true;
					clearInvSlot = i;
					break;
				}
			}
			if (inventoryClear) {
				inv[clearInvSlot].clear();
			} else {
				if (tile[mouseXTile][mouseYTile].getID() != -1) {
					if (tile[mouseXTile][mouseYTile].getID() != 0) {
						if (tile[mouseXTile][mouseYTile].getID() == 7) {
							updateInventory("dig", 8);
							updateSurroundingTiles(mouseXTile, mouseYTile, "dig");
						} else {
							updateInventory("dig", tile[mouseXTile][mouseYTile].getID());
							updateSurroundingTiles(mouseXTile, mouseYTile, "dig");
						}
					} else {
						for (int x = startPlayerRectsX; x < startPlayerRectsX + playerRectsSize; x++) {
							for (int y = startPlayerRectsY; y < startPlayerRectsY + playerRectsSize; y++) {
								try {
									if (playerRects[mouseXTile][mouseYTile].intersects(playerRect)) {
										placeTile = false;
									}
								} catch (Exception e2) {

								}
							}
						}
						if (placeTile) {
							if (!inv[inventorySlotSelected].isEmpty()) {
								updateSurroundingTiles(mouseXTile, mouseYTile, "place");
								updateInventory("place", inventorySlotSelected);
							}
						}
					}
				}
			}
		} catch (Exception e1) {

		}
	}

	/* Handles all the mouse drags */
	public void mouseDragged(MouseEvent e) {
		mouseXDragged = e.getX();
		mouseYDragged = e.getY();
		int mouseXTile = (mouseXDragged + mapxOffset) / tileLength;
		int mouseYTile = (mouseYDragged + mapyOffset) / tileLength;
		if (tile[mouseXTile][mouseYTile].getID() == 0) {
			setModifyingTile(0);
		} else {
			setModifyingTile(1);
		}
		try {
			if (tile[mouseXTile][mouseYTile].getID() != -1) {
				if (!tilesModified.contains(tile[mouseXTile][mouseYTile])) {
					int currentTileModifyingID;
					if (tile[mouseXTile][mouseYTile].getID() == 0) {
						currentTileModifyingID = 0;
					} else {
						currentTileModifyingID = 1;
					}
					if (currentTileModifyingID == modifyingTileID) {
						if (tile[mouseXTile][mouseYTile].getID() != 0) {
							if (tile[mouseXTile][mouseYTile].getID() == 7) {
								updateInventory("dig", 8);
								updateSurroundingTiles(mouseXTile, mouseYTile, "dig");
							} else {
								updateInventory("dig", tile[mouseXTile][mouseYTile].getID());
								updateSurroundingTiles(mouseXTile, mouseYTile, "dig");
							}
						} else {
							if (!inv[inventorySlotSelected].isEmpty()) {
								updateSurroundingTiles(mouseXTile, mouseYTile, "place");
								updateInventory("place", inventorySlotSelected);
							}
						}
					}
					tilesModified.add(tile[mouseXTile][mouseYTile]);
				}
			}
		} catch (Exception e1) {

		}
	}

	// A method that is used to make it so that you can only affect either ID 0 tiles or ID != 0 tiles at one time while dragging
	public void setModifyingTile(int ID) {
		if (!dontSetModifyingTileID) {
			modifyingTileID = ID;
			dontSetModifyingTileID = true;
		}
	}

	public void mouseMoved(MouseEvent e) {

	}

	/* Handles the mouse wheel movement to cycle between the inventory slots */
	public void mouseWheelMoved(MouseWheelEvent e) {
		int direction = e.getWheelRotation();
		if (direction > 0) {
			if (inventorySlotSelected < 4) {
				inventorySlotSelected++;
			}
		} else {
			if (inventorySlotSelected > 0) {
				inventorySlotSelected--;
			}
		}
	}

	/* Sets variables whenever mouse is pressed */
	public void mousePressed(MouseEvent e) {
		mouseXDragged = e.getX();
		mouseYDragged = e.getY();
		mouseXTile = (mouseXDragged + mapxOffset) / tileLength;
		mouseYTile = (mouseYDragged + mapyOffset) / tileLength;
		tilesModified = new ArrayList<Tile>();
		modifyingTileID = -1;
		dontSetModifyingTileID = false;
	}

	public void mouseEntered(MouseEvent arg0) {

	}

	public void mouseExited(MouseEvent arg0) {

	}

	public void mouseReleased(MouseEvent arg0) {

	}

}
