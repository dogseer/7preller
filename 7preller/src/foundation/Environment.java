package foundation;

public class Environment {

	public static final int VISIBILITY = 1;
	public static final int ENVIRONMENTSIZE = 2 * VISIBILITY + 1;
	
	private MapElement[][] env = new MapElement[ENVIRONMENTSIZE][ENVIRONMENTSIZE];
	private Position refPos = null;

	public Environment() {
		for (int i = 0; i < ENVIRONMENTSIZE; ++i)
			for(int j = 0; j < ENVIRONMENTSIZE; ++j) 
				env[i][j] = MapElement.EMPTY;
	}

	public Environment(Map m, Position p) {
		this();
		for (int i = 0; i < ENVIRONMENTSIZE; ++i)
			for(int j = 0; j < ENVIRONMENTSIZE; ++j) {
				int currentX = p.getColumn() - VISIBILITY + i;
				int currentY = p.getRow() - VISIBILITY + j;
				if (currentX >= 0 && currentX < Map.MAPSIZE && currentY >= 0 && currentY < Map.MAPSIZE)
					env[i][j] = m.getAt(new Position(currentX, currentY));
			}
		refPos = new Position(p);
	}

	public MapElement getAt(Position p) { 
		return env[p.getColumn()][p.getRow()];
	}
	
	public Position getRefPos() { 
		return new Position(refPos);
	}
	
	
}
