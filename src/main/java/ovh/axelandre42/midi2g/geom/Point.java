package ovh.axelandre42.midi2g.geom;

public class Point {
	private final double x, y, z;

	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public Point multiply(double scalar) {
		return new Point(scalar * getX(), scalar * getY(), scalar * getZ());
	}

	public Point add(Point point) {
		return new Point(getX() + point.getX(), getY() + point.getY(), getZ() + point.getZ());
	}

	public Point subtractUnitary(Point point) {
		return new Point(Math.copySign(1, getX() - point.getX()), Math.copySign(1, getY() - point.getY()),
				Math.copySign(1, getZ() - point.getZ()));
	}

	public double getNorm() {
		return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2) + Math.pow(getZ(), 2));
	}
}
