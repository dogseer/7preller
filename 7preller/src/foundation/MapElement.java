package foundation;

public enum MapElement {
	EMPTY, LAND, WATER, START, FINISH;    // START and FINISH are also LAND

	public static final String REPRESENTATIONSTRING = " +.SF";

	public char representation() {
		return  REPRESENTATIONSTRING.charAt(this.ordinal());
	}
	
}
