package com.lcz.shoot;

import java.util.Random;

public class Bee extends FlyingObject implements Award {

	private int xSpeed=1;//x坐标走步步数
	private int ySpeed=2;//y坐标走步步数
	private int awardType;//奖励的类型，随机产生
	
	public Bee() {
		image=ShootGame.bee;
		width=image.getWidth();
		height=image.getHeight();
		Random rand=new Random();
		x=rand.nextInt(ShootGame.WIDTH-this.width);
		y=-this.height;
		//y=200;
		awardType=rand.nextInt(2);
	}
	/*重写getType()方法*/
	public int getType() {
		return awardType;//返回奖励类型
	}
	@Override
	public void step() {
		// TODO Auto-generated method stub
		x+=xSpeed;
		y+=ySpeed;
		if(x>=ShootGame.WIDTH-this.width) {
			xSpeed=-1;
		}
		if(x<=0) {
			xSpeed=1;
		}
	}
	@Override
	public boolean outOfBounds() {
		// TODO Auto-generated method stub
		return this.y>ShootGame.HEIGHT;
	}

}
