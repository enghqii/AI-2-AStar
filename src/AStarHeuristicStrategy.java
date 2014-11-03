
public interface AStarHeuristicStrategy {
	public int Heuristic(int posX, int posY, int endX, int endY);
}

class ManhattanStrategy implements AStarHeuristicStrategy{

	public int Heuristic(int posX, int posY, int endX, int endY) {
		return Math.abs(posX - endX) + Math.abs(posY - endY);
	}
}

class EuclidStrategy implements AStarHeuristicStrategy{

	public int Heuristic(int posX, int posY, int endX, int endY) {
		return (int) Math.sqrt(Math.pow(posX - endX, 2) + Math.pow(posY - endY, 2));
	}
	
}

class ChebyshevStrategy implements AStarHeuristicStrategy{
	
	public int Heuristic(int posX, int posY, int endX, int endY) {
	
		float dx = Math.abs(posX - endX);
		float dy = Math.abs(posY - endY);
		
		//return (int) Math.max(dx, dy);
		 return (int) ((dx + dy) + (Math.sqrt(2) - 2 ) * Math.min(dx, dy));
	}
}