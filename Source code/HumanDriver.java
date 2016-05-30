/*
 CITS3001 Project 2016
 Name:			Ammar Abu Shamleh
 Student number: 21521274
 Date:           May 2016
 */

import java.util.ArrayList;
import java.util.Scanner;

/*
 * The purpose of this class is to facilitate matches between two humans
 */

public class HumanDriver {

	//Game players
	private final static int WHITE = 1;
	private final static int BLACK = 2;

	//Move types
	private final static int PLACE = 1;
	private final static int RAISE = 2;

	//Game board
	private static Pylos game;

	public static void main(String[] args) {
		//Create game board
		game = new Pylos();

		//Ask player what side they want to play as
		String human = "";
		int man = 0;
		int cpu = 0;
		boolean valid = false;
		while(!valid) {
			System.out.print("Would you like to play as WHITE or BLACK: ");
			Scanner in = new Scanner(System.in);
			human = in.next().toLowerCase();
			if(human.equals("white")) {cpu = BLACK; valid = true; man = WHITE;} 
			else if(human.equals("black")) {cpu = WHITE; valid = true; man = BLACK;}
			else {
				System.out.println("Error. Invalid input. Please enter WHITE or BLACK");
			}
		}

		//Report to player what side they are playing as
		System.out.println("Okay, you are playing as " + human + "\n\n");

		//Set up data needed to run game
		boolean complete = false; //Records when game has terminated
		int cur_player = WHITE; //Records the player who is moving next (white starts first)

		//Assign AI evaluation function
		PylosAI.setEvaluateFunction("height");

		/*Start game*/
		while(!complete) {

			//Display game board at beginning of each loop
			game.display();

			//Report whose move it is
			if(cur_player == WHITE) System.out.println("White's move");
			else System.out.println("Black's move");

			/*Check who is moving next*/
			//AI moves
			//Human moves
			//Ask human to provide move
			System.out.print("What is your move: ");
			Scanner sc = new Scanner(System.in);
			String nextMove = sc.nextLine().trim().toLowerCase();
			String tokens[] = nextMove.split(" ");
			String moveType = tokens[0];
			if(moveType.equals("place")) {
				//Player wants to place sphere at specified position
				String pos = tokens[1];
				//TODO: Error checking
				//Make move on game board
				game.place(cur_player, pos);
			}
			else if(moveType.equals("raise")) {
				//Player wants to raise a sphere from one tier to another
				String source = tokens[1];
				String dest = tokens[2];
				//TODO: Error checking
				//Make move on game board
				game.raise(cur_player, source, dest);
			}
			else {
				//TODO: Error
			}

			//Update cur_player
			if(cur_player == WHITE) cur_player = BLACK;
			else if(cur_player == BLACK) cur_player = WHITE;

			//Check if game is complete
			complete = game.isComplete();
		}

		//Game has terminated; print final board state
		game.display();

		//Print winner
		int winner = game.winner();
		if(winner == WHITE) System.out.println("White won!");
		else if(winner == BLACK) System.out.println("Black won!");
	}

	//Print a move in a human readable format
	public static void printMove(PylosMove action) {
		int moveType = action.getType();
		if(moveType == PLACE) {
			//Print move type
			System.out.print("place ");
			//Get coordinate
			int[] pos = action.getToPos();
			char ypos = Pylos.translateToLetter(action.getTier(), pos[0]);
			//Print coordinate
			System.out.print(ypos + "" + String.valueOf(pos[1]+1) + "\n"); 
		}
		else if(moveType == RAISE) {
			//Print move type
			System.out.print("raise ");
			//Acquire necessary information
			int[] source = action.getFromPos();
			int[] dest = action.getToPos();
			int tier_dest = action.getTier();
			//Print source coordinate
			int tier_source = source[0];
			int ysource = source[1];
			int xsource = source[2];
			char ySourceChar = Pylos.translateToLetter(tier_source, ysource);
			System.out.print(ySourceChar + "" + String.valueOf(xsource+1) + " to ");
			//Print destination coordinate
			int ydest = dest[0];
			int xdest = dest[1];
			char yDestChar = Pylos.translateToLetter(tier_dest, ydest);
			System.out.print(yDestChar + "" + String.valueOf(xdest+1) + "\n");
		}

		//Check if move has a remove follow up
		if(action.isRemove()) {
			//Print coordinates of removed sphere(s)
			int nremoved = action.getNumberOfSpheresToRemove();
			System.out.print("remove ");
			//Calculate coordinates of removed sphere 1
			int rem1[] = action.getRemovePos1();
			char rem1y = Pylos.translateToLetter(rem1[0], rem1[1]);
			System.out.print(rem1y + "" + String.valueOf(rem1[2]+1));
			//Calculate and print coordinates of removed sphere 2 (if applicable)
			if(nremoved == 2) {
				System.out.print(" and ");
				int rem2[] = action.getRemovePos2();
				char rem2y = Pylos.translateToLetter(rem2[0], rem2[1]);
				System.out.print(rem2y + "" + String.valueOf(rem2[2]+1));
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}

}
