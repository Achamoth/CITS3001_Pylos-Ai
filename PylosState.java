public class PylosState {
	private static final int EMPTY = -1;
	private static final int BLACK = 1;
	private static final int WHITE = 2;

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

	private int[][] bottom_tier = new int[4][4];
	private int[][] second_tier = new int[3][3];
	private int[][] third_tier = new int[2][2];
	private int[][] top_tier = new int[1][1];

	private int black_spheres;
	private int white_spheres;

	public PylosState() {
		black_spheres = 15;
		white_spheres = 15;
		initialize_board();
	}

	//Set up board
	public void initialize_board() {
		//Bottom tier
		for(int i=0; i<4; i++) {
			for(int j=0; j<4; j++) {
				bottom_tier[i][j] = EMPTY;
			}
		}
		//Second tier
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				second_tier[i][j] = EMPTY;
			}
		}
		//Third tier
		for(int i=0; i<2; i++) {
			for(int j=0; j<2; j++) {
				third_tier[i][j] = EMPTY;
			}
		}
		//Top tier
		top_tier[0][0] = EMPTY;
	}
}