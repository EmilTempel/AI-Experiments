package spiel;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import AI.OmegaStop;
import spiel.Figur.Art;
import spiel.Figur.Farbe;
import spiel.er.Rando;

public class Brett {

	Figur[][] feld;

	ArrayList<Spielzug>[] alle_züge = (ArrayList<Spielzug>[]) new ArrayList[2];

	public Brett() {
		
		loadFEN("2bqkbn1/2pppp2/np2N3/r3P1p1/p2N2B1/5Q2/PPPPKPP1/RNB2r2 1");
	}

	public Brett(Brett b) {
		feld = new Figur[8][8];

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (b.feld[i][j] != null) {
					feld[i][j] = new Figur(b.feld[i][j]);
				}

			}

		}

	}
	
	public void loadFEN(String FEN){
		feld = new Figur[8][8];
		
		String[] args = FEN.split(" ");
		String[] rows = args[0].split("/");
		
		for(int i = 0; i < 8; i++){
			int c= 0;
			for(int j = 0; j < 8; j++){
				char temp = rows[i].charAt(c);
				if(temp - '0' < 10){
					j += temp - '0';
				}else{
					Art a = Art.get(String.valueOf(temp).toLowerCase());
					
					if(a != null){
						feld[7-i][j] = new Figur(a, Character.isLowerCase(temp) ? Farbe.SCHWARZ: Farbe.WEISS);
					}
					
				}
				c++;
			}
		}
		
	}

	public Figur FigurAt(int x, int y) {
		if (x >= 0 && x < 8 && y >= 0 && y < 8) {
			return feld[y][x];
		} else {
			return null;
		}
	}

	public ArrayList<Spielzug> giballeZüge(Farbe farbe) {
		int f = farbe.ordinal();
		if (alle_züge[f] == null) {
			alle_züge[f] = alleZüge(farbe);
		}

		return alle_züge[f];
	}

	private ArrayList<Spielzug> alleZüge(Farbe farbe) {
		ArrayList<Spielzug> alle_züge = new ArrayList<Spielzug>();

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				if (feld[i][j] != null && feld[i][j].farbe == farbe) {
					Figur f = feld[i][j];
					alle_züge.addAll(giballeZüge(j, i, farbe, f.art, true));

				}

			}

		}

		return alle_züge;
	}

	public ArrayList<Spielzug> giballeZüge(int x, int y, Farbe farbe, Art art, boolean with_schach) {
		ArrayList<Spielzug> züge = sonderzüge(x, y, with_schach);

		for (int i = 0; i < art.züge.length; i++) {

			for (int j = 1; j < art.länge + 1; j++) {

				Spielzug zug = new Spielzug(x, y,
						((farbe.ordinal() == 0) ? art.züge[i] : art.züge[i].rotiere180()).mult(j));
				if (isPossible(zug, with_schach)) {
					züge.add(zug);
					if (isSchlag(zug)) {
						break;
					}
				}
				if(isBreak(zug)) {
					break;
				}
				
			}

		}

		return züge;
	}

	public ArrayList<Spielzug> sonderzüge(int x, int y, boolean with_schach) {
		ArrayList<Spielzug> züge = new ArrayList<Spielzug>();
		Figur f = feld[y][x];

		if (!f.moved) {
			switch (f.art) {
			case KOENIG:
				for (int i = 0; i < 2; i++) {

					int incr = (x < i * 7) ? 1 : -1;

					for (int j = x + incr; j != i * 7 + incr; j += incr) {

						if (feld[y][j] != null) {
							if (feld[y][j].art == Art.TURM && !feld[y][j].moved) {
								züge.add(new Spielzug(x, y, new Zug(2 * incr, 0)));
							} else {
								break;
							}

						}

					}

				}
				break;

			case BAUER:
				if (feld[y + (f.farbe == Farbe.WEISS ? 1 : -1)][x] == null) {
					züge.add(new Spielzug(x, y,
							(f.farbe.ordinal() == 0) ? new Zug(0, 2, true) : new Zug(0, 2, true).rotiere180()));
				}
				break;

			default:
				break;

			}
		}

		for (int i = 0; i < züge.size(); i++) {
			if (!isPossible(züge.get(i), with_schach)) {
				züge.remove(i);
			}
		}

		return züge;
	}

	public int score(Farbe farbe) {
		int score = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Figur f = feld[i][j];
				if (f != null && f.farbe == farbe) {
					score += f.art.wert;
				}
			}

		}
		return score;
	}

	public int AnzahlZüge(Farbe farbe, Art art) {
		ArrayList<Spielzug> alle_züge = giballeZüge(farbe);
		int erg = 0;
		for (int i = 0; i < alle_züge.size(); i++) {
			if (FigurAt(alle_züge.get(i).x, alle_züge.get(i).y).art == art) {
				erg++;
			}
		}
		return erg;
	}

	public boolean isPossible(Spielzug zug, boolean with_schach) {
		Figur f = feld[zug.y][zug.x];
		int x = zug.getFinalx();
		int y = zug.getFinaly();

		boolean bool = false;

		if (f != null && y >= 0 && y < 8 && x >= 0 && x < 8) {
			if (feld[y][x] == null) {
				if (zug.zug.beweg) {
					bool = true;
				}
			} else if (feld[y][x].farbe != f.farbe) {
				if (zug.zug.schlag) {
					bool = true;

				}

			}
		}
		if (bool) {
			if (with_schach) {

				Brett b = new Brett(this);
				b.ziehe(zug);
				boolean schach = b.isSchach(f.farbe);

				return !schach;
			} else {
				return true;
			}

		} else {
			return false;
		}

	}

	public boolean isSchlag(Spielzug zug) {
		Figur f = feld[zug.y][zug.x];
		int x = zug.getFinalx();
		int y = zug.getFinaly();

		if (feld[y][x] != null && feld[y][x].farbe != f.farbe) {
			return true;
		} else {
			return false;
		}

	}

	public boolean isBreak(Spielzug zug) {
		int x = zug.getFinalx();
		int y = zug.getFinaly();

		if (y >= 0 && y < 8 && x >= 0 && x < 8 && feld[y][x] != null) {
			return !isSchlag(zug);
		}

		return false;
	}

	public boolean isSchach(Farbe farbe) {

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (feld[i][j] != null && feld[i][j].art == Art.KOENIG && feld[i][j].farbe == farbe)
					for (int k = 0; k < Art.values().length; k++) {
						Art art = Art.values()[k];

						ArrayList<Spielzug> züge = giballeZüge(j, i, farbe, art, false);

						for (int l = 0; l < züge.size(); l++) {
							int x = züge.get(l).getFinalx();
							int y = züge.get(l).getFinaly();
							if (feld[y][x] != null && feld[y][x].art == art && feld[y][x].farbe != farbe) {
								return true;

							}

						}

					}

			}

		}

		return false;
	}
	
	public double isOver(Farbe f) {
		for(int i = 0; i < 2; i++) {
			giballeZüge(Farbe.values()[i]);
			
			if(alle_züge[i].size() == 0) {
				if(isSchach(Farbe.values()[i])) {
					return f == Farbe.WEISS ? i : 1-i;
				}else {
					return 0.5;
				}
				
			}
		}
		return -1;
	}

	public boolean isRemis() {
		return isSorL(Farbe.WEISS) && isSorL(Farbe.SCHWARZ);
	}

	public boolean isSorL(Farbe farbe) {
		int c = 0;

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (feld[i][j] != null && feld[i][j].farbe == farbe && feld[i][j].art != Art.KOENIG) {
					if (feld[i][j].art != Art.SPRINGER && feld[i][j].art != Art.LAEUFER) {
						c++;
					}

					if (c != 0) {
						return false;
					} else {
						c++;
					}
				}
			}

		}

		return true;
	}

	public void convert(int x, int y) {
		if (feld[y][x].art == Art.BAUER) {
			if (y == Math.abs(feld[y][x].farbe.ordinal() - 1) * 7) {
				feld[y][x].art = Art.DAME;
				feld[y][x].bild = feld[y][x].getBild();
			}

		}

	}

	public void rochade(int x, int y) {
		if (feld[y][x].art == Art.KOENIG && !feld[y][x].moved) {
			if (Math.abs(3 - x) > 1) {
				int t = (x > 3) ? 7 : 0;
				int incr = (x > 3) ? -1 : 1;

				feld[y][x + incr] = feld[y][t];
				feld[y][t] = null;
			}

		}
	}

	public Brett ziehe(Spielzug zug) {
		int x = zug.getFinalx();
		int y = zug.getFinaly();

		feld[y][x] = feld[zug.y][zug.x];
		feld[zug.y][zug.x] = null;

		rochade(x, y);

		feld[y][x].moved = true;

		convert(x, y);

		alle_züge = (ArrayList<Spielzug>[]) new ArrayList[2];
		return this;
	}

	public double[] asArray() {
		double[] arr = new double[64];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (feld[i][j] != null)
					arr[i * 8 + j] = feld[i][j].art.ordinal() * (feld[i][j].farbe == Farbe.WEISS ? 1 : -1);
			}
		}
		return arr;
	}

	public BufferedImage asBild(Farbe farbe) {
		BufferedImage img = new BufferedImage(8 * 50, 8 * 50, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = img.createGraphics();

		for (int i = 0; i < 8; i++) {
			int num = i % 2;
			for (int j = 0; j < 8; j++) {
				g.setColor(Farbe.values()[num].hintergrund);
				g.fillRect(j * 50, i * 50, 50, 50);

				num = num ^ 1;
			}
		}

		int f = farbe.ordinal();

		for (int i = 7 * f; f == 0 ? i < 8 : i > -1; i += 1 + -2 * f) {
			for (int j = 7 * f; f == 0 ? j < 8 : j > -1; j += 1 + -2 * f) {
				Figur figur;
				if ((figur = feld[i][j]) != null) {

					g.drawImage(figur.asBild(), j * 50, i * 50, 50, 50, null);
				}
			}
		}

		return img;
	}

	public void print() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				String s;
				if (feld[i][j] != null) {
					s = feld[i][j].art.toString();
				} else {
					s = "";
				}
				while (s.length() < 10) {
					s += " ";
				}
				System.out.print(s + "  +  ");
			}
			System.out.println();
		}

	}
}
