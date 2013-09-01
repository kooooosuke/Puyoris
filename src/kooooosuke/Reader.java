package kooooosuke;

import java.util.Scanner;

public class Reader {
	Scanner scanner;
	Input input;

	public Reader() {
		scanner = new Scanner(System.in);
	}

	public Input read() {
		String[] line = scanner.nextLine().split(" ");
		int W = Integer.parseInt(line[0]);
		int H = Integer.parseInt(line[1]);
		int T = Integer.parseInt(line[2]);
		int S = Integer.parseInt(line[3]);
		int N = Integer.parseInt(line[4]);
		int[][][] packs = new int[N][T][T];
		for (int n = 0; n < N; n++) {
			for (int h = 0; h < T; h++) {
				line = scanner.nextLine().split(" ");
				for (int w = 0; w < T; w++) {
					packs[n][h][w] = Integer.parseInt(line[w]);
				}
			}
			scanner.nextLine();
		}
		input = new Input(W, H, T, S, N, packs);

		return input;
	}

}
