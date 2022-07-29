package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import cards.Card;
import cards.CardLoader;
import collecting.Player;

public class DeckListViewer extends JPanel {

	CardLoader cloader;

	CardListViewer main_deck, extra_deck;

	public DeckListViewer(Gui gui) {
		cloader = gui.getCardLoader();

		setVisible(true);
		setLayout(new BorderLayout());

		JPanel header = new JPanel();
		header.setVisible(true);

		JTextField name = new JTextField();
		name.setPreferredSize(new Dimension(100, 30));

		JButton save = new JButton("save");
		save.addActionListener(e -> {
			save("Decks/" + name.getText() + ".YDK");
		});

		header.add(name);
		header.add(save);
		
		JPanel decks = new JPanel();
		decks.setVisible(true);
		decks.setLayout(new BoxLayout(decks, BoxLayout.Y_AXIS));

		main_deck = new CardListViewer(new ArrayList<Card>(), gui, 1) {
			protected void dequip() {
				if (main_deck.getCards().size() < 60 && gui.getMoving() != null
						&& gui.getMoving().getCard().isMainDeck()) {
					super.dequip();
				}
			}
		};

		extra_deck = new CardListViewer(new ArrayList<Card>(), gui, 1) {
			protected void dequip() {
				if (extra_deck.getCards().size() < 15 && gui.getMoving() != null
						&& !gui.getMoving().getCard().isMainDeck()) {
					super.dequip();
				}
			}
		};

		decks.add(main_deck);
		decks.add(extra_deck);

		add(header, BorderLayout.NORTH);
		add(decks, BorderLayout.CENTER);
	}
	
	public void concat(Player p) {
		p.addAll(main_deck.getCards());
		main_deck.setCards(new ArrayList<Card>());
		
		p.addAll(extra_deck.getCards());
		extra_deck.setCards(new ArrayList<Card>());
	}

	public void update() {
		main_deck.update();
		extra_deck.update();

		SwingUtilities.updateComponentTreeUI(this);
	}

	public void loadDeck(Player p, String filepath) {
		concat(p);
		
		
		try {
			BufferedReader r = new BufferedReader(new FileReader(new File(filepath)));
			
			boolean main = true;

			String line;
			while ((line = r.readLine()) != null) {
				if (!line.contains("#") && !line.contains("!")) {
					Card c = cloader.getByID(line);
					if (p.contains(c)) {
						p.remove(c);
						(main ? main_deck : extra_deck).getCards().add(c);
					}

				} else if (line.equals("#extra")) {
					main = false;

				}
			}
			
			update();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void save(String path) {
		String str = "#created by Deine Mom\r\n" + "#main";

		for (Card c : main_deck.getCards()) {
			str += "\n" + c.getID();
		}

		str += "\n#extra";

		for (Card c : extra_deck.getCards()) {
			str += "\n" + c.getID();
		}

		str += "\n!side";

		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(path)));

			w.write(str);

			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<Card> getCards() {
		ArrayList<Card> cards = new ArrayList<Card>();

		cards.addAll(main_deck.getCards());
		cards.addAll(extra_deck.getCards());

		return cards;
	}

	public void connect(InfoViewer info) {
		main_deck.connect(info);
		extra_deck.connect(info);
	}
}
