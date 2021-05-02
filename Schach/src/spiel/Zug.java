package spiel;

public class Zug {

	int x, y;
	boolean beweg;
	boolean schlag;

	public Zug(int x, int y) {
		this.x = x;
		this.y = y;
		this.beweg = true;
		this.schlag = true;
	}
	
	public Zug(int x, int y, boolean beweg) {
		this.x = x;
		this.y = y;
		this.beweg = beweg;
		this.schlag = !beweg;
	}
	
	public Zug(int x, int y, boolean beweg, boolean schlag) {
		this.x = x;
		this.y = y;
		this.beweg = beweg;
		this.schlag = schlag;
	}

	public Zug rotiere90() {
		int x = -this.y;
		int y = this.x;
		return new Zug(x, y, this.beweg, this.schlag);
	}
	
	public Zug rotiere180() {
		int x = -this.x;
		int y = -this.y;
		return new Zug(x, y, this.beweg, this.schlag);
	}
	
	public Zug mult(int num) {
		int x = num * this.x;
		int y = num * this.y;
		return new Zug(x, y, this.beweg, this.schlag);
	}
}
