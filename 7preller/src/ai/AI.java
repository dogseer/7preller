package ai;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import foundation.Direction;
import foundation.Environment;
import foundation.Map;
import foundation.MapElement;
import foundation.Position;

/**
 * @author      Patricio Reller <reller@mail.hs-ulm.de>
 * @version     1.0 
 * @since       1.0
 */
public class AI {	
	private Map visionMap = new Map();
	private HashSet<Position> visitedCells = new HashSet<Position>();
	private HashSet<Position> unvisitedCells = new HashSet<Position>();
	private LinkedList<Searcher> searcherList = new LinkedList<Searcher>();
	private final int SEARCHER_COUNT = 7;
	private States state = States.INITIALIZING;
	private Position finalGoal;
	private Random random = new Random();

	/**
	 * Public constructor (Default).
	 */
	public AI() {

		return;
	}
	
	/**
	 * Initializes the AI with access to the Environment.
	 * 
	 * @param e the environment.
	 */
	private void initialize(Environment e){
		
		this.visionMap.mergeIn(e);
        this.visionMap.setStart(e.getRefPos());

		for (int i = 0; i < this.SEARCHER_COUNT; i++) {
			searcherList.add(new Searcher(i, this.visionMap.getStart()));
		}
		this.state = States.EXPLORING;
		
		return;
	}
	
	/**
	 * Basic AI movement command. Moves Searcher snr with Environment e.
	 * 
	 * @param snr the Searcher ID
	 * @param e the environment
	 * @return Direction to move the Searcher in Application. Legal movement guaranteed.
	 */
	public Direction move(int snr, Environment e) {
		Direction dir = Direction.SOUTH; //Should not use default
		
		//Initializes if first turn. Should be made with a switch
		if (this.state == States.INITIALIZING)
			initialize(e);
		
		Searcher currentSearcher = this.searcherList.get(snr);
		
		if (this.state == States.EXPLORING){
			//Update the vision map
			this.visionMap.mergeIn(e);
			//Update the visited and unvisited cells sets
			this.visitedCells.add(currentSearcher.getPosition());
			this.unvisitedCells.remove(currentSearcher.getPosition());
			
			//Gets cells around the current searcher to check for finish or unvisited cells, and add the new unvisited cells
			List<Position> cellsAround = currentSearcher.getCellsAround();
			addNewUnvisitedCells(cellsAround);
			//If final found, changes state
			if (finalGoalIsAround(cellsAround)){
				dir = getDirToFinalGoal(currentSearcher, cellsAround);
				this.finalGoal = currentSearcher.getPosition().direct(dir);
				this.state = States.FINALFOUND;
			} else {
				//If current searcher already is traversing to an unvisited cell
				if (currentSearcherIsTraversingWithPath(currentSearcher)){
					dir = getDirFromPath(currentSearcher);
					currentSearcher.direct(dir);
				} else {
				//Move to the nearest position according to the movement priority of the current searcher
					if (isNotYetVisited(cellsAround)){
						dir = getNextTargetDirByPriority(currentSearcher, cellsAround);
						currentSearcher.direct(dir);
					}
					
					//If there are no discoverable cells around, looks with bfs for the nearest one and goes, then repeat
					else {
						currentSearcher.findShortestPathToNearestUnvisitedCell(this.visionMap, this.unvisitedCells);
						dir = getDirFromPath(currentSearcher);
						currentSearcher.direct(dir);
					}
				}
			}
		}
		
		//If final is found but paths are not yet established, define them all and change the state to be handled later
		if (this.state == States.FINALFOUND){
			for (Searcher searcher : searcherList) {
				//Not necessary to handle the searcher that has already finished
				searcher.findShortestPathToGoalNonGreedy(this.finalGoal, this.visionMap);
			}
			this.state = States.TRAVERSINGTOFINAL;
		}
		
		//If all paths to the final goal are set, just traverse to them using the A Star path found before
		if (this.state == States.TRAVERSINGTOFINAL){
			dir = getDirFromPath(currentSearcher);
			currentSearcher.direct(dir);
		}
		
		
		return dir;
	}

	/**
	 * Adds all the Position elements of the list cellsAround to the AI's unvisitedCells list. Guarantees all positions added are inside the Map.
	 * 
	 * @param cellsAround the potential cells to be added
	 */
	private void addNewUnvisitedCells(List<Position> cellsAround) {
		for (Position position : cellsAround) {
			if (position.getColumn() >= 0 && position.getColumn() <=99 && position.getRow() >= 0 && position.getRow() <=99){
				if (this.visionMap.getAt(position) == MapElement.LAND || this.visionMap.getAt(position) == MapElement.FINISH){
					if (!(this.visitedCells.contains(position)) && !(this.unvisitedCells.contains(position))){ //Could avoid the second check by using a good hashcode
						this.unvisitedCells.add(position);
					}
				}
			}
		}
	}

	/**
	 * Returns a preferred direction for a determined Searcher. A preferred direction is a predefined ideal direction for each Searcher, so each one explores in different directions.
	 * 
	 * @param currentSearcher the Searcher to move.
	 * @param cellsAround the potential cells to move the Searcher to.
	 * @return the Direction the Searcher will take.
	 */
	private Direction getNextTargetDirByPriority(Searcher currentSearcher, List<Position> cellsAround) {
		//For each direction priority of the current searcher, look if that direction is unvisited
		List<Direction> directions = currentSearcher.getDirectionsPriorities();
		for (Direction direction : directions) {
			//If it is unvisited, then return the direction to take to that position
			if (this.unvisitedCells.contains(currentSearcher.getPosition().direct(direction))){
				if (moveToDirectionIsPossible(currentSearcher.getPosition(), direction)){
					return direction;
				} else {
//					Direction randomDir = randomDirection();//TODO Could add random when cant move directly
//					if ((moveToDirectionIsPossible(currentSearcher.getPosition(), randomDir))){
//						return randomDir;
//					}
					return Direction.STAY;
				}
			}
		} 
		try {
			throw new Exception("Assumed unvisited around but received none.");
		} catch (Exception e) {
			// TODO Handle outside
			e.printStackTrace();
		}
		
		return Direction.STAY;
	}

	/**
	 * Returns a random direction.
	 * 
	 * @return the random direction.
	 */
	private Direction randomDirection() {
		Direction dir = Direction.NORTH;
		int       cnt = this.random.nextInt(8);
				
		for (Direction d : Direction.values()) {
			if (cnt == 0) {
				dir = d;
				break;
			}
			--cnt;
		}
		
		return dir;
	}

	/**
	 * Returns true if there exist a cell in cellAround that hasn't been visited.
	 * 
	 * @param cellsAround the list of cells
	 * @return boolean
	 */
	private boolean isNotYetVisited(List<Position> cellsAround) {
		for (Position position : cellsAround) {
			if(this.visionMap.getAt(position) == MapElement.LAND || this.visionMap.getAt(position) == MapElement.FINISH){
				if (this.unvisitedCells.contains(position))
					return true;
			}
		}
		return false;
	}

	/**
	 * Gets the next Direction the Searcher should take to go to its next Position in its Path.
	 * 
	 * @param currentSearcher the Searcher
	 * @return the Direction
	 */
	private Direction getDirFromPath(Searcher currentSearcher) {
		if (moveToDirectionIsPossible(currentSearcher.getPosition(), getDirFromTo(currentSearcher.getPosition(), currentSearcher.peekNextPathPosition()))){
			return getDirFromTo(currentSearcher.getPosition(), currentSearcher.popNextPathPosition());//TODO DELETE
		}
		return Direction.STAY;
	}

	/**
	 * Returns true if the movement from Position position in Direction dir is valid.
	 * 
	 * @param position the base Position
	 * @param dir the Direction
	 * @return boolean
	 */
	private boolean moveToDirectionIsPossible(Position position, Direction dir) {
		for (Searcher searcher : this.searcherList) {
			if(searcher.getPosition().equals(position.direct(dir)) && !(searcher.getPosition().equals(this.finalGoal))){
				return false;
			}
		}
		//TODO Other checks needed
		return true;
	}

	/**
	 * Returns true if the current Searcher is currently at some point of traversing its own Path.
	 * 
	 * @param currentSearcher the Searcher
	 * @return boolean
	 */
	private boolean currentSearcherIsTraversingWithPath(Searcher currentSearcher) {
		return currentSearcher.hasPathAssigned();
	}

	/**
	 * Returns the Direction to the Final Goal, when the Final Goal is in the cells Around the current Searcher. Assumes cellsAround are at distance 1 from the Searcher.
	 * 
	 * @param currentSearcher the searcher to move to the Final Goal
	 * @param cellsAround the Direction to take to reach the Final Goal
	 * @return
	 */
	private Direction getDirToFinalGoal(Searcher currentSearcher, List<Position> cellsAround) {
		Position searcherCurrentPosition = currentSearcher.getPosition();
		Position finalGoalPosition = searcherCurrentPosition; //Not necessary to handle the event of no final around 

		for (Position position : cellsAround) {
			if(this.visionMap.getAt(position) == MapElement.FINISH)
				finalGoalPosition = position;
		}
		
		return getDirFromTo(searcherCurrentPosition, finalGoalPosition);
	}
	
	/**
	 * Returns the Direction in order to get from the initial Position from to the final Position to.
	 * 
	 * @param from the initial Position.
	 * @param to the final Position.
	 * @return the Direction
	 */
	private Direction getDirFromTo(Position from, Position to){
		Direction dir;
		int colCurr = from.getColumn();
		int rowCurr = from.getRow();
		int colNext = to.getColumn();
		int rowNext = to.getRow();
		
		int colDir = colNext - colCurr;
		int rowDir = rowNext - rowCurr;
		
		if (colDir == 1 && rowDir == 1)
			dir = Direction.SOUTHEAST;
		else if (colDir == 1 && rowDir == 0)
			dir = Direction.EAST;
		else if (colDir == 1 && rowDir == -1)
			dir = Direction.NORTHEAST;
		else if (colDir == -1 && rowDir == 1)
			dir = Direction.SOUTHWEST;
		else if (colDir == -1 && rowDir == 0)
			dir = Direction.WEST;
		else if (colDir == -1 && rowDir == -1)
			dir = Direction.NORTHWEST;
		else if (colDir == 0 && rowDir == 1)
			dir = Direction.SOUTH;
		else // elif not needed
			dir = Direction.NORTH;
		
		return dir;
	}

	/**
	 * Returns true if the Final Goal is in the cellsAround List.
	 * 
	 * @param cellsAround the List to check.
	 * @return boolean
	 */
	private boolean finalGoalIsAround(List<Position> cellsAround) {
		for (Position position : cellsAround) {
			if(this.visionMap.getAt(position) == MapElement.FINISH)
				return true;
		}
		return false;
	}

}
