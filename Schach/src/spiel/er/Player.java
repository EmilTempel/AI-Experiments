package spiel.er;

import spiel.Brett;
import spiel.Figur.Farbe;
import spiel.Spielzug;

public abstract class Player {
	public abstract Spielzug ziehe(Brett brett, Farbe farbe);
}
