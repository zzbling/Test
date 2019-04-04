package com.lcz.shoot;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
/*主程序类*/
public class ShootGame extends JPanel {

	public static final int WIDTH=400;//窗口宽
	public static final int HEIGHT=654;//窗口高
	
	public static BufferedImage background;
	public static BufferedImage airplane;
	public static BufferedImage bee;
	public static BufferedImage bullet;
	public static BufferedImage gameover;
	public static BufferedImage hero0;
	public static BufferedImage hero1;
	public static BufferedImage pause;
	public static BufferedImage start;
	
	public static final int START=0;//启动
	public static final int RUNNING=1;//运行
	public static final int PAUSE=2;//暂停
	public static final int GAME_OVER=3;//结束
	private int state=START;//当前状态
	
	private Hero hero=new Hero();
	private FlyingObject[] flyings={};
	private Bullet[] bullets= {};
	
//	ShootGame(){
//		flyings=new FlyingObject[2];
//		flyings[0]=new Airplane();
//		flyings[1]=new Bee();
//		bullets=new Bullet[1];
//		bullets[0]=new Bullet(100,200);
//	}
	static {//初始化静态资源
		try {
			background=ImageIO.read(ShootGame.class.getResource("background.png"));
			airplane=ImageIO.read(ShootGame.class.getResource("airplane.png"));
			bee=ImageIO.read(ShootGame.class.getResource("bee.png"));
			bullet=ImageIO.read(ShootGame.class.getResource("bullet.png"));
			gameover=ImageIO.read(ShootGame.class.getResource("gameover.png"));
			hero0=ImageIO.read(ShootGame.class.getResource("hero0.png"));
			hero1=ImageIO.read(ShootGame.class.getResource("hero1.png"));
			pause=ImageIO.read(ShootGame.class.getResource("pause.png"));
			start=ImageIO.read(ShootGame.class.getResource("start.png"));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public FlyingObject nextOne() {

		Random rand=new Random();
		int type=rand.nextInt(20);
		if(type==0) {
			return new Bee();
		}else {
			return new Airplane();
		}
	}

	int flyEnteredIndex=0;
	public void enterAction() {
		flyEnteredIndex++;
		if(flyEnteredIndex%40==0) {
			FlyingObject one=nextOne();
			flyings=Arrays.copyOf(flyings, flyings.length+1);
			flyings[flyings.length-1]=one;
		}
		
	}
	public void stepAction() {
		hero.step();
		for(int i=0;i<flyings.length;i++) {
			flyings[i].step();
		}
		for(int i=0;i<bullets.length;i++) {
			bullets[i].step();
		}
	}
	
	int shootIndex=0;
	public void shootAction() {
		shootIndex++;
		if(shootIndex%30==0) {
			Bullet[] bs=hero.shoot();
			bullets=Arrays.copyOf(bullets, bullets.length+bs.length);
			System.arraycopy(bs, 0, bullets,bullets.length-bs.length,bs.length);
		}
	}	
	

	public void outOfBoundsAction() {
		int index=0;		
		FlyingObject[] flyingLives=new FlyingObject[flyings.length];
		
		for(int i=0;i<flyings.length;i++) {
			FlyingObject f=flyings[i];
			if(!f.outOfBounds()) {
				flyingLives[index]=f;
				index++;
			}
		}
		flyings=Arrays.copyOf(flyingLives,index);
		
		index=0;
		Bullet[] bulletLives=new Bullet[bullets.length];
		for(int i=0;i<bullets.length;i++) {
			Bullet b=bullets[i];
			if(!b.outOfBounds()) {
				bulletLives[index]=b;
				index++;
			}
		}		
		bullets=Arrays.copyOf(bulletLives, index);
	}
	
	/*多个子弹与多个敌人相撞*/
	public void bangAction() {
		for(int i=0;i<bullets.length;i++) {
			Bullet b=bullets[i];
			bang(b);//1个子弹与所有敌人的碰撞
		}
	}
	
	int score=0;//得分
	/*1个子弹与所有敌人相撞*/
	public void bang(Bullet b) {
		int index=-1;//存储被撞敌人的下标
		for(int i=0;i<flyings.length;i++) {
			FlyingObject f=flyings[i];
			if(f.shootBy(b)) {
				index=i;//记录敌人的下标
				break;
			}
		}
		if(index!=-1) {
			FlyingObject one=flyings[index];
			if(one instanceof Enemy) {
				Enemy e=(Enemy)one;
				score+=e.getScore();
			}
			if(one instanceof Award) {
				Award a=(Award)one;
				int type=a.getType();
				switch(type) {
				case Award.DOUBLE_FIRE:
					hero.addDoubleFire();
					break;
				case Award.LIFE:
					hero.addLife();
					break;
				}
			}
			/*将被撞敌人与数组最后一个元素交换*/
			FlyingObject t=flyings[index];
			flyings[index]=flyings[flyings.length-1];
			flyings[flyings.length-1]=t;
			/*缩容*/
			flyings=Arrays.copyOf(flyings, flyings.length-1);
		}
	}
	
	public void checkGameOverAction() {
		if(isGameOver()) {
			state=GAME_OVER;
		}
	}
	public boolean isGameOver() {		
		for(int i=0;i<flyings.length;i++) {
			FlyingObject f=flyings[i];
			if(hero.hit(f)) {//相撞了
				hero.subtractLife();//减命
				hero.clearDoubleFire();//
				
				FlyingObject t=flyings[i];
				flyings[i]=flyings[flyings.length-1];
				flyings[flyings.length-1]=t;
				
				flyings=Arrays.copyOf(flyings, flyings.length-1);
			}
		}		
		return hero.getLife()<=0;
	}
	public void action() {
		MouseAdapter l=new MouseAdapter() {
			/*鼠标移动事件*/
			public void mouseMoved(MouseEvent e) {
				if(state==RUNNING) {
					int x=e.getX();
					int y=e.getY();
					hero.moveTo(x, y);
				}
			}
			
			public void mouseClicked(MouseEvent e) {
				switch(state) {
				case START:
					state=RUNNING;break;
				case GAME_OVER:
					score=0;
					hero=new Hero();
					flyings=new FlyingObject[0];
					bullets=new Bullet[0];
					state=START;break;
				}
			}
			/*重写鼠标移出*/
			public void mouseExited(MouseEvent e) {
				if(state==RUNNING) {//运行状态时
					state=PAUSE;//改为暂停状态
				}
			}
			/*重写鼠标移入事件*/
			public void mouseEntered(MouseEvent e) {
				if(state==PAUSE) {
					state=RUNNING;
				}
			}
		};
		this.addMouseListener(l);
		this.addMouseMotionListener(l);
		
		Timer timer=new Timer();
		int interval=10;//
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(state==RUNNING) {
					enterAction();//敌人（敌机+小蜜蜂）入场
					stepAction();//飞行物走一步
					shootAction();//子弹入场（英雄级发射子弹）
					outOfBoundsAction();//删除越界的敌人（敌人）
					bangAction();//子弹与敌人的碰撞
					checkGameOverAction();//英雄机与敌人相撞
				}
				repaint();//重画--调用paint()方法
			}
		},interval,interval);
		
	}
	
	public void paint(Graphics g) {
		g.drawImage(background,0,0,null);
		paintHero(g);//画英雄机对象
		paintFlyingObjects(g);//画敌机
		paintBullets(g);//画子弹对象
		paintScoreAndLife(g);//画分和命
		paintState(g);
	}
	
	public void paintHero(Graphics g) {
		g.drawImage(hero.image,hero.x,hero.y,null);
	}
	public void paintFlyingObjects(Graphics g) {
		for(int i=0;i<flyings.length;i++) {
			FlyingObject f=flyings[i];
			g.drawImage(f.image,f.x,f.y,null);
		}
	}
	public void paintBullets(Graphics g) {
		for(int i=0;i<bullets.length;i++) {
			Bullet b=bullets[i];
			g.drawImage(b.image,b.x,b.y,null);
		}
	}
	
	public void paintScoreAndLife(Graphics g) {
		g.setColor(new Color(0xFF0000));
		g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,24));
		
		g.drawString("SCORE: "+score,10,25);//画分数
		g.drawString("LIFE: "+hero.getLife(),10 ,45 );
	}
	/*画状态*/
	public void paintState(Graphics g) {
		switch(state) {
		case START:
			g.drawImage(start,0,0,null);
			break;
		case PAUSE:
			g.drawImage(pause,0,0,null);
			break;
		case GAME_OVER:
			g.drawImage(gameover,0,0,null);
			break;
		}
	}
	public static void main(String[] args) {
		JFrame frame=new JFrame("Fly");
		ShootGame game=new ShootGame();
		frame.add(game);
		frame.setSize(WIDTH,HEIGHT);
		frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		game.action();
		
		
	}

}
