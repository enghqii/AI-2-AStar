import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Stack;

public class AStar {

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

	private int beginX, beginY;
	private int endX, endY;
	
	private char direction; // initial agent direction

	// frontier
	private PriorityQueue<AStarNode> openList = new PriorityQueue<AStar.AStarNode>();
	// explored set
	private Stack<AStarNode> closeList = new Stack<AStar.AStarNode>();

	private AStarHeuristicStrategy strategy;
	private Environment environment;
	
	// results
	private ArrayList<Integer[]> path = new ArrayList<Integer[]>();
	private ArrayList<Integer> StateSeq = new ArrayList<Integer>();

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
		
		this.postFind();
	}

	public void postFind() {
		
		path.clear();
		
		StateSeq.clear();
		StateSeq.add(Action.END_TRIAL);
		StateSeq.add(Action.GRAB);
		
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
		
		StateSeq.add(Action.START_TRIAL);

		Collections.reverse(path);
		Collections.reverse(StateSeq);
		
	}
	

	public ArrayList<Integer[]> getPath() {
		return this.path;
	}

}
