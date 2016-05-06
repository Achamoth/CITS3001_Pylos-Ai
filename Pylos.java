import java.util.Scanner;


public class Pylos {
	private static final int EMPTY = -1;
	private static final int WHITE = 1;
	private static final int BLACK = 2;
	
	private static final int PLACE = 1;
	private static final int RAISE = 2;
	
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
			
			//Check that the sphere at the specified position belongs to the player
			int tier = find_tier(coordinates.charAt(0));
			int coordinate_value = bottom_tier[ypos][xpos];
			switch(tier) {
				case 1:
					coordinate_value = bottom_tier[ypos][xpos];
					break;
				case 2:
					coordinate_value = second_tier[ypos][xpos];
					break;
				case 3:
					coordinate_value = third_tier[ypos][xpos];
					break;
				case 4:
					coordinate_value = top_tier[ypos][xpos];
					break;
			}
			boolean right_player = (coordinate_value == player);
			if(!right_player) {
				System.out.println("This sphere doesn't belong to you. Pick another coordinate");
				i--;
				continue;
			}
			
			//Check that the sphere is not underneath any spheres
			boolean isUnderneath = isAnythingOnTop(tier, xpos, ypos);
			if(isUnderneath) {
				System.out.println("This sphere is underneath another sphere. Pick another coordinate");
				i--;
				continue;
			}
			
			//Coordinate provided is valid; remove sphere and return it to player
			if(tier == 1) bottom_tier[ypos][xpos] = EMPTY;
			if(tier == 2) second_tier[ypos][xpos] = EMPTY;
			if(tier == 3) third_tier[ypos][xpos] = EMPTY;
			if(player == WHITE) white_spheres++;
			else if(player == BLACK) black_spheres++;
		}
	}
	
	//Raise player's sphere from original position to new position
	public void raise(int player, String source, String dest) {
		//Interpret strings and decipher them into usable format
		int ySource = interpret(source.charAt(0));
		int xSource = source.charAt(1) - '0';
		xSource--;
		
		int yDest = interpret(dest.charAt(0));
		int xDest = dest.charAt(1) - '0';
		xDest--;
		
		//TODO:This is probably insecure (I'm assuming the move is legal)
		//Move sphere from source to dest
		int tier_source = find_tier(source.charAt(0));
		//First, remove sphere from source
		if(tier_source == 1) {
			bottom_tier[ySource][xSource] = EMPTY;
		}
		else if(tier_source == 2) {
			second_tier[ySource][xSource] = EMPTY;
		}
		//Now, add it to dest
		if(tier_source == 1) {
			second_tier[yDest][xDest] = player;
		}
		else if(tier_source == 2) {
			third_tier[yDest][xDest] = player;
		}
		//Finally, check if a square, horizontal line, or vertical line of same colored spheres has been created
		boolean mustRemove = checkForRemove(tier_source+1, yDest, xDest);
		if(mustRemove) {
			System.out.println("You must remove 1/2 piece(s)");
			System.out.print("How many pieces do you want to remove:");
			Scanner sc = new Scanner(System.in);
			int npieces = sc.nextInt();
			remove(player, npieces);
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
	private int find_tier(char letter) {
		if(letter >= 'a' && letter <= 'd') return 1;
		else if(letter >='e' && letter <= 'g') return 2;
		else if(letter == 'h' || letter == 'i') return 3;
		else if(letter == 'j') return 4;
		return -1; //As fail-safe
	}
	
	//Check if a line or square has been formed with a recent insertion/raise
	public boolean checkForRemove(int tier_no, int ypos, int xpos) {
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
	private int neighbours(int array_bound, int[][] tier, int xpos, int ypos) {
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
	
	//Takes sphere at given position, and checks if any spheres are on top of it
	public boolean isAnythingOnTop(int tier, int xpos, int ypos) {
		//Need to consider four positions (a sphere can be below at most four other spheres)
		boolean result = false;
		int[][] tier_copy = second_tier.clone();
		if(tier == 1) tier_copy = second_tier.clone();
		if(tier == 2) tier_copy = third_tier.clone();
		if(tier == 3) return false;
		
		//First position [ypos-1, xpos]
		if(ypos-1 >= 0 && ypos-1 <= 4-(tier+1) && xpos <= 4-(tier+1)) {
			if(tier_copy[ypos-1][xpos] != EMPTY) result = true;
		}
		//Second position [ypos, xpos]
		if(ypos <= 4-(tier+1) && xpos <= 4-(tier+1)) {
			if(tier_copy[ypos][xpos] != EMPTY) result = true;
		}
		//Third position [ypos, xpos-1]
		if(ypos <= 4-(tier+1) && xpos-1 >= 0 && xpos-1 <= 4-(tier+1)) {
			if(tier_copy[ypos][xpos-1] != EMPTY) result = true;
		}
		//Fourth position [ypos-1, xpos-1]
		if(ypos-1 >= 0 && ypos-1 <= 4-(tier+1) && xpos-1 >= 0 && xpos-1 <= 4-(tier+1)) {
			if(tier_copy[ypos-1][xpos-1] != EMPTY) result = true;
		}
		
		return result;
	}
	
	//Record game as complete
	private void game_complete() {
		//Record that game is complete
		this.complete = true;
	}
	
	//Returns specified player's sphere count
	public int sphereCount(int player) {
		if(player == WHITE) return this.white_spheres;
		else if(player == BLACK) return this.black_spheres;
		else return -1;
	}
	
	//Returns bottom tier board
	public int[][] getBottom() {
		return this.bottom_tier;
	}
	
	//Returns second tier board
	public int[][] getSecond() {
		return this.second_tier;
	}
	
	//Returns third tier board
	public int[][] getThird() {
		return this.third_tier;
	}
	
	//Returns top tier
	public int[][] getTop() {
		return this.top_tier;
	}
	
	//Duplicate this board object and return duplicate
	public Pylos copy() {
		Pylos dupe = new Pylos();
		dupe.set(this.black_spheres, this.white_spheres, this.complete, this.bottom_tier, this.second_tier, this.third_tier, this.top_tier);
		return dupe;
	}
	
	//Returns whether or not this game has terminated
	public boolean isComplete() {
		return complete;
	}
	
	//Returns the victor of the match, if it has terminated
	public int winner() {
		return this.top_tier[0][0];
	}
	
	//Set private field values (method is purely for the use of the clone() method)
	public void set(int bSpheres, int wSpheres, boolean isComplete, int[][] fTier, int[][] sTier, int[][] tTier, int[][] topTier) {
		this.black_spheres = bSpheres;
		this.white_spheres = wSpheres;
		this.complete = isComplete;
		this.bottom_tier = fTier.clone();
		this.second_tier = sTier.clone();
		this.third_tier = tTier.clone();
		this.top_tier = topTier.clone();
	}
	
	//Translate tier(int) into tier(char) i.e. 0 -> 'a' or 3 -> 'd' or 4 -> 'e' etc.
	private char translateToLetter(int tier, int ypos) {
		char letter = 'a';
		if(tier == 1 && ypos == A) letter = 'a';
		else if(tier == 1 && ypos == B) letter = 'b';
		else if(tier == 1 && ypos == C) letter = 'c';
		else if(tier == 1 && ypos == D) letter = 'd';
		else if(tier == 2 && ypos == E) letter = 'e';
		else if(tier == 2 && ypos == F) letter = 'f';
		else if(tier == 2 && ypos == G) letter = 'g';
		else if(tier == 3 && ypos == H) letter = 'h';
		else if(tier == 3 && ypos == I) letter = 'i';
		else if(tier == 4 && ypos == J) letter = 'j';
		return letter;
	}
	
	//Alternate place() method for use in applyMove()
	private void place(int tier, int xTo, int yTo) {
		char ypos = translateToLetter(tier, yTo);
		String pos = ypos + String.valueOf(xTo+1);
		place(WHITE, pos);
	}
	
	//Alternate raise() method for use in applyMove()
	private void raise(int tier_source, int xSource, int ySource, int tier_dest, int xDest, int yDest) {
		//Compute string for source
		char ypos_source = translateToLetter(tier_source, ySource);
		String source = ypos_source + String.valueOf(xSource+1);
		
		//Computer string for dest
		char ypos_dest = translateToLetter(tier_dest, yDest);
		String dest = ypos_dest + String.valueOf(xDest+1);
		
		//Call appropriate raise function
		raise(WHITE, source, dest);
	}
	
	//Apply PylosMove object to state
	public void applyMove(PylosMove action) {
		int moveType = action.getType(); //Determine moveType
		int tier = action.getTier();
		if(moveType == PLACE) {
			//Calculate position to place sphere
			int toPos[] = action.getToPos();
			int yTo = toPos[0];
			int xTo = toPos[1];
			
			//Add sphere to specified position
			place(tier, xTo, yTo);
			
			if(action.isRemove()) {
				int spheresToRemove = action.getNumberOfSpheresToRemove();
				for(int i=0; i<spheresToRemove; i++) {
					//TODO: Remove spheres (will need to edit the code in place() and raise() for removing)
				}
			}
		}
		else if(moveType == RAISE) {
			//Calculate positions
			int fromPos[] = action.getFromPos();
			int yFrom = fromPos[0];
			int xFrom = fromPos[1];
			
			int toPos[] = action.getToPos();
			int yTo = toPos[0];
			int xTo = toPos[1];
			
			//Raise sphere from source to dest
			raise(tier, xFrom, yFrom, tier+1, xTo, yTo);
			
			if(action.isRemove()) {
				int spheresToRemove = action.getNumberOfSpheresToRemove();
				for(int i=0; i<spheresToRemove; i++) {
					//TODO: Remove spheres (will need to edit the code in place() and raise() for removing)
				}
			}
		}
	}
}
