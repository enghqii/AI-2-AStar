
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