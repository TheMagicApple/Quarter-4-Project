import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

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
		if(type.equals("Speed")) {
			g.setColor(new Color(52, 122, 235));
			g.fillRoundRect(x-10, y-10, 20, 20, 10,10);
			g.setColor(Color.white);
			((Graphics2D)g).setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			g.fillPolygon(new int[] {x-5,x+5,x-5},new int[] {y-5,y,y+5},3);
		}
		if(type.equals("Jump")) {
			g.setColor(new Color(255,0,0));
			g.fillRoundRect(x-10, y-10, 20, 20, 10,10);
			g.setColor(Color.white);
			((Graphics2D)g).setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			g.fillPolygon(new int[] {x-5,x,x+5},new int[] {y+5,y-5,y+5},3);
		}
		if(type.equals("Gravity")) {
			g.setColor(new Color(194, 3, 252));
			g.fillRoundRect(x-10, y-10, 20, 20, 10,10);
			g.setColor(Color.white);
			((Graphics2D)g).setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			g.fillPolygon(new int[] {x-5,x,x+5},new int[] {y-5,y+5,y-5},3);
		}
	}
}
