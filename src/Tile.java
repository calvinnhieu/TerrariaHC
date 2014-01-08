
public class Tile {
	
	int ID; // The ID of the Tile
	int tileImage; // The image that the time displays
	int xTile; // the x coordinate of the tile
	int yTile; // The y coordinate of the tile
	
	public Tile(int x, int y, int ID){
		xTile = x;
		yTile = y;
		this.ID = ID;
	}
	
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getTileImage() {
		return tileImage;
	}

	public void setTileImage(int tileImage) {
		this.tileImage = tileImage;
	}

	public int getxPos() {
		return xTile;
	}

	public void setxPos(int xPos) {
		this.xTile = xPos;
	}

	public int getyPos() {
		return yTile;
	}

	public void setyPos(int yPos) {
		this.yTile = yPos;
	}
	
}
