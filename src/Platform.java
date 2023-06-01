
public class Platform {
	public int ox;
	public int oy;
	public float x;
	public float y;
	public int width;
	public int height;
	public int fx;
	public int fy;
	private int moveDuration=100;
	private int moveStage=0;
	public Platform(int ox,int oy,int fx,int fy,int width,int height) {
		this.ox=ox;
		this.oy=oy;
		this.x=ox;
		this.y=oy;
		this.width=width;
		this.height=height;
		this.fx=fx;
		this.fy=fy;
	}
	public void update() {
		moveStage++;
		if(moveStage<200) {
			x+=(fx-ox)/200f;
			y+=(fy-oy)/200f;
		}else if(moveStage<400){
			x-=(fx-ox)/200f;
			y-=(fy-oy)/200f;
		}else {
			moveStage=0;
		}
	}
}
