import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Screen extends JPanel implements KeyListener,MouseListener,MouseMotionListener{
	static Player[] players=new Player[Server.n];
	static int myID;
	static ArrayList<Bullet> bullets=new ArrayList<>();
	static ArrayList<Platform> platforms=new ArrayList<>();
	boolean movingLeft=false;
	boolean movingRight=false;
	boolean movingUp=false;
	boolean movingDown=false;
	boolean shooting=false;
	Client c=new Client();
	final static int FPS=100;
	final static int WIDTH=1000;
	final static int HEIGHT=500;
	final static int BASICDAMAGE=20;
	int groundLevel=400;
	int rightGroundLevel=1000;
	int leftGroundLevel=-1000;
	int upGroundLevel=10000;
	static boolean started=false;
	static int clients=0;
	static int ready=0;
	boolean dead=false;
	static ArrayList<Player> deadPlayers=new ArrayList<>();
	int weaponCooldown=0;
	int mx=0;
	int my=0;
	public Screen() throws IOException {
		for(int i=0;i<players.length;i++) {
			players[i]=new Player();
		}
		c.go();
		
		platforms.add(new Platform(0,400,700,100));
		platforms.add(new Platform(800,400,200,100));
		platforms.add(new Platform(200,320,400,100));
		platforms.add(new Platform(10,360,50,50));
		platforms.add(new Platform(825,330,50,20));
		platforms.add(new Platform(725,270,50,20));
		platforms.add(new Platform(305,290,70,50));
		platforms.add(new Platform(940,220,100,400));
	}
	public void paintComponent(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g);
	
		if(!started) {
			g.drawString("Poly Party!", 200,200);
			g.drawString("Players Connected: "+clients+"/"+Server.n, 200,300);
			g.drawString("Players Ready: "+ready+"/"+Server.n,150,350);
		}
		if(started) {
			g.setColor(new Color(220,220,220));
			for(int i=25;i<1000;i+=50) {
				g.fillRect(i, 0, 2, 1000);
			}
			for(int i=25;i<500;i+=50) {
				g.fillRect(0, i, 1000, 2);
			}
			for(Player player:players) {
				if(!deadPlayers.contains(player)) player.draw(g);
			}
			players[myID].update();
			c.write("Player"+myID+"UMoveU"+players[myID].x+" "+players[myID].y);
			for(int i=0;i<bullets.size();i++) {
				Bullet bullet=bullets.get(i);
				bullet.update();
				bullet.draw(g);
			}
			for(Platform platform:platforms) {
				g.setColor(new Color(94, 201, 255));
				g.fillRoundRect(platform.x,platform.y,platform.width,platform.height,15,15);
			}
			
		}
	
	}
	public void animate() throws InterruptedException{
		while(true) {
			if(movingLeft && players[myID].x>leftGroundLevel) {
				players[myID].vx=-3;
				c.write("Player"+myID+"UMoveU"+players[myID].x+" "+players[myID].y);
			}
			else if(movingRight && players[myID].x+20<rightGroundLevel) {
				players[myID].vx=3;
				c.write("Player"+myID+"UMoveU"+players[myID].x+" "+players[myID].y);
			}
			else {
				players[myID].vx=0;
			}
			if(weaponCooldown>0) {
				weaponCooldown--;
			}
			for(int i=0;i<bullets.size();i++) {
				Bullet bullet=bullets.get(i);
				for(Platform plat:platforms) {
					if(collision(Math.round(bullet.x),Math.round(bullet.y),5,5,plat.x,plat.y,plat.width,plat.height)){
						bullets.remove(i);
						i--;
					}
				}
				if(collision(Math.round(bullet.x),Math.round(bullet.y),5,5,Math.round(players[myID].x),Math.round(players[myID].y),20,20)){
					if(Integer.parseInt(String.valueOf(bullet.player.charAt(6)))!=myID) {
						players[myID].health-=BASICDAMAGE;
						players[myID].animationStage=0;
						bullets.remove(i);
						i--;
						c.write("Player"+myID+"UDamageU"+BASICDAMAGE+"U"+(i+1));
						if(players[myID].health<=0) {
							System.out.println("I AM DEAD.");
							dead=true;
							deadPlayers.add(players[myID]);
							c.write("Player"+myID+"UDead");
						}
					}
				}
			}
			//COLLISIONS
			int playerXLeft=Math.round(players[myID].x);
			int playerXRight=Math.round(players[myID].x)+20;
			int playerYLeft=Math.round(players[myID].y);
			int playerYRight=Math.round(players[myID].y)+20;
			if(players[myID].vy>0 && players[myID].y+20>groundLevel) {
				players[myID].y=groundLevel-20;
				players[myID].vy=0;
				players[myID].onGround=true;
			}
			if(players[myID].y+20<groundLevel) {
				players[myID].onGround=false;
			}
			if(players[myID].vy<0 && players[myID].y<upGroundLevel) {
				players[myID].y=upGroundLevel;
				players[myID].vy=0;
			}
			if(players[myID].vx>=0 && players[myID].x+20>rightGroundLevel) {
				players[myID].x=rightGroundLevel-20;
				players[myID].vx=0;
			}
			if(players[myID].vx<=0 && players[myID].x<leftGroundLevel) {
				players[myID].x=leftGroundLevel;
				players[myID].vx=0;
			}
			
			//VERTICAL COLLISION
			int newGroundLevel=Integer.MAX_VALUE;
			for(Platform plat:platforms) {
				if((plat.x<=playerXLeft && plat.x+plat.width>=playerXLeft) || (plat.x<=playerXRight && plat.x+plat.width>=playerXRight)) {
					if(playerYRight<=plat.y) {
						if(plat.y<newGroundLevel) {
							newGroundLevel=plat.y;
						}
					}
				}
			}
			groundLevel=newGroundLevel;
			
			newGroundLevel=-Integer.MAX_VALUE;
			for(Platform plat:platforms) {
				if((plat.x<playerXLeft && plat.x+plat.width>playerXLeft) || (plat.x<playerXRight && plat.x+plat.width>playerXRight)) {
					if(playerYLeft>plat.y) {
						if(plat.y>newGroundLevel) {
							newGroundLevel=plat.y+plat.height;
						}
					}
				}
			}
			upGroundLevel=newGroundLevel;
			
			//RIGHT COLLISION
			newGroundLevel=Integer.MAX_VALUE;
			for(Platform plat:platforms) {
				if((plat.y<=playerYLeft && plat.y+plat.height>=playerYLeft) || (plat.y<=playerYRight && plat.y+plat.height>=playerYRight)) {
					if(playerXRight<=plat.x) {
						if(plat.x<newGroundLevel) {
							newGroundLevel=plat.x;
						}
					}
				}
			}
			rightGroundLevel=newGroundLevel;
			
			//LEFT COLLISION
			newGroundLevel=-Integer.MAX_VALUE;
			for(Platform plat:platforms) {
				if((plat.y<=playerYLeft && plat.y+plat.height>=playerYLeft) || (plat.y<=playerYRight && plat.y+plat.height>=playerYRight)) {
					if(playerXLeft>=plat.x+plat.width) {
						if(plat.x+plat.width>newGroundLevel) {
							newGroundLevel=plat.x+plat.width;
						}
					}
				}
			}
			leftGroundLevel=newGroundLevel;
			
			if(shooting && !dead) {
				if(weaponCooldown==0) {
					int px=Math.round(players[myID].x);
					int py=Math.round(players[myID].y);
					float deltax=mx-px;
					float deltay=my-py;
					float vx=(float)(deltax/Math.sqrt(deltax*deltax+deltay*deltay));
					float vy=-1*(float)(deltay/Math.sqrt(deltax*deltax+deltay*deltay));
					vx*=8;
					vy*=8;
					bullets.add(new Bullet(players[myID].x+20,players[myID].y+10,vx,vy,"Player"+myID));
					c.write("Player"+myID+"UShootUBasicBulletU"+vx+" "+vy);
					weaponCooldown=10;
				}
			}
			Thread.sleep(1000/FPS);
			repaint();
		}
	}
	public static boolean collision(int x1,int y1,int w1,int h1,int x2,int y2,int w2,int h2) {
		return !(x2>x1+w1 || x2+w2<x1 || y2>y1+h1 || y2+h2<y1);
	}
	public static void newMessage(String message) {
		String[] messageParts=message.split("U");
		if(messageParts.length==1) {
			if(messageParts[0].equals("START")) {
				started=true;
			}else if(messageParts[0].charAt(0)=='P'){
				myID=Integer.parseInt(String.valueOf(messageParts[0].charAt(6)));
			}else if(messageParts[0].equals("Ready")) {
					ready++;
					if(ready==Server.n) {
						started=true;
					}
			}
			else if(messageParts[0].charAt(0)=='J'){
				clients=Integer.parseInt(String.valueOf(messageParts[0].charAt(4)))+1;
			}
		}else {
			int id=Integer.parseInt(String.valueOf(messageParts[0].charAt(6)));
			if(!deadPlayers.contains(players[id])) {
				String command=messageParts[1];
				String change="";
				if(messageParts.length>2) change=messageParts[2];
				
				if(command.equals("Move")) {
					float x=Float.parseFloat(change.split(" ")[0]);
					float y=Float.parseFloat(change.split(" ")[1]);
					players[id].x=x;
					players[id].y=y;
				}
				if(command.equals("Name")) {
					players[id].name=change;
				}
				if(command.equals("Shoot")) {
					float vx=Float.parseFloat(messageParts[3].split(" ")[0]);
					float vy=Float.parseFloat(messageParts[3].split(" ")[1]);
					if(change.equals("BasicBullet")) {
						bullets.add(new Bullet(players[id].x+20,players[id].y+10,vx,vy,"Player"+id));
					}
				}
				if(command.equals("Damage")) {
					players[id].health-=Integer.parseInt(change);
					players[id].animationStage=0;
					bullets.remove(Integer.parseInt(messageParts[3]));
				}
				if(command.equals("Dead")) {
					System.out.println("Player "+id+" is Dead.");
					deadPlayers.add(players[id]);
				}
				if(command.equals("Aim")) {
					players[id].weaponRotation=Float.parseFloat(change);
				}
			}
		}
	}
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==65) {
			movingLeft=true;
		}
		if(e.getKeyCode()==68) {
			movingRight=true;
		}
		if(e.getKeyCode()==87 && players[myID].onGround) {
			players[myID].vy=-6;
			players[myID].onGround=false;
		}
		if(e.getKeyCode()==10) { //Press Enter to Automatically Fill Name as PlayerN
			players[myID].name="Player"+myID;
			c.write("Player"+myID+"UNameU"+players[myID].name);
		}
		if(e.getKeyCode()==8) { //Press Backspace to Ready Up
			c.write("Ready");
			ready++;
			if(ready==Server.n) {
				started=true;
			}
		}
	}
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==65) {
			movingLeft=false;
		}
		if(e.getKeyCode()==68) {
			movingRight=false;
		}
		if(e.getKeyCode()==38) {
			 movingUp=false;
		}
		if(e.getKeyCode()==40) {
			movingDown=false;
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		shooting=true;
		
	}
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH,HEIGHT);
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		shooting=false;
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		mx=e.getX();
		my=e.getY()-30;
		int x=e.getX();
		int y=e.getY()-30;
		int px=Math.round(players[myID].x);
		int py=Math.round(players[myID].y);
		float deltax=x-px;
		float deltay=y-py;
		if(deltax<0) {
			players[myID].weaponRotation=(float) ((float) Math.atan(deltay/deltax)+Math.toRadians(180));
			c.write("Player"+myID+"UAimU"+(float) ((float) Math.atan(deltay/deltax)+Math.toRadians(180)));
		}else {
			players[myID].weaponRotation=(float) Math.atan(deltay/deltax);
			c.write("Player"+myID+"UAimU"+(float) ((float) Math.atan(deltay/deltax)));
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		mx=e.getX();
		my=e.getY()-30;
		int x=e.getX();
		int y=e.getY()-30;
		int px=Math.round(players[myID].x);
		int py=Math.round(players[myID].y);
		float deltax=x-px;
		float deltay=y-py;
		if(deltax<0) {
			players[myID].weaponRotation=(float) ((float) Math.atan(deltay/deltax)+Math.toRadians(180));
			c.write("Player"+myID+"UAimU"+(float) ((float) Math.atan(deltay/deltax)+Math.toRadians(180)));
		}else {
			players[myID].weaponRotation=(float) Math.atan(deltay/deltax);
			c.write("Player"+myID+"UAimU"+(float) ((float) Math.atan(deltay/deltax)));
		}
		
		
	}
	
}
