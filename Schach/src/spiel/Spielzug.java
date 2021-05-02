package spiel;

import java.util.ArrayList;

public class Spielzug {

	int x, y;

	Zug zug;
	

	public Spielzug(int x, int y, Zug zug) {
		this.x = x;
		this.y = y;
		this.zug = zug;
	}

	public Spielzug(int x, int y) {
		this.x = x;
		this.y = y;
		this.zug = null;
	}

	public void setZug(Zug zug) {
		this.zug = zug;
	}
	
	public Zug getZug() {
		return zug;
		
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;

	}

	public int getFinalx() {
		return x + zug.x;
	}

	public int getFinaly() {
		return y + zug.y;
	}

	public boolean isPartOf(ArrayList<Spielzug> züge) {
		if (zug != null) {

			for (int i = 0; i < züge.size(); i++) {
				Spielzug z = züge.get(i);

				if (x == z.x && y == z.y && zug.x == z.zug.x && zug.y == z.zug.y) {
					return true;

				}
			}
		}

		return false;
	}
}
