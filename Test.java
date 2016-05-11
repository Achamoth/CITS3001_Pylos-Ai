import java.util.ArrayList;

public class Test {
	public static void main(String[] args) {
		Pylos game = new Pylos();
		
		game.place(1, "a1");
		game.place(1, "a2");
		game.place(1, "a3");
		ArrayList<PylosMove> moves = PylosAI.actions(game, 1);
		System.out.println(moves.size());
		for(PylosMove curMove : moves) {
			Driver.printMove(curMove);
		}
	}
}
