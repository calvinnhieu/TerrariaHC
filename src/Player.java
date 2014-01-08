
public class Player {
	
	boolean[] playerDirection; // Tells what direction the player is moving in
	
	int playerWidth; // The width of the player
	int playerHeight; // The height of the player
	int xVel; // The x velocity of the player
	int xPos; // The x pos of the player
	int yPos; // The y position of the player
	int yVel; // The y velocity of the player
	int initialyVel; // The initial y vel of the player
	int gravity; // The gravity that affects the player
	int sprite; // The current sprite that is shown
	int lastDirection; // The last direction that the player was facing
	int walkCounter; // Needed for the walking animation of the player

	public Player(int x, int y){
		
		playerDirection = new boolean[3];
		playerDirection[0] = false;
		playerDirection[1] = false;
		playerDirection[2] = false;
		playerWidth = 20 * 2;
		playerHeight = 20 * 3;
		xVel = 5;
		xPos = x;
		yPos = y;
		initialyVel = -10;
		yVel = initialyVel;
		gravity = 1;
		sprite = 0;
		walkCounter = 2;
		
	}

	public boolean[] getPlayerDirection() {
		return playerDirection;
	}

	public void setPlayerDirection(boolean[] playerDirection) {
		this.playerDirection = playerDirection;
	}

	public int getPlayerWidth() {
		return playerWidth;
	}

	public void setPlayerWidth(int playerWidth) {
		this.playerWidth = playerWidth;
	}

	public int getPlayerHeight() {
		return playerHeight;
	}

	public void setPlayerHeight(int playerHeight) {
		this.playerHeight = playerHeight;
	}

	public int getxVel() {
		return xVel;
	}

	public void setxVel(int xVel) {
		this.xVel = xVel;
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

	public int getyVel() {
		return yVel;
	}

	public void setyVel(int yVel) {
		this.yVel = yVel;
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

	public int getLastDirection() {
		return lastDirection;
	}

	public void setLastDirection(int lastDirection) {
		this.lastDirection = lastDirection;
	}
	
	public int getWalkCounter() {
		return walkCounter;
	}

	public void setWalkCounter(int walkCounter) {
		this.walkCounter = walkCounter;
	}
	
}
