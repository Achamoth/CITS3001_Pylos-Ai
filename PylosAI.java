import java.util.ArrayList;

public class PylosAI {
	//Move types
	private final static int PLACE = 1;
	private final static int RAISE = 2;
	
	//Position states
	private static final int EMPTY = -1;
	private static final int BLACK = 1;
	private static final int WHITE = 2;

	private static final int A = 0;
	private static final int B = 1;
	private static final int C = 2;
	private static final int D = 3;
	private static final int E = 0;
	private static final int F = 1;
	private static final int G = 2;
	private static final int H = 0;
	private static final int I = 1;
	private static final int J = 0;
	
	//State of game
	private Pylos board;
	
	//Utility function; looks at state and depth, and determines utility if it's a terminal state
	private static int utility(Pylos state, int depth) {
		if(terminal(state)) {
			//Will return utility from perspective of white player
			if(state.winner() == WHITE) return 100-depth;
			else return depth - 100;
		}
		else return -1;
	}
	
	//Terminal function; looks at state and determines whether or not it's a terminal state
	private static boolean terminal(Pylos state) {
		return state.isComplete();
	}
	
	//Result function; computes result of applying given action to given state
	private static Pylos result(Pylos state, PylosMove action) {
		Pylos newState = state.copy(); 
		//TODO: Apply 'action' to newState and return newState
	}
	
	//Actions function; returns array list of all possible actions in a given state
	private static ArrayList<PylosMove> Actions(Pylos state) {
		ArrayList<PylosMove> result = new ArrayList<PylosMove>();
		//TODO: Look at 'state' and determine all possible moves, and add each one to the array list
	}
	
	//Minimax function; looks at give state and computes best move from perspective of white player
	private static PylosMove Minimax(Pylos state) {
		//TODO: Finish all the bullshit above and write the recursive rule
	}
	
	public static void main(String[] args) {
		Pylos game = new Pylos();
	}
}