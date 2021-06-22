package spiel.er;

import spiel.Brett;
import spiel.Figur.Farbe;
import spiel.Spielzug;

public abstract class Player {
	String name;

	public abstract Spielzug ziehe(Brett brett, Farbe farbe);

	public String getName() {
		if (name == null)
			name = this.toString();
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
