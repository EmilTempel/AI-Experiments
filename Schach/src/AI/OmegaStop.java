package AI;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

	public static void train(int n, int depth, double a) {
		Map<OmegaStop, Double> players = new HashMap<OmegaStop, Double>();
	
		for(int i = 0; i < n ; i++) {
			players.put(new OmegaStop(depth, a), 0.0);
		}
		
		for(int epoch = 0; epoch == 0; epoch++) {
			
			players.forEach((k,v) -> {
				players.forEach((k2,v2) -> {
					if(!k.equals(k2)) {
						Partie partie = new Partie(k2,k,false);
						double erg = partie.play(150);
						players.put(k,v + erg);
						players.put(k2,v2 + Math.abs(1-erg));
						System.out.println(erg);
					}
				});
				
			});
			
			players.forEach((k,v) -> {System.out.println(v);});
			
			
		}
	}
	
	public static void main (String[] a) {
		train(10,3,12);
	}

}
