package ai;

import java.util.Stack;

import foundation.Position;

/**
 * @author      Patricio Reller <reller@mail.hs-ulm.de>
 * @version     1.0 
 * @since       1.0
 */
public class Path {
	private Stack<Position> path;
	
	/**
	 * Public Constructor (default).
	 */
	public Path(){
		this.path = new Stack<Position>();
		return;
	}
	
	/**
	 * Returns true if the current Path has no Positions.
	 * 
	 * @return boolean
	 */
	public boolean isEmpty(){	
		return this.path.isEmpty();
	}

	/**
	 * Returns the next Position in the Path. Also removes it from the current Path.
	 * 
	 * @return Position the Position
	 */
	public Position pop() {
		return this.path.pop();
	}

	/**
	 * Adds a new element to the head (next Position) of the current Path.
	 * 
	 * @param position the Position
	 */
	public void push(Position position) {
		this.path.push(position);
	}

	/**
	 * Returns the current next Position of the Path.
	 * 
	 * @return Position the Position.
	 */
	public Position peek() {
		return this.path.peek();
	}
	
	/**
	 * Getter for the current Path Stack.
	 * 
	 * @return the Path Stack.
	 */
	public Stack<Position> getStack() { //TODO DELETE
		return this.path;
	}
	
}
