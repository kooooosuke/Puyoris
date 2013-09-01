package kooooosuke;

import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AI {
	Input input;
	int[][][] fields;
	long score = 0;
	long min_score;
	int Fc = 0;
	ArrayList<Target> target_org;
	int[][] ideal_field;
	int[][] ojama_erase_vector = { { -1, -1 }, { 0, -1 }, { 1, -1 }, { -1, 0 },
			{ 1, 0 }, { -1, 1 }, { 0, 1 }, { 1, 1 } };
	int[][] erase_vector = { { 0, 1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } };
	int num_max = -1;
	int BOTTOM;
	int EDGE;

	// コンストラクタ
	public AI(Input input) {
		this.input = input;
		fields = new int[input.N][input.H + input.T][input.W + (input.T - 1)
				* 2];
		// 落ちてくる最大の数字
		switch (input.W) {
		case 10:
			num_max = 5;
			break;
		case 15:
			num_max = 10;
			break;
		default:
			num_max = 15;
			break;
		}
		BOTTOM = fields[0].length - 1;
		EDGE = input.T - 1;
		initExpectedField();
	}

	public void initExpectedField() {
		target_org = new ArrayList<Target>();

		// SMALL///////////////////////////////////////////////////////////////////////////////
		// best
		// int[][] tmp = { { 1, 1, 1, 1, 1, 1, 1, 2, 11, 3, 1, 1, 1, 1, 1, 1 },
		// { 1, 1, 1, 1, 1, 1, 1, 2, 11, 3, 1, 1, 1, 1, 1, 1 },
		// { 1, 1, 1, 1, 1, 1, 1, 2, 11, 3, 1, 1, 1, 1, 1, 1 },
		// { 1, 1, 1, 1, 1, 1, 1, 2, 11, 3, 1, 1, 1, 1, 1, 1 },
		// { 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 2, 2, 2 } };

		// MEDIUM///////////////////////////////////////////////////////////////////////////////
		// best
		EDGE += 1;
		int[][] tmp = {
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 21, 9, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1 },
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 10, 21, 10, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 0, 0 },
				{ 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21,
						21, 21, 21, 21, 21, 10, 21, 10 } };

		// LARGE///////////////////////////////////////////////////////////////////////////////
		// EDGE += 2;
		// int[][] tmp = {
		// { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 31, 12,
		// 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
		// { 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31,
		// 31, 31, 31, 31, 15, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		// 0, 0, 0, 0, 0 },
		// { 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31,
		// 31, 31, 31, 31, 31, 31, 31, 15, 0, 0, 0, 0, 0, 0, 0, 0,
		// 0, 0, 0, 0, 0 },
		// { 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		// 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };

		ideal_field = tmp;
		for (int i = 0; i < ideal_field.length; i++) {
			for (int j = 0; j < ideal_field[0].length; j++) {
				target_org.add(new Target(BOTTOM - j, EDGE + i,
						ideal_field[i][j]));
				// target_org.add(new Target(BOTTOM - j, EDGE + i, KARA));
			}
		}
	}

	// AI本体
	public Output[] think(long past_max) {

		// small
		// min_score = 1000000000000l;

		// medium
		min_score = 40000000000000l;

		// if (past_max == -1) {
		// end++;
		// } else {
		// min_score = past_max;
		// }

		// large
		// int end = 750;
		// int start = 350;
		// int interval = 1;
		// min_score = 3000000000000000l;

		int[] t = new int[1];
		Output[] best_outputs = makeTargets(t);
		if (t[0] == 0) {
			return best_outputs;
		}
		Output[] first_outputs = runBigFire(t[0] + 1, best_outputs, true);
		best_outputs = first_outputs;

		for (int n = t[0] + 2; n < input.N; n++) {
			Output[] outs = runBigFire(n, first_outputs, false);
			// 突然の死
			if (outs == null) {
				continue;
			}
			if (best_outputs[best_outputs.length - 1].score < outs[outs.length - 1].score) {
				best_outputs = outs;
				if (min_score < best_outputs[best_outputs.length - 1].score) {
					min_score = best_outputs[best_outputs.length - 1].score;
					logger(0, min_score, 0, "_new_record" + n);
					n--;
				}
			}
			logger(Fc, n, Fc, "__prog");
		}

		return best_outputs;
	}

	public void copyField(Output[] first_outputs) {
		for (int n = 0; n < fields.length; n++) {
			for (int i = 0; i < fields[0].length; i++) {
				for (int j = 0; j < fields[0][0].length; j++) {
					fields[n][i][j] = first_outputs[n].field[i][j];
				}
			}
		}
	}

	private Output[] runBigFire(int turn, Output[] first_outputs, boolean wait) {
		Output[] outputs = new Output[input.N];
		// コピー
		for (int i = 0; i < input.N; i++) {
			outputs[i] = new Output(first_outputs[i].x, first_outputs[i].r,
					new int[fields[0].length][fields[0][0].length],
					first_outputs[i].score, first_outputs[i].Fc);
		}

		// Nターン繰り返す
		score = first_outputs[turn - 1].score;
		Fc = first_outputs[turn - 1].Fc;
		copyField(first_outputs);
		for (int n = turn; n < input.N; n++) {
			// 評価初期化
			Evaluater best_eval = null;
			ArrayList<Target> target;
			if (wait) {
				target = initTarget(n);
			} else {
				target = target_org;
			}

			// 全ケース調査
			for (int x = 0; x < fields[0][0].length - input.T + 1; x++) {
				for (int r = 0; r < 4; r++) {
					// 評価
					Evaluater eval = simurate(x, r, n);
					if (eval == null) {
						// 置けない場所に置いたか死んだ
						continue;
					} else if (best_eval == null) {
						best_eval = eval;
					}
					// 更新
					// eval.nextChain = -1;//smallではコメントアウトしてた
					// 大発火の溜めに入る
					if (!wait) {
						eval.small_fire = false;
					}

					best_eval = best_eval
							.judge(best_eval,
									eval,
									target,
									n,
									score,
									Math.max(
											min_score,
											first_outputs[first_outputs.length - 1].score),
									input.H, input.W, wait);
					// fields[n] = best_eval.field;
				}
			}
			// 出力確定
			// どこに置いても死ぬ
			if (best_eval == null) {
				return null;
			}
			score += best_eval.score;
			outputs[n] = new Output(best_eval.x - input.T + 1, best_eval.r,
					best_eval.field, score, Fc);
			fields[n] = best_eval.field;
			if (best_eval.small_fire) {
				Fc++;
			}
			// デバッグ用
			// for (int i = 0; i < fields[n].length; i++) {
			// for (int j = 0; j < fields[n][i].length; j++) {
			// System.out.print(fields[n][i][j]);
			// }
			// System.out.println();
			// }
			// System.out.println();
		}
		return outputs;
	}

	// FC優先で1000ターン回す
	private Output[] makeTargets(int[] t) {
		Output[] outputs = new Output[input.N];
		// Nターン繰り返す
		for (int n = 0; n < input.N; n++) {
			// 評価初期化
			Evaluater best_eval = null;
			ArrayList<Target> target = initTarget(n);
			// 全ケース調査
			for (int x = 0; x < fields[0][0].length - input.T + 1; x++) {
				for (int r = 0; r < 4; r++) {
					// 評価
					Evaluater eval = simurate(x, r, n);
					if (eval == null) {
						// 置けない場所に置いたか死んだ
						continue;
					} else if (best_eval == null) {
						best_eval = eval;
					}
					// 更新
					// System.out.println(n);
					// eval.nextChain = calcNextChain(eval.field);
					eval.nextScore = calcNextScore(eval.field);
					best_eval = best_eval.judge(best_eval, eval, target, n,
							score, min_score, input.H, input.W, true);
					// fields[n] = best_eval.field;
				}
			}
			// 出力確定
			score += best_eval.score;
			outputs[n] = new Output(best_eval.x - input.T + 1, best_eval.r,
					best_eval.field, score, Fc);
			fields[n] = best_eval.field;
			if (best_eval.small_fire) {
				Fc++;
			}
			// デバッグ用
			// for (int i = 0; i < fields[n].length; i++) {
			// for (int j = 0; j < fields[n][i].length; j++) {
			// System.out.print(fields[n][i][j]);
			// }
			// System.out.println();
			// }
			// System.out.println();
			logger(n, Fc, (int) (score / 1000000000), "__prog");
			// 完成したら次のフェーズへ
			if (isCompleted(n)) {
				t[0] = n;
				for (int i = n + 1; i < input.N; i++) {
					outputs[i] = new Output(0, 0, fields[n], score, Fc);
				}
				break;
			}
		}
		return outputs;
	}

	private boolean isCompleted(int n) {
		for (int i = 0; i < ideal_field.length; i++) {
			for (int j = 0; j < ideal_field[0].length; j++) {
				if (fields[n][fields[0].length - 1 - j][i + EDGE] != ideal_field[i][j]) {
					return false;
				}
			}
		}
		return true;
	}

	private ArrayList<Target> initTarget(int n) {
		ArrayList<Target> targets = new ArrayList<Target>();

		// fieldの状態によってtarget更新
		int x;
		for (x = 0; x < ideal_field.length; x++) {
			boolean complete = true;
			for (int y = 0; y < ideal_field[0].length; y++) {
				// if (complete) {
				targets.add(new Target(BOTTOM - y, EDGE + x, ideal_field[x][y]));
				// }
				if (ideal_field[x][y] != fields[n == 0 ? 0 : n - 1][BOTTOM - y][EDGE
						+ x]) {
					complete = false;
				}
			}
			if (!complete) {
				x++;
				break;
			}
		}
		// for (int y = 0; y < ideal_field[0].length; y++) {
		// targets.add(new Target(BOTTOM - y, EDGE + x, 0));
		// }
		// targets.add(new Target(BOTTOM - 3, EDGE + x, 0));
		// targets.add(new Target(BOTTOM - 3, EDGE + x + 1, 0));
		// targets.add(new Target(BOTTOM - 3, EDGE + x + 2, 0));

		// デバッグ用
		// for (Target target : targets) {
		// System.out.println(target.i + "/" + target.j + "/" + target.num);
		// }
		// System.out.println();

		return targets;
	}

	// 途中経過ログ出力
	public void logger(int start, long n, int end, String filename) {
		File file = null;
		FileWriter filewriter = null;
		try {
			file = new File("/Users/suehiroakihisa/Desktop/Puyoris_Log/"
					+ filename + ".txt");
			filewriter = new FileWriter(file);
			filewriter.write(start + " / " + n + " / " + end + "\n");
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			if (filewriter != null) {
				try {
					filewriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// ブロックを落として、xとrの評価値を算出
	public Evaluater simurate(int x, int r, int n) {
		// パックを置く
		int[][] puted_field = new int[input.H + input.T][input.W
				+ (input.T - 1) * 2];
		puted_field = putPack(fields[n == 0 ? 0 : n - 1], input.packs[n], x, r);
		if (isOutOfBounds(puted_field)) {
			return null;
		}
		// 消す
		Evaluater eval = erase(puted_field, x, r, n);
		return eval;
	}

	// 次のターンの最大可能チェイン数を求める
	public int calcNextChain(int[][] field) {
		int max_next_chain = 0;
		for (int x = input.T - 1; x < field[0].length - input.T + 1; x++) {
			for (int num = 1; num <= num_max; num++) {
				int[][] calc_field = new int[field.length][field[0].length];
				for (int i = input.T - 1; i < field.length - input.T + 1; i++) {
					for (int j = 0; j < field[0].length - input.T + 1; j++) {
						calc_field[i][j] = field[i][j];
					}
				}
				calc_field[input.T - 1][x] = num;
				Evaluater next_eval = erase(calc_field, -1, -1, -1);
				if (next_eval != null && max_next_chain < next_eval.chain) {
					max_next_chain = next_eval.chain;
				}
			}
		}

		return max_next_chain;
	}

	// 次のターンの最大可能チェイン数を求める
	public long calcNextScore(int[][] field) {
		long max_next_score = 0;
		for (int x = (input.T - 1) + 2; x < field[0].length - input.T + 1; x++) {
			for (int num = 1; num <= num_max; num++) {
				int[][] calc_field = new int[field.length][field[0].length];
				for (int i = input.T - 1; i < field.length - input.T + 1; i++) {
					for (int j = 0; j < field[0].length - input.T + 1; j++) {
						calc_field[i][j] = field[i][j];
					}
				}
				calc_field[input.T - 1][x] = num;
				Evaluater next_eval = erase(calc_field, -1, -1, -1);
				if (next_eval != null && max_next_score < next_eval.score) {
					max_next_score = next_eval.score;
				}
			}
		}

		return max_next_score;
	}

	// packを設置する
	public int[][] putPack(int[][] field, int[][] pack, int x, int r) {
		int[][] puted_field = new int[input.H + input.T][input.W
				+ (input.T - 1) * 2];
		for (int i = 0; i < puted_field.length; i++) {
			for (int j = 0; j < puted_field[0].length; j++) {
				puted_field[i][j] = field[i][j];
			}
		}
		int[][] rotated_pack = rotate(pack, r);
		for (int i = 0; i < rotated_pack.length; i++) {
			for (int j = 0; j < rotated_pack[0].length; j++) {
				puted_field[i][j + x] = rotated_pack[i][j];
			}
		}

		return puted_field;
	}

	// ブロックを消す
	public Evaluater erase(int[][] field, int x, int r, int n) {
		boolean[][] erase_field;
		int max_haight = 0;
		int C = 0;
		long row_score = 0;

		// パックを投下
		boolean is_droped = drop(field);
		while (is_droped) {
			int E = 0;
			erase_field = new boolean[input.H + input.T][input.W
					+ (input.T - 1) * 2];
			for (int i = 0; i < field.length; i++) {
				for (int j = input.T - 1; j < field[0].length - input.T + 1; j++) {
					// 空欄とお邪魔ブロック以外に対して消去判定
					if (field[i][j] != 0 && field[i][j] != input.S + 1) {
						for (int[] vector : erase_vector) {
							E = splash(field, erase_field, i, j, vector, E);
						}
					}
				}
			}

			// お邪魔ブロック削除
			boolean[][] ojama_erase_field = new boolean[input.H + input.T][input.W
					+ (input.T - 1) * 2];
			for (int i = 0; i < erase_field.length; i++) {
				for (int j = input.T - 1; j < erase_field[0].length - input.T
						+ 1; j++) {
					if (erase_field[i][j]) {
						for (int[] vector : ojama_erase_vector) {
							if ((0 < i + vector[0]
									&& i + vector[0] < input.H + input.T
									&& 0 < j + vector[1] && j + vector[1] < input.W
									+ (input.T - 1) * 2)) {
								if (field[i + vector[0]][j + vector[1]] == input.S + 1) {
									ojama_erase_field[i + vector[0]][j
											+ vector[1]] = true;
								}
							}
						}
					}
				}
			}

			// 消す
			for (int i = 0; i < erase_field.length; i++) {
				for (int j = input.T - 1; j < erase_field[0].length - input.T
						+ 1; j++) {
					if (erase_field[i][j]) {
						field[i][j] = 0;
					} else if (ojama_erase_field[i][j]) {
						field[i][j] = 0;
						E++;
					}
				}
			}
			is_droped = drop(field);
			if (E != 0) {
				C++;
				row_score += calcChainScore(E, C);
			}
		}

		if (isDead(field)) {
			return null;
		} else {
			boolean small_fire = input.Th < row_score;
			return new Evaluater(field, small_fire, C, max_haight, x, r, C,
					input.P, calcTurnScore(row_score, Fc));
		}
	}

	// 引数の方向に消せるブロックがないか判定
	public int splash(int[][] field, boolean[][] erase_field, int i, int j,
			int[] vector, int E) {
		int sum = 0;
		ArrayList<Point> through_path = new ArrayList<Point>();

		through_path.add(new Point(j, i));
		sum += field[i][j];
		while (0 < i + vector[0] && i + vector[0] < input.H + input.T
				&& 0 < j + vector[1]
				&& j + vector[1] < input.W + (input.T - 1) * 2) {
			i += vector[0];
			j += vector[1];
			through_path.add(new Point(j, i));
			sum += field[i][j];
			if (field[i][j] == 0) {
				// 連続しているブロックの和がSに満たない
				break;
			} else if (input.S < sum) {
				// 連続しているブロックの和がSを超えた
				break;
			} else if (input.S == sum) {
				// 連続しているブロックの和がSと一致した
				E += through_path.size();
				for (Point point : through_path) {
					erase_field[point.y][point.x] = true;
				}
				break;
			} else {
				// 消去する合計数に至らなかったらもう１つ先へ
			}
		}

		return E;
	}

	// チェインのスコア計算
	public long calcChainScore(int E, int C) {
		return (long) Math.pow(2, Math.min(E / 3, input.P))
				* Math.max(1, E / 3 - input.P + 1) * C;
	}

	// ターンのスコア計算
	public long calcTurnScore(long row_score, int Fc) {
		return row_score * (Fc + 1);
	}

	// fieldで落下させる
	public boolean drop(int[][] field) {
		boolean is_droped = false;
		for (int i = input.H + input.T - 2; 0 <= i; i--) {
			for (int j = input.T - 1; j < input.W + input.T - 1; j++) {
				// 空間は無視
				if (field[i][j] == 0) {
					continue;
				}
				// 落下可能
				if (field[i][j] != 0 && field[i + 1][j] == 0) {
					// 落下地点まで探索
					int dh;
					for (dh = 1; i + dh < input.H + input.T; dh++) {
						// ブロックにぶつかるまで落ちる
						if (field[i + dh][j] != 0) {
							dh--;
							break;
						}
						// 底まで落ちる
						if (i + dh == input.H + input.T - 1) {
							break;
						}
					}
					// 落下地点に移動
					field[i + dh][j] = field[i][j];
					field[i][j] = 0;
					is_droped = true;
				}
			}
		}
		return is_droped;
	}

	// 枠外
	boolean isOutOfBounds(int[][] field) {
		for (int i = 0; i < field.length; i++) {
			for (int j = input.T - 2; 0 <= j; j--) {
				if (field[i][j] != 0) {
					return true;
				}
			}
			for (int j = input.T - 1 + input.W; j < input.T - 1 + input.W
					+ input.T - 1; j++) {
				if (field[i][j] != 0) {
					return true;
				}
			}
		}

		return false;
	}

	// 死亡判定
	boolean isDead(int[][] field) {
		for (int i = input.T - 1; 0 <= i; i--) {
			for (int j = input.T - 1; j < field[0].length - input.T + 1; j++) {
				if (field[i][j] != 0) {
					return true;
				}
			}
		}

		return false;
	}

	// パックを回転する
	public int[][] rotate(int[][] pack, int r) {
		int[][] rotated_pack = new int[input.T][input.T];

		if (r == 0) {
			// 0
			for (int i = 0; i < input.T; i++) {
				for (int j = 0; j < input.T; j++) {
					rotated_pack[i][j] = pack[i][j];
				}
			}
		} else if (r == 1) {
			// 90
			for (int i = 0; i < input.T; i++) {
				for (int j = 0; j < input.T; j++) {
					rotated_pack[j][input.T - 1 - i] = pack[i][j];
				}
			}
		} else if (r == 2) {
			// 180
			for (int i = 0; i < input.T; i++) {
				for (int j = 0; j < input.T; j++) {
					rotated_pack[input.T - 1 - i][input.T - 1 - j] = pack[i][j];
				}
			}
		} else if (r == 3) {
			// 270
			for (int i = 0; i < input.T; i++) {
				for (int j = 0; j < input.T; j++) {
					rotated_pack[input.T - 1 - j][i] = pack[i][j];
				}
			}
		}

		return rotated_pack;
	}

}
