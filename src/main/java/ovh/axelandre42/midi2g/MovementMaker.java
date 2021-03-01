package ovh.axelandre42.midi2g;

import ovh.axelandre42.midi2g.geom.AxisAlignedBB;
import ovh.axelandre42.midi2g.geom.AxisAlignedPlane;
import ovh.axelandre42.midi2g.geom.Point;
import ovh.axelandre42.midi2g.geom.Segment;

import java.util.Set;
import java.util.stream.Stream;

public class MovementMaker {
	private static final double TOLERANCE = 0.001;

	private final AxisAlignedBB printer;
	private final GCodeOutput output;
	private final Point stepsPerMM;
	private final Point microSteps;
	private Point extruderPosition = new Point(0, 0, 0);
	private	Point extruderDirection = new Point(1, 1, 1);

	public MovementMaker(AxisAlignedBB printer, Point stepsPerMM, Point microSteps, GCodeOutput output) {
		this.printer = printer;
		this.output = output;
		this.stepsPerMM = stepsPerMM;
		this.microSteps = microSteps;
	}

	public void process(Stream<NoteEvent> eventStream, int count) {
		eventStream.forEach(noteEvent -> {
			System.out.printf("Progress: %.2f%%%n", noteEvent.getIndex() * 100. / count);
			if (noteEvent.getDuration() * 60 < TOLERANCE) {
				return;
			}

			if (noteEvent.isSilent()) {
				output.dwell(noteEvent.getDuration() * 60);
				return;
			}

			Point feed = noteEvent.getFeed(stepsPerMM, microSteps, extruderDirection);
			Point next = extruderPosition.add(feed.multiply(noteEvent.getDuration()));

			output.setF(feed.getNorm());
			while (!printer.isInside(next, TOLERANCE)) {

				Segment movement = new Segment(extruderPosition, next);
				Set<AxisAlignedPlane> planes = printer.getIntersectingPlanes(new AxisAlignedBB(movement), TOLERANCE);

				for (AxisAlignedPlane plane : planes) {
					Point intersection = plane.getIntersectionPoint(movement);
					if (printer.isInside(intersection, 0.01)) {
						extruderPosition = intersection;
						next = plane.getMirroredPoint(next);
						output.linearMove(intersection, TOLERANCE);
						break;
					}
				}

			}

			output.linearMove(next, TOLERANCE);

			extruderDirection = next.subtractUnitary(extruderPosition);
			extruderPosition = next;
		});
		output.dwell(2);
	}

	public void terminate() {
		output.close();
	}
}
