/*
 CITS3001 Project 2016
 Name:			Ammar Abu Shamleh
 Student number: 21521274
 Date:           May 2016
 */

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

//This class is responsible for facilitating the game between a human and AI
public class Driver {

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

        //Print instructions for player
        try {
            File f = new File("instructions.txt");
            Scanner scanner = new Scanner(f);
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                System.out.println(line);
            }
            System.out.print("\n");
            scanner.close();
        } catch(Exception e) {
            System.out.println("Error printing instructions. \"instructions.txt\" may not be present in current directory");
        }
        
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
			if(cur_player == cpu) {
				/*Calculate move and apply it to game board*/
				
				//Calculate minimax move (and calculate how long it takes)
				/*long startTime = System.nanoTime();
				PylosMove MiniMaxMove = PylosAI.minimax(game, cpu);
				long finishTime = System.nanoTime();
				long duration = finishTime - startTime;
				System.out.println("Minimax took " + duration/1000000000 + " seconds, with a depth of " + PylosAI.getMinimaxDepth());*/
				
				//Calculate alpha beta move (and calculate how long it takes)
                PylosAI.changeABDepth(5); //Start off trying to calculate with depth of 5
				long startTime = System.nanoTime();
				PylosMove ABMove = PylosAI.alphaBetaSearch(game, cpu);
                while(ABMove == null) {
                    PylosAI.changeABDepth(PylosAI.getABDepth() - 1);
                    ABMove = PylosAI.alphaBetaSearch(game, cpu);
                }
				long finishTime = System.nanoTime();
				long duration = finishTime - startTime;
				System.out.println("Alpha beta took " + duration/1000000000 + " seconds, with depth limit of " + PylosAI.getABDepth());
				
				//Apply chosen move (alpha beta)
				game.applyMove(ABMove, cpu);
				
				//Print move for human player to see
                System.out.print("Alpha Beta Chose: ");
				printMove(ABMove);
				//System.out.print("Minimax Chose: "); printMove(MiniMaxMove);
			}

			//Human moves
			else if(cur_player == man) {
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
