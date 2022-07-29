package gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.plaf.LayerUI;

import cards.BanList;
import cards.CardLoader;
import cards.CardSet;
import collecting.Booster;
import collecting.Player;

public class Gui {

	JLayer<JPanel> layer;
	
	JComboBox<CardSet> card_sets;
	JComboBox<BanList> ban_lists;

	CardViewer moving;

	Point position;

	CardLoader cloader;

	ArrayList<Player> players;

	Player current;

	CardListViewer CardList;
	DeckListViewer DeckList;

	public Gui() {
		JFrame frame = new JFrame();
		frame.setSize(800, 800);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("closing");
				save();
				System.exit(0);
			}
		});

		JPanel panel = new JPanel();
		panel.setVisible(true);
		panel.setLayout(new BorderLayout());

		position = new Point(300, 300);

		LayerUI<JPanel> ui = new LayerUI<JPanel>() {

			public void installUI(JComponent c) {
				super.installUI(c);
				JLayer jlayer = (JLayer) c;
				jlayer.setLayerEventMask(AWTEvent.MOUSE_MOTION_EVENT_MASK);
			}

			public void paint(Graphics g, JComponent c) {
				super.paint(g, c);
				if (moving != null) {
					BufferedImage img = moving.getCard().getImage();
					g.drawImage(img, position.x - img.getWidth() / 2, position.y - img.getHeight() / 2, img.getWidth(),
							img.getHeight(), null);
				}
			}

			protected void processMouseMotionEvent(MouseEvent e, JLayer l) {
				position = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), l);
				l.repaint();
			}
		};

		layer = new JLayer<JPanel>(panel, ui);

		cloader = new CardLoader("cardinfo.php");

		loadPlayers(cloader);

		JMenuBar menu = new JMenuBar();

		JMenu change_player = new JMenu("change player");

		for (Player p : players) {
			JMenuItem item = new JMenuItem(p.getName());
			item.addActionListener(e -> {
				setCurrent(p);
				CardList.setCards(p);
			});
			change_player.add(item);
		}

		JMenuItem create = new JMenuItem("create new player");
		create.addActionListener(e -> {
			JFrame f = new JFrame();
			f.setVisible(true);
			f.setSize(200, 100);
			f.setLocationRelativeTo(null);

			JTextField field = new JTextField();
			field.setSize(f.getSize());
			field.addActionListener(a -> {
				Player p = new Player(field.getText(), cloader);
				players.add(p);
				JMenuItem item = new JMenuItem(p.getName());
				item.addActionListener(i -> {
					DeckList.concat(current);
					setCurrent(p);
					CardList.setCards(p);

				});
				System.out.println("dispose");
				f.dispose();
				change_player.add(item, change_player.getComponentCount() - 1);
			});
			f.add(field);
		});

		change_player.add(create);

		menu.add(change_player);

		JMenu booster = new JMenu("open booster");

		for (int i = 1; i < 31; i++) {
			JMenuItem item = new JMenuItem("x" + i);
			int I = i;
			item.addActionListener(e -> {
				int j = card_sets.getSelectedIndex(), k = ban_lists.getSelectedIndex();
				Booster b = card_sets.getItemAt(j).getBooster(I, current, ban_lists.getItemAt(k));
				new UnboxingFrame(b, CardList);

			});
			booster.add(item);
		}

		menu.add(booster);
		
		JMenu loadDeck = new JMenu("load deck");
		
		File decks = new File("Decks");
		for(File f : decks.listFiles()) {
			JMenuItem item = new JMenuItem(f.getName().replace(".YDK", ""));
			item.addActionListener(e -> {
				DeckList.loadDeck(current, "Decks/" + f.getName());
				CardList.update();
			});
			loadDeck.add(item);
		}
		
		menu.add(loadDeck);
		
		card_sets = new JComboBox<CardSet>();
		card_sets.setMaximumSize(new Dimension(200,200));

		for(CardSet set : cloader.getCardSets()) {
			if(set.size() >= 50) {
				card_sets.addItem(set);
			}
		}
		CardSet everything = new CardSet("69420", "Everything", "", "");
		everything.addAll(cloader.getCards());
		
		card_sets.addItem(everything);
		
		menu.add(card_sets);
		
		ban_lists = new JComboBox<BanList>();
		ban_lists.setMaximumSize(new Dimension(200,200));
		
		File lists = new File("banlists");
		for(File f : lists.listFiles()) {
			ban_lists.addItem(new BanList("banlists/" + f.getName(), cloader));
		}
		
		ban_lists.addItem(new BanList("N/A"));
		
		menu.add(ban_lists);

		JPanel card_area = new JPanel();
		card_area.setVisible(true);
		card_area.setLayout(new BoxLayout(card_area, BoxLayout.X_AXIS));

		CardList = new CardListViewer(current, this, 1);

		DeckList = new DeckListViewer(this);

		InfoViewer info = new InfoViewer(cloader.getRandom());

		CardList.connect(info);
		DeckList.connect(info);

		card_area.add(info);
		card_area.add(CardList);
		card_area.add(DeckList);

		panel.add(menu, BorderLayout.NORTH);
		panel.add(card_area, BorderLayout.CENTER);

		frame.add(layer);

		setBackground(panel, Color.LIGHT_GRAY);
		SwingUtilities.updateComponentTreeUI(panel);
	}

	public void setMoving(CardViewer moving) {
		this.moving = moving;
	}

	public CardViewer getMoving() {
		return moving;
	}

	public void setCurrent(Player p) {
		this.current = p;

	}

	public CardLoader getCardLoader() {
		return cloader;
	}

	public void loadPlayers(CardLoader cloader) {
		players = new ArrayList<Player>();

		File f = new File("player_names.txt");

		try {

			BufferedReader r = new BufferedReader(new FileReader(f));

			String line;

			while ((line = r.readLine()) != null) {
				if (line != "")
					players.add(new Player(line, cloader));
			}

			if (players.size() == 0) {
				Player p = new Player("Yami Yugi", cloader);
//				BanList TCG = new BanList("0TCG.lflist", cloader);
//
//				for (Card c : TCG) {
//					p.add(c);
//				}

				players.add(p);
			}

			current = players.get(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void save() {
		DeckList.concat(current);
		
		File f = new File("player_names.txt");

		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));

			String str = "";
			for (Player p : players) {
				str += p.getName() + "\n";
				p.save();
			}

			System.out.println(str);
			w.write(str);

			w.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setBackground(Container c, Color color) {
		c.setBackground(color);
		if (c instanceof Container) {
			for (Component child : c.getComponents()) {
				if (child instanceof Container)
					setBackground((Container) child, color);
			}
		}
	}

	public static void main(String[] args) {
		new Gui();
	}
}
