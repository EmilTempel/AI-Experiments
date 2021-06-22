package AI;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import spiel.Figur.Art;
import spiel.Figur.Farbe;
import spiel.Partie;

public class OmegaStop extends MiniMax {

	public OmegaStop(int depth, double... weights) {
		super(depth, new Bewerter[] {
				brett -> brett.giballeZüge(Farbe.WEISS).size() - brett.giballeZüge(Farbe.SCHWARZ).size(),
				brett -> brett.score(Farbe.WEISS) - brett.score(Farbe.SCHWARZ),
				brett -> brett.AnzahlZüge(Farbe.WEISS, Art.SPRINGER) - brett.AnzahlZüge(Farbe.SCHWARZ, Art.SPRINGER),
				brett -> brett.AnzahlZüge(Farbe.WEISS, Art.LAEUFER) - brett.AnzahlZüge(Farbe.SCHWARZ, Art.LAEUFER),
				brett -> brett.AnzahlZüge(Farbe.WEISS, Art.TURM) - brett.AnzahlZüge(Farbe.SCHWARZ, Art.TURM),
				brett -> brett.AnzahlZüge(Farbe.WEISS, Art.DAME) - brett.AnzahlZüge(Farbe.SCHWARZ, Art.DAME) },
				weights);

	}

	public OmegaStop(int depth, double a) {
		this(depth);
		randomizeWeights(a);
	}

	private void randomizeWeights(double a) {
		for (int i = 0; i < weights.length; i++) {
			weights[i] = (Math.random() * 2 * a) - a;
		}
	}

	public void mutate(OmegaStop d, double mutation) {
		for (int i = 0; i < weights.length; i++) {
			double dist = weights[i] - d.weights[i];

			weights[i] = (weights[i] + dist / 2) + ((Math.random() * 2) - 1) * dist * mutation;
		}
	}

	public static void train(int n, int depth, double a, double mutation) {
		Map<OmegaStop, Double> players = new HashMap<OmegaStop, Double>();

		for (int i = 0; i < n; i++) {
			players.put(new OmegaStop(depth, a), 0.0);
		}

		for (int epoch = 0; true; epoch++) {

			for (int i = 0; i < players.size(); i++) {
				OmegaStop k = (OmegaStop) players.keySet().toArray()[i];
				Double v = (Double) players.values().toArray()[i];
				for (int j = i; j < players.size(); j++) {
					OmegaStop k2 = (OmegaStop) players.keySet().toArray()[j];
					Double v2 = (Double) players.values().toArray()[j];
					Partie partie = new Partie(k2, k, false);
					double erg = partie.play(150);
					players.put(k, v + erg);
					players.put(k2, v2 + Math.abs(1 - erg));
					System.out.print("|");

				}
				System.out.println();
			}

			players.forEach((k, v) -> {
				System.out.println(v);
			});

			ArrayList<Entry<OmegaStop, Double>> list = new ArrayList<Entry<OmegaStop, Double>>();
			players.entrySet().forEach(e -> list.add(e));

			list.sort(new Comparator<Entry<OmegaStop, Double>>() {
				public int compare(Entry<OmegaStop, Double> o1, Entry<OmegaStop, Double> o2) {
					return (int) (o2.getValue() * 2 - o1.getValue() * 2);
				}
			});

			System.out.println(list.get(0).getValue());

			for (int i = 0; i < n; i++) {
				Entry<OmegaStop, Double> e = list.get(i);
				e.setValue(0.0);
				if (Math.random() > selection(i, n)) {
					OmegaStop o = e.getKey();

					for (int j = i - 1; Math.random() > selection(j, n); j--) {
						o.mutate(list.get(j).getKey(), mutation);
					}
				}

			}

			list.get(0).getKey().save("OmegaStops_mk1", "OS", false, true);
		}

	}

	public static double selection(double x, double n) {
		return (n - x) / n;
	}

	public void save(String folder, String path, boolean folderize, boolean numerize) {
		FileOutputStream fos = null;

		for (int i = 0; true; i++) {
			File f = new File(folder + i);
			if (!f.exists()) {
				folder += folderize ? i : i - 1;
				break;
			}
		}

		File f = new File(folder);

		if (!f.exists()) {
			f.mkdir();
		}

		System.out.println(folder + "  " + f.isDirectory());

		for (int i = 0; true; i++) {
			if (!new File(folder + "/" + path + i).exists()) {
				try {
					fos = new FileOutputStream(folder + "/" + path + (numerize ? i : ""));
					fos.write((weights.length + "\n").getBytes());
					fos.write((Arrays.toString(weights) + "\n").getBytes());
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	public static void main(String[] a) {
		train(10, 3, 100, 0.1);
	}

}
