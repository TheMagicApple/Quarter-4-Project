import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import java.awt.font.TextAttribute;
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
	final static int MACHINEGUNDAMAGE=4;
	final static int ASSAULTDAMAGE=10;
	final static int SNIPERDAMAGE=50;
	final static int SHOTGUNDAMAGE=25;
	final static int TRISHOTDAMAGE=10;
	int groundLevel=400;
	int rightGroundLevel=1000;
	int leftGroundLevel=-1000;
	int upGroundLevel=10000;
	static boolean started=false;
	static boolean ended=false;
	static int clients=0;
	static int ready=0;
	boolean dead=false;
	static ArrayList<Player> deadPlayers=new ArrayList<>();
	int weaponCooldown=0;
	int mx=0;
	int my=0;
	int px=0;
	int py=0;
	float deltax=0;
	float deltay=0;
	int bulletCounter=0;
	int frameCounter=0;
	boolean readyHover=false;
	boolean read_e=false;
	static DecimalFormat df=	new DecimalFormat("0.00");
	static String winner="";
	String myName="";
	boolean enteringName=false;
	public Screen() throws IOException {
		for(int i=0;i<players.length;i++) {
			players[i]=new Player();
			players[i].x=100+200*i;
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
			Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
			attributes.put(TextAttribute.TRACKING, -0.02);
			g.setFont(new Font("Open Sans Bold",Font.PLAIN,100).deriveFont(attributes));
			
			g.drawString("P   LY", 170,150);
			Map<TextAttribute, Object> attributes2 = new HashMap<TextAttribute, Object>();
			attributes2.put(TextAttribute.TRACKING, 0.04);
			g.setFont(new Font("Open Sans Bold",Font.PLAIN,100).deriveFont(attributes2));
			g.drawString("P  RTY!", 470,150);
			g.setColor(new Color(255,0,0));
			g.fillOval(235,80,70,70);
			g.setColor(new Color(52, 122, 235));
			g.fillPolygon(new int[] {525,560,595},new int[] {150,80,150},3);
			g.setFont(new Font("Open Sans",Font.PLAIN,30));
			g.setColor(new Color(255,0,0));
			g.fillRoundRect(300,200,400,50,10,10);
			g.setColor(Color.white);
			drawCenteredString(g,"Players Connected: "+clients+"/"+Server.n, new Rectangle(300,200,400,50),new Font("Open Sans Bold",Font.PLAIN,30));
			if(read_e)  g.setColor(new Color(57, 191, 86));
			else if(readyHover) g.setColor(new Color(47, 110, 212));
			else g.setColor(new Color(52, 122, 235));
			g.fillRoundRect(400,300,200,70,10,10);
			g.setColor(Color.white);
			drawCenteredString(g,"Ready", new Rectangle(400,300,200,70),new Font("Open Sans Bold",Font.PLAIN,45));
			g.setColor(Color.black);
			drawCenteredString(g,ready+"/"+Server.n, new Rectangle(400,390,200,10),new Font("Open Sans Bold",Font.PLAIN,30));
			if(enteringName) g.setColor(new Color(52, 122, 235));
			else g.setColor(new Color(0,0,0,50));
			((Graphics2D)g).setStroke(new BasicStroke(3));
			g.drawRoundRect(700,310,200,50,10,10);
			g.setColor(new Color(30,30,30));
			drawCenteredString(g,"Username", new Rectangle(650,280,200,30),new Font("Open Sans",Font.PLAIN,20));
			drawCenteredString(g,players[myID].name, new Rectangle(700,310,200,50),new Font("Open Sans",Font.PLAIN,30));
			//drawCenteredString(g,"(Enter to Submit)", new Rectangle(700,360,200,30),new Font("Open Sans",Font.PLAIN,12));
		}else if(ended) {
			g.setColor(new Color(30,30,30));
			g.drawString(winner, 200,200);
		}
		else{
			
			g.setColor(new Color(220,220,220));
			for(int i=25;i<1000;i+=50) {
				g.fillRect(i, 0, 2, 1000);
			}
			for(int i=25;i<500;i+=50) {
				g.fillRect(0, i, 1000, 2);
			}
			
			g.setColor(new Color(255,0,0,120));
			g.drawLine(px+8, py, px+8+Math.round(deltax)*10, py+Math.round(deltay)*10);
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
			g.setColor(new Color(30,30,30));
			if(frameCounter<=500) {
				drawCenteredString(g,""+df.format((500-frameCounter)/100f),new Rectangle(0,0,1000,200),new Font("Open Sans Bold",Font.PLAIN,90));
			}else if(frameCounter<=550) {
				g.setColor(new Color(57, 191, 86));
				drawCenteredString(g,"GO!",new Rectangle(0,0,1000,200),new Font("Open Sans Bold",Font.PLAIN,90));
			}
			
		}
	
	}
	public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
	    FontMetrics metrics = g.getFontMetrics(font);
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    g.setFont(font);
	    g.drawString(text, x, y);
	}
	public void animate() throws InterruptedException{
		while(true) {
			if(players[myID].y>HEIGHT) {
				dead=true;
				deadPlayers.add(players[myID]);
				c.write("Player"+myID+"UDead");
			}
			if(started) {
				frameCounter++;
			}
			if(deadPlayers.size()==Server.n-1 && !ended) {
				if(!dead) {
					ended=true;
					winner="YOU are the WINNER!";
					c.write("Player"+myID+"UWinner");
				}
			}
			if(!dead) {
					
				
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
				px=Math.round(players[myID].x+10);
				py=Math.round(players[myID].y+10);
				deltax=mx-px;
				deltay=my-py;
				if(weaponCooldown>0) {
					weaponCooldown--;
				}
				
				for(int i=0;i<bullets.size();i++) {
					boolean bad=false;
					Bullet bullet=bullets.get(i);
					if(bullet.x>1000f || bullet.x<0f) {
						bullets.remove(i);
						i--;
						
						bad=true;
					}
					for(Platform plat:platforms) {
						if(!bad && collision(Math.round(bullet.x),Math.round(bullet.y),5,5,plat.x,plat.y,plat.width,plat.height)){
							bullets.remove(i);
							i--;
							bad=true;
						}
					}
					if(collision(Math.round(bullet.x),Math.round(bullet.y),bullet.width,bullet.height,Math.round(players[myID].x),Math.round(players[myID].y),20,20)){
						if(!bad && Integer.parseInt(String.valueOf(bullet.player.charAt(6)))!=myID) {
							String weaponClass=players[Integer.parseInt(String.valueOf(bullet.player.charAt(6)))].weaponClass;
							if(weaponClass.equals("MachineGun")) {
								players[myID].health-=MACHINEGUNDAMAGE;
								c.write("Player"+myID+"UDamageU"+MACHINEGUNDAMAGE+"U"+(i));
							}else if(weaponClass.equals("Assault")) {
								players[myID].health-=ASSAULTDAMAGE;
								c.write("Player"+myID+"UDamageU"+ASSAULTDAMAGE+"U"+(i));
							}else if(weaponClass.equals("Sniper")) {
								players[myID].health-=SNIPERDAMAGE;
								c.write("Player"+myID+"UDamageU"+SNIPERDAMAGE+"U"+(i));
							}else if(weaponClass.equals("Shotgun")) {
								players[myID].health-=SHOTGUNDAMAGE;
								c.write("Player"+myID+"UDamageU"+SHOTGUNDAMAGE+"U"+(i));
							}else if(weaponClass.equals("TriShot")) {
								players[myID].health-=TRISHOTDAMAGE;
								c.write("Player"+myID+"UDamageU"+TRISHOTDAMAGE+"U"+(i));
							}
							players[myID].animationStage=0;
							bullets.remove(i);
							i--;
							if(players[myID].health<=0) {
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
				//frame counter: grace period for 500/100=5 seconds
				if(shooting && !dead && frameCounter>=500) {
					if(weaponCooldown==0) {
						bulletCounter++;
						//System.out.println(px+" "+py+" "+mx+" "+my+" "+deltax+" "+deltay);
						float vx=(float)(deltax/Math.sqrt(deltax*deltax+deltay*deltay));
						float vy=-1*(float)(deltay/Math.sqrt(deltax*deltax+deltay*deltay));
						vx*=10;
						vy*=10;
						int width=5;
						int height=5;
						if(players[myID].weaponClass.equals("Shotgun")) {
							width=15;
							height=15;
						}
						bullets.add(new Bullet(players[myID].x+18,players[myID].y+10,width,height,vx,vy,"Player"+myID));
						c.write("Player"+myID+"UShootU"+players[myID].weaponClass+"BulletU"+vx+" "+vy);
						if(players[myID].weaponClass.equals("MachineGun")) {
							weaponCooldown=5;
						}else if(players[myID].weaponClass.equals("Assault")){
							weaponCooldown=15;
						}else if(players[myID].weaponClass.equals("Sniper")) {
							weaponCooldown=70;
						}else if(players[myID].weaponClass.equals("Shotgun")) {
							weaponCooldown=30;
						}else if(players[myID].weaponClass.equals("TriShot")) {
							if(bulletCounter%3==0) {
								weaponCooldown=50;
							}else {
								weaponCooldown=3;
							}
						}
					}
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
				if(command.equals("Winner") && !ended) {
					ended=true;
					winner=players[id].name+" is the WINNER!";
					
				}
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
					int width=5;
					int height=5;
					if(players[id].weaponClass.equals("Shotgun")) {
						width=15;
						height=15;
					}
					if(change.equals("MachineGunBullet")) { //This allows for different bullets for different classes, not used rn
						bullets.add(new Bullet(players[id].x+20,players[id].y+10,width,height,vx,vy,"Player"+id));
					}else if(change.equals("AssaultBullet")) {
						bullets.add(new Bullet(players[id].x+20,players[id].y+10,width,height,vx,vy,"Player"+id));
					}else if(change.equals("SniperBullet")){
						bullets.add(new Bullet(players[id].x+20,players[id].y+10,width,height,vx,vy,"Player"+id));
					}else if(change.equals("ShotgunBullet")) {
						bullets.add(new Bullet(players[id].x+20,players[id].y+10,width,height,vx,vy,"Player"+id));
					}else if(change.equals("TriShotBullet")) {
						bullets.add(new Bullet(players[id].x+20,players[id].y+10,width,height,vx,vy,"Player"+id));
					}
				}
				if(command.equals("Damage")) {
					players[id].health-=Integer.parseInt(change);
					players[id].animationStage=0;
					if(bullets.size()==1) {
						bullets.remove(0);
					}
					else {
						if (Integer.parseInt(messageParts[3]) < bullets.size())
							bullets.remove(Integer.parseInt(messageParts[3]));
					}
				}
				if(command.equals("Dead")) {
					deadPlayers.add(players[id]);
				}
				if(command.equals("Aim")) {
					players[id].weaponRotation=Float.parseFloat(change);
				}
				if(command.equals("Class")) {
					players[id].weaponClass=change;
				}
			}
		}
	}
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void keyPressed(KeyEvent e) {
		if(enteringName) {
			if(e.getKeyCode()==8) {
				players[myID].name=players[myID].name.substring(0,players[myID].name.length()-1);

			}else if(e.getKeyCode()==10) {
				/*players[myID].name=myName;
				c.write("Player"+myID+"UNameU"+players[myID].name);
				enteringName=false;*/
			}else if(e.getKeyCode()>=44 && e.getKeyCode()<=95){
				players[myID].name+=String.valueOf(e.getKeyChar());
			}
		}else {
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
			/*if(e.getKeyCode()==10) { //Press Enter to Automatically Fill Name as PlayerN
				players[myID].name="Player"+myID;
				c.write("Player"+myID+"UNameU"+players[myID].name);
			}*/
			if(e.getKeyCode()==49) { //Select MachineGun Class
				players[myID].weaponClass="MachineGun";
				c.write("Player"+myID+"UClassUMachineGun");
			}
			if(e.getKeyCode()==50) { //Select Assault Class
				players[myID].weaponClass="Assault";
				c.write("Player"+myID+"UClassUAssault");
			}
			if(e.getKeyCode()==51) { //Select Sniper Class
				players[myID].weaponClass="Sniper";
				c.write("Player"+myID+"UClassUSniper");
			}
			if(e.getKeyCode()==52) { //Select Shotgun Class
				players[myID].weaponClass="Shotgun";
				c.write("Player"+myID+"UClassUShotgun");
			}
			if(e.getKeyCode()==53) { //Select TriShot Class
				players[myID].weaponClass="TriShot";
				c.write("Player"+myID+"UClassUTriShot");
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
		if(e.getX()>=400 && e.getX()<=600 && e.getY()>=300 && e.getY()<=370 && !read_e) {
			c.write("Ready");
			ready++;
			read_e=true;
			if(ready==Server.n) {
				started=true;
			} 
			
		}
		if(e.getX()>=700 && e.getX()<=900 && e.getY()>=340 && e.getY()<=390 && !started) {
			enteringName=true;
		}else {
			enteringName=false;
		}
		
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
		if(started) {
			mx=e.getX()-7;
			my=e.getY()-30;
			int x=e.getX()-7;
			int y=e.getY()-30;
			px=Math.round(players[myID].x+18);
			py=Math.round(players[myID].y+10);
			float deltax=x-px;
			float deltay=y-py;
			//System.out.println(x+" "+y+" "+px+" "+py+" "+deltax+" "+deltay);
			if(deltax<0) {
				players[myID].weaponRotation=(float) ((float) Math.atan(deltay/deltax)+Math.toRadians(180));
				c.write("Player"+myID+"UAimU"+(float) ((float) Math.atan(deltay/deltax)+Math.toRadians(180)));
			}else {
				players[myID].weaponRotation=(float) Math.atan(this.deltay/this.deltax);
				c.write("Player"+myID+"UAimU"+(float) ((float) Math.atan(deltay/deltax)));
			}
		}
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		if(started) {
			mx=e.getX()-7;
			my=e.getY()-30;
			int x=e.getX()-7;
			int y=e.getY()-30;
			px=Math.round(players[myID].x+10);
			py=Math.round(players[myID].y+10);
			float deltax=x-px;
			float deltay=y-py;
			if(deltax<0) {
				players[myID].weaponRotation=(float) ((float) Math.atan(deltay/deltax)+Math.toRadians(180));
				c.write("Player"+myID+"UAimU"+(float) ((float) Math.atan(deltay/deltax)+Math.toRadians(180)));
			}else {
				players[myID].weaponRotation=(float) Math.atan(deltay/deltax);
				c.write("Player"+myID+"UAimU"+(float) ((float) Math.atan(deltay/deltax)));
			}
		}else {
			if(e.getX()>=400 && e.getX()<=600 && e.getY()>=300 && e.getY()<=400) {
				readyHover=true;
			}else {
				readyHover=false;
			}
		}
		
		
		
	}
	
}
