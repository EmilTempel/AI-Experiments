package Gui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import spiel.Brett;
import spiel.Figur;
import spiel.Figur.Farbe;
import spiel.Spielzug;
import spiel.Zug;
import spiel.er.Player;

public class Mensch extends Player implements MouseListener, MouseMotionListener {

	Fenster fenster;

	Farbe farbe;
	Spielzug zug;
	Brett brett;

	int x, y;

	ArrayList<Spielzug> züge;

	public Mensch() {

	}

	public Mensch(Fenster fenster) {
		this.fenster = fenster;
		fenster.addMouseListener(this);
		fenster.addMouseMotionListener(this);

		int size = fenster.getGröße();

		fenster.addGobj(new GraphicsObject() {
			public void paint(Graphics g) {

				if (brett != null && zug != null) {
					Figur f = brett.FigurAt(zug.getX(), zug.getY());
					g.setColor(Farbe.values()[(zug.getY() % 2) ^ (zug.getX() % 2)].getHintergrund());
					g.fillRect(size * zug.getX(), size * zug.getY(), size, size);
					if (f != null) {
						g.drawImage(f.asBild(), x - size / 2, y - size / 2, size, size, null);

					}

					
				}
				 
			}

		});
	}

	public Spielzug ziehe(Brett brett, Farbe farbe) {
		this.brett = brett;
		this.farbe = farbe;

		züge = brett.giballeZüge(farbe);

		synchronized (this) {
			while (true) {
				try {
					System.out.println("waiting...");
					wait();
					System.out.println("finished waiting!");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (zug.isPartOf(züge)) {
					züge = null;
					return zug;
				}

				zug = null;
			}
		}

	}

	public void mousePressed(MouseEvent e) {
		if (zug == null) {
			zug = new Spielzug(fenster.convert(e.getX()), fenster.convert(e.getY()));
			System.out.println("pressed at: " + zug.getX() + "  " + zug.getY());
		}

	}

	public void mouseReleased(MouseEvent e) {

		System.out.println("released");
		if (zug != null) {
			int x = fenster.convert(e.getX());
			int y = fenster.convert(e.getY());
			zug.setZug(new Zug(x - zug.getX(), y - zug.getY()));

			if (brett != null && brett.isPossible(zug, true)) {
				synchronized (this) {
					System.out.println("notify");
					notify();
				}
			}else {
				zug = null;
			}
		}
		
	}

	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		x = e.getX();
		y = e.getY();

	}

}
