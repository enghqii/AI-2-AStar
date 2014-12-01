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

		int[] goldLocation = environment.getGoldLocation();
		this.endX = goldLocation[0];
		this.endY = goldLocation[1];
		
		switch (heuristic) {
		case 1:
			strategy = new ChebyshevStrategy();
			break;
		case 2:
			strategy = new EuclidStrategy();
			break;
		case 3:
			strategy = new ManhattanStrategy();
			break;
		case 4:
			strategy = new WeightedEuclidStrategy(6.00f);
			break;
		case 5:
			strategy = new WeightedManhattanStrategy(4.50f);
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

		//System.out.println("Begins at " + beginX + ", " + beginY);
		//System.out.println("Ends at " + endX + ", " + endY);

		int h = strategy.Heuristic(beginX, beginY, endX, endY);
		AStarNode initial = new AStarNode(beginX, beginY, 0, h, ' ');
		openList.add(initial);
		
		int i = 0;

		while (openList.isEmpty() == false) {
			i++;
			
			AStarNode node = openList.poll();
			closeList.push(node);

			System.out.println("\nNow Inspecting (" + node.x + "," + node.y+ ") G is [" + node.g + "] H is [" + node.h + "] F = ["+ node.f + "]");

			if (node.x == this.endX && node.y == this.endY) {
				break; // destination
			}
			
			this.pushOpenList(node.x - 1, node.y, node.g + 1, 'N', node.direction);
			this.pushOpenList(node.x + 1, node.y, node.g + 1, 'S', node.direction);
			this.pushOpenList(node.x, node.y - 1, node.g + 1, 'W', node.direction);
			this.pushOpenList(node.x, node.y + 1, node.g + 1, 'E', node.direction);
		}

		System.out.println("closeList size " + closeList.size() + " openList size " + openList.size());
		
		this.postFindPath();
		this.postFindStateSeq();
		
		//System.out.println("loop cnt " + i);
		System.out.println("ACTIONS " + this.StateSeq.size());
	}

	private void pushOpenList(int x, int y, int g, char direction, char lastDirection) {

		for (AStarNode node : closeList) {
			if (node.x == x && node.y == y) {
				return;
			}
		}

		if (environment.isAvailableLocation(x, y)) {
			
			int diff = CharDirToInt(lastDirection) - CharDirToInt(direction);
			
			// discriminative weights
			if (diff == 1 || diff == -3 || diff == -1 || diff == 3) {
				g += 1;
			} else if (diff == 2 || diff == -2) {
				g += 2;
			} 
			
			// wumpus shooting cost
			if(environment.isWumpusLocation(x, y)){
				g += 2; // shoot cost
			}
			
			int h = strategy.Heuristic(x, y, endX, endY);
			AStarNode expanded = new AStarNode(x, y, g, h, direction);
			
			for (AStarNode node : openList) {
				if (node.x == x && node.y == y && node.f < expanded.f) {
					return;
				}
			}
			
			openList.add(expanded);
			//System.out.println(">> Exanding " + expanded.x + ", " + expanded.y);
		}
		
	}

	private void postFindPath() {

		path.clear();
		
		int nextX = endX, nextY = endY;

		while (closeList.isEmpty() == false) {

			AStarNode node = closeList.pop();

			if (nextX == node.x && nextY == node.y) {
				Integer[] pos = new Integer[2];
				pos[0] = node.x; pos[1] = node.y;

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
				}
			}
		}
		
		Collections.reverse(path);
	}
	
	private void postFindStateSeq() {

		int agentDir 	= CharDirToInt(environment.getAgentDirection());
		int nodeDir 	= -1;
		
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

			/* find nodeDir */ {
				
				int dirX = pos[0] - before[0];
				int dirY = pos[1] - before[1];

				if (dirX == 1 && dirY == 0) {
					nodeDir = NORTH;
				} else if (dirX == -1 && dirY == 0) {
					nodeDir = SOUTH;
				} else if (dirX == 0 && dirY == -1) {
					nodeDir = WEST;
				} else if (dirX == 0 && dirY == 1) {
					nodeDir = EAST;
				}
			}

			int diff = agentDir - nodeDir;
			//System.out.println("agentDir [" + agentDir + "] nodeDir [" + nodeDir + "] diff [" + diff + "]");

			/* Agent Turn */{

				if (diff == 1 || diff == -3) {
					// left
					StateSeq.add(Action.TURN_LEFT);
					
				} else if (diff == -1 || diff == 3) {
					// right
					StateSeq.add(Action.TURN_RIGHT);
					
				} else if (diff == 2 || diff == -2) {
					// left * 2
					StateSeq.add(Action.TURN_LEFT);
					StateSeq.add(Action.TURN_LEFT);
				}
			}
			
			/* Agent Shoot */
			{
				//Integer[] wumpusLocation = environment.getWumpusLocation();
				if(environment.isWumpusLocation(pos[0], pos[1])){
					StateSeq.add(Action.SHOOT);
				}
			}
			
			/* Agent GoFwd */
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
		case 'N': return NORTH; case 'S': return SOUTH; 
		case 'W': return WEST; case 'E': return EAST; }
		return -1;
	}

}
