import java.util.ArrayList;

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
	private static ArrayList<PylosMove> Actions(Pylos state, int player) {
		ArrayList<PylosMove> result = new ArrayList<PylosMove>();
		PylosMove curMove = null;
		
		//First, determine all possible 'place' moves
		for(int tier=1; tier<5; tier++) {
			//Loop through tiers
			
			//BOTTOM TIER
			if(tier == 1) {
				for(int ypos=A; ypos<E; ypos++) {
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
										for(int yrem=A; yrem<E; yrem++) {
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
										for(int yrem=E; yrem<H; yrem++) {
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
										for(int yrem=H; yrem<J; yrem++) {
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
				for(int ypos=E; ypos<H; ypos++) {
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
										for(int yrem=A; yrem<E; yrem++) {
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
										for(int yrem=E; yrem<H; yrem++) {
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
										for(int yrem=H; yrem<J; yrem++) {
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
				for(int ypos=H; ypos<J; ypos++) {
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
								for(int yrem=H; yrem<J; yrem++) {
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
				for(int ypos=A; ypos<E; ypos++) {
					for(int xpos=0; xpos<4; xpos++) {
						if(state.canRaise(player, tier, ypos, xpos)) {
							//Sphere at this position can be raised. Record position
							int sourcePos[] = new int[2];
							sourcePos[0] = ypos; sourcePos[1] = xpos;
							
							//Now, find all possible positions the sphere can be moved to
							//TODO: Above (note, sphere can only be moved from tier 1 to tiers 2 or 3) (Also need to make sure sourcePos isn't underneath destPos)
						}
					}
				}
			}
			else if(tier == 2) {
				
			}
		}
		
		//Now, return array-list containing all valid moves
		return result;
	}
	
	//Minimax function; looks at give state and computes best move from perspective of white player
	private static PylosMove Minimax(Pylos state, int curDepth) {
		//TODO: Finish all the bullshit above and write the recursive rule
	}
}