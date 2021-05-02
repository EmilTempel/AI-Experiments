package AI;

import java.util.Arrays;
import java.util.Comparator;

import Gui.Mensch;
import spiel.Partie;

public class Trainer {

	SteckerlFisch[] trainee;
	int max_züge, epochs;
	double eta;

	public Trainer(int num, int depth, int max_züge, double eta, int epochs) {
		trainee = new SteckerlFisch[num];
		for (int i = 0; i < num; i++) {
			trainee[i] = new SteckerlFisch(depth);
		}
		this.max_züge = max_züge;
		this.eta = eta;
		this.epochs = epochs;
	}

	public void train(double selection_rate, int steps, String folder, String path) {
		for (int c = 0; true; c++) {
			int top = (int) (trainee.length * selection_rate);
			System.out.println("Starte Turnier");
			SteckerlFisch[] best = tournament(top);

			for (int i = 0; i < top; i++) {
				trainee[i] = best[i];
			}

			best[0].save(folder, path, c == 0, true);

			for (int i = top; i < trainee.length; i++) {

				SteckerlFisch[] neu = new SteckerlFisch[2];
				for (int j = 0; j < 2; j++) {

					neu[j] = new SteckerlFisch(best[(int) (Math.random() * top)]);
				}

				trainee[i] = train(steps, neu);
				System.out.println("Gerade bei Schritt: " + i);
			}

			System.out.println("starte Trainingslager: ");

			for (int i = 0; i < trainee.length; i++) {
				for (int k = 0; k < steps; k++) {
					train(10, trainee[i], trainee[(int) (Math.random() * top)]);
				}
			}

			System.out.println("Step " + c + " completed");
			c++;
		}
	}

	public SteckerlFisch train(int steps, SteckerlFisch... p) {
		int w = Duell(p[0], p[1], 10);
		Partie partie = new Partie(p[0], p[1], false);
		for (int i = 0; i < steps; i++) {
			partie.play(max_züge);
			p[Math.abs(w - 1)].train(partie.getSpiel(), p[w], eta, epochs);
			System.out.println("/");
		}
		return p[Math.abs(w - 1)];
	}

	public int Duell(SteckerlFisch p1, SteckerlFisch p2, int games) {
		Partie partie = new Partie(p1, p2, false);
		int[] scores = new int[2];
		for (int i = 0; i < games; i++) {
			int s = partie.play(max_züge);
			scores[0] += ((s == 0) ? 1 : 0);
			scores[1] += ((s == 1) ? 1 : 0);
		}

		return (scores[0] > scores[1]) ? 0 : 1;
	}

	public SteckerlFisch[] tournament(int top) {
		int[] scores = new int[trainee.length];

		for (int i = 0; i < trainee.length; i++) {
			for (int j = i + 1; j < trainee.length; j++) {
				Partie p = new Partie(trainee[i], trainee[j], false);
				int s = p.play(max_züge);
				scores[i] += ((s == 0) ? 1 : 0);
				scores[j] += ((s == 1) ? 1 : 0);
				System.out.print("|");
			}
			System.out.println("  gerade bei Spieler: " + i + "   ");
		}
		int[] sorted = sortIndeces(scores);

		SteckerlFisch[] sf = new SteckerlFisch[top];
		for (int i = 0; i < top; i++) {
			sf[i] = trainee[sorted[trainee.length - i - 1]];
		}

		System.out.println("Turnier beendet");
		System.out.println("Der beste Score war: " + scores[trainee.length - 1]);
		return sf;

	}

	public int max(int[] in) {
		int max = 0;
		for (int i = 1; i < in.length; i++) {
			if (in[i] > in[max]) {
				max = i;

			}

		}
		return max;
	}

	public int[] sortIndeces(int[] in) {
		Integer[] idx = new Integer[in.length];
		for (int i = 0; i < in.length; i++) {
			idx[i] = i;
		}

		Arrays.sort(idx, new Comparator<Integer>() {
			@Override
			public int compare(final Integer o1, final Integer o2) {
				return Integer.compare(in[o1], in[o2]);
			}
		});

		int[] r = new int[idx.length];
		for (int i = 0; i < idx.length; i++) {
			r[i] = idx[i];

		}

		return r;
	}

	public static void main(String[] args) {
		Trainer t = new Trainer(30, 2, 80, 0.001, 100);

		t.train(0.5, 10, "SteckerlFische_mk", "fisch");
	}
}
