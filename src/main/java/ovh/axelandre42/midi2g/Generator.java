package ovh.axelandre42.midi2g;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;
import org.apache.logging.log4j.message.FormattedMessage;
import ovh.axelandre42.midi2g.geom.AxisAlignedBB;
import ovh.axelandre42.midi2g.geom.Point;

import javax.sound.midi.*;
import java.io.*;
import java.util.List;
import java.util.Properties;

public class Generator {
	private static final Logger LOGGER = LogManager.getLogger(Generator.class);

	public static void printHelpMessage(OptionParser parser, Exception e) throws IOException {
		LOGGER.catching(e);
		printHelpMessage(parser);
	}
	public static void printHelpMessage(OptionParser parser) throws IOException {
		LOGGER.error("See the following for usage:");
		parser.printHelpOn(IoBuilder.forLogger(LOGGER).setLevel(Level.ERROR).buildPrintStream());
		System.exit(-1);
	}

	public static void main(String[] args) throws IOException {
		OptionParser parser = new OptionParser();
		OptionSpec<Void> help = parser.acceptsAll(List.of("h", "help"), "Prints help message and exits.")
				.forHelp();
		OptionSpec<Integer> maxX = parser.acceptsAll(List.of("X", "max-x"), "Printer maximum X coordinate.")
				.withRequiredArg()
				.ofType(Integer.class);
		OptionSpec<Integer> maxY = parser.acceptsAll(List.of("Y", "max-y"), "Printer maximum Y coordinate.")
				.withRequiredArg()
				.ofType(Integer.class);
		OptionSpec<Integer> maxZ = parser.acceptsAll(List.of("Z", "max-z"), "Printer maximum Z coordinate.")
				.withRequiredArg()
				.ofType(Integer.class);
		OptionSpec<Integer> minX = parser.acceptsAll(List.of("x", "min-x"), "Printer minimum X coordinate.")
				.withRequiredArg()
				.ofType(Integer.class)
				.defaultsTo(0);
		OptionSpec<Integer> minY = parser.acceptsAll(List.of("y", "min-y"), "Printer minimum Y coordinate.")
				.withRequiredArg()
				.ofType(Integer.class)
				.defaultsTo(0);
		OptionSpec<Integer> minZ = parser.acceptsAll(List.of("z", "min-z"), "Printer minimum Z coordinate.")
				.withRequiredArg()
				.ofType(Integer.class)
				.defaultsTo(0);
		OptionSpec<Integer> microStepsX = parser.accepts("micro-steps-x", "Printer micro-steps for X axis.")
				.withRequiredArg()
				.ofType(Integer.class);
		OptionSpec<Integer> microStepsY = parser.accepts("micro-steps-y", "Printer micro-steps for Y axis.")
				.withRequiredArg()
				.ofType(Integer.class);
		OptionSpec<Integer> microStepsZ = parser.accepts("micro-steps-z", "Printer micro-steps for Z axis.")
				.withRequiredArg()
				.ofType(Integer.class);
		OptionSpec<Double> stepsPerMMX = parser.accepts("steps-per-mm-x", "Printer steps per mm for X axis.")
				.withRequiredArg()
				.ofType(Double.class);
		OptionSpec<Double> stepsPerMMY = parser.accepts("steps-per-mm-y", "Printer steps per mm for Y axis.")
				.withRequiredArg()
				.ofType(Double.class);
		OptionSpec<Double> stepsPerMMZ = parser.accepts("steps-per-mm-z", "Printer steps per mm for Z axis.")
				.withRequiredArg()
				.ofType(Double.class);
		OptionSpec<Integer> microSteps = parser.acceptsAll(
				List.of("M", "micro-steps"), "Printer micro-steps for all axis.")
				.withRequiredArg()
				.ofType(Integer.class)
				.defaultsTo(16);
		OptionSpec<File> printerFile = parser.acceptsAll(List.of("P", "printer"),
				"Use a properties file as printer configuration.")
				.requiredUnless(maxX, maxY, maxZ, stepsPerMMX, stepsPerMMY, stepsPerMMZ)
				.withRequiredArg()
				.ofType(File.class);
		OptionSpec<File> outputFile = parser.acceptsAll(List.of("o", "output"),
				"Outputs G-code to a file rather than the standard output.")
				.withRequiredArg()
				.ofType(File.class);

		parser.nonOptions("MIDI files to process.").ofType(File.class);

		OptionSet options;
		try {
			options = parser.parse(args);
		} catch (OptionException e) {
			printHelpMessage(parser, e);
			return;
		}

		if (options.has(help)) {
			printHelpMessage(parser);
		}

		Properties printerProperties = new Properties();

		if (options.has(printerFile)) {
			printerProperties.load(new FileInputStream(options.valueOf(printerFile)));
		}

		AxisAlignedBB printerBB = new AxisAlignedBB(
				options.has(printerFile) && printerProperties.containsKey("bed_minimum_x") ?
						Integer.parseInt(printerProperties.getProperty("bed_minimum_x")) : options.valueOf(minX),
				options.has(printerFile) && printerProperties.containsKey("bed_minimum_y") ?
						Integer.parseInt(printerProperties.getProperty("bed_minimum_y")) : options.valueOf(minY),
				options.has(printerFile) && printerProperties.containsKey("bed_minimum_z") ?
						Integer.parseInt(printerProperties.getProperty("bed_minimum_z")) : options.valueOf(minZ),
				options.has(printerFile) && printerProperties.containsKey("bed_maximum_x") ?
						Integer.parseInt(printerProperties.getProperty("bed_maximum_x")) : options.valueOf(maxX),
				options.has(printerFile) && printerProperties.containsKey("bed_maximum_y") ?
						Integer.parseInt(printerProperties.getProperty("bed_maximum_y")) : options.valueOf(maxY),
				options.has(printerFile) && printerProperties.containsKey("bed_maximum_z") ?
						Integer.parseInt(printerProperties.getProperty("bed_maximum_z")) : options.valueOf(maxZ));

		PrintStream outputStream = System.out;

		if (options.has(outputFile)) {
			File output = options.valueOf(outputFile);
			if (output.createNewFile())
				LOGGER.info(new FormattedMessage("%s did not exist. Created!", output));
			outputStream = new PrintStream(new FileOutputStream(output));
		}

		MovementMaker maker = new MovementMaker(printerBB,
				new Point(
						options.has(printerFile) && printerProperties.containsKey("motors_stepsPerMM_x") ?
								Double.parseDouble(printerProperties.getProperty("motors_stepsPerMM_x")) :
								options.valueOf(stepsPerMMX),
						options.has(printerFile) && printerProperties.containsKey("motors_stepsPerMM_y") ?
								Double.parseDouble(printerProperties.getProperty("motors_stepsPerMM_y")) :
								options.valueOf(stepsPerMMY),
						options.has(printerFile) && printerProperties.containsKey("motors_stepsPerMM_z") ?
								Double.parseDouble(printerProperties.getProperty("motors_stepsPerMM_z")) :
								options.valueOf(stepsPerMMZ)),
				new Point(
						options.has(printerFile) && printerProperties.containsKey("motors_microSteps_x") ?
								Integer.parseInt(printerProperties.getProperty("motors_microSteps_x")) :
								options.has(microStepsX) ? options.valueOf(microStepsX) : options.valueOf(microSteps),
						options.has(printerFile) && printerProperties.containsKey("motors_microSteps_y") ?
								Integer.parseInt(printerProperties.getProperty("motors_microSteps_y")) :
								options.has(microStepsY) ? options.valueOf(microStepsY) : options.valueOf(microSteps),
						options.has(printerFile) && printerProperties.containsKey("motors_microSteps_z") ?
								Integer.parseInt(printerProperties.getProperty("motors_microSteps_z")) :
								options.has(microStepsZ) ? options.valueOf(microStepsZ) : options.valueOf(microSteps)),
				new GCodeOutput(outputStream));

		List<?> arguments = options.nonOptionArguments();
		if (arguments.isEmpty()) {
			LOGGER.error("Missing at least one MIDI file.");
			printHelpMessage(parser);
		}

		arguments.stream().map(o -> (File) o).forEach(file -> {
			try {
				Sequence midi = MidiSystem.getSequence(file);
				MidiConsumer consumer = new MidiConsumer(midi);
				maker.process(consumer.stream(), consumer.getCount());
			} catch (InvalidMidiDataException | IOException e) {
				LOGGER.catching(e);
			}
		});

		maker.terminate();
		LOGGER.info("Successfully transformed MIDI into G-code!");
	}
}
