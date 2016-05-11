import java.util.ArrayList;

/*
 * WHITE aims to maximize utility
 * BLACK aims to minimize utility
 */

public class PylosAI {
	//Move types
	private final static int PLACE = 1;
	private final static int RAISE = 2;

	//Position states
	private static final int EMPTY = -1;
	private static final int WHITE = 1;
	private static final int BLACK = 2;

	//Depth limit on game search tree
	private static final int DEPTH_LIMIT_MINIMAX = 5;
	private static final int DEPTH_LIMIT_ALPHABETA = 5;

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

	//Utility function; looks at state (and maybe depth, in the future), and determines utility, if it's a terminal state (utility determined from perspective of specified player)
	private static int utility(Pylos state) {
		if(terminal(state)) {
			//Will return utility from perspective of player
			if(state.winner() == WHITE) return 1000;
			else return -1000;
		}
		else return -1;
	}

	//Terminal function; looks at state and determines whether or not it's a terminal state
	private static boolean terminal(Pylos state) {
		return state.isComplete();
	}

	//A simple evaluation function that looks at the number of spheres retained by each player, and computes an evaluation for specified player
	private static int evaluate_simple(Pylos state) {
		//To start with, we'll use a simple evaluation function that just compares number of spheres
		int whiteSpheres = state.sphereCount(WHITE);
		int blackSpheres = state.sphereCount(BLACK);
		int result = (whiteSpheres - blackSpheres) * 10; //Maximizing value from white's perspective 
		return result;
	}

	//Second evaluation function written; extends on evaluate_simple. This function considers number of spheres placed, but also what tier they've been placed on (higher is better)
	private static int evaluate_height(Pylos state) {
		int result = 0;

		//Find number of spheres placed by each player on the bottom tier, and each has a weight of 15 or -15 (black spheres or white spheres, respectively)
		int bottom_tier[][] = state.getBottom();
		for(int y=A; y<=E; y++) {
			for(int x=0; x<4; x++) {
				if(bottom_tier[y][x] == WHITE) result -= 15;
				else if(bottom_tier[y][x] == BLACK) result += 15;
			}
		}

		//Find number of spheres placed by each player on the second tier, and each has a weight of 7 or -7 (black spheres or white spheres, respectively)
		int second_tier[][] = state.getSecond();
		for(int y=E; y<=G; y++) {
			for(int x=0; x<3; x++) {
				if(second_tier[y][x] == WHITE) result -= 7;
				else if(second_tier[y][x] == BLACK) result += 7;
			}
		}

		//Find number of spheres placed by each player on the third tier, and each has a weight of 2 or -2 (black spheres or white spheres, respectively)
		int third_tier[][] = state.getThird();
		for(int y=H; y<=I; y++) {
			for(int x=0; x<2; x++) {
				if(third_tier[y][x] == WHITE) result -= 2;
				else if(third_tier[y][x] == BLACK) result += 2;
			}
		}

		return result;
	}

	/*Evaluate function currently in use by AI; it simply calls another evaluation function.
	 *Doing this simply allows me to very quickly change the evaluation function being used by the AI (only need to change the function call in here)
	 */
	private static int evaluate(Pylos state) {
		return evaluate_simple(state);
	}

	//Result function; computes resulting state when applying a given action to a given state
	private static Pylos result(Pylos state, PylosMove action, int player) {
		Pylos newState = state.copy();
		newState.applyMove(action, player);
		return newState;
	}

	//Actions function; returns array list of all possible actions in a given state
	public static ArrayList<PylosMove> actions(Pylos state, int player) {
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

							//Generate action based on placing sphere at current position
							curMove = new PylosMove(PLACE, tier, pos, null, false, 0, null, null);

							//Now consider if the placement requires a removal follow-up
							Pylos testState = result(state, curMove, player);

							if(testState.checkForRemove(tier, ypos, xpos)) {
								//If it does, consider all possible follow-ups
								int rem1[] = new int[3];

								/*First, consider all possible single spheres that can be removed*/
								//Now, consider all other possible spheres
								for(int tier_removal=1; tier_removal < 4; tier_removal++) {
									if(tier_removal == 1) {
										for(int yrem=A; yrem<=D; yrem++) {
											for(int xrem=0; xrem<4; xrem++) {
												if(testState.canRemove(player, tier_removal, yrem, xrem)) {
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
												if(testState.canRemove(player, tier_removal, yrem, xrem)) {
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
												if(testState.canRemove(player, tier_removal, yrem, xrem)) {
													//Sphere at this position can be removed; mark and store the move
													rem1[0] = tier_removal; rem1[1] = yrem; rem1[2] = xrem;
													curMove = new PylosMove(PLACE, tier, pos, null, true, 1, rem1, null);
													result.add(curMove);
												}
											}
										}
									}
								}

								//Consider all possible pairs of spheres that can be removed
								addSpherePairRemovals(result, testState, PLACE, tier, pos, null, player);
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

							//Generate action based on placing sphere at current position
							curMove = new PylosMove(PLACE, tier, pos, null, false, 0, null, null);

							//Now consider if the placement requires a removal follow up
							Pylos testState = result(state, curMove, player);

							if(testState.checkForRemove(tier, ypos, xpos)) {
								//If it does, consider all possible follow ups
								int rem1[] = new int[3];

								/*First consider all possible single sphere removals*/
								for(int tier_removal=1; tier_removal<4; tier_removal++) {
									if(tier_removal == 1) {
										for(int yrem=A; yrem<=D; yrem++) {
											for(int xrem=0; xrem<4; xrem++) {
												if(testState.canRemove(player, tier_removal, yrem, xrem)) {
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
												if(testState.canRemove(player, tier_removal, yrem, xrem)) {
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
												if(testState.canRemove(player, tier_removal, yrem, xrem)) {
													//Sphere at this position can be removed; store the move
													rem1[0] = tier_removal; rem1[1] = yrem; rem1[2] = xrem;
													curMove = new PylosMove(PLACE, tier, pos, null, true, 1, rem1, null);
													result.add(curMove);
												}
											}
										}
									}
								}

								//Consider all possible pairs of spheres that can be removed
								addSpherePairRemovals(result, testState, PLACE, tier, pos, null, player);
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

							//Generate action based on placing sphere at current position
							curMove = new PylosMove(PLACE, tier, pos, null, false, 0, null, null);

							//Now consider if the move requires a removal follow up
							Pylos testState = result(state, curMove, player);

							if(testState.checkForRemove(tier, ypos, xpos)) {
								//If it does, consider all possible follow ups
								int rem1[] = new int[3];

								/*Consider all possible single sphere removals*/
								//Now, consider all single spheres (only need to consider tiers 2 and 3, in this case)
								int tier_removal = 3;
								//We only need to consider tier 3 for single sphere removal
								for(int yrem=H; yrem<=I; yrem++) {
									for(int xrem=0; xrem<2; xrem++) {
										if(testState.canRemove(player, tier_removal, yrem, xrem)) {
											//Sphere at this position can be removed; store the move
											rem1[0] = tier_removal; rem1[1] = yrem; rem1[2] = xrem;
											curMove = new PylosMove(PLACE, tier, pos, null, true, 1, rem1, null);
											result.add(curMove);
										}
									}
								}

								//Consider all possible pairs of spheres that can be removed
								addSpherePairRemovals(result, testState, PLACE, tier, pos, null, player);
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

													//Generate action based on raising sphere from sourcePos to destPos
													curMove = new PylosMove(RAISE, tier_dest, destPos, sourcePos, false, 0, null, null);

													//Now consider whether or not raising to this position requires a removal follow up
													Pylos testState = result(state, curMove, player);

													if(testState.checkForRemove(tier_dest, yDest, xDest)) {
														//If it does, compute all possible removals
														int rem1[] = new int[3];

														//Consider all single sphere removals
														int tier_removal = 1;
														for(tier_removal=1; tier_removal<4; tier_removal++) {
															if(tier_removal == 1) {
																for(int yrem=A; yrem<=D; yrem++) {
																	for(int xrem=0; xrem<4; xrem++) {
																		if(testState.canRemove(player, tier_removal, yrem, xrem)) {
																			//Sphere at this position can be removed; store the move
																			rem1[0] = tier_removal; rem1[1] = yrem; rem1[2] = xrem;
																			curMove = new PylosMove(RAISE, tier_dest, destPos, sourcePos, true, 1, rem1, null);
																			result.add(curMove);
																		}
																	}
																}
															}
															else if(tier_removal == 2) {
																for(int yrem=E; yrem<=G; yrem++) {
																	for(int xrem=0; xrem<3; xrem++) {
																		if(testState.canRemove(player, tier_removal, yrem, xrem)) {
																			//Sphere at this position can be removed; store the move
																			rem1[0] = tier_removal; rem1[1] = yrem; rem1[2] = xrem;
																			curMove = new PylosMove(RAISE, tier_dest, destPos, sourcePos, true, 1, rem1, null);
																			result.add(curMove);
																		}
																	}
																}
															}
															else if(tier_removal == 3) {
																for(int yrem=H; yrem<=I; yrem++) {
																	for(int xrem=0; xrem<2; xrem++) {
																		if(testState.canRemove(player, tier_removal, yrem, xrem)) {
																			//Sphere at this position can be removed; store the move
																			rem1[0] = tier_removal; rem1[1] = yrem; rem1[2] = xrem;
																			curMove = new PylosMove(RAISE, tier_dest, destPos, sourcePos, true, 1, rem1, null);
																			result.add(curMove);
																		}
																	}
																}
															}
														}

														//Consider all possible pairs of spheres that can be removed
														addSpherePairRemovals(result, testState, RAISE, tier_dest, destPos, sourcePos, player);

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

												//Generate and store action based on raising sphere from sourcePos to destPos
												//Note: If we are raising to tier 3, it is impossible to follow up with a removal (by the rules of the game)
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

										//Generate and store action based on raising sphere from sourcePos to destPos
										//Note: If we are raising from tier 2, we can only raise to tier 3. Thus it is impossible to follow up with a removal
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

		//Now, return array-list containing all valid moves
		return result;
	}

	//Called within actions() to determine all possible configurations of moves involving placing/raising a sphere at specified position(s) on a specified game board, and following up with the removal of 2 spheres
	//Method adds all such actions to the 'result' arraylist passed to it (and returned) by actions()
	private static void addSpherePairRemovals(ArrayList<PylosMove> result, Pylos state, int moveType, int tier_dest, int destPos[], int sourcePos[], int player) {
		int rem1[] = new int[3];
		int rem2[] = new int[3];

		//First, find first sphere that can be removed
		for(int tier_removal=1; tier_removal<4; tier_removal++) {
			if(tier_removal == 1) {
				for(int yrem=A; yrem<=D; yrem++) {
					for(int xrem=0; xrem<4; xrem++) {
						if(state.canRemove(player, tier_removal, yrem, xrem)) {
							//This sphere can be removed. Record the position
							rem1[0] = tier_removal; rem1[1] = yrem; rem1[2] = xrem;

							/*Now find second sphere that can be removed*/
							int start_tier = 1;
							int ystart = yrem;
							int xstart = xrem+1;
							
							//First, calculate what position to start looking for second sphere at
							if(yrem == D && xrem == 3) start_tier = 2; //Calculate tier
							if(start_tier == 1 && xrem == 3) {
								//Calculate y and x positions
								ystart = yrem+1; 
								xstart = 0; 
							}

							//And now find it
							for(int tier_removal2=start_tier; tier_removal2<4; tier_removal2++) {
								if(tier_removal2 == 1) {
									for(int yrem2=ystart; yrem2<=D; yrem2++) {
										for(int xrem2=xstart; xrem2<4; xrem2++) {
											if(state.canRemove(player, tier_removal2, yrem2, xrem2)) {
												//This sphere can be removed. Record the position
												rem2[0] = tier_removal2; rem2[1] = yrem2; rem2[2] = xrem2;

												//Now generate and store action
												PylosMove newMove = new PylosMove(moveType, tier_dest, destPos, sourcePos, true, 2, rem1, rem2);
												result.add(newMove);
											}
										}
										xstart = 0;
									}
								}
								else if(tier_removal2 == 2) {
									for(int yrem2=E; yrem2<=G; yrem2++) {
										for(int xrem2=0; xrem2<3; xrem2++) {
											if(state.canRemove(player, tier_removal2, yrem2, xrem2)) {
												//This sphere can be removed. Record the position
												rem2[0] = tier_removal2; rem2[1] = yrem2; rem2[2] = xrem2;

												//Now generate and store action
												PylosMove newMove = new PylosMove(moveType, tier_dest, destPos, sourcePos, true, 2, rem1, rem2);
												result.add(newMove);
											}
										}
									}
								}
								else if(tier_removal2 == 3) {
									for(int yrem2=H; yrem2<=I; yrem2++) {
										for(int xrem2=0; xrem2<2; xrem2++) {
											if(state.canRemove(player, tier_removal2, yrem2, xrem2)) {
												//This sphere can be removed. Record the position
												rem2[0] = tier_removal2; rem2[1] = yrem2; rem2[2] = xrem2;

												//Now generate and store action
												PylosMove newMove = new PylosMove(moveType, tier_dest, destPos, sourcePos, true, 2, rem1, rem2);
												result.add(newMove);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			else if(tier_removal == 2) {
				for(int yrem=E; yrem<=G; yrem++) {
					for(int xrem=0; xrem<3; xrem++) {
						if(state.canRemove(player, tier_removal, yrem, xrem)) {
							//This sphere can be removed. Record position
							rem1[0] = tier_removal; rem1[1] = yrem; rem1[2] = xrem;

							/*Now find second sphere that can be removed*/
							int start_tier = 2;
							int ystart = yrem;
							int xstart = xrem+1;
							//First, calculate what position to start looking for second sphere at
							if(yrem == G && xrem == 2) {
								start_tier = 3; //Calculate tier
							}
							if(start_tier == 2 && xrem == 2) {
								//Calculate y position and x position
								ystart = yrem + 1; 
								xstart = 0; 
							}

							//And now find the second sphere
							for(int tier_removal2 = start_tier; tier_removal2 < 4; tier_removal2++) {
								if(tier_removal2 == 2) {
									for(int yrem2=ystart; yrem2<=G; yrem2++) {
 										for(int xrem2=xstart; xrem2<3; xrem2++) {
											if(state.canRemove(player, tier_removal2, yrem2, xrem2)) {
												//This sphere can be removed. Record the position
												rem2[0] = tier_removal2; rem2[1] = yrem2; rem2[2] = xrem2;

												//Now generate and store action
												PylosMove newMove = new PylosMove(moveType, tier_dest, destPos, sourcePos, true, 2, rem1, rem2);
												result.add(newMove);
											}
										}
										xstart = 0;
									}
								}
								else if(tier_removal2 == 3) {
									for(int yrem2=H; yrem2<=I; yrem2++) {
										for(int xrem2=0; xrem2<2; xrem2++) {
											if(state.canRemove(player, tier_removal2, yrem2, xrem2)) {
												//This sphere can be removed. Record the position
												rem2[0] = tier_removal2; rem2[1] = yrem2; rem2[2] = xrem2;

												//Now generate and store the action
												PylosMove newMove = new PylosMove(moveType, tier_dest, destPos, sourcePos, true, 2, rem1, rem2);
												result.add(newMove);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			else if(tier_removal == 3) {
				for(int yrem=H; yrem<=I; yrem++) {
					for(int xrem=0; xrem<2; xrem++) {
						if(state.canRemove(player, tier_removal, yrem, xrem)) {
							//This sphere can be removed. Record the position
							rem1[0] = tier_removal; rem1[1] = yrem; rem1[2] = xrem;

							/*Now find second sphere that can be removed*/
							//First, make sure we aren't on the last sphere
							if(xrem != 1 || yrem != I) {
								//Next sphere to be removed will be on tier 3 (we've already considered all others earlier in the function)
								int tier_removal2 = 3;
								int xstart = xrem+1;
								int ystart = yrem;
								//First, calculate what position to start looking for second sphere at
								if(xrem == 1){
									ystart = yrem+1;
									xstart = 0;
								}
								//And now find the second sphere
								for(int yrem2=ystart; yrem2<=I; yrem2++) {
									for(int xrem2=xstart; xrem2<2; xrem2++) {
										if(state.canRemove(player, tier_removal2, yrem2, xrem2)) {
											//This sphere can be removed. Record the position
											rem2[0] = tier_removal2; rem2[1] = yrem2; rem2[2] = xrem2;
											
											//Now generate and store action
											PylosMove newMove = new PylosMove(moveType, tier_dest, destPos, sourcePos, true, 2, rem1, rem2);
										}
									}
									xstart = 0;
								}
							}
							else {
								//We've now considered all spheres, so simply do nothing and let the method end
							}
						}
					}
				}
			}
		}
	}

	/*
	 * Minimax function; looks at given state and computes best move from perspective of specified player, without exploring past depth limit
	 * If CPU is white, minimax will try to maximize utility
	 * If CPU is black, minimax will try to minimize utility
	 */
	public static PylosMove minimax(Pylos state, int player) {
		PylosMove move = null;

		//If CPU is white
		if(player == WHITE) {
			//Compute all actions that can be performed
			ArrayList<PylosMove> moves = actions(state, player);
			int maxMinVal = (int) Double.NEGATIVE_INFINITY;
			//Maximize the minimum value that the opponent can select
			for(PylosMove curMove : moves) {
				int minVal = minValue(result(state, curMove, player), 0);
				if(minVal > maxMinVal) {
					maxMinVal = minVal;
					move = curMove;
				}
			}
		}

		//If CPU is black
		else if(player == BLACK) {
			//Compute all actions that can be performed
			ArrayList<PylosMove> moves = actions(state, player);
			int minMaxVal = (int) Double.POSITIVE_INFINITY;
			//Minimize the maximum value the opponent can select
			for(PylosMove curMove : moves) {
				int maxVal = maxValue(result(state, curMove, player), 0);
				if(maxVal < minMaxVal) {
					minMaxVal = maxVal;
					move = curMove;
				}
			}
		}

		return move;
	}

	//Max-value function; returns utility/evaluation value, aiming to maximize
	private static int maxValue(Pylos state, int curDepth) {
		//Check if the state is a terminal state
		if(terminal(state)) {
			//If it is, return the utility of the state
			return utility(state);
		}

		//If it isn't, check if the depth limit has been reached
		if(curDepth == DEPTH_LIMIT_MINIMAX) {
			//If depth limit has been reached, return evaluation of current state
			return evaluate(state);
		}

		//Otherwise, compute all actions possible by white player in current state
		ArrayList<PylosMove> moves = actions(state, WHITE);

		//Start return value at negative infinity
		int maxMinVal = (int) Double.NEGATIVE_INFINITY;

		//Loop over all actions possible by WHITE
		for(PylosMove curMove : moves) {
			//Compute resulting state (after applying curMove to 'state')
			Pylos resultState = result(state, curMove, WHITE);

			//Work out the largest utility this action will lead me to (assuming BLACK plays optimally)
			int minVal = minValue(resultState, curDepth+1);

			//Update maxMinVal (i.e. if we've found an action that leads to a better state than any previous actions, then we should update)
			if(maxMinVal < minVal) {
				maxMinVal = minVal;
			}
		}
		return maxMinVal;
	}

	//Min-value function; returns utility/evaluation value, aiming to minimize
	private static int minValue(Pylos state, int curDepth) {
		//Check if the state is a terminal state
		if(terminal(state)) {
			//If it is, return the utility of the state
			return utility(state);
		}

		//If it isn't, check if the depth limit has been reached
		if(curDepth == DEPTH_LIMIT_MINIMAX) {
			//If depth limit has been reached, return evaluation of current state
			return evaluate(state);
		}

		//Otherwise, compute all actions possible by black player in current state
		ArrayList<PylosMove> moves = actions(state, BLACK);

		//Start return value at positive infinity
		int minMaxVal = (int) Double.POSITIVE_INFINITY;

		//Loop over all actions possible by BLACK
		for(PylosMove curMove : moves) {
			//Compute resulting state (after applying curMove to 'state')
			Pylos resultState = result(state, curMove, BLACK);

			//Work out the smallest utility this action will lead me to (assuming WHITE plays optimally)
			int maxVal = maxValue(resultState, curDepth+1);

			//Update minMaxVal (i.e. if we've found an action that leads to a better state than any previous actions, then we should update)
			if(minMaxVal > maxVal) {
				minMaxVal = maxVal;
			}
		}
		return minMaxVal;
	}

	//Returns move using alpha beta search on game tree
	public static PylosMove alphaBetaSearch(Pylos state, int player) {
		PylosMove move = null;

		//If CPU is white
		if(player == WHITE) {
			//Compute all actions that can be performed
			ArrayList<PylosMove> moves = actions(state, player);
			int maxMinVal = (int) Double.NEGATIVE_INFINITY;
			//Maximize the minimum value that the opponent can select
			for(PylosMove curMove : moves) {
				int minVal = minValueAB(result(state, curMove, player), 0, (int)Double.NEGATIVE_INFINITY, (int)Double.POSITIVE_INFINITY);
				if(minVal > maxMinVal) {
					maxMinVal = minVal;
					move = curMove;
				}
			}
		}

		//If CPU is black
		else if(player == BLACK) {
			//Compute all actions that can be performed
			ArrayList<PylosMove> moves = actions(state, player);
			int minMaxVal = (int) Double.POSITIVE_INFINITY;
			//Minimize the maximum value the opponent can select
			for(PylosMove curMove : moves) {
				int maxVal = maxValueAB(result(state, curMove, player), 0, (int)Double.NEGATIVE_INFINITY, (int)Double.POSITIVE_INFINITY);
				if(maxVal < minMaxVal) {
					minMaxVal = maxVal;
					move = curMove;
				}
			}
		}

		return move;
	}

	//Variant of maxValue used for alpha beta search
	private static int maxValueAB(Pylos state, int curDepth, int alpha, int beta) {
		//Check if the state is a terminal state
		if(terminal(state)) {
			//If it is, return the utlity of the state
			return utility(state);
		}

		//If it isn't, check if the depth limit has been reached
		if(curDepth == DEPTH_LIMIT_ALPHABETA) {
			//If depth limit has been reached, return evaluation of current state
			return evaluate(state);
		}

		//Otherwise, compute all actions possible by white player in current state
		ArrayList<PylosMove> moves = actions(state, WHITE);

		//Start return value at negative infinity
		int maxMinVal = (int) Double.NEGATIVE_INFINITY;

		//Loop over all actions possible by WHITE
		for(PylosMove curMove : moves) {
			//Compute resulting state (after applying curMove to 'state')
			Pylos resultState = result(state, curMove, WHITE);

			//Work out the largest utility this action will lead me to (assuming BLACK plays optimally)
			int minVal = minValueAB(resultState, curDepth+1, alpha, beta);

			//Update maxMinVal (i.e. if we've found an action that leads to a better state than any previous actions, then we should update)
			if(maxMinVal < minVal) {
				maxMinVal = minVal;
			}

			//Check if maxMinVal exceeds beta
			if(maxMinVal >= beta) {
				return maxMinVal;
			}

			//Update alpha
			if(alpha < maxMinVal) {
				alpha = maxMinVal;
			}
		}
		return maxMinVal;
	}

	//Variant of minValue used for alpha beta search
	private static int minValueAB(Pylos state, int curDepth, int alpha, int beta) {
		//Check if the state is a terminal state
		if(terminal(state)) {
			//If it is, return the utility of the state
			return utility(state);
		}

		//If it isn't, check if the depth limit has been reached
		if(curDepth == DEPTH_LIMIT_ALPHABETA) {
			//If depth limit has been reached, return evaluation of current state
			return evaluate(state);
		}

		//Otherwise, compute all actions possible by black player in current state
		ArrayList<PylosMove> moves = actions(state, BLACK);

		//Start return value at positive infinity
		int minMaxVal = (int) Double.POSITIVE_INFINITY;

		//Loop over all actions possible by BLACK
		for(PylosMove curMove : moves) {
			//Compute resulting state (after applying curMove to 'state')
			Pylos resultState = result(state, curMove, BLACK);

			//Work out the smallest utility this action will lead me to (assuming WHITE plays optimally)
			int maxVal = maxValueAB(resultState, curDepth+1, alpha, beta);

			//Update minMaxVal (i.e. if we've found an action that leads to a better state than any previous actions, then we should update)
			if(minMaxVal > maxVal) {
				minMaxVal = maxVal;
			}

			//Check if alpha exceeds minMaxVal
			if(minMaxVal <= alpha) {
				return minMaxVal;
			}

			//Update beta
			if(beta > minMaxVal) {
				beta = minMaxVal;
			}
		}
		return minMaxVal;
	}
}