package spiel;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Figur {

	Art art;
	Farbe farbe;
	boolean moved;

	BufferedImage bild;

	public Figur(Art art, Farbe farbe) {
		this.art = art;
		this.farbe = farbe;

		moved = false;
		
		bild = getBild();
	}

	public Figur(Figur f) {
		this.art = f.art;
		this.farbe = f.farbe;

		moved = f.moved;
		
		bild = f.asBild();
	}

	public Art getArt() {
		return art;
	}

	public BufferedImage asBild() {
		return bild;
	}
	
	public BufferedImage getBild() {
		BufferedImage figuren = null;
		try {
			figuren = ImageIO.read(new File("schachfiguren.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return figuren.getSubimage(art.ordinal() * 50, farbe.ordinal() * 50, 50, 50);
	}
	

	public enum Art {
		KOENIG(true, 1, 0, new Zug[] { new Zug(-1, 1), new Zug(0, 1) }),
		DAME(true, 7, 9, new Zug[] { new Zug(-1, 1), new Zug(0, 1) }), TURM(true, 7, 5, new Zug[] { new Zug(0, 1) }),
		LAEUFER(true, 7, 3, new Zug[] { new Zug(-1, 1) }),
		SPRINGER(true, 1, 3, new Zug[] { new Zug(-2, 1), new Zug(-1, 2) }),
		BAUER(false, 1, 1, new Zug[] { new Zug(-1, 1, false), new Zug(0, 1, true), new Zug(1, 1, false) });

		Zug[] züge;
		int länge, wert;

		Art(boolean rotation, int länge, int wert, Zug[] Züge_Vor) {
			this.länge = länge;
			this.wert = wert;

			if (rotation) {
				züge = new Zug[Züge_Vor.length * 4];

				for (int i = 0; i < Züge_Vor.length; i++) {
					züge[i * 4] = Züge_Vor[i];

					for (int j = 1; j < 4; j++) {
						züge[i * 4 + j] = züge[i * 4 + j - 1].rotiere90();
					}

				}

			} else {
				züge = Züge_Vor;
			}

		}
	}

	public enum Farbe {
		WEISS(new Color(200, 200, 200)), SCHWARZ(new Color(80, 80, 80));

		Color hintergrund;

		Farbe(Color hintergrund) {
			this.hintergrund = hintergrund;
		}

		public Color getHintergrund() {
			return hintergrund;

		}
		
		public static Farbe get(int f) {
			return f == 0 ? WEISS : SCHWARZ;
		}
		
		public Farbe getOther() {
			return this == WEISS ? SCHWARZ : WEISS;
		}
	}

	public static void main(String[] args) {

		for (int i = 0; i < 6; i++) {
			Figur f = new Figur(Art.values()[i], Farbe.WEISS);
			Zug[] züge = f.art.züge;

			for (int j = 0; j < züge.length; j++) {
				System.out.println(f.art.toString() + "  ZUG Nummer" + j + ":  " + züge[j].y + "  ,  " + züge[j].x);
			}
		}

	}
}
