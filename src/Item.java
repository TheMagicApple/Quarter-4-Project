import java.awt.Color;
import java.awt.Graphics;

public class Item {
	public int x;
	public int y;
	public String type;
	public Item(int x,int y,String type) {
		this.x=x;
		this.y=y;
		this.type=type;
	}
	public void draw(Graphics g) {
		if(type.equals("Health")) {
			g.setColor(new Color(57, 191, 86));
			g.fillRoundRect(x-10, y-10, 20, 20, 10,10);
			g.setColor(Color.white);
			g.fillRoundRect(x-2,y-7,4,14,5,5);
			g.fillRoundRect(x-7,y-2,14,4,5,5);
		}
	}
}
