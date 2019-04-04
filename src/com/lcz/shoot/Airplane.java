package com.lcz.shoot;
import java.util.Random;

public class Airplane extends FlyingObject implements Enemy {

	private int speed=2;//走步的步数
	
	public Airplane(){
		image=ShootGame.airplane;
		width=image.getWidth();
		height=image.getHeight();
		Random rand=new Random();
		x=rand.nextInt(ShootGame.WIDTH-this.width);
		y=-this.height;
		//y=200;
	}
	/*重写getScore()*/
	@Override
	public int getScore() {
		// TODO Auto-generated method stub
		return 5;//打掉一个敌机得5分
	}
	@Override
	public void step() {
		// TODO Auto-generated method stub
		y+=speed;
	}
	@Override
	public boolean outOfBounds() {
		// TODO Auto-generated method stub
		return this.y>ShootGame.HEIGHT;
	}
	
	
}
