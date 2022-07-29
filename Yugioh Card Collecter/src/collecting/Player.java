package collecting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import cards.Card;
import cards.CardLoader;

public class Player extends ArrayList<Card> {

	String name;

	File f;

	public Player(String name, CardLoader cloader) {
		this.name = name;

		f = new File("Players/" + name + ".txt");

		if (f.exists()) {
			try {
				BufferedReader r = new BufferedReader(new FileReader(f));

				String line;

				while ((line = r.readLine()) != null) {
					Card c = cloader.getByID(line);
					if (c != null)
						add(c);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void save() {
		String str = "";
		for (Card c : this) {
			str += c.getID() + "\n";
		}

		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));

			w.write(str);
			
			w.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getName() {
		return name;
	}
}
