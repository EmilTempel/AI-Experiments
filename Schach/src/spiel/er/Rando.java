package spiel.er;

import java.util.ArrayList;

import spiel.Brett;
import spiel.Figur.Farbe;
import spiel.Spielzug;

public class Rando extends Player {
	public Spielzug ziehe(Brett brett, Farbe farbe) {
		ArrayList<Spielzug> z�ge = brett.giballeZ�ge(farbe);
		if (z�ge.size() != 0) {
			return z�ge.get((int) (Math.random() * z�ge.size()));
		} else {
			
			return null;
		}
	}

}
