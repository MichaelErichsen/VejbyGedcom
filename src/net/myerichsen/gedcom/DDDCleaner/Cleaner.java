package net.myerichsen.gedcom.DDDCleaner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * This program needs a set of KIP csv files as input.
 * <p>
 * It converts national characters from UTF-8 to ANSI
 * <p>
 * The output is sent to text files.
 *
 * @author Michael Erichsen
 * @version 2023-01-20
 *
 */
public class Cleaner {
	private static BufferedWriter bw;
	private static BufferedReader br;

	/**
	 * Constructor
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: Cleaner csvfiledirectory outputfiledirectory");
			System.exit(4);
		}

		final Cleaner cleaner = new Cleaner();
		cleaner.execute(args);

		System.out.println("Output saved in " + args[1]);
		System.exit(0);
	}

	private String outFileDirectory = "";
	private String csvFileDirectory = "";

	/**
	 * Worker method
	 *
	 * @param args
	 */
	private void execute(String[] args) {
		setCsvFileDirectory(args[0]);
		setOutFileDirectory(args[1]);

		try {
			final Set<String> listFilesUsingDirectoryStream = listFilesUsingDirectoryStream(csvFileDirectory);

			for (final String string : listFilesUsingDirectoryStream) {
				System.out.println(string);
				processCsvFile(string);
			}

		} catch (final IOException e1) {
			e1.printStackTrace();
			System.exit(16);
		}
	}

	/**
	 * List all .csv files in the directory
	 *
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	public Set<String> listFilesUsingDirectoryStream(String dir) throws IOException {
		final Set<String> fileSet = new HashSet<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
			for (final Path path : stream) {
				if (!Files.isDirectory(path)) {
					System.out.println(path.toString());
					if (path.toString().endsWith(".csv")) {
						fileSet.add(path.getFileName().toString());
					}
				}
			}
		}
		return fileSet;
	}

	/**
	 * Open a csv file, convert the code page and save it again
	 *
	 * @param csvFileName
	 * @throws IOException
	 */
	private void processCsvFile(String csvFileName) throws IOException {
		br = new BufferedReader(new FileReader(new File(csvFileDirectory + csvFileName)));
		bw = new BufferedWriter(new FileWriter(new File(outFileDirectory + csvFileName)));

		String line;
		byte[] ba;
		boolean first = true;

		while ((line = br.readLine()) != null) {
			ba = line.getBytes(StandardCharsets.UTF_8);
			line = new String(ba, StandardCharsets.UTF_8);
			bw.write(line + "\n");

			if (first) {
				System.out.println(line);
				first = false;
			}

		}

		br.close();
		bw.flush();
		bw.close();

	}

	/**
	 * @param csvFileDirectory
	 *            the csvFileDirectory to set
	 */
	public void setCsvFileDirectory(String csvFileDirectory) {
		if (!csvFileDirectory.endsWith("/")) {
			csvFileDirectory = csvFileDirectory + "/";
		}

		this.csvFileDirectory = csvFileDirectory;
	}

	/**
	 * @param outFileDirectory
	 *            the outFileDirectory to set
	 */
	public void setOutFileDirectory(String outFileDirectory) {
		if (!outFileDirectory.endsWith("/")) {
			outFileDirectory = outFileDirectory + "/";
		}

		this.outFileDirectory = outFileDirectory;
	}

}
