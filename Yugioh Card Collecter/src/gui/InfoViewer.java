package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import cards.Card;

public class InfoViewer extends JPanel {

	CardViewer viewer;

	InfoPanel panel;

	public InfoViewer() {
		setVisible(true);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

//		addComponentListener(new ComponentAdapter() {
//			public void componentResized(ComponentEvent e) {
//				setSize(new Dimension(400,1000));
//			}
//		});

		viewer = new CardViewer(false, 3);
		panel = new InfoPanel();

		add(viewer);
		add(panel);
	}

	public InfoViewer(Card c) {
		this();
		setCard(c);
	}

	public CardViewer getCardViewer() {
		return viewer;
	}

	public void setCard(Card c) {
		viewer.setCard(c);
		panel.setCard(c);
	}

	class InfoPanel extends JPanel {

		JTextField name;

		JTextArea area;

		InfoPanel() {
			setVisible(true);
			setLayout(new BorderLayout());
			setMaximumSize(new Dimension(177 * 2, 254 * 2));

			JPanel header = new JPanel();
			name = new JTextField();
			name.setFont(getFont().deriveFont(17f));

			header.add(name);

			area = new JTextArea(20, 20);
			area.setEditable(true);
			area.setLineWrap(true);
			area.setWrapStyleWord(true);
			area.setFont(getFont().deriveFont(15f));
			area.setMaximumSize(new Dimension(177 * 2, 254 * 2));
			area.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 10));

			add(header, BorderLayout.NORTH);
			add(area, BorderLayout.CENTER);
		}

		public void setCard(Card c) {
			name.setText(c.getName());

			String text = "";

			text += "[" + c.getType() + "] ";
			if (c.getType().contains("Monster")) {
				text += c.getAttribute() + " " + c.getRace() + "\n";
				if (!c.getType().contains("Link")) {
					text += "Level: " + c.getLevel() + "   " + c.getATK() + "/" + c.getDEF();
				} else {
					text += "Link: " + c.getLinkVal() + "   " + c.getATK();
				}
			}
			text += "\n\n" + c.getDescription();

			text.replace("\n", "<br>");
			area.setText(text);
		}
	}
}
