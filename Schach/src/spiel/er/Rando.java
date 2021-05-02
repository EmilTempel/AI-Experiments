package spiel.er;

import java.util.ArrayList;

import spiel.Brett;
import spiel.Figur.Farbe;
import spiel.Spielzug;

public class Rando extends Player {
	public Spielzug ziehe(Brett brett, Farbe farbe) {
		ArrayList<Spielzug> züge = brett.giballeZüge(farbe);
		if (züge.size() != 0) {
			return züge.get((int) (Math.random() * züge.size()));
		} else {
			
			return null;
		}
	}

}
