import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;


public class AStar {
	
	private class AStarNode implements Comparable<AStarNode>{
		
		// position
		public int x;
		public int y;
		
		// values
		public int g;
		public int h;
		
		public int f;
		
		public char direction; // 'N' 'W' 'E' 'S'
		
		public AStarNode(int x, int y, int g, int h, char direction){
			
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

	
	private int mapSize; // boundary (0 - mapSize-1)
	
	private int beginX, beginY;
	private int endX, endY;

	// frontier
	private PriorityQueue<AStarNode> 	openList = new PriorityQueue<AStar.AStarNode>();
	// explored set
	private Stack<AStarNode> 			closeList = new Stack<AStar.AStarNode>();

	private AStarHeuristicStrategy 		strategy;
	private Environment 				environment;
	
	public AStar(Environment environment, int heuristic){
		
		this.mapSize = environment.getWorldSize();
		
		int[] agentLocation = environment.getAgentLocation();
		this.beginX = agentLocation[0];
		this.beginY = agentLocation[1];

		int[] goldLocation = environment.getGoldLocation();
		this.endX = goldLocation[0];
		this.endY = goldLocation[1];

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

	public void PathFind(){
		
		openList.clear();
		closeList.clear();

		System.out.println("Begins at " + beginX + ", " + beginY);
		System.out.println("Ends at " + endX + ", " + endY);
		
		int h = strategy.Heuristic(beginX, beginY, endX, endY);
		AStarNode initial = new AStarNode(beginX, beginY, 0, h, ' ');
		openList.add(initial);
		
		while(openList.isEmpty() == false){
			
			AStarNode node = openList.poll();
			closeList.push(node);
			
			System.out.println("Now Inspecting " + node.x + "," + node.y + " G is " + node.g + " H is " + node.h);
			
			if(node.x == this.endX && node.y == this.endY){
				break;
			}

			// expand only 'explorable' node	
			if( node.x-1 >= 0 && environment.isAvailableLocation(node.x-1, node.y)){
				h = strategy.Heuristic(node.x - 1, node.y, endX, endY);
				AStarNode expanded = new AStarNode(node.x - 1, node.y, node.g + 1, h, 'N');
				openList.add(expanded);
				System.out.println("Exanding " + expanded.x + ", " + expanded.y);
			}
			if( node.x+1 < mapSize && environment.isAvailableLocation(node.x+1, node.y)){
				h = strategy.Heuristic(node.x+1, node.y, endX, endY);
				AStarNode expanded = new AStarNode(node.x + 1, node.y, node.g + 1, h, 'S');
				openList.add(expanded);
				System.out.println("Exanding " + expanded.x + ", " + expanded.y);
			}
			if( node.y-1 >= 0 && environment.isAvailableLocation(node.x, node.y-1)){
				h = strategy.Heuristic(node.x, node.y-1, endX, endY);
				AStarNode expanded = new AStarNode(node.x, node.y - 1, node.g + 1, h, 'W');
				openList.add(expanded);
				System.out.println("Exanding " + expanded.x + ", " + expanded.y);
			}
			if( node.y+1 < mapSize && environment.isAvailableLocation(node.x, node.y+1)){
				h = strategy.Heuristic(node.x, node.y+1, endX, endY);
				AStarNode expanded = new AStarNode(node.x, node.y+1, node.g + 1, h, 'E');
				openList.add(expanded);
				System.out.println("Exanding " + expanded.x + ", " + expanded.y);
			}
		}
	}
	
	private void pushOpenList(int x, int y, int g){
		
	}
	
	public ArrayList<Integer[]> getPath(){
		
		ArrayList<Integer[]> path = new ArrayList<Integer[]>();
		
		int nextX = endX, nextY = endY;
		
		while(closeList.isEmpty() == false){
			
			AStarNode node = closeList.pop();
			
			if(nextX == node.x && nextY == node.y){
				Integer[] pos = new Integer[2];
			
				pos[0] = node.x;
				pos[1] = node.y;
				
				path.add(pos);
				
				switch(node.direction){
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
					System.out.println("end?");
					break;
				}
			}
		}
		
		return path;
	}

}
