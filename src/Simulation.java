/*
 * Class that defines the simulation environment.
 * 
 * Written by James P. Biagioni (jbiagi1@uic.edu)
 * for CS511 Artificial Intelligence II
 * at The University of Illinois at Chicago
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

import java.util.*;

class Simulation {

	private int currScore = 0;
	private static int actionCost = 1;
	private static int deathCost = 0;
	private static int shootCost = 2;
	private static int goldCost = 0;
	private int stepCounter = 0;
	private int lastAction = 0;

	private boolean simulationRunning;

	private Agent agent;
	private Environment environment;
	private TransferPercept transferPercept;

	private ArrayList<Integer> StateSeq = new ArrayList<Integer>();
	private ArrayList<Integer[]> path = new ArrayList<Integer[]>();

	public Simulation(Environment wumpusEnvironment, int maxSteps,
			boolean nonDeterministic, int heuristic) {

		// implement here
		
		transferPercept = new TransferPercept(wumpusEnvironment);
		environment = wumpusEnvironment;
		
		agent = new Agent(environment, transferPercept, nonDeterministic);
		
		environment.placeAgent(agent);

		// just find the path, and seq.
		// getScore will do.

		AStar astar = new AStar(environment, heuristic);
		astar.PathFind();
		this.path = astar.getPath();
		this.StateSeq = astar.getStateSeq();

	}

	public String getStateSeq() {

		if(StateSeq.isEmpty() == false){
			String seq = Action.printAction(StateSeq.get(0)) + ",";

			for (int i = 1; i < StateSeq.size() - 1; i++) {
				seq += (i) + "_" + Action.printAction(StateSeq.get(i)) + ",";
			}

			seq += Action.printAction(StateSeq.get(StateSeq.size() - 1));

			return seq;
		}else{
			return "";
		}
	}

	public String getpath() {

		if (this.path.size() > 0) {

			String path = "(" + this.path.get(0)[0] + "," + this.path.get(0)[1]
					+ ")";

			for (int i = 1; i < this.path.size(); i++)
				path += ",(" + this.path.get(i)[0] + "," + this.path.get(i)[1]
						+ ")";

			return path;
			
		} else {
			return "";
		}
	}

	public int getScore() {
		for (int i = 0; i < StateSeq.size(); i++)
			this.handleAction(StateSeq.get(i));	// actions're handled here

		return currScore;
	}

	public void printEndWorld() {
		try {

			environment.printEnvironment();

			System.out.println("Final score: " + currScore);
			System.out
					.println("Last action: " + Action.printAction(lastAction));

		} catch (Exception e) {
			System.out.println("An exception was thrown: " + e);
		}
	}

	public void printCurrentPerceptSequence() {

		try {

			System.out.print("Percept: <");

			if (transferPercept.getBump() == true) {
				System.out.print("bump,");

			} else if (transferPercept.getBump() == false) {
				System.out.print("none,");

			}
			if (transferPercept.getGlitter() == true) {
				System.out.print("glitter,");

			} else if (transferPercept.getGlitter() == false) {
				System.out.print("none,");

			}
			if (transferPercept.getBreeze() == true) {
				System.out.print("breeze,");

			} else if (transferPercept.getBreeze() == false) {
				System.out.print("none,");

			}
			if (transferPercept.getStench() == true) {
				System.out.print("stench,");

			} else if (transferPercept.getStench() == false) {
				System.out.print("none,");

			}
			if (transferPercept.getScream() == true) {
				System.out.print("scream>\n");

			} else if (transferPercept.getScream() == false) {
				System.out.print("none>\n");

			}

		} catch (Exception e) {
			System.out.println("An exception was thrown: " + e);
		}

	}

	public void handleAction(int action) {

		try {

			if (action == Action.GO_FORWARD) {

				if (environment.getBump() == true)
					environment.setBump(false);

				agent.goForward();
				environment.placeAgent(agent);

				if (environment.checkDeath() == true) {

					currScore += deathCost;
					simulationRunning = false;

					agent.setIsDead(true);
					
				} else {
					
					currScore += actionCost;
				}

				if (environment.getScream() == true)
					environment.setScream(false);

				lastAction = Action.GO_FORWARD;
				
			} else if (action == Action.TURN_RIGHT) {

				currScore += actionCost;
				agent.turnRight();
				environment.placeAgent(agent);

				if (environment.getBump() == true)
					environment.setBump(false);
				if (environment.getScream() == true)
					environment.setScream(false);

				lastAction = Action.TURN_RIGHT;
				
			} else if (action == Action.TURN_LEFT) {

				currScore += actionCost;
				agent.turnLeft();
				environment.placeAgent(agent);

				if (environment.getBump() == true)
					environment.setBump(false);
				if (environment.getScream() == true)
					environment.setScream(false);

				lastAction = Action.TURN_LEFT;
				
			} else if (action == Action.GRAB) {

				if (environment.grabGold() == true) {

					currScore += goldCost;
					simulationRunning = false;

					agent.setHasGold(true);
					
				} else
					currScore += actionCost;

				environment.placeAgent(agent);

				if (environment.getBump() == true)
					environment.setBump(false);
				if (environment.getScream() == true)
					environment.setScream(false);

				lastAction = Action.GRAB;
				
			} else if (action == Action.SHOOT) {

				currScore += shootCost;

				environment.shootArrow();
				environment.placeAgent(agent);

				if (environment.getBump() == true)
					environment.setBump(false);

				lastAction = Action.SHOOT;
				
			} else if (action == Action.NO_OP) {

				environment.placeAgent(agent);

				if (environment.getBump() == true)
					environment.setBump(false);
				if (environment.getScream() == true)
					environment.setScream(false);

				lastAction = Action.NO_OP;
			}
			
			environment.printEnvironment();

		} catch (Exception e) {

			System.out.println("An exception was thrown: " + e);
		}
	}

}