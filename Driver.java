import java.util.ArrayList;

public class Driver {
	
	private final static int WHITE = 1;
	private final static int BLACK = 2;
	
	public static void main(String[] args) {
		Pylos game = new Pylos();
		
		ArrayList<PylosMove> moves = PylosAI.Actions(game, WHITE);
		//System.out.println(moves.size());
		PylosMove move = moves.get(2);
		game.applyMove(move, WHITE);
		game.display();
		
	}
}
