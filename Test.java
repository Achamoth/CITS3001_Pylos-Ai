
public class Test {
	public static void main(String[] args) {
		Pylos game = new Pylos();
		game.place(1, "a1");
		game.place(2, "a2");
		game.place(1, "b1");
		game.place(2, "b2");
		game.place(1, "d3");
		game.place(2, "d2");
		game.display();
		game.raise(1, "d3", "e1");
		game.display();
		
		//Test copy() method
		Pylos dupe = game.copy();
		dupe.display();
	}
}
