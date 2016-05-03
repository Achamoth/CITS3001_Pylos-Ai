import java.util.Scanner;


public class Pylos {
	private static final int EMPTY = -1;
	private static final int WHITE = 1;
	private static final int BLACK = 2;
	
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
	
	//Letters first, then numbers (when addressing a square. i.e. "a3" is bottom_tier[0][2])
	private int[][] bottom_tier = new int[4][4];
	private int[][] second_tier = new int[3][3];
	private int[][] third_tier = new int[2][2];
	private int[][] top_tier = new int[1][1];
	
	private int black_spheres;
	private int white_spheres;
	
	private boolean complete;
	
	public Pylos() {
		black_spheres = 15;
		white_spheres = 15;
		complete = false;
		initialize_board();
	}
	
	//Set up board
	public void initialize_board() {
		//Bottom tier
		for(int i=0; i<4; i++) {
			for(int j=0; j<4; j++) {
				bottom_tier[i][j] = -1;
			}
		}
		//Second tier
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				second_tier[i][j] = -1;
			}
		}
		//Third tier
		for(int i=0; i<2; i++) {
			for(int j=0; j<2; j++) {
				third_tier[i][j] = -1;
			}
		}
		//Top tier
		top_tier[0][0] = -1;
	}
	
	//Print game board
	public void display() {
		//Bottom tier
		System.out.println("Bottom tier:");
		for(int i=0; i<4; i++) {
			for(int j=0; j<4; j++) {
				System.out.print(bottom_tier[i][j]+"\t");
			}
			System.out.print("\n");
		}
		
		//Second tier
		System.out.println("Second tier:");
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				System.out.print(second_tier[i][j]+"\t");
			}
			System.out.print("\n");
		}
		
		//Third tier
		System.out.println("Third tier:");
		for(int i=0; i<2; i++) {
			for(int j=0; j<2; j++) {
				System.out.print(third_tier[i][j]+"\t");
			}
			System.out.print("\n");
		}
		
		//Top tier
		System.out.println("Top tier:");
		System.out.println(top_tier[0][0]);
		
		//Spheres
		System.out.println("Black spheres:" + black_spheres);
		System.out.println("White spheres:" + white_spheres);
		
		//Newline
		System.out.print("\n\n");
	}
	
	//Place player's sphere at position 'pos'
	public void place(int player, String pos) {
		//Interpret 'pos' string
		char letter = pos.charAt(0);
		int pos1 = interpret(letter);
		int pos2 = pos.charAt(1) - '0';
		pos2--;
		int tier = find_tier(letter);
		
		//Make move (i.e. fill square with appropriate colour sphere)
		if(tier == 1) bottom_tier[pos1][pos2] = player;
		else if(tier == 2) second_tier[pos1][pos2] = player;
		else if(tier == 3) third_tier[pos1][pos2] = player;
		else if(tier == 4) top_tier[pos1][pos2] = player;
		
		//Deduct from player's sphere count
		if(player == WHITE) white_spheres--;
		else if(player == BLACK) black_spheres--;
		
		//Check if game has been completed
		if(top_tier[0][0] != EMPTY) game_complete();
		
		//Check if the player has completed a square or line; if so, prompt them to remove 1 or 2 squares
		if(!complete) {
			boolean mustRemove = checkForRemove(tier, pos1, pos2);
			if(mustRemove) {
				System.out.println("You must remove 1/2 piece(s)");
				System.out.print("How many pieces do you want to remove:");
				Scanner sc = new Scanner(System.in);
				int npieces = sc.nextInt();
				remove(player, npieces);
			}
		}
	}
	
	//Remove piece(s) specified by player
	private void remove(int player, int npieces) {
		for(int i=0; i<npieces; i++) {
			//Ask player for coordinates of piece to remove
			System.out.println("What are the coordinates of piece "+(i+1)+" to remove?");
			Scanner sc = new Scanner(System.in);
			String coordinates = sc.next();
			
			//Convert string input into usable values
			int ypos = interpret(coordinates.charAt(0));
			int xpos = coordinates.charAt(1) - '0';
			xpos--;
			
			//Remove piece (if coordinates are valid)
			int tier = find_tier(coordinates.charAt(0));
			//TODO; CHECK VALIDITY OF COORDINATES PROVIDED
		}
	}
	
	//Interpret letter and translate into array coordinate
	private static int interpret(char letter) {
		int pos1 = -1; //As failsafe
		switch(letter) {
		case 'a':
			pos1 = A;
			break;
		case 'b':
			pos1 = B;
			break;
		case 'c':
			pos1 = C;
			break;
		case 'd':
			pos1 = D;
			break;
		case 'e':
			pos1 = E;
			break;
		case 'f':
			pos1 = F;
			break;
		case 'g':
			pos1 = G;
			break;
		case 'h':
			pos1 = H;
			break;
		case 'i':
			pos1 = I;
			break;
		case 'j':
			pos1 = J;
			break;
		}
		return pos1;
	}
	
	//From letter coordinate, find appropriate tier
	int find_tier(char letter) {
		if(letter >= 'a' && letter <= 'd') return 1;
		else if(letter >='e' && letter <= 'g') return 2;
		else if(letter == 'h' || letter == 'i') return 3;
		else if(letter == 'j') return 4;
		return -1; //As fail-safe
	}
	
	//Check if a line or square has been formed with a recent insertion/raise
	boolean checkForRemove(int tier_no, int ypos, int xpos) {
		boolean horizontal_line = false;
		boolean vertical_line = false;
		boolean square = false;
		int count = 0;
		
		//Use appropriate array
		int[][] tier;
		if(tier_no == 1) tier = bottom_tier.clone();
		else if(tier_no == 2) tier = second_tier.clone();
		else if(tier_no == 3) tier = third_tier.clone();
		else tier = top_tier.clone();
		
		//Check for vertical line
		count = 0;
		int i;
		for(i=0; i<=4-tier_no; i++) {
			if(tier[i][xpos] == tier[ypos][xpos]) count++;
		}
		if(count == i && tier_no <= 2) vertical_line = true;
		
		//Check for horizontal line
		count = 0;
		for(i=0; i<=4-tier_no; i++) {
			if(tier[ypos][i] == tier[ypos][xpos]) count++;
		}
		if(count == i && tier_no <= 2) horizontal_line = true;
		
		//Check for square
		count = 0;
		count = neighbours(4-tier_no, tier, xpos, ypos);
		if(count >= 4) square = true;
		
		//Return result
		return (square||vertical_line||horizontal_line);
	}
	
	//Calculate the number of squares surrounding the current position that have spheres of the same color
	int neighbours(int array_bound, int[][] tier, int xpos, int ypos) {
		int res = 1;
		//Left and up
		if(xpos-1 >= 0 && ypos-1 >= 0) {
			if(tier[ypos-1][xpos-1] == tier[ypos][xpos]) res++;
		}
		//Left
		if(xpos-1 >= 0) {
			if(tier[ypos][xpos-1] == tier[ypos][xpos]) res++;
		}
		//Left and down
		if(xpos-1 >= 0 && ypos+1 <= array_bound) {
			if(tier[ypos+1][xpos-1] == tier[ypos][xpos]) res++;
		}
		//Up
		if(ypos-1 >= 0) {
			if(tier[ypos-1][xpos] == tier[ypos][xpos]) res++;
		}
		//Down
		if(ypos+1 <= array_bound) {
			if(tier[ypos+1][xpos] == tier[ypos][xpos]) res++;
		}
		//Right and up
		if(xpos+1 <= array_bound && ypos-1 >= 0) {
			if(tier[ypos-1][xpos+1] == tier[ypos][xpos]) res++;
		}
		//Right
		if(xpos+1 <= array_bound) {
			if(tier[ypos][xpos+1] == tier[ypos][xpos]) res++;
		}
		//Right and down
		if(xpos+1 <= array_bound && ypos+1 <= array_bound) {
			if(tier[ypos+1][xpos+1] == tier[ypos][xpos]) res++;
		}
		return res;
	}
	
	//Print completion message and winning player
	void game_complete() {
		//Record that game is complete
		this.complete = true;
		
		//Work out who won
		int winner = top_tier[0][0];
		String winner_string;
		if(winner == BLACK) winner_string = "Black";
		else winner_string = "White";
		
		//Print completion message
		System.out.println("Game is now complete. Winning player is " + winner_string);
	}
}

