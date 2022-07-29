package cards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class BanList extends ArrayList<Card> {
	
	String name;
	
	public BanList(String filepath, CardLoader cloader) {
		try {
			File f = new File(filepath);
			name = f.getName();
			name = name.substring(0, name.length()-7);
			BufferedReader r = new BufferedReader(new FileReader(f));

			String line;
			while ((line = r.readLine()) != null) {
				if(line.charAt(0) != '#' && line.charAt(0) != '!') {
					String[] split = line.split(" ");
					
					for(int i = 0; i < 3 - Integer.parseInt(split[1]); i++) {
						add(cloader.getByID(split[0]));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BanList(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
}
