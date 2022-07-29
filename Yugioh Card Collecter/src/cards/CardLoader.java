package cards;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import collecting.Booster;
import collecting.Player;

public class CardLoader {

	ArrayList<Card> cards;
	HashMap<String, Card> card_table;

	ArrayList<CardSet> sets;
	HashMap<String, CardSet> set_table;

	public CardLoader(String filepath) {
		String data = "";
		try {
			data = new String(Files.readAllBytes(Paths.get(filepath)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cards = new ArrayList<Card>();
		card_table = new HashMap<String, Card>();

		sets = new ArrayList<CardSet>();
		set_table = new HashMap<String, CardSet>();

		loadCards(data);
		
		sets.sort(new Comparator<CardSet>() {

			@Override
			public int compare(CardSet set1, CardSet set2) {
				return set1.getName().compareTo(set2.getName());
			}
			
		});
	}

	public Card getByID(String ID) {
		return card_table.get(ID);
	}

	public Card getRandom() {
		return cards.get((int) (Math.random() * cards.size()));
	}
	
	public ArrayList<CardSet> getCardSets(){
		return sets;
	}
	
	public ArrayList<Card> getCards(){
		return cards;
	}

	public ArrayList<Card> loadCards(String s) {

		transform(s, str -> {
			switch (getData(str, "type")) {
			case "Token":
			case "Skill Card":
				break;
			default:
				Card card = new Card(str);
				cards.add(card);
				card_table.put(card.getID(), card);

				String string = getData(str, "card_sets");
				if (string != null) {
					transform(string, set -> {
						CardSet cardset = set_table.get(getData(set, "set_name"));
						if (cardset == null) {
							cardset = new CardSet(set);
							set_table.put(cardset.getName(), cardset);
							sets.add(cardset);
						}

						if (!cardset.contains(card)) {
							cardset.add(card);
							card.addCardSet(cardset);
						}
					});
				}
				break;
			}
		});

		return cards;
	}

	

	public static void transform(String s, Action a) {
		int j = 0, c = 0, k = 0;

		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '{') {
				if (c == 0) {
					j = i;
				}
				c++;
			} else if (s.charAt(i) == '}') {
				c--;
				if (c == 0) {
					String str = s.substring(j + 1, i);
					a.execute(str);
				}
			}
			k = i;
		}
	}

//	"id":88753985,"name":"Fox Fire","type":"Effect Monster","desc":"During the End Phase, if this card was destroyed by battle and sent to the Graveyard this turn and was face-up at the start of the Damage Step: Special Summon this card from the Graveyard. This face-up card cannot be Tributed for a Tribute Summon.","atk":300,"def":200,"level":2,"race":"Pyro","attribute":"FIRE","card_sets":[{"set_name":"Battle Pack: Epic Dawn","set_code":"BP01-EN010","set_rarity":"Rare","set_rarity_code":"(R)","set_price":"1.19"},{"set_name":"Battle Pack: Epic Dawn","set_code":"BP01-EN010","set_rarity":"Starfoil Rare","set_rarity_code":"(SFR)","set_price":"1.19"},{"set_name":"Dark Revelation Volume 3","set_code":"DR3-EN089","set_rarity":"Common","set_rarity_code":"(C)","set_price":"1.33"},{"set_name":"Onslaught of the Fire Kings Structure Deck","set_code":"SDOK-EN018","set_rarity":"Common","set_rarity_code":"(C)","set_price":"1.11"},{"set_name":"Rise of Destiny","set_code":"RDS-EN029","set_rarity":"Common","set_rarity_code":"(C)","set_price":"1.52"},{"set_name":"Structure Deck: Blaze of Destruction","set_code":"SD3-EN013","set_rarity":"Common","set_rarity_code":"(C)","set_price":"1.67"}],"card_images":[{"id":88753985,"image_url":"https://storage.googleapis.com/ygoprodeck.com/pics/88753985.jpg","image_url_small":"https://storage.googleapis.com/ygoprodeck.com/pics_small/88753985.jpg"}],"card_prices":[{"cardmarket_price":"0.04","tcgplayer_price":"0.13","ebay_price":"0.99","amazon_price":"0.25","coolstuffinc_price":"0.25"}]

	public static String getData(String source, String data) {
		data = "\"" + data + "\"";

		for (int i = 0; i < source.length(); i++) {
			if (source.charAt(i) == '"' && i + data.length() < source.length()
					&& data.equals(source.substring(i, i + data.length()))) {
				i = i + data.length() + 1;

				int j = i, c = 1;

				char begin = source.charAt(i);

				while (c != 0) {
					j++;
					char now = source.charAt(j);
					if ((Character.isDigit(begin) && !Character.isDigit(now))
							|| (begin == '"' && now == '"' && source.charAt(j - 1) != '\\')
							|| (begin == '[' && now == '[')) {
						c--;
					}

					if (begin == '[' && now == ']') {
						c--;
					}

				}
				return source.substring(Character.isDigit(begin) ? i : i + 1, j);
			}
		}
		return null;
	}

	public interface Action {
		public abstract void execute(String str);
	}

//	public static void main(String[] args) {
//		CardLoader cloader = new CardLoader("cardinfo.php");
//		
//		BanList TCG = new BanList("0TCG.lflist", cloader);
//		
//		System.out.println(TCG);
//		
//		ArrayList<Card> A = new ArrayList<Card>();
//		
//		Card random1 = cloader.getRandom();
//		Card random2 = cloader.getRandom();
//		
//		A.add(random1);
//		A.add(random1);
//		A.add(random1);
//		A.add(random2);
//		A.add(random2);
//		A.add(random2);
//		
//		System.out.println("A = " + A);
//		
//		ArrayList<Card> B = new ArrayList<Card>();
//		
//		B.add(random1);
//		B.add(random2);
//		B.add(random2);
//		
//		System.out.println("B = " + B);
//		
//		System.out.println(Complement(A,B));
//		
//		System.out.println("A = " + A);
//		System.out.println("B = " + B);
//	}

	public static void main(String[] args) {
//		CardLoader cloader = new CardLoader("cardinfo.php");
//
//		int c = 0;
//		for (CardSet set : cloader.sets) {
//			if (set.size() > 70) {
//				System.out.println(set);
//				c++;
//			}
//		}
//		System.out.println(c);

//		ArrayList<String> types = new ArrayList<String>();
//
//		for (Card c : cloader.cards) {
//			if (!types.contains(c.getType())) {
//				types.add(c.getType());
//			}
//		}
//
//		System.out.println(types);
	}
}
