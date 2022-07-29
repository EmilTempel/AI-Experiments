package cards;

import java.util.ArrayList;

import collecting.Booster;
import collecting.Player;

public class CardSet extends ArrayList<Card> {

	String code, name, rarity, price;

	public CardSet(String code, String name, String rarity, String price) {
		this.code = code;
		this.name = name;
		this.rarity = rarity;
		this.price = price;

	}

	public CardSet(String data) {
		this(CardLoader.getData(data, "set_code"), CardLoader.getData(data, "set_name"),
				CardLoader.getData(data, "set_rarity"), CardLoader.getData(data, "set_price"));
	}
	
	public Booster getBooster(int size, Player p, BanList banlist) {
		ArrayList<Card> possible = new ArrayList<Card>();
		for (Card c : this) {
			possible.add(c);
			possible.add(c);
			possible.add(c);
		}

		possible = Complement(Complement(possible, banlist), p);

		Booster booster = new Booster();

		for (int i = 0; i < size; i++) {
			Card c = possible.get((int) (Math.random() * possible.size()));
			booster.add(c);
			p.add(c);

			possible.remove(c);
		}

		return booster;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public String getRarity() {
		return rarity;
	}
	
	public String getPrice() {
		return price;
	}
	
	public String toString() {
		return name;
	}

	public static <E> ArrayList<E> Complement(ArrayList<E> A, ArrayList<E> B) {
		ArrayList<E> A_ = (ArrayList<E>) A.clone();

		for (E e : B) {
			A_.remove(e);
		}
		return A_;
	}
}
