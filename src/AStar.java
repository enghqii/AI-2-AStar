import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;


public class AStar {
	
	private class AStarNode implements Comparable<AStarNode>{
		
		// position
		public int x;
		public int y;
		
		// values
		public int g;
		public int h;
		
		public int f;
		
		public AStarNode(int x, int y, int g, int h){
			
			this.x = x;
			this.y = y;
			
			this.g = g;
			this.h = h;
			
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
	private Queue<AStarNode> 			closeList = new LinkedList<AStar.AStarNode>();

	private AStarHeuristicStrategy 		strategy;
	
	private Environment environment;
	
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
		AStarNode initial = new AStarNode(beginX, beginY, 0, h);
		openList.add(initial);
		
		while(openList.isEmpty() == false){
			
			AStarNode node = openList.poll();
			closeList.add(node);
			
			System.out.println("Now Inspecting " + node.x + "," + node.y);
			
			if(node.x == this.endX && node.y == this.endY){
				break;
			}

			// expand only 'explorable' node	
			if( node.x-1 > 0 && environment.isAvailableLocation(node.x-1, node.y)){
				h = strategy.Heuristic(node.x - 1, node.y, endX, endY);
				openList.add(new AStarNode(node.x - 1, node.y, node.g + 1, h));
			}
			if( node.x+1 < mapSize && environment.isAvailableLocation(node.x+1, node.y)){
				h = strategy.Heuristic(node.x+1, node.y, endX, endY);
				openList.add(new AStarNode(node.x+1, node.y, node.g + 1, h));
			}
			if( node.y-1 > 0 && environment.isAvailableLocation(node.x, node.y-1)){
				h = strategy.Heuristic(node.x, node.y-1, endX, endY);
				openList.add(new AStarNode(node.x, node.y-1, node.g + 1, h));
			}
			if( node.y+1 < mapSize && environment.isAvailableLocation(node.x, node.y+1)){
				h = strategy.Heuristic(node.x, node.y+1, endX, endY);
				openList.add(new AStarNode(node.x, node.y+1, node.g + 1, h));
			}
		}
		
		for(AStarNode node : closeList){
			System.out.println("" + node.x + ", " + node.y);
		}
	}
	
	public ArrayList<Integer[]> getPath(){
		return new ArrayList<Integer[]>();
	}

}
