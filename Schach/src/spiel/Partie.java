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
import spiel.er.Rando;

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

	public int play(int max_züge) {
		verlauf = new ArrayList<Brett>();

		brett = new Brett();

		int p = Math.random() > 0.5 ? 0 : 1;

		for (int i = 0; i < max_züge; i++) {
			if (watch)
				img = brett.asBild(farbe);

			verlauf.add(new Brett(brett));

			Spielzug zug = player[(p + i) % 2].ziehe(brett, Farbe.values()[i % 2]);

			if (zug == null) {
				if (brett.isSchach(Farbe.values()[i % 2])) {
					return Math.abs(Math.abs((p + i) % 2) - 1);
				}
			} else if (brett.isRemis()) {
				return -1;
			} else {
				brett.ziehe(zug);
			}
			
		}
		return -1;
	}

	public Brett[] getSpiel() {
		Brett[] spiel = new Brett[verlauf.size()];
		for (int i = 0; i < verlauf.size(); i++) {
			spiel[i] = verlauf.get(i);
		}
		return spiel;
	}

	public static void main(String[] args) {
		Partie p = new Partie(new OmegaStop(4,10,1,3,4,2,1),new Mensch(), true);

		int score = 0;
		
		for (int i = 0; i < 100; i++) {
			score += p.play(250) == 0? 1 : 0 ;
			System.out.println(score);
		}

		System.out.println(score);
	}
}
