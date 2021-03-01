package ovh.axelandre42.midi2g;

import ovh.axelandre42.midi2g.geom.AxisAlignedBB;
import ovh.axelandre42.midi2g.geom.Point;

import javax.sound.midi.*;
import java.io.*;

public class Generator {
	public static void main(String[] args) throws InvalidMidiDataException, IOException {
		AxisAlignedBB printerBB = new AxisAlignedBB(0, 0, 0, 200, 180, 190);

		Sequence midi = MidiSystem.getSequence(new File("windows.mid"));

		File output = new File("windows.gcode");
		if (output.createNewFile())
			System.out.println("Created new file.");

		MovementMaker maker = new MovementMaker(printerBB,
				new Point(134.74, 134.74, 4266.66),
				new Point(16, 16, 16),
				new GCodeOutput(new PrintStream(new BufferedOutputStream(new FileOutputStream(output)))));
		MidiConsumer consumer = new MidiConsumer(midi, 80);
		maker.process(consumer.stream(), consumer.getCount());
		maker.terminate();

	}
}
