package cards;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Card {

	String id, name, type, description, race, atk, def, level, attribute, linkval, archetype;

	BufferedImage img;

	ArrayList<CardSet> sets;

	public static BufferedImage default_image = loadImage("pics/default_image.png");

	public static String[] extra_deck_type = { "Fusion Monster", "Link Monster", "XYZ Monster", "Synchro Monster",
			"Synchro Tuner Monster", "Synchro Pendulum Effect Monster", "XYZ Pendulum Effect Monster",
			"Pendulum Effect Fusion Monster" };

	public Card(String id, String name, String type, String description, String race, String atk, String def,
			String level, String attribute, String linkval, String archetype) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.description = description;
		this.race = race;
		this.atk = atk;
		this.def = def;
		this.level = level;
		this.attribute = attribute;
		this.linkval = linkval;
		this.archetype = archetype;

		sets = new ArrayList<CardSet>();
	}

	public Card(String source) {
		this(CardLoader.getData(source, "id"), CardLoader.getData(source, "name"), CardLoader.getData(source, "type"),
				CardLoader.getData(source, "desc"), CardLoader.getData(source, "race"),
				CardLoader.getData(source, "atk"), CardLoader.getData(source, "def"),
				CardLoader.getData(source, "level"), CardLoader.getData(source, "attribute"),
				CardLoader.getData(source, "linkval"), CardLoader.getData(source, "archetype"));
	}

	public Card(BufferedImage img) {
		this.img = img;
	}

	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public String getRace() {
		return race;
	}

	public String getATK() {
		return atk;
	}

	public String getDEF() {
		return def;
	}

	public String getLevel() {
		return level;
	}

	public String getLinkVal() {
		return linkval;
	}

	public String getAttribute() {
		return attribute;
	}

	public BufferedImage getImage() {
		if (img == null) {
			try {
				img = ImageIO.read(new File("pics/" + id + ".jpg"));
			} catch (IOException e) {
				img = default_image;
			}
		}
		return img;
	}

	public void addCardSet(CardSet set) {
		sets.add(set);
	}

	public boolean isMainDeck() {
		for (int i = 0; i < extra_deck_type.length; i++) {
			System.out.println(type + "  =  " + extra_deck_type[i]);
			if (type.trim().equals(extra_deck_type[i])) {
				return false;
			}
		}
		return true;
	}

	public static BufferedImage loadImage(String path) {
		try {
			return ImageIO.read(new File(path));
		} catch (IOException e) {
			return null;
		}
	}

	public String toString() {
		return name;
//		return "ID: " + id + " | NAME: " + name + " | TYPE: " + type + " | RACE: " + race + " | ARCHETYPE: " + archetype;
	}

}
