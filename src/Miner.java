
public class Miner {
	
	int xTile; // The current x Tile of the miner
	int yTile; // The current y tile of the miner
	int placeID; // The ID that the miner is placing
	
	String preferedDirection; // The direction that the miner prefers
	
	
	public Miner(int x, int y, int ID, String direction){
		xTile = x;
		yTile = y;
		placeID = ID;
		preferedDirection = direction;
	}

	public int getxTile() {
		return xTile;
	}

	public void setxTile(int xTile) {
		this.xTile = xTile;
	}

	public int getyTile() {
		return yTile;
	}

	public void setyTile(int yTile) {
		this.yTile = yTile;
	}

	public int getplaceID() {
		return placeID;
	}

	public void setplaceID(int iDPlaced) {
		placeID = iDPlaced;
	}

	public String getPreferedSide() {
		return preferedDirection;
	}

	public void setPreferedSide(String preferedSide) {
		this.preferedDirection = preferedSide;
	}

}
