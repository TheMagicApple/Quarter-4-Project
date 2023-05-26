import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Player {
	public float x=100;
	public float vx=0;
	public float y=200;
	public float vy=0;
	final float g=0.2f;
	public boolean onGround=false;
	public boolean touchingRight=false;
	public boolean touchingLeft=false;
	public String name="";
	public int health=100;
	BufferedImage image;
	public int animationStage=-1;
	int R=252;
	int G=186;
	int B=3;

	public float weaponRotation=0;
	
	public String weaponClass="";
	
	public Player() {
		
		try {
			image=ImageIO.read(new File("player (1).png"));
			
		} catch (IOException e) {
			System.out.println("CANNOT LOAD PLAYER IMAGE");
		}
	}
	public void update(){
		if(onGround && vy>0) vy=0;
		if(touchingRight && vx>0)vx=0;
		if(touchingLeft && vx<0)vx=0;
		//System.out.println(touchingLeft+" "+vx);
		if(!onGround) vy+=g;
		y+=vy;
		x+=vx;
		
	}
	public void draw(Graphics g) {
		
		//252, 66, 63
		if(animationStage>=0) {
			if(animationStage==0) {
				R=252;
				G=186;
				B=3;
			}
			if(animationStage<6) {
				G-=20;
				B+=10;
			}else if(animationStage>=12 && animationStage<18) {
				G+=20;
				B-=10;
			}else if(animationStage==18) {
				animationStage=-1;
			}
			if(animationStage!=-1) animationStage++;
		}
		g.setColor(new Color(R,G,B));
		g.fillRoundRect(Math.round(x),Math.round(y),20,20,13,13);
		g.setColor(new Color(60,60,60));
		g.fillOval(Math.round(x)+3, Math.round(y)+3, 6,6);
		g.fillOval(Math.round(x)+11, Math.round(y)+3, 6,6);
		//g.drawImage(image, Math.round(x),Math.round(y)-20,null);
		g.drawString(name,Math.round(x),Math.round(y)-20);
		if(health>50) g.setColor(new Color(0,255,0));
		else g.setColor(new Color(255,0,0));
		
		
		Graphics2D gg=(Graphics2D)g;
		gg.setStroke(new BasicStroke(1));
		g.fillRect(Math.round(x), Math.round(y)-10, Math.round(20*(health/100f)), 5);
		g.setColor(new Color(0,0,0));
		g.drawRect(Math.round(x), Math.round(y)-10, 20, 5);
		g.setColor(new Color(0,0,0));
		gg.rotate(weaponRotation,Math.round(x)+18,Math.round(y)+10);
		gg.setStroke(new BasicStroke(2));
		gg.drawRoundRect(Math.round(x)+18,Math.round(y)+10,8,3,3,3);
		gg.setColor(new Color(94, 201, 255));
		gg.fillRoundRect(Math.round(x)+18,Math.round(y)+10,8,3,3,4);
		gg.rotate(-weaponRotation,Math.round(x)+18,Math.round(y)+10);
		
	}	
}