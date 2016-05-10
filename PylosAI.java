import java.util.ArrayList;

/*
 * Regarding PylosMove and remove-type moves:
 * As I've currently implemented this, there is significant complexity in the actions() method.
 * Basically, a PylosMove defines a PLACE or RAISE move, as well as any necessary follow up.
 * This requires expensive and complex computation
 * A better alternative is to specify a move as being PLACE, RAISE or REMOVE
 * Then, actions() will only generate a list of PLACE and RAISE moves that can be made with a give Pylos object
 * And Minimax will call result(), which will use applyMove() to apply an action to a state
 * If that move then requires a removal follow up, applyMove() will return true
 * The calling function (result()) will then generate a list of remove type actions that can be performed on the resulting state
 * Not sure about a few implementation details here, but it's just something for consideration
 */

public class PylosAI {
	//Move types
	private final static int PLACE = 1;
	private final static int RAISE = 2;
	
	//Position states
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
		
	//Utility function; looks at state (and maybe depth, in the future), and determines utility, if it's a terminal state (utility determined from perspective of white player)
	private static int utility(Pylos state) {
		if(terminal(state)) {
			//Will return utility from perspective of white player
			if(state.winner() == WHITE) return 1000;
			else return -1000;
		}
		else return -1;
	}
	
	//Terminal function; looks at state and determines whether or not it's a terminal state
	private static boolean terminal(Pylos state) {
		return state.isComplete();
	}
	
	//Evaluation function; looks at state and computes evaluation of state from perspective of white player
	private static int evaluate(Pylos state) {
		//To start with, we'll use a simple evaluation function that just compares number of spheres
		int whiteSpheres = state.sphereCount(WHITE);
		int blackSpheres = state.sphereCount(BLACK);
		int result = (whiteSpheres - blackSpheres) * 10;
		return result;
	}
	
	//Result function; computes resulting state when applying a given action to a given state
	private static Pylos result(Pylos state, PylosMove action, int player) {
		Pylos newState = state.copy();
		newState.applyMove(action, player);
		return newState;
	}
	
	//Actions function; returns array list of all possible actions in a given state
	public static ArrayList<PylosMove> Actions(Pylos state, int player) {
		ArrayList<PylosMove> result = new ArrayList<PylosMove>();
		PylosMove curMove = null;
		
		//First, determine all possible 'place' moves
		for(int tier=1; tier<5; tier++) {
			//Loop through tiers
			
			//BOTTOM TIER
			if(tier == 1) {
				for(int ypos=A; ypos<=D; ypos++) {
					//Loop through rows
					for(int xpos=0; xpos<4; xpos++) {
						//Loop through columns
						if(state.canPlace(tier, xpos, ypos)) {
							//Valid placement; record position
							int pos[] = new int[2];
							pos[0] = ypos; pos[1] = xpos;
							
							//Now consider if the placement requires a removal follow-up
							if(state.checkForRemove(tier, ypos, xpos)) {
								//If it does, consider all possible follow-ups
								int rem1[] = new int[3];
								int rem2[] = new int[3];
								
								/*First, consider all possible single spheres that can be removed*/
								//To start with, consider the simple case of removing the sphere that's just been placed
								rem1[0] = tier; rem1[1] = pos[0]; rem1[2] = pos[1];
								curMove = new PylosMove(PLACE, tier, pos, null, true, 1, rem1, null);
								result.add(curMove);
								//Now, consider all other possible spheres
								for(int tier_removal=1; tier_removal < 4; tier_removal++) {
									if(tier_removal == 1) {
										for(int yrem=A; yrem<=D; yrem++) {
											for(int xrem=0; xrem<4; xrem++) {
												if(state.canRemove(player, tier_removal, yrem, xrem)) {
													//Sphere at this position can be removed; mark and store the move
													rem1[0] = tier_removal; rem1[1] = yrem; rem1[2] = xrem;
													curMove = new PylosMove(PLACE, tier, pos, null, true, 1, rem1, null);
													result.add(curMove);
												}
											}
										}
									}
									else if(tier_removal == 2) {
										for(int yrem=E; yrem<=G; yrem++) {
											for(int xrem=0; xrem<3; xrem++) {
												if(state.canRemove(player, tier_removal, yrem, xrem)) {
													//Sphere at this position can be removed; mark and store the move
													rem1[0] = tier_removal; rem1[1] = yrem; rem1[2] = xrem;
													curMove = new PylosMove(PLACE, tier, pos, null, true, 1, rem1, null);
													result.add(curMove);
												}
											}
										}
									}
									else if(tier_removal == 3) {
										for(int yrem=H; yrem<=I; yrem++) {
											for(int xrem=0; xrem<2; xrem++) {
												if(state.canRemove(player, tier_removal, yrem, xrem)) {
													//Sphere at this position can be removed; mark and store the move
													rem1[0] = tier_removal; rem1[1] = yrem; rem1[2] = xrem;
													curMove = new PylosMove(PLACE, tier, pos, null, true, 1, rem1, null);
													result.add(curMove);
												}
											}
										}
									}
								}
								
								//TODO:Consider all possible pairs of spheres that can be removed
							}
							else {
								//Otherwise, no spheres need to be removed
								curMove = new PylosMove(PLACE, tier, pos, null, false, 0, null, null);
								result.add(curMove);
							}
						}
					}
				}
			}
			
			//SECOND TIER
			else if(tier == 2) {
				for(int ypos=E; ypos<=G; ypos++) {
					//Loop through rows
					for(int xpos=0; xpos<3; xpos++) {
						//Loop through columns
						if(state.canPlace(tier, xpos, ypos)) {
							//Valid placement; record position
							int pos[] = new int[2];
							pos[0] = ypos; pos[1] = xpos;
							
							//Now check if placing at this position requires a removal follow up
							if(state.checkForRemove(tier, ypos, xpos)) {
								//If it does, consider all possible follow ups
								int rem1[] = new int[3];
								int rem2[] = new int[3];
								
								/*First consider all possible single sphere removals*/
								//Start with the simple case of removing the sphere we just added
								rem1[0] = tier; rem1[1] = pos[0]; rem1[2] = pos[1];
								curMove = new PylosMove(PLACE, tier, pos, null, true, 1, rem1, null);
								result.add(curMove);
								//Now, consider all other possible spheres
								for(int tier_removal=1; tier_removal<4; tier_removal++) {
									if(tier_removal == 1) {
										for(int yrem=A; yrem<=D; yrem++) {
											for(int xrem=0; xrem<4; xrem++) {
												if(state.canRemove(player, tier_removal, yrem, xrem)) {
													//Sphere at this position can be removed; store the move
													rem1[0] = tier_removal; rem1[1] = yrem; rem1[2] = xrem;
													curMove = new PylosMove(PLACE, tier, pos, null, true, 1, rem1, null);
													result.add(curMove);
												}
											}
										}
									}
									else if(tier_removal == 2) {
										for(int yrem=E; yrem<=G; yrem++) {
											for(int xrem=0; xrem<3; xrem++) {
												if(state.canRemove(player, tier_removal, yrem, xrem)) {
													//Sphere at this position can be removed; store the move
													rem1[0] = tier_removal; rem1[1] = yrem; rem1[2] = xrem;
													curMove = new PylosMove(PLACE, tier, pos, null, true, 1, rem1, null);
													result.add(curMove);
												}
											}
										}
									}
									else if(tier_removal == 3) {
										for(int yrem=H; yrem<=I; yrem++) {
											for(int xrem=0; xrem<2; xrem++) {
												if(state.canRemove(player, tier_removal, yrem, xrem)) {
													//Sphere at this position can be removed; store the move
													rem1[0] = tier_removal; rem1[1] = yrem; rem1[2] = xrem;
													curMove = new PylosMove(PLACE, tier, pos, null, true, 1, rem1, null);
													result.add(curMove);
												}
											}
										}
									}
								}
								
								//TODO: Consider all possible pairs of spheres that can be removed
							}
							else {
								//Otherwise, a follow up is not needed
								curMove = new PylosMove(PLACE, tier, pos, null, false, 0, null, null);
								result.add(curMove);
							}
						}
					}
				}
			}
			
			//THIRD TIER
			else if(tier == 3) {
				for(int ypos=H; ypos<=I; ypos++) {
					for(int xpos=0; xpos<2; xpos++) {
						if(state.canPlace(tier, xpos, ypos)) {
							//Valid placement; record position
							int pos[] = new int[2];
							pos[0] = ypos; pos[1] = xpos;
							
							//Now, check if placing at this position requires a removal follow up
							if(state.checkForRemove(tier, ypos, xpos)) {
								//If it does, consider all possible follow ups
								int rem1[] = new int[3];
								int rem2[] = new int[3];
								
								/*Consider all possible single sphere removals*/
								//First, consider the simple case where we remove the sphere that's just been placed
								rem1[0] = tier; rem1[1] = ypos; rem1[2] = xpos;
								curMove = new PylosMove(PLACE, tier, pos, null, true, 1, rem1, null);
								result.add(curMove);
								//Now, consider all other spheres (only need to consider tiers 2 and 3, in this case)
								int tier_removal = 3;
								//We only need to consider tier 3 for single sphere removal
								for(int yrem=H; yrem<=I; yrem++) {
									for(int xrem=0; xrem<2; xrem++) {
										if(state.canRemove(player, tier_removal, yrem, xrem)) {
											//Sphere at this position can be removed; store the move
											rem1[0] = tier_removal; rem1[1] = yrem; rem1[2] = xrem;
											curMove = new PylosMove(PLACE, tier, pos, null, true, 1, rem1, null);
											result.add(curMove);
										}
									}
								}
								
								//TODO: Consider all possible pairs of spheres that can be removed
							}
							else {
								//Otherwise, a follow up is not needed
								curMove = new PylosMove(PLACE, tier, pos, null, false, 0, null, null);
								result.add(curMove);
							}
						}
					}
				}
			}
			
			//FOURTH TIER
			else if(tier == 4) {
				if(state.canPlace(tier, 0, 0)) {
					//Can't remove after placing at 4th tier
					int pos[] = {0, 0};
					curMove = new PylosMove(PLACE, tier, pos, null, false, 0, null, null);
					result.add(curMove);
				}
			}
		}
		
		//Now, determine all possible 'raise' moves
		for(int tier=1; tier<3; tier++) {
			if(tier == 1) {
				for(int ypos=A; ypos<=D; ypos++) {
					for(int xpos=0; xpos<4; xpos++) {
						if(state.canRaise(player, tier, ypos, xpos)) {
							//Sphere at this position can be raised. Record position
							int sourcePos[] = new int[3];
							sourcePos[0] = tier; sourcePos[1] = ypos; sourcePos[2] = xpos;
							
							//Now, find all possible positions the sphere can be moved to (sphere can only be moved from tier 1 to tiers 2 or 3)
							//Need to make sure sourcePos isn't underneath destPos when moving from tier 1 to tier 2
							for(int tier_dest=2; tier_dest<4; tier_dest++) {
								if(tier_dest == 2) {
									for(int yDest=E; yDest<=G; yDest++) {
										for(int xDest=0; xDest<3; xDest++) {
											if(state.canPlace(tier_dest, xDest, yDest)) {
												//Sphere can be placed at this position, but first ensure this position isn't on top of the source position
												if(!state.isUnderneath(tier, ypos, xpos, tier_dest, yDest, xDest)) {
													//Dest isn't underneath source, so sphere can be placed here. Record position
													int destPos[]  = new int[2];
													destPos[0] = yDest; destPos[1] = xDest;
													
													//Now check if raising to this position requires a removal follow up
													if(state.checkForRemove(tier_dest, yDest, xDest)) {
														//If it does, consider all possible follow ups
														//Need to make sure sourcePos isn't considered for removal; need to make sure destPos is considered for removal
														//Need to make sure any spheres underneath sourcePos are considered for removal; need to make sure any spheres underneath destPos aren't considered for removal
														//TODO: Above
													}
													
													else {
														//Otherwise, a follow up is not needed
														curMove = new PylosMove(RAISE, tier_dest, destPos, sourcePos, false, 0, null, null);
														result.add(curMove);
													}
												}
											}
										}
									}
								}
								else if(tier_dest == 3) {
									for(int yDest=H; yDest<=I; yDest++) {
										for(int xDest=0; xDest<2; xDest++) {
											if(state.canPlace(tier_dest, xDest, yDest) && !state.isUnderneath(tier, ypos, xpos, tier_dest, yDest, xDest)) {
												//Sphere can be raised from sourcePos to destPos. Record destPos position
												int destPos[] = new int[2];
												destPos[0] = yDest; destPos[1] = xDest;
												
												//Now check if raising to this position requires a removal follow up
												if(state.checkForRemove(tier_dest, yDest, xDest)) {
													//If it does, consider all possible follow ups
													//Need to make sure sourcePos isn't considered for removal; need to make sure destPos is considered for removal
													//Need to make sure any spheres underneath sourcePos are considered for removal; need to make sure any spheres underneath destPos aren't considered for removal
													//TODO: Find all spheres that can be removed 
												}
												
												else {
													//Otherwise, a follow up is not needed
													curMove = new PylosMove(RAISE, tier_dest, destPos, sourcePos, false, 0, null, null);
													result.add(curMove);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			else if(tier == 2) {
				for(int ypos=E; ypos<=G; ypos++) {
					for(int xpos=0; xpos<3; xpos++) {
						if(state.canRaise(player, tier, ypos, xpos)) {
							//Sphere at this position can be raised; record position
							int sourcePos[] = new int[3];
							sourcePos[0] = tier; sourcePos[1] = ypos; sourcePos[2] = xpos;
							
							//Now find all possible positions the sphere can be moved to (the sphere can only be moved from tier 2 to tier 3)
							int tier_dest = 3;
							for(int yDest=H; yDest<=I; yDest++) {
								for(int xDest=0; xDest<2; xDest++) {
									if(state.canPlace(tier_dest, xDest, yDest) && !state.isUnderneath(tier, ypos, xpos, tier_dest, yDest, xDest)) {
										//Sphere can be raised from sourcePos to destPos; record destPos
										int destPos[] = new int[2];
										destPos[0] = yDest; destPos[1] = xDest;
										
										//Now check if raising to this position requires a removal follow up
										if(state.checkForRemove(tier_dest, yDest, xDest)) {
											//If it does, consider all possible follow ups
											//Need to make sure sourcePos isn't considered for removal; make sure destPos is considered for removal
											//Need to make sure any spheres underneath sourcePos are considered for removal; need to make sure any spheres underneath destPos aren't considered for removal
											//TODO: Find all spheres that can be removed
										}
										else {
											//Otherwise, a follow up is not needed
											curMove = new PylosMove(RAISE, tier_dest, destPos, sourcePos, false, 0, null, null);
											result.add(curMove);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		//Now, return array-list containing all valid moves
		return result;
	}
	
	//Minimax function; looks at give state and computes best move from perspective of white player
	private static PylosMove Minimax(Pylos state, int curDepth) {
		//TODO: Finish all the bullshit above and write the recursive rule
		PylosMove move = null;
		return move;
	}
}