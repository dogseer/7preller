package ai;

import foundation.Map;
import foundation.Position;

/**
 * @author Patricio Reller <reller@mail.hs-ulm.de>
 * @version 1.0
 * @since 1.0
 */
public class Node implements Comparable<Node> {
	private Node parent;
	private Position position;
	private int f, g, h;

	/**
	 * Public constructor. For usage with A Star Algorithm. Requires the Goal
	 * Position.
	 * 
	 * @param p
	 *            the Position of the Node.
	 * @param parent
	 *            the parent Node of the Node.
	 * @param finish
	 *            the Goal Position.
	 */
	public Node(Position p, Node parent, Position finish) {
		this.position = p;
		this.parent = parent;
		// this.visited = false;

		// Heuristic
		this.h = Math.max(Math.abs(this.position.getColumn() - finish.getColumn()),
				Math.abs(this.position.getRow() - finish.getRow()));
		if (this.parent == null) // Could fix by getting rid of setG
			this.g = 0;
		else
			this.g = parent.getG() + 1;
		// Total value
		this.f = this.h + this.g;
	}

	/**
	 * Public constructor. To use with BFS, BFSMod or Dijkstra. Basic Node
	 * structure.
	 * 
	 * @param p
	 *            the Position of the Node.
	 * @param parent
	 *            the parent Node of the Node.
	 */
	public Node(Position p, Node parent) {
		this.position = p;
		this.parent = parent;
		this.h = 0;
		if (this.parent == null) // Could fix by getting rid of setG
			this.g = 0;
		else
			this.g = parent.getG() + 1;
		this.f = 0;

	}

	/**
	 * Sets the G value (real distance to the Initial Node).
	 * 
	 * @param g
	 *            value
	 */
	public void setG(int g) {
		this.g = g;

		return;
	}
	
	/**
	 * Getter for the G (real distance from Initial Node) value.
	 * 
	 * @return g
	 */
	public int getG() {
		return this.g;
	}
	
	/**
	 * Getter for the H (Heuristic) value.
	 * 
	 * @return h
	 */
	public int getH() {
		return this.h;
	}

	/**
	 * Getter for the F (real distance plus heuristic) value.
	 * 
	 * @return f
	 */
	public int getF() {
		return this.f;
	}

	/**
	 * Getter for the Position of the Node.
	 * 
	 * @return Position
	 */
	public Position getPosition() {
		return this.position;
	}

	/**
	 * CompareTo.
	 */
	@Override
	public int compareTo(Node n) {
		return this.getF() - n.getF();
	}

	/**
	 * Reparents the current Node with a new Parent.
	 * 
	 * @param newParent the new Parent.
	 */
	public void reparent(Node newParent) {
		this.parent = newParent;

		return;
	}

	/**
	 * Returns the Parent of the current Node.
	 * 
	 * @return parent
	 */
	public Node getParent() {
		return this.parent;
	}

	/**
	 * HashCode.
	 */
	@Override
	public int hashCode() {
		return this.position.getColumn() * Map.MAPSIZE + this.position.getRow();
	}

	/**
	 * Equals.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Node))
			return false;

		if (((Node) o).getPosition().getColumn() == this.position.getColumn()
				&& ((Node) o).getPosition().getRow() == this.getPosition().getRow())
			return true;

		return false;
	}
}
