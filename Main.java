import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/** This class allows for users to compress and decompress text files using the 
 * LZW compression algorithm.
 * 
 * @author mingescharlie
 *
 */
public class Main {

	/**Main method is responsible for creating FileReader and Scanners that will
	 * be used in compress/decompress methods, and calls respective methods with
	 * those objects as parameters.
	 * 
	 * compress(new FileReader(fileToCompress));
	 * decompress(new FileInputStream(fileToDecompress))
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main ( String[] args ) {
		Boolean repeat = true;
		while (repeat) {
			Scanner input = new Scanner(System.in);

			System.out.println("COMPRESS or DECOMPRESS?");
			String option = input.nextLine().toUpperCase();

			try {
				if (option.equals("COMPRESS")) {
					System.out.println("Please enter full path to file you wish to compress");
					String path = input.nextLine();
					File toCompress = new File(path);
					FileReader reader = new FileReader(toCompress);

					System.out.println("Please give name for compressed file");
					String compressedFileName = input.nextLine();
					File compressedFile = new File(compressedFileName);

					System.out.println("Compressing file...");
					compress(reader, compressedFileName);
					double ratio = getCompressionRatio(toCompress, compressedFile);

					System.out.println("We compressed your file by " + NumberFormat.getPercentInstance().format(ratio));
				} else if (option.equals("DECOMPRESS")) {
					System.out.println("Please enter full path of file you wish to decompress");
					String path = input.nextLine();
					FileInputStream in = new FileInputStream(path);
					System.out.println("Please enter desired name (and path if you choose) of decompressed file");
					String filename = input.nextLine();
					decompress(in, filename);
				} else {
					System.out.println("Sorry, invalid command, please try again!");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			System.out.println("Would you like to compress/decompress another file? YES or NO");
			String ans = input.nextLine().toUpperCase();
			if (ans.equals("NO")) {
				repeat = false;
				System.out.println("Exiting program...");
				input.close();
			} else if (!ans.equals("YES")) {
				System.out.println("Sorry, invalid input, exiting program...");
				input.close();
			}
		}

	}

	/**
	 * compress takes a FileReader which will feed it char's from a text file.
	 * The method will build of strings of char's and check if they are in 
	 * string list (contains char-code pairs). If so, replaces sequence with code,
	 * otherwise add's most recent sequences code and starts new sequence.
	 * 
	 * @param reader
	 * 		FileReader that provides stream of characters from text file we wish to compress
	 * @throws Exception 
	 * @throws MapOverflowException 
	 */
	public static void compress(FileReader reader, String name) throws Exception {

		/* HashMap to hold character sequences corresponding code */
		HashMap<String, Integer> stringList = new HashMap<>();

		int i; 															/* counter for stringList */

		/* initializes stringList with characters we plan on seeing in text file */
		for (i = 1; i < 96; i++) { 						/* however many ASCII chars you want */
			char t = (char) (i+31); 
			stringList.put(Character.toString(t),i);
		}

		/* OutputStream to write bytes representing codes to file */
		FileOutputStream out = new FileOutputStream(new File(name));

		/* string of char's seen in text each time new character read */
		String prefix = "";

		int c;
		while ((c = reader.read()) != -1) { 		/* while there are still characters to read */

			/* build current string with prefix and character read in */
			String temp = prefix + (char)c;

			if (stringList.containsKey(temp)) { /* if temp is in list */
				prefix = temp; 									/* make prefix equal to temp */
			} else { 													/* output prefix */
				int code = stringList.get(prefix);
				out.write(code);

				stringList.put(temp,i); 					/* add temp to stringList */
				i++; 														/* increment stringList index value */
				prefix = "" + (char)c; 						/* update prefix */
			}

			if (i >= 255) {
				throw new Exception("Too many unique strings found in text, map not large enough");
			}
		}

		if (stringList.containsKey(prefix)) {
			int code = stringList.get(prefix);
			out.write(code);
		} else {
			stringList.put(prefix,i);
			int code = stringList.get(prefix);
			out.write(code);
		}

		reader.close();

		out.flush();
		out.close();

		System.out.println("Text file successfully compressed!");
	}

	/**
	 * decompress takes a scanner that is already connected to a file containing
	 * the compressed code of some text file. It iterates through the codes,
	 * translating them into their corresponding char(s), or, if the code isn't
	 * is code list (which starts with just codes to represent individual chars),
	 * it derives the code-char pair.
	 * 
	 * @param in
	 * 		Scanner that reads int's from compressed text file
	 * @throws Exception 
	 * @throws MapOverflowException 
	 */
	public static void decompress(FileInputStream in, String filename) throws Exception {

		/* HashMap to hold codes and their corresponding character strings */
		HashMap<Integer, String> codeList = new HashMap<>();

		int i; 						/* counter for codeList */

		/* initialize codeList with codes for characters we know will be in text */
		for (i = 1; i < 96; i++) {
			char t = (char) (i+31);
			codeList.put(i, Character.toString(t));
		}

		String output = ""; 							/* decompressed, original text */

		int c = in.read();
		if (codeList.containsKey(c)) {
			output += codeList.get(c); 			/* add corresponding char(s) to output string */
		}
		int old = c; 										/* set old to be c */

		while ((c = in.read()) != -1) { 	/*while still ints to be read */
			if (codeList.containsKey(c)) { 	/* if c is in list */
				String temp = codeList.get(c);
				output = output + temp; 			/* add c to output */

				/* add old codes string + new codes first character as we know that this
				 * string would have been added at this point in compression
				 */
				codeList.put(i, codeList.get(old) + temp.charAt(0));
				i++;
				old = c;
			} else { /* its not, must derive corresponding char(s) */
				String temp = codeList.get(old); /* get old codes char(s) */

				/* put temp plus temp's first char in table */
				codeList.put(i, temp + temp.charAt(0));
				i++;
				//add temp plus temp's first char to output */
				output = output + temp + temp.charAt(0);
			}

			if (i >= 255) {
				throw new Exception("Too many unique strings found in text, map not large enough");
			}
		}

		in.close();

		FileWriter write = new FileWriter(new File(filename));
		write.write(output);
		write.flush();
		write.close();
		System.out.println("Text file successfully decompressed!");
	}

	/**
	 * Method to calculate how much the algorithm compressed the original file.
	 * 
	 * @param initial
	 * 		original file
	 * @param compressed
	 * 		compressed file
	 * @return
	 * 		how much the algorithm compressed the original file
	 */
	public static double getCompressionRatio(File initial, File compressed) {
		long initSize = initial.length();
		long compSize = compressed.length();

		long diff = initSize - compSize;

		double ratio = ((double)diff/initSize);
		return ratio;
	}

}
