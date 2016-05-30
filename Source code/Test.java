/*
 CITS3001 Project 2016
 Name:			Ammar Abu Shamleh
 Student number: 21521274
 Date:           May 2016
 */

import java.util.ArrayList;

//This is simply a testing class, for bug fixing and error checking
public class Test {
	public static void main(String[] args) {
		Pylos game = new Pylos();
		
		game.place(1, "a1");
		game.place(1, "a2");
		game.place(1, "a3");
		ArrayList<PylosMove> moves = PylosAI.actions(game, 1);
		System.out.println(moves.size());
		for(PylosMove curMove : moves) {
			Driver.printMove(curMove);
		}
	}
}
