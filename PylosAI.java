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
	private PylosState board;
	
	public static void main(String[] args) {
		Pylos game = new Pylos();
		game.place(1, "a1");
		game.place(1, "a2");
		game.place(1, "a3");
		game.place(1, "a4");
		
	}
}