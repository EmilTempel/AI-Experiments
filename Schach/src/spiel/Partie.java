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
					player[i] = new Mensch(fenster,player[i].getName());
					System.out.println("mensch");
				}
			}

			farbe = Farbe.WEISS;
		}
	}

	public double play(long max_züge, Brett...b) {
		verlauf = new ArrayList<Brett>();

		brett = b.length == 0 ? new Brett() : b [0];

		int p = Math.random() > 0.5 ? 0 : 1;
		

		for (int i = 0; i < max_züge; i++) {
			if (watch)
				img = brett.asBild(farbe);

			verlauf.add(new Brett(brett));

			double erg = brett.isOver(Farbe.values()[1-p]);
			if(erg == -1) {
				Spielzug zug = player[(p + i) % 2].ziehe(brett, Farbe.values()[i % 2]);
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
		Partie p = new Partie(new Mensch("P1"),new OmegaStop(4,46796.63829809371, 1482.7546966071022, 11458.265730802192, -7239.829211886708, -7386.140998964242, 8423.572140880713), true);

		
		for (int i = 0; i < 100; i++) {
			double score = p.play(250);
			System.out.println(score != 0.5 ? p.player[(int)score].getName() : "Unentschieden");
		}

	}
}
