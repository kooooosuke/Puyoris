package kooooosuke;

public class Input {

	int W;
	int H;
	int T;
	int S;
	int N;
	int[][][] packs = new int[N][H][W];
	int P;
	int A;
	int B_obs;
	int Th;

	public Input(int W, int H, int T, int S, int N, int[][][] packs) {
		this.W = W;
		this.H = H;
		this.T = T;
		this.S = S;
		this.N = N;
		this.packs = packs;
		if (W == 10) {
			// easy
			P = 25;
			A = 400;
			B_obs = 1000;
			Th = 100;
		} else if (W == 15) {
			// medium
			P = 30;
			A = 213;
			B_obs = 3000;
			Th = 1000;
		} else {
			// large
			P = 35;
			A = 240;
			B_obs = 7200;
			Th = 10000;
		}
	}

}
