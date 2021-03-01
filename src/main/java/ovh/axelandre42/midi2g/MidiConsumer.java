package ovh.axelandre42.midi2g;

import javax.sound.midi.*;
import java.util.*;
import java.util.stream.Stream;

public class MidiConsumer {
	private final List<NoteEvent> events;

	public int getCount() {
		return count;
	}

	private int count = 0;

	private Track mergeTracks(Sequence midi) throws InvalidMidiDataException {
		Track[] tracks = midi.getTracks();
		Sequence sequence = new Sequence(midi.getDivisionType(), midi.getResolution());
		Track track = sequence.createTrack();

		for (Track t : tracks) {
			for (int i = 0; i < t.size(); i++) {
				track.add(t.get(i));
			}
		}

		return track;
	}

	private double ticksToMinutes(long timestamp, int resolution, double bpm) {
		return timestamp / (resolution * bpm);
	}

	private List<NoteEvent> process(Sequence midi, double bpm) throws InvalidMidiDataException {
		Track track = mergeTracks(midi);
		long lastTimestamp = 0;
		List<NoteEvent> events = new ArrayList<>();
		int resolution = midi.getResolution();
		Set<Integer> notes = new LinkedHashSet<>();

		for (int i = 0; i < track.size(); i++) {
			MidiEvent mEvent = track.get(i);
			long timestamp = mEvent.getTick();
			MidiMessage message = mEvent.getMessage();

			if (!(message instanceof ShortMessage)) continue;
			count++;
			ShortMessage sm = (ShortMessage) message;
			int command = sm.getCommand();

			events.add(new NoteEvent(count, ticksToMinutes(timestamp, resolution, bpm)
					- ticksToMinutes(lastTimestamp, resolution, bpm), notes));
			lastTimestamp = timestamp;

			switch (command) {
			case ShortMessage.NOTE_ON:
				if (notes.size() >= 3) notes.remove(notes.iterator().next());
				notes.add(sm.getData1());
				break;
			case ShortMessage.NOTE_OFF:
				notes.remove(sm.getData1());
			}
		}

		return events;
	}

	public MidiConsumer(Sequence midi, double bpm) throws InvalidMidiDataException {
		this.events = process(midi, bpm);
	}

	public Stream<NoteEvent> stream() {
		return events.stream();
	}
}
