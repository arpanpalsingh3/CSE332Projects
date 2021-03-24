package cse332.types;

// A class to represent a the corners of the map
// You do not have to use this, but it's quite convenient
public class MapCorners {
    // invariant: right >= left and top >= bottom (i.e., numbers get bigger as you move up/right)
    // note in our census data longitude "West" is a negative number which nicely matches bigger-to-the-right
	public float west;
	public float east;
	public float north;
	public float south;
	
	public MapCorners(float l, float r, float t, float b) {
		west   = l;
		east  = r;
		north    = t;
		south = b;
	}

    public MapCorners(CensusGroup censusGroup) {
        this(censusGroup.longitude, censusGroup.longitude, censusGroup.latitude, censusGroup.latitude);
    }

    // a functional operation: returns a new MapCorners that is the smallest rectangle
	// containing this and that
	public MapCorners encompass(MapCorners that) {
		return new MapCorners(Math.min(this.west,   that.west),
						     Math.max(this.east,  that.east),
						     Math.max(this.north,    that.north),
				             Math.min(this.south, that.south));
	}
	
	public String toString() {
		return "[west=" + west + " east=" + east + " north=" + north + " south=" + south + "]";
	}
}
