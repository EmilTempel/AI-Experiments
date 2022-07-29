package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import cards.Card;

public class CardListViewer extends JPanel {
	Gui gui;

	JPanel search_bar, body;

	JTextField field;

	JLabel card_count;

	JScrollBar scrollbar;

	GridLayout layout;

	ArrayList<Card> cards, shown;

	int index, x_size, y_size;
	double card_size;

	CardViewer CardInFocus;

	ArrayList<InfoViewer> connected;

	public CardListViewer(ArrayList<Card> cards, Gui gui, double card_size) {
		this.gui = gui;

		this.cards = cards;
		this.shown = cards;

		index = 0;
		x_size = 5;
		y_size = 6;

		this.card_size = card_size;

		connected = new ArrayList<InfoViewer>();

		setVisible(true);

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				update();
			}
		});

		search_bar = new JPanel();

		field = new JTextField();
		field.setPreferredSize(new Dimension(100, 30));
		field.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {

			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				setIndex(0);
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				setIndex(0);
			}

		});

		card_count = new JLabel(cards.size() + " cards");

		JButton sort = new JButton("sort");
		sort.addActionListener(e -> {
			cards.sort(new Comparator<Card>() {

				public int compare(Card arg0, Card arg1) {
					return arg0.getName().compareTo(arg1.getName());
				}

			});

			System.out.println("sort");

			update();
		});

		search_bar.add(field);
		search_bar.add(sort);
		search_bar.add(card_count);

		body = new JPanel();

//		BoxLayout layout = new BoxLayout(body, BoxLayout.Y_AXIS);

		layout = new GridLayout(y_size, x_size);

		body.setLayout(layout);

		body.addMouseWheelListener(e -> {
			setIndex(index + e.getWheelRotation() * x_size);
		});

		body.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				equip();
			}

			public void mouseReleased(MouseEvent e) {
				dequip();
			}
		});

		scrollbar = new JScrollBar();

		scrollbar.addAdjustmentListener(e -> {
			System.out.println(e.getValue());

			setIndex(e.getValue() * x_size);
			update();

		});

		setLayout(new BorderLayout());

		add(search_bar, BorderLayout.NORTH);
		add(body, BorderLayout.CENTER);
		add(scrollbar, BorderLayout.EAST);

		update();
	}

	protected void equip() {
		if (gui.getMoving() == null && CardInFocus != null) {

			cards.remove(CardInFocus.getCard());

			gui.setMoving(CardInFocus);

			update();

			System.out.println("equip");
		}
	}

	protected void dequip() {
		if (gui.getMoving() != null) {

			int i = CardInFocus != null ? cards.indexOf(CardInFocus.getCard()) : cards.size();

			cards.add(i != -1 ? i : cards.size(), gui.getMoving().getCard());

			gui.setMoving(null);

			update();

			System.out.println("dequip");
		}
	}

	public void update() {
		body.removeAll();

		x_size = (int) ((float) getWidth() / (177f * card_size));
		y_size = (int) ((float) getHeight() / (254f * card_size));

		layout.setColumns(x_size == 0 ? 1 : x_size);
		layout.setRows(y_size);

		shown = search(cards, field.getText());

		for (int i = index; i < index + x_size * y_size; i++) {
			if (i < shown.size()) {
				CardViewer c = new CardViewer(shown.get(i), true, card_size);
				c.addCardListViewer(this);
				body.add(c);
			} else {
				CardViewer c = new CardViewer(false, card_size);
				c.addCardListViewer(this);
				body.add(c);
			}
		}

		card_count.setText(shown.size() + " cards");

		if (x_size != 0)
			scrollbar.setValues(index / x_size, 0, 0, shown.size() / x_size);

//		Gui.setBackground(this, Color.LIGHT_GRAY);

		SwingUtilities.updateComponentTreeUI(body);
	}

	public void setIndex(int index) {
		this.index = index < shown.size() ? index : shown.size() - 1;
		if (this.index < 0)
			this.index = 0;

		update();
	}

	public void setCardInFocus(CardViewer CardInFocus) {
		this.CardInFocus = CardInFocus;
		for (InfoViewer c : connected) {
			c.setCard(CardInFocus.getCard());
		}
	}

	public void connect(InfoViewer c) {
		connected.add(c);
	}

	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
		this.shown = cards;

		update();
	}

	public ArrayList<Card> getCards() {
		return cards;
	}

	public static ArrayList<Card> search(ArrayList<Card> cards, String str) {
		ArrayList<Card> containing = new ArrayList<Card>();
		System.out.println(str);
		for (Card c : cards) {
			if (c != null) {
				String name = c.getName().toLowerCase();
				String desc = c.getDescription().toLowerCase();

				if (name.contains(str.toLowerCase()) || desc.contains(str.toLowerCase())) {
					containing.add(c);
				}
			}
		}

		return containing;
	}
}
