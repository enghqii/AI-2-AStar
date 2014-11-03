/*
 * Wumpus-Lite, version 0.21 alpha
 * A lightweight Java-based Wumpus World Simulator
 * 
 * Written by James P. Biagioni (jbiagi1@uic.edu)
 * for CS511 Artificial Intelligence II
 * at The University of Illinois at Chicago
 * 
 * Thanks to everyone who provided feedback and
 * suggestions for improving this application,
 * especially the students from Professor
 * Gmytrasiewicz's Spring 2007 CS511 class.
 * 
 * Last modified 4/14/08
 * 
 * DISCLAIMER:
 * Elements of this application were borrowed from
 * the client-server implementation of the Wumpus
 * World Simulator written by Kruti Mehta at
 * The University of Texas at Arlington.
 * 
 */

import java.io.*;
import java.util.*;

class WorldApplication {

	private static String VERSION = "v0.21a";
	
	String 	gameboard = "";
	String 	out = "";
	
	int 	numTrials;
	int 	maxSteps;
	int 	worldSize;
	int 	heuristic;

	public static void main(String args[]) throws Exception {

		WorldApplication wa = new WorldApplication();

		boolean nonDeterministicMode = false;

		if (wa.readPara(args) == 6) {
			FileWriter fw = new FileWriter(wa.out);

			int trialScores[] = new int[wa.numTrials];
			String trialStateSeqs[] = new String[wa.numTrials];
			String path[] = new String[wa.numTrials];
			int totalScore = 0;

			for (int currTrial = 0; currTrial < wa.numTrials; currTrial++) {

				char[][][] wumpusWorld = readWumpusWorld(wa.worldSize, wa.gameboard);
				//char[][][] wumpusWorld = generateRandomWumpusWorld((int) System.currentTimeMillis(), wa.worldSize, true);
				Environment wumpusEnvironment = new Environment(wa.worldSize, wumpusWorld);

				Simulation trial = new Simulation(wumpusEnvironment,
						wa.maxSteps, nonDeterministicMode, wa.heuristic); // ,
																			// outputWriter,
																			// nonDeterministicMode);
				trialScores[currTrial] 		= trial.getScore();
				trialStateSeqs[currTrial] 	= trial.getStateSeq();
				path[currTrial] 			= trial.getpath();

			}

			for (int i = 0; i < wa.numTrials; i++) {

				fw.write("\nTrial " + (i + 1) + " score: " + trialScores[i]
						+ "\n");
				fw.write("Trial " + (i + 1) + " StateSeq: " + trialStateSeqs[i]
						+ "\n");
				fw.write("Trial " + (i + 1) + " path: " + path[i] + "\n");

				totalScore += trialScores[i];

			}

			fw.write("\nNumber of trials: " + wa.numTrials + "\n");
			fw.write("Total Score: " + totalScore + "\n");
			fw.write("Average Score: "
					+ ((double) totalScore / (double) wa.numTrials) + "\n");

			fw.close();

		} else {
			wa.usage();
		}

	}

	private void usage() {

		System.out.println("Usage:\n\n-i gameboard.txt");
		System.out.println("-o output.txt");
		System.out.println("-n number of trails");
		System.out.println("-ms max steps");
		System.out.println("-ws world size");
		System.out.println("-h heuristic number 1,2,3,4,5");

		System.out.println("\njava WorldApplication -i gameborad.txt -o output.txt -n 1 -ms 50 -ws 4 -h 1");

	}

	private int readPara(String args[]) {

		int n = 0;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-i")) {
				this.gameboard = args[i + 1];
				n++;
			} else if (args[i].equals("-o")) {
				this.out = args[i + 1];
				n++;
			} else if (args[i].equals("-n")) {
				this.numTrials = Integer.parseInt(args[i + 1]);
				n++;
			} else if (args[i].equals("-ms")) {
				this.maxSteps = Integer.parseInt(args[i + 1]);
				n++;
			} else if (args[i].equals("-ws")) {
				this.worldSize = Integer.parseInt(args[i + 1]);
				n++;
			} else if (args[i].equals("-h")) {
				this.heuristic = Integer.parseInt(args[i + 1]);
				n++;
			}
		}

		return n;
	}

	public static char[][][] generateRandomWumpusWorld(int seed, int size,
			boolean randomlyPlaceAgent) {

		char[][][] newWorld = new char[size][size][4];
		boolean[][] occupied = new boolean[size][size];

		int x, y;

		Random randGen = new Random(seed);

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				for (int k = 0; k < 4; k++) {
					newWorld[i][j][k] = ' ';
				}
			}
		}

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				occupied[i][j] = false;
			}
		}

		int pits = size;

		// default agent location
		// and orientation
		int agentXLoc = 0;
		int agentYLoc = 0;
		char agentIcon = '>';

		// randomly generate agent
		// location and orientation
		if (randomlyPlaceAgent == true) {

			agentXLoc = randGen.nextInt(size);
			agentYLoc = randGen.nextInt(size);

			switch (randGen.nextInt(4)) {

			case 0:
				agentIcon = 'A';
				break;
			case 1:
				agentIcon = '>';
				break;
			case 2:
				agentIcon = 'V';
				break;
			case 3:
				agentIcon = '<';
				break;
			}
		}

		// place agent in the world
		newWorld[agentXLoc][agentYLoc][3] = agentIcon;

		// Pit generation
		for (int i = 0; i < pits; i++) {

			x = randGen.nextInt(size);
			y = randGen.nextInt(size);

			while ((x == agentXLoc && y == agentYLoc) | occupied[x][y] == true) {
				x = randGen.nextInt(size);
				y = randGen.nextInt(size);
			}

			occupied[x][y] = true;

			newWorld[x][y][0] = 'P';

		}

		// Wumpus Generation
		x = randGen.nextInt(size);
		y = randGen.nextInt(size);

		while (x == agentXLoc && y == agentYLoc) {
			x = randGen.nextInt(size);
			y = randGen.nextInt(size);
		}

		occupied[x][y] = true;

		newWorld[x][y][1] = 'W';

		// Gold Generation
		x = randGen.nextInt(size);
		y = randGen.nextInt(size);

		// while (x == 0 && y == 0) {
		// x = randGen.nextInt(size);
		// y = randGen.nextInt(size);
		// }

		occupied[x][y] = true;

		newWorld[x][y][2] = 'G';

		return newWorld;
	}

	public static char[][][] readWumpusWorld(int size, String gameboard)
			throws Exception {

		char[][][] newWorld = new char[size][size][4]; // generateRandomWumpusWorld(123,
														// 4, true);

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				newWorld[i][j][0] = ' ';
				newWorld[i][j][1] = ' ';
				newWorld[i][j][2] = ' ';
				newWorld[i][j][3] = ' ';
			}
		}

		// 4 P,W,G,A

		File f = new File(gameboard);
		FileReader red = new FileReader(f);

		BufferedReader reader = new BufferedReader(red);

		int row = size;

		while (true) {

			String line = reader.readLine();

			if (line == null)
				break;

			int col = -1;

			if (line.startsWith(" -")) {
				row--;
				continue;
			} else if (line.startsWith("|")) {

				for (int i = 0; i < line.length(); i++) {
					if (line.charAt(i) == '|') {
						col++;
					}

					if (line.charAt(i) == 'P' || line.charAt(i) == 'W'
							|| line.charAt(i) == 'G' || line.charAt(i) == 'A'
							|| line.charAt(i) == '<' || line.charAt(i) == '>'
							|| line.charAt(i) == 'V') {
						switch (line.charAt(i)) {
						case 'P':
							newWorld[row][col][0] = 'P';
							break;
						case 'W':
							newWorld[row][col][1] = 'W';
							break;
						case 'G':
							newWorld[row][col][2] = 'G';
							break;
						case 'A':
							//System.out.println("!!!!! A row : " + row + " col : " + col);
							newWorld[row][col][3] = 'A';
							break;
						case '<':
							//System.out.println("!!!!! < row : " + row + " col : " + col);
							newWorld[row][col][3] = '<';
							break;
						case '>':
							//System.out.println("!!!!! > row : " + row + " col : " + col);
							newWorld[row][col][3] = '>';
							break;
						case 'V':
							//System.out.println("!!!!! V row : " + row + " col : " + col);
							newWorld[row][col][3] = 'V';
							break;

						}
					}
				}
			}
		}

		reader.close();

		return newWorld;
	}

}