
public class Driver {
	
	private final static int WHITE = 1;
	private final static int BLACK = 2;
	
	public static void main(String[] args) {
		Pylos game = new Pylos();
		
		game.place(WHITE, "a1");
		game.place(WHITE, "a2");
		game.place(WHITE, "b1");
		game.place(BLACK, "b2");
		game.place(WHITE, "a3");
		
		game.raise(WHITE, "a3", "e2");
		
		game.display();
	}
}
