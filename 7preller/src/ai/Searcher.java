package ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

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
public class Searcher {
	private int id;
	private Position currentPosition;
	private Path currentPath;
	private List<Direction> directionsPriority;

	/**
	 * Public constructor. Requieres the id of the Searcher and its initial Position.
	 * 
	 * @param id the id.
	 * @param start the initial Position.
	 */
	public Searcher(int id, Position start) {
		this.id = id;
		this.currentPosition = start;
		this.currentPath = new Path();
		this.directionsPriority = new ArrayList<Direction>();
		
		Direction N = Direction.NORTH;
		Direction S = Direction.SOUTH;
		Direction E = Direction.EAST;
		Direction W = Direction.WEST;
		Direction NE = Direction.NORTHEAST;
		Direction SE = Direction.SOUTHEAST;
		Direction NW = Direction.NORTHWEST;
		Direction SW = Direction.SOUTHWEST;
		
		//Should handle magic numbers with an arraylist of arraylists, better
		switch(id){
		case 0:
			this.directionsPriority = Arrays.asList(N, NW, NE, W, E, SW, SE, S);
			break;
		case 1:
			this.directionsPriority = Arrays.asList(NW,N, W, NE, SW, E, S, SE);
			break;
		case 2:
			this.directionsPriority = Arrays.asList(W, SW, NW, S, N, SE, NE, E);
			break;
		case 3:
			this.directionsPriority = Arrays.asList(SW, S, W, SE, NW, E, N, NE);
			break;
		case 4:
			this.directionsPriority = Arrays.asList(NE, N, E, NW, SE, W, S, SW);
			break;
		case 5:
			this.directionsPriority = Arrays.asList(SE, S, E, SW, NE, W, N, NW);
			break;
		case 6:
			this.directionsPriority = Arrays.asList(S, SE, SW, E, W, NE, NW, N);
			break;
		default:
			break; //Should tHrow exception (number of searchers)
		}
		
		return;
	}

	/**
	 * Getter for the Searcher's current position.
	 * 
	 * @return Position the current position.
	 */
	public Position getPosition() {
		return this.currentPosition;
	}

	/**
	 * Returns a List of all the cells that are around the current Searcher. Guarantees values that are inside of the Map.
	 * 
	 * @return cellsAround the list of cells
	 */
	public List<Position> getCellsAround() {
		ArrayList<Position> list = new ArrayList<Position>();
		
		for (int i = 0; i <= Environment.VISIBILITY * 2 ; i++) {
			for (int j = 0; j <= Environment.VISIBILITY * 2; j++) {
				Position pos = new Position(this.currentPosition.getColumn() + i - 1, this.currentPosition.getRow() + j - 1);
				if (pos.getColumn() >= 0 && pos.getColumn() <= 99 && pos.getRow() >= 0 && pos.getRow() <= 99)
					list.add(pos);
			}
		}
		list.remove(this.currentPosition);
		return list;
	}
	
	/**
	 * Returns true if the current searcher currently has an assigned Path.
	 * 
	 * @return boolean
	 */
	public boolean hasPathAssigned() {
		return !this.currentPath.isEmpty();
	}

	/**
	 * Returns the next position in the current Searcher's Path. It also removes it from its Path.
	 * 
	 * @return Position the next Position.
	 */
	public Position popNextPathPosition() {
		return this.currentPath.pop();
	}

	/**
	 * Returns a List of the current Searcher's directions' priorities, sorted by importance.
	 * 
	 * @return the List of Directions.
	 */
	public List<Direction> getDirectionsPriorities() {
		return this.directionsPriority;
	}

	/**
	 * Implementation of A Star Algorithm for shortest path to Position goal. Does not use fog of war for greedy traversing.
	 * 
	 * @param goal the Goal Position.
	 * @param visionMap the current visible Map.
	 */
	public void findShortestPathToGoalNonGreedy(Position goal, Map visionMap) {
		//initialize unvisited, visited, and unvisitredRetrieval
		
		//put current node in the visited, and remove it from the unvisited and retrieval
		//get nodes around current node
			//if successor is finish, end
			//if successor is land
				//if successor is in the unvisited list
					//if current.G+1 is smaller than successor.G, reparent successor
				//else, add it to the unvisited list
		
		
		Node currentNode, successor;
		Node goalNode = null;
		
		Boolean goalFound = false;
		
		//initialize unvisited, visited, and unvisitredRetrieval
		PriorityQueue<Node> unvisited = new PriorityQueue<>();
//			ArrayList<Node> unvisitedRetrieval = new ArrayList<Node>();
		HashSet<Node> visited = new HashSet<Node>();
		
		//put current node in the visited, and remove it from the unvisited and retrieval
		currentNode = new Node(this.currentPosition, null, goal);
		//add base node to unvisited
		unvisited.add(currentNode);

		//while goal not found and the queue still has nodes
		while ((!goalFound) && (!unvisited.isEmpty())){
			//poll smalles node from the unvisited list
			currentNode = unvisited.poll();
			visited.add(currentNode);

			//get nodes around current node
			List<Position> around = visionMap.getAround(currentNode.getPosition());
			for (Position successorPosition : around) {
			
				//if successor is finish, end
				if (successorPosition.equals(goal)){
					goalFound = true;
					goalNode = new Node(successorPosition, currentNode, goal);
				} else {
					
					//if successor is land
					if (visionMap.getAt(successorPosition) == MapElement.LAND){
						successor = new Node(successorPosition, currentNode, goal);
						if (!visited.contains(successor)){

							//if successor is in the unvisited list
							if (unvisited.contains(successor)){
								//if current.G+1 is smaller than successor.G, reparent successor
								if (currentNode.getG() + 1 < successor.getG()){
									successor.reparent(currentNode);
								} 
							} else {
								//else, add it to the unvisited list
								unvisited.add(successor);
	//							unvisitedRetrieval.add(successor);
							}
							
						}
					}
				}
			}
		}
				
		if (!goalFound)
			try {
				throw new Exception("Goal not reachable Exception.");
			} catch (Exception e) {
				// TODO Handle outside
				e.printStackTrace();
			}
		
		Path pathToGoalNode = new Path();
		//Should be better as return parameter
		while (goalNode.getPosition() != this.currentPosition){
			pathToGoalNode.push(goalNode.getPosition());
			goalNode = goalNode.getParent();
		}
		this.currentPath = pathToGoalNode;
		
		return;		
	}
	
	/**
	 * Implementation of BFS Algorithm for minimum distance to an unvisited Cell.
	 * 
	 * @param visionMap the current visible Map.
	 * @param unvisitedCells the set of Cells.
	 */
	public void findShortestPathToNearestUnvisitedCell(Map visionMap, HashSet<Position> unvisitedCells){
			
			//initialize unvisited, visited, and unvisitredRetrieval
			
			//put current node in the visited, and remove it from the unvisited and retrieval
			//get nodes around current node
				//if successor is finish, end
				//if successor is land
					//if successor is in the unvisited list
						//if current.G+1 is smaller than successor.G, reparent successor
					//else, add it to the unvisited list
			
			
			Node currentNode, successor;
			Node goalNode = null;
			
			Boolean frontierFound = false;
			
			//initialize unvisited, visited, and unvisitredRetrieval
			Queue<Node> unvisited = new LinkedList<Node>();
	//		ArrayList<Node> unvisitedRetrieval = new ArrayList<Node>();
			HashSet<Node> visited = new HashSet<Node>();
			
			//put current node in the visited, and remove it from the unvisited and retrieval
			currentNode = new Node(this.currentPosition, null);
			//add base node to unvisited
			unvisited.add(currentNode);
	
			//while goal not found and the queue still has nodes
			while ((!frontierFound) && (!unvisited.isEmpty())){
				//poll smalles node from the unvisited list
				currentNode = unvisited.poll();
				visited.add(currentNode);
	
				//get nodes around current node
				List<Position> around = visionMap.getAround(currentNode.getPosition());
				for (Position successorPosition : around) {
				
					//if successor is finish, end
					if (unvisitedCells.contains(successorPosition)){
						frontierFound = true;
						goalNode = new Node(successorPosition, currentNode);
					} else {
						
						//if successor is land
						if (visionMap.getAt(successorPosition) == MapElement.LAND){
							successor = new Node(successorPosition, currentNode);
							if (!visited.contains(successor)){
	
								//if successor is in the unvisited list
								if (unvisited.contains(successor)){
									//if current.G+1 is smaller than successor.G, reparent successor
									if (currentNode.getG() + 1 < successor.getG()){
										successor.reparent(currentNode);
									} 
								} else {
									//else, add it to the unvisited list
									unvisited.add(successor);
		//							unvisitedRetrieval.add(successor);
								}
								
							}
						}
					}
				}
			}
					
			if (!frontierFound)
				try {
					throw new Exception("Goal not reachable Exception.");
				} catch (Exception e) {
					// TODO Handle outside
					e.printStackTrace();
				}
			Path pathToGoalNode = new Path();
			//Should be better as return parameter
					
			while (goalNode.getPosition() != this.currentPosition){
				pathToGoalNode.push(goalNode.getPosition());
				goalNode = goalNode.getParent();
			}
			
			this.currentPath = pathToGoalNode;
					
			return;
		}
	
	/**
	 * Changes the current Position of the Searcher to the corresponding new Position directed to Direction dir.
	 * 
	 * @param dir the Direction.
	 */
	public void direct(Direction dir) {
		this.currentPosition = this.currentPosition.direct(dir);
	}

	/**
	 * Getter for the ID of the current Searcher.
	 * 
	 * @return id the id.
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * Returns the next Position in the Path of the current Searcher.
	 * 
	 * @return Position the Position
	 */
	public Position peekNextPathPosition() {
		return this.currentPath.peek();
	}
	
	/**
	 * Getter of the Path of the current Searcher.
	 * 
	 * @return
	 */
	public Stack<Position> getPath(){ //TODO DELETE
		return this.currentPath.getStack();
	}
	
}
