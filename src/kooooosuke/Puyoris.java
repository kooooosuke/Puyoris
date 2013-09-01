package kooooosuke;

public class Puyoris {

	public static void main(String[] args) {
		Reader reader = new Reader();
		Input input = reader.read();

		AI ai = new AI(input);
		Output[] output1 = ai.think(-1);
//		ai = new AI(input);
//		Output[] output2 = ai.think(output1[output1.length - 1].score);

		long score1 = output1[output1.length - 1].score;
//		long score2 = output2[output2.length - 1].score;

//		Writer writer = new Writer(input, score1 < score2 ? output2 : output1);
		 Writer writer = new Writer(input, output1);
		writer.write();
		writer.makeLog(score1, score1);
	}

}
