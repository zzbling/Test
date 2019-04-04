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
/*��������*/
public class ShootGame extends JPanel {

	public static final int WIDTH=400;//���ڿ�
	public static final int HEIGHT=654;//���ڸ�
	
	public static BufferedImage background;
	public static BufferedImage airplane;
	public static BufferedImage bee;
	public static BufferedImage bullet;
	public static BufferedImage gameover;
	public static BufferedImage hero0;
	public static BufferedImage hero1;
	public static BufferedImage pause;
	public static BufferedImage start;
	
	public static final int START=0;//����
	public static final int RUNNING=1;//����
	public static final int PAUSE=2;//��ͣ
	public static final int GAME_OVER=3;//����
	private int state=START;//��ǰ״̬
	
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
	static {//��ʼ����̬��Դ
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
	
	/*����ӵ�����������ײ*/
	public void bangAction() {
		for(int i=0;i<bullets.length;i++) {
			Bullet b=bullets[i];
			bang(b);//1���ӵ������е��˵���ײ
		}
	}
	
	int score=0;//�÷�
	/*1���ӵ������е�����ײ*/
	public void bang(Bullet b) {
		int index=-1;//�洢��ײ���˵��±�
		for(int i=0;i<flyings.length;i++) {
			FlyingObject f=flyings[i];
			if(f.shootBy(b)) {
				index=i;//��¼���˵��±�
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
			/*����ײ�������������һ��Ԫ�ؽ���*/
			FlyingObject t=flyings[index];
			flyings[index]=flyings[flyings.length-1];
			flyings[flyings.length-1]=t;
			/*����*/
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
			if(hero.hit(f)) {//��ײ��
				hero.subtractLife();//����
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
			/*����ƶ��¼�*/
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
			/*��д����Ƴ�*/
			public void mouseExited(MouseEvent e) {
				if(state==RUNNING) {//����״̬ʱ
					state=PAUSE;//��Ϊ��ͣ״̬
				}
			}
			/*��д��������¼�*/
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
					enterAction();//���ˣ��л�+С�۷䣩�볡
					stepAction();//��������һ��
					shootAction();//�ӵ��볡��Ӣ�ۼ������ӵ���
					outOfBoundsAction();//ɾ��Խ��ĵ��ˣ����ˣ�
					bangAction();//�ӵ�����˵���ײ
					checkGameOverAction();//Ӣ�ۻ��������ײ
				}
				repaint();//�ػ�--����paint()����
			}
		},interval,interval);
		
	}
	
	public void paint(Graphics g) {
		g.drawImage(background,0,0,null);
		paintHero(g);//��Ӣ�ۻ�����
		paintFlyingObjects(g);//���л�
		paintBullets(g);//���ӵ�����
		paintScoreAndLife(g);//���ֺ���
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
		
		g.drawString("SCORE: "+score,10,25);//������
		g.drawString("LIFE: "+hero.getLife(),10 ,45 );
	}
	/*��״̬*/
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
