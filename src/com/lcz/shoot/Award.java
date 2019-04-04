package com.lcz.shoot;
/*奖励*/
public interface Award {
	public static final int DOUBLE_FIRE=0;//活力值
	public static final int LIFE=1;//生命值
	/*获取奖励类型,返回0为活力值，返回1为命*/
	public int getType();
}
