package AI;

import java.util.ArrayList;

import spiel.Brett;
import spiel.Figur.Farbe;
import spiel.Spielzug;
import spiel.er.Player;

public class SteckerlFisch extends MiniMax {

	Network network;

	public SteckerlFisch(int depth, Network network) {
		super(depth, new Bewerter[] { b -> {
			double erg = 0;
			double[] out = network.calc(b.asArray());
			for (int i = 0; i < out.length; i++)
				erg += out[i];
			return erg;
		} }, new double[0]);
		
		this.network = network;
	}

	public SteckerlFisch(int depth) {
		this(depth,new Network(64, 64, 64, 1));
	}

	public SteckerlFisch(SteckerlFisch sf) {
		this (sf.depth,new Network(sf.network));
	}

	public SteckerlFisch(String path, int depth) {
		this(depth,new Network(path));
	}

	public void train(Brett[] spiel, SteckerlFisch fisch, double eta, int epochs) {
		ArrayList<double[]> in = new ArrayList<double[]>();

		for (int i = 0; i < spiel.length; i++) {
			Farbe f = Farbe.values()[i % 2];

			ArrayList<Spielzug> züge = spiel[i].giballeZüge(f);
			for (int j = 0; j < züge.size(); j++) {
				Brett b = new Brett(spiel[i]);
				b.ziehe(züge.get(j));
				in.add(spiel[i].asArray());
			}

		}

		double[][] input = new double[in.size()][];
		double[][] output = new double[in.size()][];

		for (int i = 0; i < input.length; i++) {
			input[i] = in.get(i);
			output[i] = fisch.network.calc(input[i]);
			System.out.print(i + "  ");
		}

		network.train(input, output, eta, epochs);

	}

	public void save(String folder, String path, boolean folderize, boolean numerize) {
		network.save(folder, path, folderize, numerize);
	}
}
