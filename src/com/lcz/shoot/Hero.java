package com.lcz.shoot;
import java.awt.image.BufferedImage;
/*英雄机*/
public class Hero extends FlyingObject {
	private int life;//命
	private int doubleFire;//生命值
	private BufferedImage[] images;//图片数组
	private int index;//协助图片切换
	
	public Hero() {
		image=ShootGame.hero0;
		width=image.getWidth();
		height=image.getHeight();
		x=150;
		y=400;
		life=3;
		doubleFire=0;
		images=new BufferedImage[] {ShootGame.hero0,ShootGame.hero1};
		index=0;
	}

	@Override
	public void step() {
		// TODO Auto-generated method stub
		index++;
		image=images[index/10%2];
	}
	
	public Bullet[] shoot() {
		int xStep=this.width/4;
		int yStep=20;
		if(doubleFire>0) {
			Bullet[] bs=new Bullet[2];
			bs[0]=new Bullet(this.x+1*xStep,this.y-yStep);
			bs[1]=new Bullet(this.x+3*xStep,this.y-yStep);
			doubleFire-=2;
			return bs;
		}else {
			Bullet[] bs=new Bullet[1];
			bs[0]=new Bullet(this.x+2*xStep,this.y-yStep);
			return bs;
		}
	}
	
	 public void moveTo(int x,int y) {
		 this.x=x-this.width/2;
		 this.y=y-this.height/2;
	 }

	@Override
	public boolean outOfBounds() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/*英雄机增命*/
	public void addLife() {
		life++;
	}
	
	public void subtractLife() {
		life--;
	}
	/*获取命*/
	public int getLife() {
		return life;
	}
	/*英雄机增火力*/
	public void addDoubleFire() {
		doubleFire+=40;
	}
	
	public void clearDoubleFire() {
		doubleFire=0;
	}
	/*英雄机撞敌人*/
	public boolean hit(FlyingObject obj) {
		int x1=obj.x-this.width/2;
		int x2=obj.x+obj.width+this.width/2;
		int y1=obj.y-this.height/2;
		int y2=obj.y+obj.height+this.width/2;
		
		int x=this.x+this.width/2;
		int y=this.y+this.height/2;
		
		return x>=x1&&x<=x2&&y>=y1&&y<=y2;
	}
}
