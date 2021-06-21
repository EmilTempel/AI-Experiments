package spiel;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import AI.OmegaStop;
import Gui.Fenster;
import Gui.GraphicsObject;
import Gui.Mensch;
import spiel.Figur.Farbe;
import spiel.er.Player;

public class Partie {

	Player[] player;
	Brett brett;
	ArrayList<Brett> verlauf;

	boolean watch;
	Farbe farbe;
	BufferedImage img;

	public Partie(Player eins, Player zwei, boolean watch) {

		this.player = new Player[] { eins, zwei };

		this.watch = watch;

		if (watch || eins instanceof Mensch || zwei instanceof Mensch) {

			Fenster fenster = new Fenster(2, true);
			fenster.addGobj(new GraphicsObject() {
				public void paint(Graphics g) {
					if (img != null)
						g.drawImage(img, 0, 0, fenster.getWidth(), fenster.getHeight(), null);
				}

			});

			for (int i = 0; i < 2; i++) {
				if (player[i] instanceof Mensch) {
					player[i] = new Mensch(fenster);
					System.out.println("mensch");
				}
			}

			farbe = Farbe.WEISS;
		}
	}

	public double play(long max_z�ge, Brett...b) {
		verlauf = new ArrayList<Brett>();

		brett = b.length == 0 ? new Brett() : b [0];

		int p = Math.random() > 0.5 ? 0 : 1;

		for (int i = 0; i < max_z�ge; i++) {
			if (watch)
				img = brett.asBild(farbe);

			verlauf.add(new Brett(brett));

			Spielzug zug = player[(p + i) % 2].ziehe(brett, Farbe.values()[i % 2]);

			double erg = brett.isOver(Farbe.values()[i % 2]);
			if(erg == -1) {
				brett.ziehe(zug);
			}else {
				return erg;
			}
			
		}
		return 0.5;
	}

	public Brett[] getSpiel() {
		Brett[] spiel = new Brett[verlauf.size()];
		for (int i = 0; i < verlauf.size(); i++) {
			spiel[i] = verlauf.get(i);
		}
		return spiel;
	}

	public static void main(String[] args) {
		Partie p = new Partie(new Mensch(),new OmegaStop(4), true);

		int score = 0;
		
		for (int i = 0; i < 100; i++) {
			score += p.play(250) == 0? 1 : 0;
			System.out.println(score);
		}

		System.out.println(score);
	}
}
