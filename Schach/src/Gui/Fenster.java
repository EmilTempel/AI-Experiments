package Gui;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Fenster extends JLabel {

	final int width, size;
	JFrame f;
	ArrayList<GraphicsObject> gobjs;

	public Fenster(int size, boolean exit) {
		this.width = 8 * 50 * size;
		this.size = size;

		f = new JFrame();
		f.setSize(width+16, width + 39);
		f.setVisible(true);
		if (exit)
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(this);
		f.setLayout(null);

		this.setLocation(0, 0);
		this.setSize(width, width);
		this.setVisible(true);

		gobjs = new ArrayList<GraphicsObject>();
	}

	public void addGobj(GraphicsObject gobj) {
		gobjs.add(gobj);
	}

	public int getGröße() {
		return size * 50;
	}

	public int convert(int x) {
		return x / (50 * size);
	}

	public void paintComponent(Graphics g) {
		for (int i = 0; i < gobjs.size(); i++) {
			gobjs.get(i).paint(g);
		}

		repaint();
	}

}
