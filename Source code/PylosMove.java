/*
 CITS3001 Project 2016
 Name:			Ammar Abu Shamleh
 Student number: 21521274
 Date:           May 2016
 */

//This class offers an internatal representation of a valid pylos move
public class PylosMove {
	private final static int PLACE = 1;
	private final static int RAISE = 2;
	
	private int moveType;
	int tier;
	private int[] to;
	private int[] from; //Only applicable to 'raise' move
	private boolean remove;
	private int nSpheresToRemove;
	private int[] sphere1pos;
	private int[] sphere2pos;
	
	public PylosMove(int type, int t, int[] toPos, int[] fromPos, boolean rem, int nSpheres, int[] pos1, int[] pos2) {
		this.moveType = type;
		this.tier = t;
		this.remove = false; //Default to false
		this.to = toPos.clone();
		if(type == PLACE) {
			this.from = null;
		}
		else {
			this.from = fromPos.clone();
		}
		this.remove = rem;
		this.nSpheresToRemove = nSpheres;
		if(!remove) {
			this.sphere1pos = null;
			this.sphere2pos = null;
		}
		else if(nSpheres == 1) {
			this.sphere2pos = null;
			this.sphere1pos = pos1.clone();
		}
		else {
			this.sphere1pos = pos1.clone();
			this.sphere2pos = pos2.clone();
		}
	}
	
	public int getType() {
		return this.moveType;
	}
	
	public int getTier() {
		return this.tier;
	}
	
	public int[] getToPos() {
		return this.to;
	}
	
	public int[] getFromPos() {
		return this.from;
	}
	
	public boolean isRemove() {
		return this.remove;
	}
	
	public int getNumberOfSpheresToRemove() {
		return this.nSpheresToRemove;
	}
	
	public int[] getRemovePos1() {
		return this.sphere1pos;
	}
	
	public int[] getRemovePos2() {
		return this.sphere2pos;
	}
}
