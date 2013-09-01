package kooooosuke;

import java.util.ArrayList;

public class Evaluater {

	boolean small_fire;
	int chain;
	int waight;
	int haight;
	int ojama;
	int x;
	int r;
	int C;
	int P;
	int[][] field;
	long score;
	int nextChain;
	long nextScore;
	int gravity;
	boolean target_completed = false;

	public Evaluater(int[][] field, boolean small_fire, int chain,
			int max_waight, int x, int r, int C, int P, long score) {
		this.field = field;
		this.small_fire = small_fire;
		this.chain = chain;
		this.waight = max_waight;
		this.x = x;
		this.r = r;
		this.C = C;
		this.P = P;
		this.score = score;
	}

	public Evaluater judge(Evaluater best_eval, Evaluater eval,
			ArrayList<Target> target, int n, long past_score, long best_score,
			int H, int W, boolean wait) {
		// 大発火━━━━━━(゜∀゜)━━━━━━ !!!!!
		if (best_score < eval.score - past_score
				|| best_score < best_eval.score - past_score) {
			return eval.score < best_eval.score ? best_eval : eval;
		}

		// 終盤回収(´･ω･`)
		// if (998 < n) {
		// return eval.chain < best_eval.chain ? best_eval : eval;
		// }

		// タワー増築(´･ω･`)
		// できあがったのはこわさない
		int noise_eval = 0;
		int noise_best_eval = 0;
		int c;
		for (c = 0; c < target.size() / H; c++) {
			boolean eval_column_complete = true;
			boolean best_eval_column_complete = true;
			for (int j = 0; j < H; j++) {
				if (eval.field[target.get(c * H + j).i][target.get(c * H + j).j] != target
						.get(c * H + j).num) {
					eval_column_complete = false;
					if (eval.field[target.get(c * H + j).i][target.get(c * H
							+ j).j] != 0) {
						noise_eval++;
					}
				}
				if (best_eval.field[target.get(c * H + j).i][target.get(c * H
						+ j).j] != target.get(c * H + j).num) {
					best_eval_column_complete = false;
					if (best_eval.field[target.get(c * H + j).i][target.get(c
							* H + j).j] != 0) {
						noise_best_eval++;
					}
				}
			}
			if (eval_column_complete && best_eval_column_complete) {
				continue;
			} else if (eval_column_complete) {
				return eval;
			} else if (best_eval_column_complete) {
				return best_eval;
			} else {
				break;
			}
		}

		// 新しい列のゴミを少なくする
		if (noise_best_eval != 0 || noise_eval != 0) {
			return noise_best_eval < noise_eval ? best_eval : eval;
		}

		// きれいになった列にゴミを入れない
		for (Target tar : target) {
			if (eval.field[tar.i][tar.j] != tar.num
					&& eval.field[tar.i][tar.j] != 0) {
				return best_eval;
			}
		}
		for (Target tar : target) {
			if (best_eval.field[tar.i][tar.j] != tar.num
					&& best_eval.field[tar.i][tar.j] != 0) {
				return eval;
			}
		}

		// つみあげる
		for (Target tar : target) {
			// 番号指定
			if (best_eval.field[tar.i][tar.j] == tar.num
					&& eval.field[tar.i][tar.j] == tar.num) {
			} else if (best_eval.field[tar.i][tar.j] == tar.num) {
				return best_eval;
			} else if (eval.field[tar.i][tar.j] == tar.num) {
				return eval;
			} else {
				break;
			}
		}

		// 小発火(゜∀゜)
		if (best_eval.small_fire && eval.small_fire) {
			best_eval.calcWeight();
			eval.calcWeight();
			return best_eval.waight > eval.waight ? best_eval : eval;
		} else if (best_eval.small_fire) {
			return best_eval;
		} else if (eval.small_fire) {
			return eval;
		}

		// チェイン溜める(´･ω･`)
		// if (best_eval.nextChain != -1) {
		// if (best_eval.nextChain < eval.nextChain) {
		// return eval;
		// } else {
		// return best_eval;
		// }
		// }

		// large以外なくしたほうがいいかも
		// 死にそう(´；ω；`)
		best_eval.calcHeight();
		eval.calcHeight();
		if (field.length - best_eval.haight < 9 + 2
				&& field.length - eval.haight >= 9 + 2) {
			return eval;
		} else if (field.length - best_eval.haight >= 9 + 2
				&& field.length - eval.haight < 9 + 2) {
			return best_eval;
		}

		// タワー付近掃除
		if (wait) {
			int EDGE = 1;
			int height = 5;
			int b2 = best_eval.getHeight(2 + EDGE);
			int b4 = best_eval.getHeight(4 + EDGE);
			int b5 = best_eval.getHeight(5 + EDGE);
			int b6 = best_eval.getHeight(6 + EDGE);
			int b7 = best_eval.getHeight(7 + EDGE);
			int e2 = eval.getHeight(2 + EDGE);
			int e4 = eval.getHeight(4 + EDGE);
			int e5 = eval.getHeight(5 + EDGE);
			int e6 = eval.getHeight(6 + EDGE);
			int e7 = eval.getHeight(7 + EDGE);
			if (b4 > e4 && field.length - 5 > e4) {
				return best_eval;
			} else if (b4 < e4 && field.length - height > b4) {
				return eval;
			} else if (b5 > e5 && field.length - height > e5 && 0 < c) {
				return best_eval;
			} else if (b5 < e5 && field.length - height > b5 && 0 < c) {
				return eval;
			} else if (b6 > e6 && field.length - height > e6 && 1 < c) {
				return best_eval;
			} else if (b6 < e6 && field.length - height > b6 && 1 < c) {
				return eval;
			} else if (b7 > e7 && field.length - height > e7 && 2 < c) {
				return best_eval;
			} else if (b7 < e7 && field.length - height > b7 && 2 < c) {
				return eval;
			} else if (b2 > e2 && field.length - height > e2) {
				return best_eval;
			} else if (b2 < e2 && field.length - height > b2) {
				return eval;
			}

			// 右に寄せる
			// if (best_eval.x > eval.x) {
			// return best_eval;
			// } else if (best_eval.x < eval.x) {
			// return eval;
			// }
		}

		if (wait) {
			// スコア溜めてFc溜める
			if (best_eval.nextScore != -1) {
				if (best_eval.nextScore < eval.nextScore) {
					return eval;
				} else if (best_eval.nextScore > eval.nextScore) {
					return best_eval;
				}
			}

			// チェインを伸ばす(`･ω･´)
			if (best_eval.nextChain < eval.nextChain) {
				return eval;
			} else if (best_eval.nextChain > eval.nextChain) {
				return best_eval;
			}
		}

		// ブロック溜める(`･ω･´)
		best_eval.calcWeight();
		eval.calcWeight();
		if (best_eval.waight > eval.waight) {
			return best_eval;
		} else if (best_eval.waight < eval.waight) {
			return eval;
		}

		// 平坦にならす(´･ω･`)
		eval.calcGravity();
		best_eval.calcGravity();
		if (best_eval.gravity < eval.gravity) {
			return best_eval;
		} else if (best_eval.gravity > eval.gravity) {
			return eval;
		}

		return best_eval;
	}

	private int getHeight(int j) {
		int i;
		for (i = field.length - 1; 0 < i; i--) {
			if (field[i][j] == 0) {
				break;
			}
		}
		return i;
	}

	// public Evaluater judge1(Evaluater best_eval, Evaluater eval) {
	// ブロック溜める
	// if (eval.C < best_eval.C) {
	// return eval;
	// } else if (eval.C > best_eval.C) {
	// return best_eval;
	// }
	// best_eval.calcWeight();
	// eval.calcWeight();
	// if (eval.waight > best_eval.waight) {
	// return eval;
	// } else {
	// return best_eval;
	// }
	// }

	private void calcWeight() {
		waight = 0;
		for (int i = 3; i < field.length; i++) {
			for (int j = 3; j < field[0].length - 3; j++) {
				if (field[i][j] != 0) {
					waight++;
				}
			}
		}
	}

	private void calcGravity() {
		gravity = 0;
		for (int i = 3; i < field.length; i++) {
			for (int j = 3; j < field[0].length - 3; j++) {
				if (field[i][j] != 0) {
					gravity += field.length - i;
				}
			}
		}
	}

	// private void calcOjama() {
	// ojama = 0;
	// for (int i = 0; i < field.length; i++) {
	// for (int j = 0; j < field[0].length; j++) {
	// if (field[i][j] == 31) {
	// ojama++;
	// }
	// }
	// }
	// }

	private void calcHeight() {
		haight = 1;
		for (int i = 3; i < field.length; i++) {
			for (int j = 3 + 4; j < field[0].length - 3; j++) {
				if (field[i][j] != 0) {
					haight = field.length - i;
					// System.out.println("@@@@@@@@@@" + haight);
					return;
				}
			}
		}
	}

}
