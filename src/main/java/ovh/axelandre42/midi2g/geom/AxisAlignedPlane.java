package ovh.axelandre42.midi2g.geom;

public class AxisAlignedPlane {
	public enum Orientation {
		XY, XZ, YZ
	}

	private final double offset;
	private final Orientation orientation;

	public AxisAlignedPlane(double offset, Orientation orientation) {
		this.offset = offset;
		this.orientation = orientation;
	}

	public double getOffset() {
		return offset;
	}

	public Point getMirroredPoint(Point point) {
		if (orientation == Orientation.XY) {
			return new Point(point.getX(), point.getY(), 2 * offset - point.getZ());
		} else if (orientation == Orientation.XZ) {
			return new Point(point.getX(), 2 * offset - point.getY(), point.getZ());
		} else if (orientation == Orientation.YZ) {
			return new Point(2 * offset - point.getX(), point.getY(), point.getZ());
		}
		return null;
	}

	private double projectedIntersection(double offset, double originX, double originY,
										 double targetX, double targetY) {
		return ((originX * targetY - originY * targetX) + offset * (originY - targetY)) / (originX - targetX);
	}

	public Point getIntersectionPoint(Segment segment) {
		if (orientation == Orientation.XY) {
			return new Point(
					projectedIntersection(offset, segment.getOrigin().getZ(), segment.getOrigin().getX(),
							segment.getEnd().getZ(), segment.getEnd().getX()),
					projectedIntersection(offset, segment.getOrigin().getZ(), segment.getOrigin().getY(),
							segment.getEnd().getZ(), segment.getEnd().getY()),
					offset);
		} else if (orientation == Orientation.XZ) {
			return new Point(
					projectedIntersection(offset, segment.getOrigin().getY(), segment.getOrigin().getX(),
							segment.getEnd().getY(), segment.getEnd().getX()),
					offset,
					projectedIntersection(offset, segment.getOrigin().getY(), segment.getOrigin().getZ(),
							segment.getEnd().getY(), segment.getEnd().getZ()));
		} else if (orientation == Orientation.YZ) {
			return new Point(
					offset,
					projectedIntersection(offset, segment.getOrigin().getX(), segment.getOrigin().getY(),
							segment.getEnd().getX(), segment.getEnd().getY()),
					projectedIntersection(offset, segment.getOrigin().getX(), segment.getOrigin().getZ(),
							segment.getEnd().getX(), segment.getEnd().getZ()));
		}
		return null;
	}
}
