import java.awt.Rectangle;
import java.util.Random;


public class Enemy {
	
	int xPos; // x position of the enemy
	int yPos; // y position of the enemy
	int xVel; // walking speed of the enemy
	int yVel; // y speed (up and down) of the enemy
	int initialyVel; // falling speed of the enemy
	int gravity; // gravity effect on the enemy
	int enemyWidth; // width of the enemy
	int enemyHeight; // height of the enemy
	int health; // enemy's health
	int damage; // damage enemy deals
	int rectsX; // radius to check collisions (x)
	int rectsY; // radius to check collisions (y)
	int startRectsX; // starting point to check collisions (x)
	int startRectsY; // starting point to check collisions (y)
	int sprite; // sprite number to be drawn
	boolean[] enemyDirection; // array to hold enemy's direction
	boolean[] lastDirection; // array to hold enemy's previous direction
	Rectangle enemyRect = new Rectangle(); // rectangle for enemy position
	Rectangle enemyMove = new Rectangle(); // rectangle for enemy collision
	Random random = new Random(); // import random values
	
	public Enemy (int xPos, int yPos) {
		enemyDirection = new boolean [3];
		enemyDirection[0] = false;
		enemyDirection[1] = false;
		enemyDirection[2] = false;
		enemyWidth = 20;
		enemyHeight = 20;
		health = 50;
		damage = 1;
		xVel = 1;
		yVel = initialyVel;
		initialyVel = -10;
		gravity = 1;
		
		this.xPos = xPos / 20;
		this.yPos = yPos / 20;
		
	}

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public int getEnemyWidth() {
		return enemyWidth;
	}

	public void setEnemyWidth(int enemyWidth) {
		this.enemyWidth = enemyWidth;
	}

	public int getEnemyHeight() {
		return enemyHeight;
	}

	public int getxVel() {
		return xVel;
	}

	public void setxVel(int xVel) {
		this.xVel = xVel;
	}
	
	public int getyVel() {
		return yVel;
	}

	public void setyVel(int yVel) {
		this.yVel = yVel;
	}

	public boolean[] getEnemyDirection() {
		return enemyDirection;
	}

	public void setEnemyDirection(boolean[] enemyDirection) {
		this.enemyDirection = enemyDirection;
	}

	public void setEnemyHeight(int enemyHeight) {
		this.enemyHeight = enemyHeight;
	}

	public int getInitialyVel() {
		return initialyVel;
	}

	public void setInitialyVel(int initialyVel) {
		this.initialyVel = initialyVel;
	}

	public int getGravity() {
		return gravity;
	}

	public void setGravity(int gravity) {
		this.gravity = gravity;
	}

	public int getSprite() {
		return sprite;
	}

	public void setSprite(int sprite) {
		this.sprite = sprite;
	}
	
}
