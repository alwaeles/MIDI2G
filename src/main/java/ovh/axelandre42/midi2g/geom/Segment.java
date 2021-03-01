package ovh.axelandre42.midi2g.geom;

public class Segment {
	private final Point origin, end;

	public Segment(Point origin, Point end) {
		this.origin = origin;
		this.end = end;
	}

	public Point getOrigin() {
		return origin;
	}

	public Point getEnd() {
		return end;
	}
}
