package ovh.axelandre42.midi2g.geom;

import java.util.LinkedHashSet;
import java.util.Set;

public class AxisAlignedBB {
	private final Segment base;

	public AxisAlignedBB(Segment base) {
		this.base = base;
	}

	public AxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
		this(new Segment(new Point(x1, y1, z1), new Point(x2, y2, z2)));
	}

	public double getMaxX() {
		return Math.max(base.getOrigin().getX(), base.getEnd().getX());
	}

	public double getMaxY() {
		return Math.max(base.getOrigin().getY(), base.getEnd().getY());
	}

	public double getMaxZ() {
		return Math.max(base.getOrigin().getZ(), base.getEnd().getZ());
	}
	
	public double getMinX() {
		return Math.min(base.getOrigin().getX(), base.getEnd().getX());
	}

	public double getMinY() {
		return Math.min(base.getOrigin().getY(), base.getEnd().getY());
	}

	public double getMinZ() {
		return Math.min(base.getOrigin().getZ(), base.getEnd().getZ());
	}

	public boolean isInside(Point point, double tolerance) {
		return (point.getX() >= getMinX() - tolerance && point.getX() <= getMaxX() + tolerance) &&
				(point.getY() >= getMinY() - tolerance && point.getY() <= getMaxY() + tolerance) &&
				(point.getZ() >= getMinZ() - tolerance && point.getZ() <= getMaxZ() + tolerance);
	}

/*
	public boolean isIntersecting(AxisAlignedBB aabb) {
		return (getMinX() <= aabb.getMaxX() && getMaxX() >= aabb.getMinX()) &&
				(getMinY() <= aabb.getMaxY() && getMaxY() >= aabb.getMinY()) &&
				(getMinZ() <= aabb.getMaxZ() && getMaxZ() >= aabb.getMinZ());
	}
*/

	public Set<AxisAlignedPlane> getIntersectingPlanes(AxisAlignedBB aabb, double tolerance) {
		Set<AxisAlignedPlane> planes = new LinkedHashSet<>();

		if (getMaxX() + tolerance <= aabb.getMaxX() && getMaxX() + tolerance >= aabb.getMinX())
			planes.add(new AxisAlignedPlane(getMaxX(), AxisAlignedPlane.Orientation.YZ));
		if (getMinX() - tolerance <= aabb.getMaxX() && getMinX() - tolerance >= aabb.getMinX())
			planes.add(new AxisAlignedPlane(getMinX(), AxisAlignedPlane.Orientation.YZ));
		if (getMaxY() + tolerance <= aabb.getMaxY() && getMaxY() + tolerance >= aabb.getMinY())
			planes.add(new AxisAlignedPlane(getMaxY(), AxisAlignedPlane.Orientation.XZ));
		if (getMinY() - tolerance <= aabb.getMaxY() && getMinX() - tolerance >= aabb.getMinY())
			planes.add(new AxisAlignedPlane(getMinY(), AxisAlignedPlane.Orientation.XZ));
		if (getMaxZ() + tolerance <= aabb.getMaxZ() && getMaxZ() + tolerance >= aabb.getMinZ())
			planes.add(new AxisAlignedPlane(getMaxZ(), AxisAlignedPlane.Orientation.XY));
		if (getMinZ() - tolerance <= aabb.getMaxZ() && getMinZ() - tolerance >= aabb.getMinZ())
			planes.add(new AxisAlignedPlane(getMinZ(), AxisAlignedPlane.Orientation.XY));

		return planes;
	}
}
