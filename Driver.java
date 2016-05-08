
public class Driver {
	public static void main(String[] args) {
		Pylos game = new Pylos();
		
		int to[] = {0,1};
		PylosMove nextMove = new PylosMove(1, 1, to, null, false, 0, null, null);
		game.applyMove(nextMove);
		
		to[0] = 1; to[1] = 1;
		nextMove = new PylosMove(1, 1, to, null, false, 0, null, null);
		game.applyMove(nextMove);
		
		to[0]  = 0; to[1] = 0;
		nextMove = new PylosMove(1, 1, to, null, false, 0, null, null);
		game.applyMove(nextMove);
		
		to[0] = 1; to[1] = 0;
		nextMove = new PylosMove(1, 1, to, null, false, 0, null, null);
		game.applyMove(nextMove);
		
		to[0] = 1; to[1] = 3;
		nextMove = new PylosMove(1, 1, to, null, false, 0, null, null);
		game.applyMove(nextMove);
		
		to[0] = 0; to[1] = 0;
		nextMove = new PylosMove(1, 1, to, null, false, 0, null, null);
		game.applyMove(nextMove);
		
		to[0] = 0; to[1] = 3;
		nextMove = new PylosMove(1, 1, to, null, false, 0, null, null);
		game.applyMove(nextMove);
		
		int from[] = {0, 3};
		to[0] = 0; to[1] = 0;
		nextMove = new PylosMove(2, 1, to, from, false, 0, null, null);
		game.applyMove(nextMove);
		
		to[0] = 1; to[1] = 2;
		//int rem1[] = {2, 0, 0};
		//int rem2[] = {1, 1, 1};
		nextMove = new PylosMove(1, 2, to, null, false, 0, null, null);
		game.applyMove(nextMove);
		
		game.display();
	}
}
