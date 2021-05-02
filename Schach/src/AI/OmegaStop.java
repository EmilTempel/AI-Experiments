package AI;

import java.util.ArrayList;

import spiel.Brett;
import spiel.Figur.Art;
import spiel.Figur.Farbe;
import spiel.Spielzug;

public class OmegaStop extends MiniMax {

	public OmegaStop(int depth, double... weights) {
		super(depth,
				new Bewerter[] {
						brett -> brett.giballeZ�ge(Farbe.WEISS).size() - brett.giballeZ�ge(Farbe.SCHWARZ).size(),
						brett -> brett.score(Farbe.WEISS) - brett.score(Farbe.SCHWARZ),
						brett -> brett.AnzahlZ�ge(Farbe.WEISS, Art.SPRINGER) - brett.AnzahlZ�ge(Farbe.SCHWARZ, Art.SPRINGER),
						brett -> brett.AnzahlZ�ge(Farbe.WEISS, Art.LAEUFER) - brett.AnzahlZ�ge(Farbe.SCHWARZ, Art.LAEUFER),
						brett -> brett.AnzahlZ�ge(Farbe.WEISS, Art.TURM) - brett.AnzahlZ�ge(Farbe.SCHWARZ, Art.TURM),
						brett -> brett.AnzahlZ�ge(Farbe.WEISS, Art.DAME) - brett.AnzahlZ�ge(Farbe.SCHWARZ, Art.DAME)
				}, weights);

	}

}
