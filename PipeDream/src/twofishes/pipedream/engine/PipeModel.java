package twofishes.pipedream.engine;

import twofishes.pipedream.engine.goo.GooChangeListener;

import twofishes.pipedream.engine.goo.GooGeneratedListener;
import twofishes.pipedream.pipe.AbsPipe;
import twofishes.pipedream.pipe.Entrance;
import twofishes.pipedream.pipe.PipeState;
import twofishes.pipedream.tile.Tile;
import twofishes.pipedream.tile.TileModel;

public class PipeModel implements GooGeneratedListener {

	AbsPipe currentPipe = null;

	GooChangeListener gooChangeListener = null;

	TileModel playingField = null;
	
	boolean ignoreWall ;

	public PipeModel(TileModel tileModel, GooChangeListener gooChangeListener, AbsPipe startingPipe, boolean ignoreWall) {
		this.currentPipe = startingPipe;
		this.playingField = tileModel;
		this.gooChangeListener = gooChangeListener;
		this.ignoreWall = ignoreWall ;
	}

	public void gooAdvanced() throws Exception {

		Entrance directionOfGoo = currentPipe.gooAdvance(this.gooChangeListener);

		if (currentPipe.getState(this.gooChangeListener).equals(PipeState.FULL)) {
			boolean stillGoing = this.findNextTileAndPipe(directionOfGoo);
			if (!stillGoing) {
				this.gooChangeListener.gooBlocked();
			}
		}
}

	/**
	 * If the current pipe is connected to the next pipe (based on the direction
	 * of flow) then return true
	 * 
	 * Also initializes the new pipe and sets it to the current
	 * 
	 * @return true if pipe still going, false if not
	 */
	private boolean findNextTileAndPipe(Entrance exitOfLastTile) throws Exception{

		Tile newTile = null;
		AbsPipe newPipe = null;
		
		if(exitOfLastTile.equals(Entrance.END)){
			return false;
		}
		
		if (exitOfLastTile.equals(Entrance.NORTH)) {
			newTile = this.playingField.getTileToTheNorth(
					this.currentPipe.getTile(), ignoreWall);
			newPipe = tryToStartNewPipe(newTile, Entrance.SOUTH);
		} else if (exitOfLastTile.equals(Entrance.SOUTH)) {
			newTile = this.playingField.getTileToTheSouth(
					this.currentPipe.getTile(), ignoreWall);
			newPipe = tryToStartNewPipe(newTile, Entrance.NORTH);
		} else if (exitOfLastTile.equals(Entrance.EAST)) {
			newTile = this.playingField.getTileToTheEast(
					this.currentPipe.getTile(), ignoreWall);
			newPipe = tryToStartNewPipe(newTile, Entrance.WEST);
		} else if (exitOfLastTile.equals(Entrance.WEST)) {
			newTile = this.playingField.getTileToTheWest(
					this.currentPipe.getTile(), ignoreWall);
			newPipe = tryToStartNewPipe(newTile, Entrance.EAST);
		} else if(exitOfLastTile.equals(Entrance.BLOCKED)){
			return false;
		}

		if (newPipe != null) {
			this.currentPipe = newPipe;
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Looks for pipe in the new tile. If none, returns null. Set up the new
	 * pipe for flow. Lock the tile down, set the state, etc. Call the methods
	 * on the tile that ... should be called? whatever.
	 * 
	 * @param pipe
	 */
	private AbsPipe tryToStartNewPipe(Tile newTile, Entrance entrance) throws Exception {
		if (newTile == null) {
			// Then we hit a wall
			return null;
		}

		AbsPipe newPipe = newTile.getCurrentPipe();
		if (null == newPipe) {
			return null;
		}

		Entrance exit = newPipe.getExit(entrance);
		if (exit.equals(Entrance.BLOCKED)) {
			return null;
		}

		newTile.setTileLocked(true);
		newPipe.gooEntering(entrance, gooChangeListener);
		
		return newPipe;
	}
}
