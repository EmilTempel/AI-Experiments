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
	
	ArrayList<Spielzug>[] alle_z�ge = (ArrayList<Spielzug>[]) new ArrayList[2];

	public Brett() {
		feld = standard_Aufstellung();
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

	public Figur[][] standard_Aufstellung() {
		Figur[][] feld = new Figur[8][8];

		Art[] aufstellung = { Art.TURM, Art.SPRINGER, Art.LAEUFER, Art.KOENIG, Art.DAME, Art.LAEUFER, Art.SPRINGER,
				Art.TURM };

		for (int i = 0; i < aufstellung.length; i++) {
			feld[0][i] = new Figur(aufstellung[i], Farbe.WEISS);
			feld[1][i] = new Figur(Art.BAUER, Farbe.WEISS);

			feld[7][i] = new Figur(aufstellung[i], Farbe.SCHWARZ);
			feld[6][i] = new Figur(Art.BAUER, Farbe.SCHWARZ);

		}

		return feld;
	}

	public Figur[][] rochade_Aufstellung() {
		Figur[][] feld = new Figur[8][8];

		Art[] aufstellung = { Art.TURM, null, null, Art.KOENIG, null, null, null, Art.TURM };

		for (int i = 0; i < aufstellung.length; i++) {
			if (aufstellung[i] != null) {
				feld[0][i] = new Figur(aufstellung[i], Farbe.WEISS);
			}
			feld[1][i] = new Figur(Art.BAUER, Farbe.WEISS);

			if (aufstellung[i] != null) {
				feld[7][i] = new Figur(aufstellung[i], Farbe.SCHWARZ);
			}
			feld[6][i] = new Figur(Art.BAUER, Farbe.SCHWARZ);

		}

		return feld;
	}

	public Figur FigurAt(int x, int y) {
		if (x >= 0 && x < 8 && y >= 0 && y < 8) {
			return feld[y][x];
		} else {
			return null;
		}
	}

	public ArrayList<Spielzug> giballeZ�ge(Farbe farbe) {
		int f = farbe.ordinal();
		if(alle_z�ge[f] == null) {
			alle_z�ge[f] = alleZ�ge(farbe);
		}
		
		return alle_z�ge[f];
	}
	
	private ArrayList<Spielzug> alleZ�ge(Farbe farbe) {
		ArrayList<Spielzug> alle_z�ge = new ArrayList<Spielzug>();

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				if (feld[i][j] != null && feld[i][j].farbe == farbe) {
					Figur f = feld[i][j];
					alle_z�ge.addAll(giballeZ�ge(j, i, farbe, f.art, true));

				}

			}

		}

		return alle_z�ge;
	}

	public ArrayList<Spielzug> giballeZ�ge(int x, int y, Farbe farbe, Art art, boolean with_schach) {
		ArrayList<Spielzug> z�ge = sonderz�ge(x, y, with_schach);

		for (int i = 0; i < art.z�ge.length; i++) {

			for (int j = 1; j < art.l�nge + 1; j++) {

				Spielzug zug = new Spielzug(x, y,
						((farbe.ordinal() == 0) ? art.z�ge[i] : art.z�ge[i].rotiere180()).mult(j));
				if (isPossible(zug, with_schach)) {
					z�ge.add(zug);
					if (isSchlag(zug)) {
						break;
					}
				} else {
					break;
				}
			}

		}

		return z�ge;
	}

	public ArrayList<Spielzug> sonderz�ge(int x, int y, boolean with_schach) {
		ArrayList<Spielzug> z�ge = new ArrayList<Spielzug>();
		Figur f = feld[y][x];

		if (!f.moved) {
			switch (f.art) {
			case KOENIG:
				for (int i = 0; i < 2; i++) {

					int incr = (x < i * 7) ? 1 : -1;

					for (int j = x + incr; j != i * 7 + incr; j += incr) {

						if (feld[y][j] != null) {
							if (feld[y][j].art == Art.TURM && !feld[y][j].moved) {
								z�ge.add(new Spielzug(x, y, new Zug(2 * incr, 0)));
							} else {
								break;
							}

						}

					}

				}
				break;

			case BAUER:
				if (feld[y + (f.farbe == Farbe.WEISS ? 1 : -1)][x] == null) {
					z�ge.add(new Spielzug(x, y,
							(f.farbe.ordinal() == 0) ? new Zug(0, 2, true) : new Zug(0, 2, true).rotiere180()));
				}
				break;

			default:
				break;

			}
		}

		for (int i = 0; i < z�ge.size(); i++) {
			if (!isPossible(z�ge.get(i), with_schach)) {
				z�ge.remove(i);
			}
		}

		return z�ge;
	}
	
	public int score(Farbe farbe) {
		int score = 0; 
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				Figur f = feld[i][j];
				if(f != null && f.farbe == farbe) {
					score += f.art.wert;
				}
			}
			
		}
		return score;
	}
	
	public int AnzahlZ�ge(Farbe farbe, Art art) {
		ArrayList<Spielzug> alle_z�ge = giballeZ�ge(farbe);
		int erg = 0;
		for(int i = 0; i < alle_z�ge.size(); i++) {
			if(FigurAt(alle_z�ge.get(i).x,alle_z�ge.get(i).y).art == art) {
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
		
		
		if(y >= 0 && y < 8 && x >= 0 && x < 8 && feld[y][x] != null) {
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

						ArrayList<Spielzug> z�ge = giballeZ�ge(j, i, farbe, art, false);

						for (int l = 0; l < z�ge.size(); l++) {
							int x = z�ge.get(l).getFinalx();
							int y = z�ge.get(l).getFinaly();
							if (feld[y][x] != null && feld[y][x].art == art && feld[y][x].farbe != farbe) {
								return true;

							}

						}

					}

			}

		}
		

		return false;
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
		
		alle_z�ge = (ArrayList<Spielzug>[]) new ArrayList[2];
		return this;
	}

	public double[] asArray() {
		double[] arr = new double[64];
		for (int i = 0; i < 8 ; i ++) {
			for (int j = 0; j < 8; j ++) {
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

		for (int i = 7 * f; f==0?i<8:i>-1; i+= 1 + -2*f) {
			for (int j = 7 * f; f==0?j<8:j>-1; j+= 1 + -2*f) {
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

	public static void main(String[] args) {
		Brett b = new Brett();

//		for (int i = 0; i < alle_z�ge.size(); i++) {
//
//			int x = alle_z�ge.get(i).x;
//			int y = alle_z�ge.get(i).y;
//			if (b.feld[y][x] != null)
//				System.out.println(b.feld[y][x].art.toString() + "    " + b.feld[y][x].farbe.toString());
//
//		}

//		JFrame f = new JFrame();
//		f.setSize(8 * 50, 8 * 60);
//		f.setVisible(true);
//		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		Graphics g = f.getGraphics();
//		Player[] net = { new Rando(), new SteckerlFisch("SteckerlFische/fisch8",2) };

		Partie p = new Partie(new OmegaStop(3),new Rando(),false);
		
		int win_count = 0;
		for(int i = 0; i < 10; i++) {
			int w = p.play(100);
			if(w == 0) {
				win_count++;
			}
			System.out.println(i);
		}
		win_count = win_count;
		System.out.println("avergae accuracy:  " + win_count);

//		for (int i = 0; i < (int) Double.POSITIVE_INFINITY; i++) {
//
//			b.ziehe(net[i % 2].ziehe(b, Farbe.values()[i % 2]));
//
//			g.drawImage(b.asBild(), 0, 50, 8 * 50, 8 * 50, null);
//			try {
//				if (!b.isSchach(Farbe.values()[Math.abs((i % 2) - 1)])) {
//					Thread.sleep(0);
//				} else {
//					System.out.println("SCHACH!!");
//					try {
//						Thread.sleep(0);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}
}
