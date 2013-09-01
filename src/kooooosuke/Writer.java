package kooooosuke;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Writer {
	Input input;
	Output[] outputs;

	public Writer(Input input, Output[] outputs) {
		this.input = input;
		this.outputs = outputs;
	}

	// パックの設置情報を出力
	public void write() {
		for (Output output : outputs) {
			System.out.println(output.x + " " + output.r);
		}
	}

	// 結果ログ出力
	public void makeLog(long score1, long score2) {
		File file = null;
		FileWriter filewriter = null;
		try {
			// ファイル名を日時に
			file = new File("/Users/suehiroakihisa/Desktop/Puyoris_Log/"
					+ new SimpleDateFormat("MMdd_HHmm").format(new Date())
					+ ".txt");
			filewriter = new FileWriter(file);

			// スコア
			filewriter.write("SCORE1: "
					+ new DecimalFormat("0.00E0").format(score1) + "  SCORE2: "
					+ new DecimalFormat("0.00E0").format(score2) + "\n");

			// 毎ターン表示
			for (int i = 0; i < input.N; i++) {
				filewriter.write("TURN: " + (i + 1) + "  Fc: " + outputs[i].Fc
						+ "  Score: " + outputs[i].score + "\n");
			}

			// filewriter.write("######## TURN " + (i + 1) + " ########\n");
			//
			// filewriter.write("■ pack ■\n");
			// for (int[] pack : input.packs[i]) {
			// for (int p : pack) {
			// filewriter.write((p == 0 ? "_" : p) + " ");
			// }
			// filewriter.write("\n");
			// }
			//
			// filewriter.write("■ output ■\n x=" + outputs[i].x + "\n r="
			// + outputs[i].r + "\n");
			//
			// filewriter.write("■ field ■\n");
			// if (0 < i) {
			// for (int[] fie : outputs[i].field) {
			// for (int f : fie) {
			// filewriter.write((f == 0 ? "_" : f) + " ");
			// }
			// filewriter.write("\n");
			// }
			// }
			// filewriter.write("■ score ■\n" + outputs[i].score + "\n\n");
			// }
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
}
