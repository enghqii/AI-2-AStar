import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Stack;

public class AStar {

	// Compass
	private static int NORTH = 0 ,EAST = 1,SOUTH = 2, WEST = 3;
	
	private class AStarNode implements Comparable<AStarNode> {

		// position
		public int x;
		public int y;

		// values
		public int g;
		public int h;

		public int f;

		public char direction; // 'N' 'W' 'E' 'S'

		public AStarNode(int x, int y, int g, int h, char direction) {

			this.x = x;
			this.y = y;

			this.g = g;
			this.h = h;

			this.direction = direction;

			this.f = this.g + this.h;
		}

		public int compareTo(AStarNode arg) {
			return (this.f - arg.f);
		}
	}

	private int 						beginX, beginY;
	private int 						endX, endY;
	
	private char 						direction; 											// initial agent direction

	private PriorityQueue<AStarNode> 	openList 	= new PriorityQueue<AStar.AStarNode>(); // frontier
	private Stack<AStarNode> 			closeList 	= new Stack<AStar.AStarNode>(); 		// explored set

	private AStarHeuristicStrategy 		strategy;
	private Environment 				environment;
	
	// results
	private ArrayList<Integer[]> 		path = new ArrayList<Integer[]>();
	private ArrayList<Integer> 			StateSeq = new ArrayList<Integer>();

	// CTOR
	public AStar(Environment environment, int heuristic) {

		int[] agentLocation = environment.getAgentLocation();
		this.beginX = agentLocation[0];
		this.beginY = agentLocation[1];
		
		this.direction = environment.getAgentDirection();

		int[] goldLocation = environment.getGoldLocation();
		this.endX = goldLocation[0];
		this.endY = goldLocation[1];

		heuristic = 1;

		switch (heuristic) {
		case 1:
			strategy = new ManhattanStrategy();
			break;
		case 2:
			strategy = new EuclidStrategy();
			break;
		default:
			strategy = null;
			break;
		}

		this.environment = environment;
	}

	public void PathFind() {

		openList.clear();
		closeList.clear();

		System.out.println("Begins at " + beginX + ", " + beginY);
		System.out.println("Ends at " + endX + ", " + endY);

		int h = strategy.Heuristic(beginX, beginY, endX, endY);
		AStarNode initial = new AStarNode(beginX, beginY, 0, h, ' ');
		openList.add(initial);

		while (openList.isEmpty() == false) {

			AStarNode node = openList.poll();
			closeList.push(node);

			System.out.println("\nNow Inspecting (" + node.x + "," + node.y+ ") G is [" + node.g + "] H is [" + node.h + "] F = ["+ node.f + "]");

			if (node.x == this.endX && node.y == this.endY) {
				break; // destination
			}
			
			this.pushOpenList(node.x - 1, node.y, node.g + 1, 'N');
			this.pushOpenList(node.x + 1, node.y, node.g + 1, 'S');
			this.pushOpenList(node.x, node.y - 1, node.g + 1, 'W');
			this.pushOpenList(node.x, node.y + 1, node.g + 1, 'E');
		}
		
		this.postFindPath();
		this.postFindStateSeq();
	}

	private void pushOpenList(int x, int y, int g, char direction) {

		for (AStarNode node : closeList) {
			if (node.x == x && node.y == y) {
				System.out.println("exclusive " + node.x + " " + node.y);
				return;
			}
		}

		if (environment.isAvailableLocation(x, y)) {

			int h = strategy.Heuristic(x, y, endX, endY);
			AStarNode expanded = new AStarNode(x, y, g, h, direction);
			openList.add(expanded);
			System.out.println(">> Exanding " + expanded.x + ", " + expanded.y);
		}
		
	}

	private void postFindPath() {
		
		// path
		path.clear();
		
		int nextX = endX, nextY = endY;

		while (closeList.isEmpty() == false) {

			AStarNode node = closeList.pop();

			if (nextX == node.x && nextY == node.y) {
				Integer[] pos = new Integer[2];

				pos[0] = node.x;
				pos[1] = node.y;

				path.add(pos);

				switch (node.direction) {
				case 'N':
					nextX++;
					break;
				case 'S':
					nextX--;
					break;
				case 'W':
					nextY++;
					break;
				case 'E':
					nextY--;
					break;
				default:
					System.out.println("end");
					break;
				}
			}
		}
		
		Collections.reverse(path);
	}
	
	private void postFindStateSeq() {
		
		// state seq
		int agentDir = CharDirToInt(environment.getAgentDirection());
		int nodeDir = -1;
		
		StateSeq.clear();
		StateSeq.add(Action.START_TRIAL);
		
		Integer[] before = null;
		for(Integer[] pos : path){
			
			if(before == null){
			
				before = new Integer[2];
				
				before[0] = pos[0];
				before[1] = pos[1];
				continue;
			}

			// find nodeDir
			int dirX = pos[0] - before[0];
			int dirY = pos[1] - before[1];
			
			if( dirX == 1 && dirY == 0 ){
				nodeDir = NORTH;
				System.out.println("N");
			}else if( dirX == -1 && dirY == 0 ){
				nodeDir = SOUTH;
				System.out.println("S");
			}else if( dirX == 0 && dirY == -1){
				nodeDir = WEST;
				System.out.println("W");
			}else if( dirX == 0 && dirY == 1 ){
				nodeDir = EAST;
				System.out.println("E");
			}
			
			int diff = agentDir - nodeDir;
			
			System.out.println("agentDir [" + agentDir + "] nodeDir [" + nodeDir + "] diff [" + diff + "]");
			
			if(diff == 1 || diff == -3){
				// left
				StateSeq.add(Action.TURN_LEFT);
				System.out.println("turn left");
			}else if(diff == -1 || diff == 3){
				// right
				StateSeq.add(Action.TURN_RIGHT);
				System.out.println("turn right");
			}else if(diff == 2){
				// left * 2
				StateSeq.add(Action.TURN_LEFT);
				StateSeq.add(Action.TURN_LEFT);
				System.out.println("turn left * 2");
			}
			
			
			StateSeq.add(Action.GO_FORWARD);
			
			// succeeding..
			agentDir = nodeDir;
			before[0] = pos[0];
			before[1] = pos[1];
		}

		StateSeq.add(Action.GRAB);
		StateSeq.add(Action.END_TRIAL);
	}
	

	public ArrayList<Integer[]> getPath() {
		return this.path;
	}
	
	public ArrayList<Integer> getStateSeq() {
		return this.StateSeq;
	}
	
	private int CharDirToInt(char d){
		switch(d){
		case 'N':
			return NORTH;
		case 'S':
			return SOUTH;
		case 'W':
			return WEST;
		case 'E':
			return EAST;
		}
		return -1;
	}

}
