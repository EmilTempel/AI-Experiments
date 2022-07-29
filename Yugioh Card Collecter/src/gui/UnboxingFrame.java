package gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import cards.Card;
import collecting.Booster;

public class UnboxingFrame extends JFrame {

	CardViewer viewer;
	Booster booster;
	int index;

	CardListViewer clv;

	public UnboxingFrame(Booster booster) {
		setSize(531, 762);
		setVisible(true);
		setLocationRelativeTo(null);

		viewer = new CardViewer(new Card(Card.loadImage("pics/booster.jpg")), true);

		index = -1;

		JFrame frame = this;

		viewer.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("click");

				index++;

				if (index >= booster.size()) {
					frame.dispose();
					if (clv != null) {
						clv.update();
						System.out.println("update");
					}
				} else {

					
					viewer.setCard(booster.get(index));
				}
			}
		});

		add(viewer);
	}

	public UnboxingFrame(Booster booster, CardListViewer clv) {
		this(booster);
		this.clv = clv;
	}
}
