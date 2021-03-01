package ovh.axelandre42.midi2g;

import ovh.axelandre42.midi2g.geom.Point;

import java.util.Set;

public class NoteEvent {
	private final double duration;
	private final int index;

	private final Integer[] notes;

	public NoteEvent(int index, double duration, Integer... notes) {
		this.index = index;
		this.duration = duration;
		this.notes = notes;
	}

	public NoteEvent(int index, double duration, Set<Integer> notes) {
		this(index, duration, notes.toArray(new Integer[0]));
	}

	private double noteToFrequency(int note) {
		return (440 / 32.) * Math.pow(2, (note - 9) / 12.);
	}

	public boolean isSilent() {
		return notes.length == 0;
	}

	public int getIndex() {
		return index;
	}

	public double getDuration() {
		return duration;
	}

	public Point getFeed(Point stepsPerMM, Point microsteps, Point direction) {
		double feedX = notes.length > 0 ?
				noteToFrequency(notes[0]) * microsteps.getX() * 60 / stepsPerMM.getX() : 0;
		double feedY = notes.length > 1 ?
				noteToFrequency(notes[1]) * microsteps.getY() * 60 / stepsPerMM.getY() : 0;
		double feedZ = notes.length > 2 ?
				noteToFrequency(notes[2]) * microsteps.getZ() * 60 / stepsPerMM.getZ() : 0;

		return new Point(
				feedX * direction.getX(),
				feedY * direction.getY(),
				feedZ * direction.getZ());
	}
}
