package AI;

import java.util.ArrayList;

import AI.MiniMax.Bewerter;
import spiel.Brett;
import spiel.Figur.Farbe;
import spiel.er.Player;
import spiel.Spielzug;

public class MiniMax extends Player {

	Bewerter[] bewerter;

	double[] weights;

	int depth;

	public MiniMax(int depth, Bewerter[] bewerter, double[] weights) {
		this.bewerter = bewerter;
		this.weights = new double[bewerter.length];

		for (int i = 0; i < this.weights.length; i++) {
			this.weights[i] = i < weights.length ? weights[i] : 1;
		}

		this.depth = depth;
	}
	public Spielzug ziehe(Brett brett, Farbe farbe) {
		ArrayList<Spielzug> alle_züge = brett.giballeZüge(farbe);
		if (alle_züge.size() != 0) {

			Spielzug best = alle_züge.get((int) miniMax(brett, farbe, depth,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY));

			return best;
		} else {
			return null;
		}
	}
	
	public double bewerte(Brett brett) {
		double erg = 0;
		for (int i = 0; i < bewerter.length; i++) {
			erg += bewerter[i].bewerte(brett) * weights[i];
		}
		return erg;
	}

	private double miniMax(Brett brett, Farbe farbe, int depth, double alpha, double beta) {
		ArrayList<Spielzug> alle_züge = brett.giballeZüge(farbe);
		if (depth == 1) {
			return bewerte(brett);
		} else {
			if (alle_züge.size() != 0) {

				double[] bewertungen = new double[alle_züge.size()];

				for (int i = 0; i < alle_züge.size(); i++) {
					Brett b = new Brett(brett).ziehe(alle_züge.get(i));
					bewertungen[i] = miniMax(b, farbe.getOther(), depth - 1,alpha,beta);
					if(farbe == Farbe.WEISS) {
						alpha = bewertungen[i] > alpha ? bewertungen[i] : alpha;
						
					}else {
						beta = bewertungen[i] < beta ? bewertungen[i] : beta;
					}
					
					if(alpha >= beta) {
						break;
					}
				}
				if (depth == this.depth) {
					return max(bewertungen, farbe != Farbe.WEISS, true);
				} else {
					return max(bewertungen, farbe != Farbe.WEISS, false);
				}

			} else {
				return farbe == Farbe.WEISS ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
			}
		}

	}

	private static double max(double[] arr, boolean reverse, boolean index) {
		int idx = 0;

		for (int i = 1; i < arr.length; i++) {
			idx = !reverse ? arr[i] > arr[idx] ? i : idx : arr[i] < arr[idx] ? i : idx;

		}
		return index ? idx : arr[idx];
	}

	public interface Bewerter {
		abstract double bewerte(Brett b);
	}

	public static void main(String[] args) {
		double[] test = { 1, 2, 3, 4, 5, 6, 23, -1, -10000, 24, 12 };
		System.out.println("index " + max(test, false, true) + " : " + max(test, false, false));
	}
}
