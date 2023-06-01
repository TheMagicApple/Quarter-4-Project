import java.awt.Color;
import java.awt.Graphics;

public class Bullet {
	public float x;
	public float y;
	public int width;
	public int height;
	public float vx;
	public float vy;
	
	public String player;
	public Bullet(float x,float y,int width,int height,float vx,float vy,String player) {
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.vx=vx;
		this.vy=vy;
		this.player=player;
	}
	public void update() {
		x+=vx;
		y-=vy;
	}
	public void draw(Graphics g) {
		g.setColor(new Color(40,40,40));
		g.fillOval(Math.round(x), Math.round(y), width, height);
	}

}

