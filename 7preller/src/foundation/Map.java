package foundation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Map {

	public static final int MAPSIZE = 100;

	private MapElement[][] map = new MapElement[MAPSIZE][MAPSIZE];
	private Position start;
	private Position finish;

	public Map() {
		for (int i = 0; i < MAPSIZE; ++i)
			for(int j = 0; j < MAPSIZE; ++j) 
				map[i][j] = MapElement.EMPTY;
		start = null;
		finish = null;
	}

	public Map(Map m) {
		for (int i = 0; i < MAPSIZE; ++i)
			for(int j = 0; j < MAPSIZE; ++j) 
				map[i][j] = m.map[i][j];
		start = new Position(m.start); 
		finish = new Position(m.finish); 
	}

	public Map(String fileName) {
		this();

		String line = "";
		String[] result = null;
		BufferedReader in = null;

		try {
			// open the file
			in = new BufferedReader(new FileReader(fileName));

			// read header
			line = in.readLine();
			result = line.split(" ");
			if (result.length != 1 || !result[0].toUpperCase().trim().equals("<MAP>"))
				throw new MapException("MapException: Error reading header of " + fileName);

			for (int linecnt = 0; linecnt < MAPSIZE; ++linecnt) {
				// read the map line vector
				line = in.readLine();
				result = line.split(" ");
				if (result.length != 3 || 
					!result[0].toUpperCase().trim().equals("<MAPLINE>") ||
					!result[2].toUpperCase().trim().equals("</MAPLINE>") ||
					result[1].length() != MAPSIZE)
					throw new MapException("MapException: Error reading mapline " + linecnt + " in " + fileName);
				for (int i = 0; i < MAPSIZE; ++i) {
					char mapchar = result[1].charAt(i);
					switch (mapchar) {
					case '.':  map[i][linecnt] = MapElement.WATER; break;
					case '+':  map[i][linecnt] = MapElement.LAND; break;
					case 'S':  map[i][linecnt] = MapElement.START; 
			           if (start != null)
							throw new MapException("MapException: Too many starting points in " + fileName);
			           start = new Position(i, linecnt);
			           break;
					case 'F':  map[i][linecnt] = MapElement.FINISH; 
			           if (finish != null)
							throw new MapException("MapException: Too many finish points in " + fileName);
			           finish = new Position(i, linecnt);
			           break;
			        default:
			        	throw new MapException("MapException: Erroneous character in mapline " + linecnt + " in " + fileName);
					}
				}
			}
			// read footer
			line = in.readLine();
			result = line.split(" ");
			if (result.length != 1 || !result[0].toUpperCase().trim().equals("</MAP>"))
				throw new MapException("MapException: Error reading footer of " + fileName);
			if (start == null)
				throw new MapException("MapException: No starting point found in " + fileName);
			if (finish == null)
				throw new MapException("MapException: No finish point found in " + fileName);
		} catch (Exception e) {
			throw new MapException("MapException: Error reading " + fileName);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			} 
		}

	}

	public MapElement getAt(Position p) { 
		return map[p.getColumn()][p.getRow()];
	}

	public Position getStart() {		
		return new Position(start);
	}

	public Position getFinish() {		
		return new Position(finish);
	}

	public void setAt(Position p, MapElement value) {
		map[p.getColumn()][p.getRow()] = value;
	}

	public void mergeIn(Environment e) {
		Position refPos = e.getRefPos();
		for (int i = 0; i < Environment.ENVIRONMENTSIZE; ++i)
			for(int j = 0; j < Environment.ENVIRONMENTSIZE; ++j) {
				int currentX = refPos.getColumn() - Environment.VISIBILITY + i;
				int currentY = refPos.getRow() - Environment.VISIBILITY + j;
				if (currentX >= 0 && currentX < Map.MAPSIZE && currentY >= 0 && currentY < Map.MAPSIZE)
					map[currentX][currentY] = e.getAt(new Position(i, j));
			}

	}

	public List<Position> getAround(Position p){
		// Gets valid Positions around a point p
		ArrayList<Position> list = new ArrayList<Position>();
		
		for (int i = 0; i <= Environment.VISIBILITY * 2 ; i++) {
			for (int j = 0; j <= Environment.VISIBILITY * 2; j++) {
				Position pos = new Position(p.getColumn() + i - 1, p.getRow() + j - 1);
				if (pos.getColumn() >= 0 && pos.getColumn() <= 99 && pos.getRow() >= 0 && pos.getRow() <= 99)
					list.add(pos);
			}
		}
		list.remove(p); //TODO Check if hashcode works as expected
		
		
		return list;
	}

	public void setStart(Position start) {
		this.start = start;		
	}
}
