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
						brett -> brett.giballeZüge(Farbe.WEISS).size() - brett.giballeZüge(Farbe.SCHWARZ).size(),
						brett -> brett.score(Farbe.WEISS) - brett.score(Farbe.SCHWARZ),
						brett -> brett.AnzahlZüge(Farbe.WEISS, Art.SPRINGER) - brett.AnzahlZüge(Farbe.SCHWARZ, Art.SPRINGER),
						brett -> brett.AnzahlZüge(Farbe.WEISS, Art.LAEUFER) - brett.AnzahlZüge(Farbe.SCHWARZ, Art.LAEUFER),
						brett -> brett.AnzahlZüge(Farbe.WEISS, Art.TURM) - brett.AnzahlZüge(Farbe.SCHWARZ, Art.TURM),
						brett -> brett.AnzahlZüge(Farbe.WEISS, Art.DAME) - brett.AnzahlZüge(Farbe.SCHWARZ, Art.DAME)
				}, weights);

	}

}
