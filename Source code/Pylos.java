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

	//Given a position on the board, determines if that position belongs to the specified player
	public boolean belongsToPlayer(int player, int tier, int ypos, int xpos) {
		if(tier == 1) {
			if(bottom_tier[ypos][xpos] == player) return true;
		}
		else if(tier == 2) {
			if(second_tier[ypos][xpos] == player) return true;
		}
		else if(tier == 3) {
			if(third_tier[ypos][xpos] == player) return true;
		}
		return false;
	}

	//Place player's sphere at position 'pos'
	public void place(int player, String pos) {
		//Interpret 'pos' string
		char letter = pos.charAt(0);
		int pos1 = interpret(letter); //y coordinate
		int pos2 = pos.charAt(1) - '0'; //x coordinate
		pos2--;
		int tier = find_tier(letter);

		//First, check that sphere can be placed at specified coordinate
		if(!canPlace(tier, pos2, pos1)) {
			//Position not valid; ask user for another position
			boolean valid = false;
			while(!valid) {
				System.out.println("Sphere cannot be placed there. Please enter another destination coordinate: ");
				Scanner sc = new Scanner(System.in);
				String coordinates = sc.next();

				try {
					//Convert entered coordinates into usable form
					pos1 = interpret(coordinates.charAt(0));
					pos2 = coordinates.charAt(1) - '0';
					pos2--;

					//Calulate tier
					tier = find_tier(coordinates.charAt(0));

					//Check if newly provided coordinates are valid
					valid = canPlace(tier, pos2, pos1);
				} catch(Exception e) {
					System.out.println("Invalid input");
					continue;
				}
			}
		}

		//Make move (i.e. fill square with appropriate colour sphere)
		if(tier == 1) bottom_tier[pos1][pos2] = player;
		else if(tier == 2) second_tier[pos1][pos2] = player;
		else if(tier == 3) third_tier[pos1][pos2] = player;
		else if(tier == 4) top_tier[pos1][pos2] = player;

		//Deduct from player's sphere count
		if(player == WHITE) white_spheres--;
		else if(player == BLACK) black_spheres--;

		//Check if game has been completed
		if(top_tier[0][0] != EMPTY) this.game_complete();

		//Check if the player has completed a square or line; if so, prompt them to remove 1 or 2 squares
		if(!this.complete) {
			boolean mustRemove = checkForRemove(tier, pos1, pos2);
			if(mustRemove) {
				//First, inform player they must remove spheres
				System.out.println("You must remove 1/2 piece(s)");
				
				//Now ask them how many spheres they want to remove
				boolean valid = false;
				int npieces = 0;
				while(!valid) {
					System.out.print("How many pieces do you want to remove:");
					Scanner sc = new Scanner(System.in);
					try {
						npieces = sc.nextInt();
						if(npieces == 1 || npieces == 2) valid = true;
						else System.out.println("Invalid number of pieces");
					} catch(Exception e) {
						System.out.println("Invalid input. Please enter 1 or 2");
						continue;
					}
				}
				
				//Now, prompt player for pieces to remove
				remove(player, npieces);
			}
			else {
				//Check if the acting player has just run out of spheres (thus ending the game)
				int nspheres;
				if(player == WHITE) nspheres = this.white_spheres;
				else nspheres = this.black_spheres;
				if(nspheres == 0) this.game_complete();
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

			int ypos;
			int xpos;
			
			try {
			//Convert string input into usable values
			ypos = interpret(coordinates.charAt(0));
			xpos = coordinates.charAt(1) - '0';
			xpos--;
			if(coordinates.length() > 2) {
				System.out.println("Error. Invalid coordinates");
				continue;
			}
			} catch(Exception e) {
				System.out.println("Invalid input");
				continue;
			}

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

	//Given positions of 2 spheres, checks if the first is underneath the second
	public boolean isUnderneath(int tier1, int ypos1, int xpos1, int tier2, int ypos2, int xpos2) {

		if(ypos1 == ypos2 && xpos1 == xpos2) {
			return true;
		}
		else if(ypos1 == ypos2 && xpos1 == xpos2+1) {
			return true;
		}
		else if(ypos1 == ypos2+1 && xpos1 == xpos2) {
			return true;
		}
		else if(ypos1 == ypos2+1 && xpos1 == xpos2+1) {
			return true;
		}

		return false;
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

		//Calculate tier of source coordinate
		int tier_source = find_tier(source.charAt(0));

		//Check that source coordinate is valid
		if(!canRaise(player, tier_source, xSource, ySource)) {
			//If it isn't, ask user for another source coordinate
			boolean valid = false;
			while(!valid) {
				System.out.println("Sphere cannot be raised from there. Please enter another source coordinate: ");
				Scanner sc = new Scanner(System.in);
				String coordinates = sc.next();

				try {
					//Convert enterd coordinates into usable form
					ySource = interpret(coordinates.charAt(0));
					xSource = coordinates.charAt(1) - '0';
					xSource--;
					tier_source = find_tier(source.charAt(0));

					//Check if newly provided coordinates are valid
					valid = canRaise(player, tier_source, xSource, ySource);
				} catch(Exception e) {
					System.out.println("Invalid input");
					continue;
				}
			}
		}

		//Check that destination coordinate is valid
		if(!canPlace(tier_source+1, xDest, yDest)) {
			//If it isn't, ask user for another destination coordinate
			boolean valid = false;
			while(!valid) {
				System.out.println("Sphere cannot be placed there. Please enter another destination coordinate: ");
				Scanner sc = new Scanner(System.in);
				String coordinates = sc.next();

				try {
					//Convert entered coordinates into usable form
					yDest = interpret(coordinates.charAt(0));
					xDest = coordinates.charAt(1) - '0';
					xDest--;

					//Calulate tier
					int tier_dest = find_tier(coordinates.charAt(0));

					//Check if newly provided coordinates are valid
					valid = canPlace(tier_dest, xDest, yDest);
				} catch(Exception e) {
					System.out.println("Invalid input");
					continue;
				}
			}
		}

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

	//Checks if a sphere can be placed in a specified position
	public boolean canPlace(int tier, int xpos, int ypos) {
		//First, check that no other sphere exists there
		boolean result = true;
		int tier_clone[][] = null;

		if(tier == 1) tier_clone = bottom_tier.clone();
		else if(tier == 2) tier_clone = second_tier.clone();
		else if(tier == 3) tier_clone = third_tier.clone();
		else if(tier == 4) tier_clone = top_tier.clone();

		//First, make sure a sphere doesn't already exist in that position
		if(tier_clone[ypos][xpos] != EMPTY) {
			return false;
		}

		//If the tier is 1, then the position is valid
		if(tier == 1) {
			return true;
		}
		//Next, if the tier is 2, check that there are already four spheres underneath it on tier 1
		if(tier == 2) {
			int sphere1 = bottom_tier[ypos][xpos];
			int sphere2 = bottom_tier[ypos][xpos+1];
			int sphere3 = bottom_tier[ypos+1][xpos];
			int sphere4 = bottom_tier[ypos+1][xpos+1];
			if(sphere1 != EMPTY && sphere2 != EMPTY && sphere3 != EMPTY && sphere4 != EMPTY) {
				return true;
			}
		}
		//Next, if the tier is 3, check that there are already four spheres underneath it on tier 2
		else if(tier == 3) {
			int sphere1 = second_tier[ypos][xpos];
			int sphere2 = second_tier[ypos][xpos+1];
			int sphere3 = second_tier[ypos+1][xpos];
			int sphere4 = second_tier[ypos+1][xpos+1];
			if(sphere1 != EMPTY && sphere2 != EMPTY && sphere3 != EMPTY && sphere4 != EMPTY) {
				return true;
			}
		}
		//Finally, if the tier is 4, check that there are already four spheres underneath it on tier 3
		else if(tier == 4) {
			int sphere1 = third_tier[ypos][xpos];
			int sphere2 = third_tier[ypos][xpos+1];
			int sphere3 = third_tier[ypos+1][xpos];
			int sphere4 = third_tier[ypos+1][xpos+1];
			if(sphere1 != EMPTY && sphere2 != EMPTY && sphere3 != EMPTY && sphere4 != EMPTY) {
				return true;
			}
		}
		return false;
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

		//First ensure that position is not empty
		if(tier[ypos][xpos] == EMPTY) {
			//If it is empty, simply return false
			return false;
		}

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
		if(isInSquare(4-tier_no, tier, xpos, ypos)){
			square = true;
		}

		//Return result
		return (square||vertical_line||horizontal_line);
	}

	//Calculate the number of squares surrounding the current position that have spheres of the same color
	private boolean isInSquare(int array_bound, int[][] tier, int xpos, int ypos) {
		//Set flags
		boolean leftAndUp = false;
		boolean left = false;
		boolean leftAndDown = false;
		boolean up = false;
		boolean down = false;
		boolean rightAndUp = false;
		boolean right = false;
		boolean rightAndDown = false;


		//Left and up
		if(xpos-1 >= 0 && ypos-1 >= 0) {
			if(tier[ypos-1][xpos-1] == tier[ypos][xpos]) leftAndUp = true;
		}
		//Left
		if(xpos-1 >= 0) {
			if(tier[ypos][xpos-1] == tier[ypos][xpos]) left = true;
		}
		//Left and down
		if(xpos-1 >= 0 && ypos+1 <= array_bound) {
			if(tier[ypos+1][xpos-1] == tier[ypos][xpos]) leftAndDown = true;
		}
		//Up
		if(ypos-1 >= 0) {
			if(tier[ypos-1][xpos] == tier[ypos][xpos]) up = true;
		}
		//Down
		if(ypos+1 <= array_bound) {
			if(tier[ypos+1][xpos] == tier[ypos][xpos]) down = true;
		}
		//Right and up
		if(xpos+1 <= array_bound && ypos-1 >= 0) {
			if(tier[ypos-1][xpos+1] == tier[ypos][xpos]) rightAndUp = true;
		}
		//Right
		if(xpos+1 <= array_bound) {
			if(tier[ypos][xpos+1] == tier[ypos][xpos]) right = true;
		}
		//Right and down
		if(xpos+1 <= array_bound && ypos+1 <= array_bound) {
			if(tier[ypos+1][xpos+1] == tier[ypos][xpos]) rightAndDown = true;
		}

		/*Each sphere can be a corner of 4 squares*/
		//Square 1: Up, Left, Left and Up
		if(up && left && leftAndUp) return true;
		//Square 2: Up, Right, Right and Up
		else if(up && right && rightAndUp) return true;
		//Square 3: Down, Right, Right and Down
		else if(down && right && rightAndDown) return true;
		//Square 4: Down, Left, Left and Down
		else if(down && left && leftAndDown) return true;
		//Not a part of any square (if all 4 cases above fail)
		else return false;
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

	//Determine if sphere at given position can be raised
	public boolean canRaise(int player, int tier, int ypos, int xpos) {
		//First, check that the position belongs to the player
		if(!this.belongsToPlayer(player, tier, ypos, xpos)) {
			return false;
		}

		//Next, check that nothing is on top of the sphere
		if(this.isAnythingOnTop(tier, xpos, ypos)) {
			return false;
		}

		//Sphere belongs to player and nothing is on top. Return true
		return true;
	}

	//Checks a position and determines whether or not the specified player can remove the sphere at that position
	public boolean canRemove(int player, int tier, int ypos, int xpos) {
		int tier_copy[][] = null;

		//First, check if the sphere at that position belongs to the player
		if(tier == 1) tier_copy = bottom_tier.clone();
		else if(tier == 2) tier_copy = second_tier.clone();
		else if(tier == 3) tier_copy = third_tier.clone();

		if(tier_copy[ypos][xpos] != player) return false;

		//If it does belong to the player, then check if the sphere has anything underneath it
		if(isAnythingOnTop(tier, xpos, ypos)) {
			return false;
		}

		//If nothing is on top, then the sphere can be removed
		return true;
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
		if(this.top_tier[0][0] != EMPTY) return this.top_tier[0][0];
		else {
			if(this.white_spheres == 0) return BLACK;
			else return WHITE;
		}
	}

	//Set private field values (method is purely for the use of the clone() method)
	public void set(int bSpheres, int wSpheres, boolean isComplete, int[][] fTier, int[][] sTier, int[][] tTier, int[][] topTier) {
		this.black_spheres = bSpheres;
		this.white_spheres = wSpheres;
		this.complete = isComplete;
		this.bottom_tier = array_copy(fTier, 4);
		this.second_tier = array_copy(sTier, 3);
		this.third_tier = array_copy(tTier, 2);
		this.top_tier = array_copy(topTier, 1);
	}

	//Creates new 2D array and copies existing 2d array contents into it (used to copy tiers)
	private int[][] array_copy(int[][] tier, int size) {
		int[][] result = new int[size][size];
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				result[i][j] = tier[i][j];
			}
		}
		return result;
	}

	//Translate tier(int) into tier(char) i.e. 0 -> 'a' or 3 -> 'd' or 4 -> 'e' etc.
	public static char translateToLetter(int tier, int ypos) {
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
	private void place(int player, int tier, int xTo, int yTo) {
		char ypos = translateToLetter(tier, yTo);
		String pos = ypos + String.valueOf(xTo+1);
		place_alternate(player, pos);
	}

	//Alternate raise() method for use in applyMove()
	private void raise(int player, int tier_source, int xSource, int ySource, int tier_dest, int xDest, int yDest) {
		//Compute string for source
		char ypos_source = translateToLetter(tier_source, ySource);
		String source = ypos_source + String.valueOf(xSource+1);

		//Computer string for dest
		char ypos_dest = translateToLetter(tier_dest, yDest);
		String dest = ypos_dest + String.valueOf(xDest+1);

		//Call appropriate raise function
		raise_alternate(player, source, dest);
	}

	//Apply PylosMove object to state
	public void applyMove(PylosMove action, int player) {
		int moveType = action.getType(); //Determine moveType
		int tier = action.getTier();
		if(moveType == PLACE) {
			//Calculate position to place sphere
			int toPos[] = action.getToPos();
			int yTo = toPos[0];
			int xTo = toPos[1];

			//Add sphere to specified position
			place(player, tier, xTo, yTo);

			if(action.isRemove()) {
				int spheresToRemove = action.getNumberOfSpheresToRemove();
				for(int i=0; i<spheresToRemove; i++) {
					//Remove specified spheres
					int rem_coordinates[] = null;
					if(i==0) rem_coordinates = action.getRemovePos1().clone();
					else if(i==1) rem_coordinates = action.getRemovePos2().clone();
					this.remove_alternate(player, rem_coordinates[0], rem_coordinates[1], rem_coordinates[2]);
				}
			}
			//Check if currently acting player has run out of spheres
			int nspheres;
			if(player == WHITE) nspheres = this.white_spheres;
			else nspheres = this.black_spheres;
			if(nspheres == 0) {
				//Currently acting player has lost the game
				this.game_complete();
			}
		}

		else if(moveType == RAISE) {
			//Calculate positions
			int fromPos[] = action.getFromPos();
			int tier_source = fromPos[0];
			int yFrom = fromPos[1];
			int xFrom = fromPos[2];

			int toPos[] = action.getToPos();
			int yTo = toPos[0];
			int xTo = toPos[1];

			//Raise sphere from source to dest
			raise(player, tier_source, xFrom, yFrom, tier, xTo, yTo);

			if(action.isRemove()) {
				int spheresToRemove = action.getNumberOfSpheresToRemove();
				for(int i=0; i<spheresToRemove; i++) {
					//Remove specified spheres
					int rem_coordinates[] = null;
					if(i==0) rem_coordinates = action.getRemovePos1();
					else if(i==1) rem_coordinates = action.getRemovePos2();
					this.remove_alternate(player, rem_coordinates[0], rem_coordinates[1], rem_coordinates[2]);
				}
			}
		}
	}

	//Alternate place method for use with apply_move()
	private void place_alternate(int player, String pos) {
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
	}

	//Alternate raise method for use with apply_move()
	private void raise_alternate(int player, String source, String dest) {
		//Interpret strings and decipher them into usable format
		int ySource = interpret(source.charAt(0));
		int xSource = source.charAt(1) - '0';
		xSource--;

		int tier_dest = find_tier(dest.charAt(0));
		int yDest = interpret(dest.charAt(0));
		int xDest = dest.charAt(1) - '0';
		xDest--;

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
			if(tier_dest == 2) second_tier[yDest][xDest] = player;
			else if(tier_dest == 3) third_tier[yDest][xDest] = player;
		}
		else if(tier_source == 2) {
			third_tier[yDest][xDest] = player;
		}
	}

	//Alternate remove method for use with apply_move()
	private void remove_alternate(int player, int tier, int ypos, int xpos) {
		//Remove sphere and return it to player
		if(tier == 1) bottom_tier[ypos][xpos] = EMPTY;
		if(tier == 2) second_tier[ypos][xpos] = EMPTY;
		if(tier == 3) third_tier[ypos][xpos] = EMPTY;
		if(player == WHITE) white_spheres++;
		else if(player == BLACK) black_spheres++;
	}
}