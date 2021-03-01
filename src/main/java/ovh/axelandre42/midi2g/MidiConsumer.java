package ovh.axelandre42.midi2g;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.FormattedMessage;

import javax.sound.midi.*;
import java.util.*;
import java.util.stream.Stream;

public class MidiConsumer {
	private static final Logger LOGGER = LogManager.getLogger(MidiConsumer.class);
	private final List<NoteEvent> events;

	public int getCount() {
		return count;
	}

	private int count = 0;

	private Track mergeTracks(Sequence midi) throws InvalidMidiDataException {
		LOGGER.debug("Merging tracks.");
		Track[] tracks = midi.getTracks();
		Sequence sequence = new Sequence(midi.getDivisionType(), midi.getResolution());
		Track track = sequence.createTrack();

		for (Track t : tracks) {
			for (int i = 0; i < t.size(); i++) {
				MidiEvent midiEvent = t.get(i);
				track.add(midiEvent);
			}
		}

		return track;
	}

	private double ticksToMinutes(long timestamp, double ticksPerMinute) {
		return timestamp / ticksPerMinute;
	}

	private List<NoteEvent> process(Track track, int resolution) {
		LOGGER.debug("Processing events.");
		long lastTimestamp = 0;
		List<NoteEvent> events = new ArrayList<>();
		Set<Integer> notes = new LinkedHashSet<>();
		double bpm = 0;

		for (int i = 0; i < track.size(); i++) {
			MidiEvent mEvent = track.get(i);
			long timestamp = mEvent.getTick();
			MidiMessage message = mEvent.getMessage();

			if (message instanceof MetaMessage) {
				MetaMessage mm = (MetaMessage) message;
				if (mm.getType() == 81) {
					byte[] data = mm.getData();
					int tempo = (data[0] & 0xff) << 16 | (data[1] & 0xff) << 8 | (data[2] & 0xff);
					bpm = 60000000. / tempo;
					LOGGER.info(new FormattedMessage("New BPM: %.2f", bpm));
				}
			}

			if (!(message instanceof ShortMessage)) continue;
			count++;
			ShortMessage sm = (ShortMessage) message;
			int command = sm.getCommand();

			events.add(new NoteEvent(count, ticksToMinutes(timestamp, bpm * resolution)
					- ticksToMinutes(lastTimestamp, bpm * resolution), notes));
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

	public MidiConsumer(Sequence midi) throws InvalidMidiDataException {
		this.events = process(mergeTracks(midi), midi.getResolution());
	}

	public Stream<NoteEvent> stream() {
		return events.stream();
	}
}
