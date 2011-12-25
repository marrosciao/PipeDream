package twofishes.pipedream.tile;

import twofishes.pipedream.pipe.AbsPipe;

//can have multiple pipes going.
//Should only need one TileModel per process
//but that is not currently enforced
//(might be a good idea at some point though)

public class TileModel {

	/**
	 * 
	 * @param numTilesWide how many tiles wide (x-axis) 
	 * @param numTilesHigh how many tiles high (y-axis) 
	 */
	
	private int numTilesWide = 0;
	private int numTilesHigh = 0;
	private Tile[][] tileGrid;
	
	public TileModel(int numTilesWide, int numTilesHigh){
	    this.numTilesHigh = numTilesWide;
	    this.numTilesWide = numTilesHigh;
		tileGrid = new Tile[numTilesWide][numTilesHigh];
		
		for(int x=0; x<numTilesWide; x++){
			for(int y=0; y<numTilesHigh; y++){
				tileGrid[x][y]=new Tile(x,y);
			}
		}
		
	}
	
	public Tile getTile(int x,int y){
		return this.tileGrid[x][y];
	}
	
	/**
	 * Lay down pipe. 
	 * (is that dirty? Sounds familiar)
	 * @param pipe
	 * @param x
	 * @param y
	 * @return true if pipe was placed, false if not
	 */
	public boolean setPipe(AbsPipe pipe, int x, int y){
		Tile tile = this.getTile(x, y);
		if(tile.getCurrentState().equals(TileState.DESTROYING)||tile.isTileLocked()){
			//do nothing
			return false;
		}else if(tile.getCurrentState().equals(TileState.HAS_PIPE)){
			tile.setCurrentState(TileState.DESTROYING);
			//deploy a worker thread to do the animation
			//don't save the tile for later, user has 
			//to click again to actually set it when 
			//the destroy animation is finished.
			return false;
		}else if(tile.getCurrentState().equals(TileState.EMPTY)){
			tile.setCurrentPipe(pipe);
		    //send some pipe.. notification or something.(?)
			//maybe not, and let gui grab it in the next refresh..
			return true;
		}
		return false;
	}
	
	
	public Tile getTileToTheEast(Tile tile){
		if(tile.getX()>this.numTilesWide){
			return getTile(0,tile.getY());
		}
		return getTile(tile.getX()+1,tile.getY());
	}
	
	public Tile getTileToTheWest(Tile tile){
		if(tile.getX()==0){
			return getTile(this.numTilesWide,tile.getY());
		}
		return getTile(tile.getX()-1,tile.getY());
	}
	
	public Tile getTileToTheNorth(Tile tile){
		if(tile.getY()==this.numTilesHigh){
			return getTile(tile.getX(),0);
		}
		return getTile(tile.getX(),tile.getY()+1);
	}
	
	public Tile getTileToTheSouth(Tile tile){
		if(tile.getY()==0){
			return getTile(tile.getX(),this.numTilesHigh);
		}
		return getTile(tile.getX(),tile.getY()-1);
	}
	
	

	
	
}