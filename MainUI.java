import java.io.*;
import java.util.*;

public class MainUI {

	// Retrieve data to the end of the line as an argument for a method call
	// Include two special kinds of arguments:
	// "null" asks us to return no string
	// "empty" asks us to return an empty string
	public static String filePath = null;

	private static String getEndingString(Scanner userInput) {
		String userArgument = null;

		userArgument = userInput.nextLine();
		userArgument = userArgument.trim();

		// Include a "hack" to provide null and empty strings for testing
		if (userArgument.equalsIgnoreCase("empty")) {
			userArgument = "";
		} else if (userArgument.equalsIgnoreCase("null")) {
			userArgument = null;
		}

		return userArgument;
	}

	// Main program to process user commands.
	// This method is not robust. When it asks for a command, it expects all
	// arguments to be there.
	// It is a quickly-done test harness rather than a full solution for an
	// assignment.

	public static void main(String[] args) throws IOException {
		// Command options

		String loadCommand = "load";
		String readyCommand = "ready";
		String solveCommand = "solve";
		String printCommand = "print";
		String choicesCommand = "choices";
		String quitCommand = "quit";

		// Define variables to manage user input

		String userCommand = "";
		String userArgument = "";
		Scanner userInput = new Scanner(System.in);

		// Define the mathdoku that we will be testing.

		Mathdoku kenken = new Mathdoku();

		// Define variables to catch the return values of the methods

		boolean booleanOutcome;
		String result = null;
		int choice = 0;

		// Let the user know how to use this interface

		System.out.println("Commands available:");
		System.out.println("  " + loadCommand + " <filename>");
		System.out.println("  " + readyCommand);
		System.out.println("  " + solveCommand);
		System.out.println("  " + printCommand);
		System.out.println("  " + choicesCommand);
		System.out.println("  " + quitCommand);

		// Process the user input until they provide the command "quit"

		do {
			// Find out what the user wants to do
			userCommand = userInput.next();

			/* Do what the user asked for. */

			if (userCommand.equalsIgnoreCase(loadCommand)) {
				// Get the parameters.

				userArgument = getEndingString(userInput);
				filePath = userArgument;

				// catch the FileNotFoundException if the stream is fail to create
				try {
					BufferedReader stream = new BufferedReader(new FileReader(filePath));

					// Call the method
					
					booleanOutcome = kenken.loadPuzzle(stream);
					System.out.println(userCommand + " \"" + userArgument + "\" outcome " + booleanOutcome);
					
				} catch (FileNotFoundException e) {
					userCommand = "quit";
					System.out.println("no such file or direction");
				}
			} else if (userCommand.equalsIgnoreCase(readyCommand)) {

				// Call the method

				booleanOutcome = kenken.readyToSolve();
				System.out.println(userCommand + " " + "outcome " + booleanOutcome);

				// if it is not ready to solve, quit the testing
				if (!booleanOutcome) {
					userCommand = "quit";
				}

			} else if (userCommand.equalsIgnoreCase(solveCommand)) {

				// Call the method

				booleanOutcome = kenken.solve();
				System.out.println(userCommand + " " + "outcome " + booleanOutcome);
			} else if (userCommand.equalsIgnoreCase(printCommand)) {

				// Call the method

				result = kenken.print();

				System.out.println(userCommand + " " + "outcome " + result);
			} else if (userCommand.equalsIgnoreCase(choicesCommand)) {

				// Call the method

				choice = kenken.choices();

				System.out.println(userCommand + " " + "outcome " + choice);
			} else if (userCommand.equalsIgnoreCase(quitCommand)) {
				break;
			} else {
				System.out.println("Bad command: " + userCommand);
			}
		} while (!userCommand.equalsIgnoreCase("quit"));

		// The user is done so close the stream of user input before ending.

		System.out.println("quit");
		userInput.close();
	}
}
