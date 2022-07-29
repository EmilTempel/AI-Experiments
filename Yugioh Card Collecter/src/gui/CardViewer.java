package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import cards.Card;

public class CardViewer extends JPanel {

	JLabel label;

	double card_size;

	Card card;

	CardListViewer viewer;

	public CardViewer(boolean highlight, double card_size) {

		setVisible(true);
		setLayout(new BorderLayout());
		setMaximumSize(new Dimension((int) (177 * card_size), (int) (254 * card_size)));

		label = new JLabel() {
			protected void paintComponent(Graphics g) {
//				setSize(new Dimension(label.getWidth(), (int)(label.getWidth() * 254/177)));
				if (card != null) {
					int preferred_width = (int) (label.getHeight() * 177 / 254);
					int preferred_height = (int) (label.getWidth() * 254 / 177);

					int diff = 0;

					if ((diff = label.getWidth() - preferred_width) > 0) {
						g.drawImage(card.getImage(), diff / 2, 0, preferred_width, label.getHeight(), null);
					} else if ((diff = label.getHeight() - preferred_height) > 0) {
						g.drawImage(card.getImage(), 0, diff / 2, label.getWidth(), preferred_height, null);
					}
				}

				repaint();
			}
		};
		label.setMaximumSize(new Dimension((int) (177 * card_size), (int) (254 * card_size)));

		add(label, BorderLayout.CENTER);

		if (highlight) {
			CardViewer c = this;

			addMouseListener(new MouseAdapter() {

				public void mouseEntered(MouseEvent e) {
					setBorder(BorderFactory.createLineBorder(getBackground(), 5));
					if (viewer != null)
						viewer.setCardInFocus(c);

				}

				public void mouseExited(MouseEvent e) {
					setBorder(null);
				}

				@Override
				public void mousePressed(MouseEvent e) {
					if (viewer != null)
						viewer.body.dispatchEvent(e);
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					if (viewer != null)
						viewer.body.dispatchEvent(e);
				}
			});
		}
	}

	public CardViewer(Card c, boolean highlight) {
		this(c, highlight, 1);

	}

	public CardViewer(Card c, boolean highlight, double card_size) {
		this(highlight, card_size);
		setCard(c);
	}

	public void addCardListViewer(CardListViewer viewer) {
		this.viewer = viewer;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}
}
