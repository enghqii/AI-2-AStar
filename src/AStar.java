import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;


public class AStar {
	
	class AStarNode implements Comparable<AStarNode>{
		
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

	private int beginX, beginY;
	private int endX, endY;

	// frontier
	private PriorityQueue<AStarNode> 	openList = new PriorityQueue<AStar.AStarNode>();
	// explored set
	private Queue<AStarNode> 			closeList = new LinkedList<AStar.AStarNode>();
	
	private AStarHeuristicStrategy 		strategy;

	public AStar(int beginX, int beginY, int endX, int endY, int heuristic) {

		this.beginX = beginX;
		this.beginY = beginY;

		this.endX = endX;
		this.endY = endY;
		
		switch (heuristic) {
		case 1:
			strategy = new ManhattanStrategy();
			break;
		case 2:
			strategy = new EuclidStrategy();
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			break;
		default:
			strategy = null;
			break;
		}
	}
	
	public void PathFind(){
		
	}
	
	public void getPath(){
		
	}

}
