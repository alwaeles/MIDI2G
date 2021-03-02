package ovh.axelandre42.midi2g;

import ovh.axelandre42.midi2g.geom.Point;

import java.io.PrintStream;
import java.util.Locale;

public class GCodeOutput {
	private final PrintStream printStream;
	private double feed;
	private Point lastTarget = new Point(0, 0, 0);

	public GCodeOutput(PrintStream printStream) {
		this.printStream = printStream;
		this.printStream.println("G21");
		this.printStream.println("G28 X0 Y0 Z0");
		dwell(1);
	}

	public void setF(double feed) {
		this.feed = feed;
	}

	private boolean doubleNotEquals(double a, double b, double tolerance) {
		return !(a > b - tolerance && a < b + tolerance);
	}

	public void linearMove(Point target, double tolerance) {
		printStream.print("G0");

		if (doubleNotEquals(target.getX(), lastTarget.getX(), tolerance)) {
			printStream.printf(Locale.US, " X%.2f", target.getX());
		}
		if (doubleNotEquals(target.getY(), lastTarget.getY(), tolerance)) {
			printStream.printf(Locale.US, " Y%.2f", target.getY());
		}
		if (doubleNotEquals(target.getZ(), lastTarget.getZ(), tolerance)) {
			printStream.printf(Locale.US, " Z%.2f", target.getZ());
		}
		if (feed >= 0) {
			printStream.printf(Locale.US, " F%.2f", feed);
			feed = -1;
		}

		printStream.println();
		lastTarget = target;
	}

	public void dwell(double seconds) {
		printStream.printf(Locale.US, "G4 S%.3f%n", seconds);
	}

	public void close() {
		printStream.println("G28 X0 Y0 Z0");
		printStream.close();
	}
}
