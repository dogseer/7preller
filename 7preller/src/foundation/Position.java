package foundation;

public class Position {

	private int column;
	private int row;
	
	public Position(int column, int row) {
		this.column = column;
		this.row = row;
	}

	public Position(Position pos) {
		this.column = pos.column;
		this.row = pos.row;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}
	
	public boolean legal() {
		return column >= 0 && column < Map.MAPSIZE && row >= 0 && row < Map.MAPSIZE;
	}
	
	public Position direct(Direction to) {
		Position res = null;
		switch (to) {
		case NORTH: 	res =  new Position(column, row-1);		break;
		case NORTHEAST:	res =  new Position(column+1, row-1);	break;
		case EAST: 		res =  new Position(column+1, row);		break;
		case SOUTHEAST: res =  new Position(column+1, row+1);	break;
		case SOUTH: 	res =  new Position(column, row+1);		break;
		case SOUTHWEST: res =  new Position(column-1, row+1);	break;
		case WEST: 		res =  new Position(column-1, row);		break;
		case NORTHWEST: res =  new Position(column-1, row-1);	break;
		case STAY:		res =  new Position(column, row);	break;
		}
		if (res.legal())
			return res;
		else
			return this;
	}
	
	public boolean equals(Object o) {
		return o instanceof Position && ((Position) o).column == column && ((Position) o).row == row; 
	}
	
	public String toString() {
		return "(" + column + "/" + row + ")";
	}
	
	@Override
	public int hashCode(){
		return this.column*Map.MAPSIZE + this.row;
	}
}
